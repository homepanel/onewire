package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.onewire.client.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwfsWriteCallable extends OwfsCallable<Void> {

    private final static int RETRIES = 5;

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsWriteCallable.class);

    private String value;

    private String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;
    }

    public OwfsWriteCallable(PriorityThreadPoolExecutor.PRIORITY priority, String route, String property, String value) {
        super(priority, route, property);
        setValue(value);
    }

    @Override
    public Void call() throws Exception {

        String path = getRoute() + "/" + getProperty();

        for (int retry = 1; retry <= RETRIES; retry++) {
            try {
                OwfsClientFactory.getInstance().write(path, getValue());
            } catch (OwfsException e) {
                LOGGER.warn(String.format("can not write path \"%s\" with value \"%s\" to ow server. retry %s of  %s", path, getValue(), retry, RETRIES), e);
                Thread.sleep(50);
            }
        }

        throw new Exception(String.format("can not write path \"%s\" with value \"%s\" to ow server", path, getValue()));
    }
}