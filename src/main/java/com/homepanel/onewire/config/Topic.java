package com.homepanel.onewire.config;

import com.homepanel.core.config.InterfaceTopic;
import com.homepanel.core.config.InterfaceTopicPolling;
import com.homepanel.core.config.InterfaceTopicValue;
import com.homepanel.core.config.TypeAdapter;
import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.core.state.Type;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

public class Topic implements InterfaceTopic, InterfaceTopicValue, InterfaceTopicPolling {

    private String path;
    private Type type;
    private String route;
    private String property;
    private Object lastValue;
    private LocalDateTime lastDateTime;
    private PriorityThreadPoolExecutor.PRIORITY priority;
    private Boolean currentlyReading;
    private Integer refreshIntervalValue;
    private TimeUnit refreshIntervalUnit;

    @XmlValue
    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(TypeAdapter.class)
    @Override
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @XmlAttribute
    public String getRoute() {
        return route;
    }

    private void setRoute(String route) {
        this.route = route;
    }

    @XmlAttribute
    public String getProperty() {
        return property;
    }

    private void setProperty(String property) {
        this.property = property;
    }

    @XmlTransient
    @Override
    public Object getLastValue() {
        return lastValue;
    }

    public void setLastValue(Object lastValue) {
        this.lastValue = lastValue;
    }

    @XmlTransient
    @Override
    public LocalDateTime getLastDateTime() {
        return lastDateTime;
    }

    @Override
    public void setLastDateTime(LocalDateTime lastDateTime) {
        this.lastDateTime = lastDateTime;
    }

    @XmlAttribute
    public PriorityThreadPoolExecutor.PRIORITY getPriority() {
        return priority;
    }

    public void setPriority(PriorityThreadPoolExecutor.PRIORITY priority) {
        this.priority = priority;
    }

    @XmlTransient
    public Boolean getCurrentlyReading() {
        return currentlyReading;
    }

    public void setCurrentlyReading(Boolean currentlyReading) {
        this.currentlyReading = currentlyReading;
    }

    @XmlAttribute
    @Override
    public Integer getRefreshIntervalValue() {
        return refreshIntervalValue;
    }

    @Override
    public void setRefreshIntervalValue(Integer refreshIntervalValue) {
        this.refreshIntervalValue = refreshIntervalValue;
    }

    @XmlAttribute
    @Override
    public TimeUnit getRefreshIntervalUnit() {
        return refreshIntervalUnit;
    }

    @Override
    public void setRefreshIntervalUnit(TimeUnit refreshIntervalUnit) {
        this.refreshIntervalUnit = refreshIntervalUnit;
    }
}