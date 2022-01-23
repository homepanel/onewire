/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package com.homepanel.onewire.client.internal;

public abstract class Packet {

	private Header header;

	public Packet(int version, int payloadLength, int function, int dataLength, Flags flags, int offset) {
		header = new Header(version, payloadLength, function, flags, dataLength, offset);
	}

	public Header getHeader() {
		return header;
	}
}