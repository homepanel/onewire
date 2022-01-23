/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/

package com.homepanel.onewire.client;

import com.homepanel.onewire.client.internal.Flags;

public class OwfsConnectionConfig {

	private String hostName;

	private int portNumber;

	private Flags flags = new Flags();

	private int connectionTimeout = 4000; // default to 4s timeout

	public OwfsConnectionConfig(String hostName, int portNumber) {
		this.hostName = hostName;
		this.portNumber = portNumber;
	}

	public String getHostName() {
		return hostName;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public Flags getFlags() {
		return flags;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setTimeout(int timeout) {
		connectionTimeout = timeout;
	}

	public void setDeviceDisplayFormat(Enums.OwDeviceDisplayFormat deviceDisplay) {
		flags.setDeviceDisplayFormat(deviceDisplay);
	}

	public Enums.OwDeviceDisplayFormat getOwDeviceDisplayFormat() {
		return flags.getDeviceDisplayFormat();
	}

	public void setTemperatureScale(Enums.OwTemperatureScale tempScale) {
		flags.setTemperatureScale(tempScale);
	}

	public Enums.OwTemperatureScale getTemperatureScale() {
		return flags.getTemperatureScale();
	}

	public void setPersistence(Enums.OwPersistence persistence) {
		flags.setPersistence(persistence);
	}

	public Enums.OwPersistence getPersistence() {
		return flags.getPersistence();
	}

	public void setAlias(Enums.OwAlias alias) {
		flags.setAlias(alias);
	}

	public Enums.OwAlias getAlias() {
		return flags.getAlias();
	}

	public void setBusReturn(Enums.OwBusReturn busReturn) {
		flags.setBusReturn(busReturn);
	}

	public Enums.OwBusReturn getBusReturn() {
		return flags.getBusReturn();
	}
}
