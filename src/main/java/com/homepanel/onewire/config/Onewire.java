package com.homepanel.onewire.config;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "onewire")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Onewire {

    private String host;
    private Integer port;
    private Integer pollingExecutorServicePoolSize;

    public String getHost() {
        return host;
    }

    private void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    private void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPollingExecutorServicePoolSize() {
        return pollingExecutorServicePoolSize;
    }

    private void setPollingExecutorServicePoolSize(Integer pollingExecutorServicePoolSize) {
        this.pollingExecutorServicePoolSize = pollingExecutorServicePoolSize;
    }
}