/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package com.homepanel.onewire.client.internal;

import com.homepanel.onewire.client.Enums;

public class RequestPacket extends Packet {

	byte[] payload;
	byte[] dataToWrite;
	boolean writingData = false;

	public RequestPacket(Enums.OwMessageType function, int dataLength, Flags flags, String payload) {
		super(0, payload.length() + 1, function.intValue, dataLength, flags, 0);

		byte[] bytes = payload.getBytes();
		this.payload = new byte[bytes.length + 1];
		int i = 0;
		for (; i < bytes.length; i++) {
			this.payload[i] = bytes[i];
		}

		this.payload[i] = 0;

	}

	public RequestPacket(Enums.OwMessageType function, Flags flags, String payload, String dataToWrite) {
		super(0, payload.length() + 1 + dataToWrite.length(), function.intValue, dataToWrite.length(), flags, 0);

		byte[] bytes = payload.getBytes();
		this.payload = new byte[bytes.length + 1];
		int i = 0;
		for (; i < bytes.length; i++) {
			this.payload[i] = bytes[i];
		}
		this.payload[i] = 0;

		bytes = dataToWrite.getBytes();
		this.dataToWrite = new byte[bytes.length];
		for (i = 0; i < bytes.length; i++) {
			this.dataToWrite[i] = bytes[i];
		}
		writingData = true;

	}

	public byte[] getPayload() {
		return payload;
	}

	public boolean isWritingData() {
		return writingData;
	}

	public Enums.OwMessageType getFunction() {
		return Enums.OwMessageType.getEnum(getHeader().getFunction());
	}

	public byte[] getDataToWrite() {
		return dataToWrite;
	}

}
