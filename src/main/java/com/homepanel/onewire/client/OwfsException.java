/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package com.homepanel.onewire.client;

public class OwfsException extends Exception {

	private static final long serialVersionUID = 2322213126593948880L;
	private final int errorCode;

	public OwfsException(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}
}
