package com.homepanel.onewire.service;

import com.homepanel.onewire.client.Enums;
import com.homepanel.onewire.client.OwfsConnectionClient;
import com.homepanel.onewire.client.OwfsConnectionConfig;
import com.homepanel.onewire.client.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class OwfsClient {

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsClient.class);

    private OwfsConnectionConfig owfsConnectionConfig;
    private OwfsConnectionClient owfsConnectionClient;
    private Integer reconnectCount;
    private Boolean inUse;

    private OwfsConnectionConfig getOwfsConnectionConfig() {
        return owfsConnectionConfig;
    }

    private void setOwfsConnectionConfig(OwfsConnectionConfig owfsConnectionConfig) {
        this.owfsConnectionConfig = owfsConnectionConfig;
    }

    private OwfsConnectionClient getOwfsConnectionClient() {
        return owfsConnectionClient;
    }

    private void setOwfsConnectionClient(OwfsConnectionClient owfsConnectionClient) {
        this.owfsConnectionClient = owfsConnectionClient;
    }

    private Integer getReconnectCount() {
        return reconnectCount;
    }

    private void setReconnectCount(Integer reconnectCount) {
        this.reconnectCount = reconnectCount;
    }

    public Boolean getInUse() {
        return inUse;
    }

    public void setInUse(Boolean inUse) {
        this.inUse = inUse;
    }

    public OwfsClient(String host, Integer port) {

        setReconnectCount(0);

        setOwfsConnectionConfig(new OwfsConnectionConfig(host, port));
        getOwfsConnectionConfig().setDeviceDisplayFormat(Enums.OwDeviceDisplayFormat.F_DOT_I);
        getOwfsConnectionConfig().setTemperatureScale(Enums.OwTemperatureScale.CELSIUS);
        getOwfsConnectionConfig().setPersistence(Enums.OwPersistence.ON);
        getOwfsConnectionConfig().setBusReturn(Enums.OwBusReturn.ON);

        connect();
    }

    private void connect() {
        if (getOwfsConnectionClient() != null) {
            try {
                getOwfsConnectionClient().disconnect();
            } catch (Exception e) {}
        }

        setOwfsConnectionClient(null);

        try {
            setOwfsConnectionClient(new OwfsConnectionClient(getOwfsConnectionConfig()));
        } catch (Exception e) {
            reconnect();
        }
    }

    private void reconnect() {
        try {
            Thread.sleep(Math.min(getReconnectCount() * 1000, 60000));
        } catch (InterruptedException e) {
        }

        connect();
    }

    public String read(String path) throws OwfsException, InterruptedException {

        try {
            return getOwfsConnectionClient().read(path).trim();
        } catch (IOException e) {
            LOGGER.error(String.format("io exception when reading path \"%s\"", path), e);
            reconnect();
            return read(path);
        } catch (OwfsException e) {
            throw e;
        }
    }

    public List<String> listDirectory(String path) throws OwfsException, InterruptedException {

        try {
            return getOwfsConnectionClient().listDirectory(path);
        } catch (IOException e) {
            LOGGER.error(String.format("io exception when listing directory path \"%s\"", path), e);
            reconnect();
            return listDirectory(path);
        } catch (OwfsException e) {
            throw e;
        }
    }

    public void write(String path, String value) throws OwfsException, InterruptedException {

        try {
            getOwfsConnectionClient().write(path, value);
        } catch (IOException e) {
            LOGGER.error(String.format("io exception when writing path \"%s\" and value \"%s\"", path, value), e);
            reconnect();
            write(path, value);
        } catch (OwfsException e) {
            throw e;
        }
    }

    public void close() {

        try {
            getOwfsConnectionClient().disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
