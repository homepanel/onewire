package com.homepanel.onewire.service;

import com.homepanel.onewire.client.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwfsWriteRunnable implements Runnable {

    private final static int RETRIES = 5;

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsWriteRunnable.class);

    private String route;
    private String property;
    private String value;

    public OwfsWriteRunnable(String route, String property, String value) {
        this.route = route;
        this.property = property;
        this.value = value;
    }

    @Override
    public void run() {

        String path = this.route + "/" + this.property;

        try {
            for (int retry = 1; retry <= RETRIES; retry++) {
                try {
                    OwfsClientFactory.getInstance().write(path, this.value);

                    return;
                } catch (OwfsException e) {
                    LOGGER.warn("can not write path \"{}\" with value \"{}\" to ow server. retry {} of  {}", path, this.value, retry, RETRIES, e);
                    Thread.sleep(50);
                }
            }

            LOGGER.error("can not write path \"{}\" with value \"{}\" to ow server - all retries ({}) unsuccessful", path, this.value, RETRIES);

        } catch (InterruptedException e) {
            LOGGER.error("can not write path \"{}\" with value \"{}\" to ow server", path, this.value, e);
        }
    }
}