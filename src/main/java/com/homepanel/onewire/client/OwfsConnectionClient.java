/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package com.homepanel.onewire.client;

import com.homepanel.onewire.client.internal.Flags;
import com.homepanel.onewire.client.internal.RequestPacket;
import com.homepanel.onewire.client.internal.ResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OwfsConnectionClient {

	private final static Logger LOGGER = LoggerFactory.getLogger(OwfsConnectionClient.class);
	private final static int OWNET_DEFAULT_DATALEN = 4096; // default data length

	private OwfsConnectionConfig config;
	private Socket owSocket;
	private DataInputStream owIn;
	private DataOutputStream owOut;

	public OwfsConnectionClient(OwfsConnectionConfig config) {
		this.config = config;
	}

	public void setConfiguration(OwfsConnectionConfig config) {
		this.config = config;
	}

	private boolean connect(boolean force) throws IOException {
		if (force) {
			closeSocketAndDataStreams();
		}
		if (owSocket == null || !owSocket.isConnected()) {
			return tryToSocketConnectionAndStreamsInitialization();
		} else {
			return false;
		}
	}

	private boolean tryToSocketConnectionAndStreamsInitialization() throws IOException {
		owSocket = new Socket();
		for (InetAddress inetAddress : InetAddress.getAllByName(config.getHostName())) {
			try {
				owSocket.connect(new InetSocketAddress(inetAddress, config.getPortNumber()), config.getConnectionTimeout());
				owIn = new DataInputStream(owSocket.getInputStream());
				owOut = new DataOutputStream(owSocket.getOutputStream());
				return true;
			} catch (Exception ste) {
				LOGGER.debug(ste.getMessage());
				owSocket = null;
			}
		}
		return false;
	}

	public void disconnect() throws IOException {
		closeSocketAndDataStreams();
	}

	private void closeSocketAndDataStreams() throws IOException {
		if (owSocket != null) {
			owSocket.close();
		}
		owSocket = null;
		close(owOut);
		owOut = null;
		close(owIn);
		owIn = null;
	}

	private void close(Closeable closeable) throws IOException {
		if (closeable != null) {
			closeable.close();
		}
	}

	private void establishConnectionIfNeeded() throws IOException {
		if (owSocket == null || !owSocket.isConnected() || !isPersistenceEnabled()) {
			connect(true);
		}
	}

	private void disconnectIfConfigured() throws IOException {
		if (!isPersistenceEnabled()) {
			disconnect();
		}
	}

	private boolean isPersistenceEnabled() {
		return config.getFlags().getPersistence() == Enums.OwPersistence.ON;
	}

	public String read(String path) throws IOException, OwfsException {
		ResponsePacket response;
		RequestPacket request = new RequestPacket(Enums.OwMessageType.READ, OWNET_DEFAULT_DATALEN, config.getFlags(), path);
		sendRequest(request);
		do {
			response = readPacket();
			// Ignore PING messages (i.e. messages with payload length -1)
		} while (response.getHeader().getPayloadLength() == -1);

		disconnectIfConfigured();
		return response.getPayload();
	}

	public void write(String path, String dataToWrite) throws IOException, OwfsException {
		RequestPacket request = new RequestPacket(Enums.OwMessageType.WRITE, config.getFlags(), path, dataToWrite);
		sendRequest(request);
		/*
		* Even if we're not interested in the result of the response packet
		* we must read the packet from the socket. Partly to clean incoming
		* bytes but also in order to throw exceptions on error.
		*/
		readPacket();
		disconnectIfConfigured();
	}

	public Boolean exists(String path) throws IOException, OwfsException {
		ResponsePacket response;
		RequestPacket request = new RequestPacket(Enums.OwMessageType.PRESENCE, 0, config.getFlags(), path);
		sendRequest(request);
		response = readPacket();

		disconnectIfConfigured();

		return response.getHeader().getFunction() >= 0;

	}

	public List<String> listDirectoryAll(String path) throws OwfsException, IOException {
		RequestPacket request = new RequestPacket(Enums.OwMessageType.DIRALL, 0, config.getFlags(), path);
		sendRequest(request);
		ResponsePacket response = readPacket();
		List<String> list = new ArrayList<String>();
		if (response != null && response.getPayload() != null) {
			String[] arr = response.getPayload().split(",");
			Collections.addAll(list, arr);
		}
		disconnectIfConfigured();
		return list;
	}

	public List<String> listDirectory(String path) throws OwfsException, IOException {
		RequestPacket request = new RequestPacket(Enums.OwMessageType.DIR, 0, config.getFlags(), path);
		sendRequest(request);
		ResponsePacket response;
		List<String> list = new ArrayList<String>();
		while ((response = readPacket()) != null && response.getHeader().getPayloadLength() != 0) {
			list.add(response.getPayload());
		}
		disconnectIfConfigured();
		return list;
	}

	private void sendRequest(RequestPacket packet) throws IOException, OwfsException {
		establishConnectionIfNeeded();

		if (owOut != null) {
			owOut.writeInt(packet.getHeader().getVersion());
			owOut.writeInt(packet.getHeader().getPayloadLength());
			owOut.writeInt(packet.getHeader().getFunction());
			owOut.writeInt(packet.getHeader().getFlags().intValue());
			owOut.writeInt(packet.getHeader().getDataLength());
			owOut.writeInt(packet.getHeader().getOffset());

			owOut.write(packet.getPayload());

			if (packet.isWritingData()) {
				owOut.write(packet.getDataToWrite());
			}
		} else {
			throw new OwfsException("ow server seems to be down", 500);
		}
	}

	private ResponsePacket readPacket() throws IOException, OwfsException {
		int[] rawHeader = new int[6];
		try {
			rawHeader[0] = owIn.readInt(); // version
			rawHeader[1] = owIn.readInt(); // payload length
			rawHeader[2] = owIn.readInt(); // function return value
			rawHeader[3] = owIn.readInt(); // flags
			rawHeader[4] = owIn.readInt(); // data length
			rawHeader[5] = owIn.readInt(); // offset
		} catch (EOFException e) {
			return null;
		}
		grantOrDenyPersistence(rawHeader[3]);
		String payload = null;
		if (rawHeader[2] >= 0) { /* Check return value */
			if (rawHeader[1] >= 0) { /* Bytes to read */
				byte[] payloadBytes = new byte[rawHeader[1]];
				owIn.readFully(payloadBytes, 0, payloadBytes.length);

				if (rawHeader[1] > 0) {
					// Remove ending zero byte if any
					if (payloadBytes[rawHeader[1] - 1] == 0) {
						payload = new String(payloadBytes, 0, rawHeader[1] - 1);
					} else {
						payload = new String(payloadBytes, 0, rawHeader[1]);
					}
				} else {
					payload = null;
				}
			}
		} else {
			throw new OwfsException("Error " + rawHeader[2], rawHeader[2]);
		}
		return new ResponsePacket(rawHeader[0], rawHeader[1], rawHeader[2], new Flags(rawHeader[3]), rawHeader[4], rawHeader[5], payload);
	}

	private void grantOrDenyPersistence(int value) {
		Flags returnFlags = new Flags(value);
		if (returnFlags.getPersistence() == Enums.OwPersistence.ON) {
			config.getFlags().setPersistence(Enums.OwPersistence.ON);
		} else {
			config.getFlags().setPersistence(Enums.OwPersistence.OFF);
		}
	}
}