/*******************************************************************************
 * Copyright (c) 2009,2010 Patrik Akerfeldt
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD license
 * which accompanies this distribution, see the COPYING file.
 *
 *******************************************************************************/
package com.homepanel.onewire.client.internal;

import com.homepanel.onewire.client.Enums;

public class Flags {

	private static final int OWNET_REQUEST = 0x00000100; // default flag
	private int flags;
	public Flags() {
		this(OWNET_REQUEST);
	}

	public Flags(int value) {
		this.flags = value;
	}

	public int intValue() {
		return flags;
	}

	public void setDeviceDisplayFormat(Enums.OwDeviceDisplayFormat deviceDisplay) {
		flags &= ~Enums.OwDeviceDisplayFormat.getBitmask();
		flags |= deviceDisplay.intValue;
	}

	public Enums.OwDeviceDisplayFormat getDeviceDisplayFormat() {
		return Enums.OwDeviceDisplayFormat.getEnum(Enums.OwDeviceDisplayFormat.getBitmask() & flags);
	}

	public void setTemperatureScale(Enums.OwTemperatureScale tempScale) {
		flags &= ~Enums.OwTemperatureScale.getBitmask();
		flags |= tempScale.intValue;
	}

	public Enums.OwTemperatureScale getTemperatureScale() {
		return Enums.OwTemperatureScale.getEnum(Enums.OwTemperatureScale.getBitmask() & flags);
	}

	public void setPersistence(Enums.OwPersistence persistence) {
		flags &= ~Enums.OwPersistence.getBitmask();
		flags |= persistence.intValue;
	}

	public Enums.OwPersistence getPersistence() {
		return Enums.OwPersistence.getEnum(Enums.OwPersistence.getBitmask() & flags);
	}

	public void setAlias(Enums.OwAlias alias) {
		flags &= ~Enums.OwAlias.getBitmask();
		flags |= alias.intValue;
	}

	public Enums.OwAlias getAlias() {
		return Enums.OwAlias.getEnum(Enums.OwAlias.getBitmask() & flags);
	}

	public void setBusReturn(Enums.OwBusReturn busReturn) {
		flags &= ~Enums.OwBusReturn.getBitmask();
		flags |= busReturn.intValue;
	}

	public Enums.OwBusReturn getBusReturn() {
		return Enums.OwBusReturn.getEnum(Enums.OwBusReturn.getBitmask() & flags);
	}
}