/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/

package com.homepanel.onewire.client.internal;

public class ResponsePacket extends Packet {

	String payload;

	public ResponsePacket(int version, int payloadLength, int returnValue, Flags flags, int dataLength, int offset, String payload) {
		super(version, payloadLength, returnValue, dataLength, flags, 0);
		this.payload = payload;
	}

	public String getPayload() {
		return payload;
	}
}
