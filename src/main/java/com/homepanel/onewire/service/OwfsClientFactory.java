package com.homepanel.onewire.service;

import com.homepanel.onewire.client.OwfsException;
import com.homepanel.onewire.config.Config;

import java.util.ArrayList;
import java.util.List;

public class OwfsClientFactory {

    private static OwfsClientFactory INSTANCE;

    private String host;
    private Integer port;
    private final List<OwfsClient> owfsClients = new ArrayList<>();

    public String read(String path) throws OwfsException, InterruptedException {

        OwfsClient owfsClient = getOwfsClient();

        try {
            String result = owfsClient.read(path);
            owfsClient.setInUse(false);
            return result;
        } catch (Exception e) {
            owfsClient.setInUse(false);
            throw e;
        }
    }

    public List<String> listDirectory(String path) throws OwfsException, InterruptedException {

        OwfsClient owfsClient = getOwfsClient();

        try {
            List<String> result = owfsClient.listDirectory(path);
            owfsClient.setInUse(false);
            return result;
        } catch (Exception e) {
            owfsClient.setInUse(false);
            throw e;
        }
    }

    public void write(String path, String value) throws OwfsException, InterruptedException {

        OwfsClient owfsClient = getOwfsClient();

        try {
            owfsClient.write(path, value);
            owfsClient.setInUse(false);
        } catch (Exception e) {
            owfsClient.setInUse(false);
            throw e;
        }
    }

    private synchronized OwfsClient getOwfsClient() {
        for (OwfsClient owfsClient : this.owfsClients) {
            if (!owfsClient.getInUse()) {
                owfsClient.setInUse(true);
                return owfsClient;
            }
        }

        OwfsClient owfsClient = new OwfsClient(this.host, this.port);
        owfsClient.setInUse(true);
        this.owfsClients.add(owfsClient);

        return owfsClient;
    }

    public void close() {
        for (OwfsClient owfsClient : this.owfsClients) {
            owfsClient.close();
        }
    }

    public static OwfsClientFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new OwfsClientFactory();
        }
        return INSTANCE;
    }

    private void build(OwfsClientFactory.Builder builder) {
        Config config = builder.config;

        if (config.getOnewire() != null && config.getOnewire().getHost() != null && config.getOnewire().getPort() != null) {
            this.host = config.getOnewire().getHost();
            this.port = config.getOnewire().getPort();
        }
    }

    public static class Builder {

        private Config config;

        public Builder(Config config) {
            this.config = config;
        }

        public void build() {
            OwfsClientFactory.getInstance().build(this);
        }
    }
}
