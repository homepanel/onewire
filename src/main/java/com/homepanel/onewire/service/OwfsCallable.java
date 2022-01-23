package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityCallable;
import com.homepanel.core.executor.PriorityThreadPoolExecutor;

public abstract class OwfsCallable<V> extends PriorityCallable<V> {

    private String route;
    private String property;

    protected String getRoute() {
        return route;
    }

    private void setRoute(String route) {
        this.route = route;
    }

    protected String getProperty() {
        return property;
    }

    private void setProperty(String property) {
        this.property = property;
    }

    public OwfsCallable(PriorityThreadPoolExecutor.PRIORITY priority, String route, String property) {
        super(priority);
        setRoute(route);
        setProperty(property);
    }
}