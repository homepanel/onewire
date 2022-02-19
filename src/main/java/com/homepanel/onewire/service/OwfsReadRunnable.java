package com.homepanel.onewire.service;

import com.homepanel.onewire.client.OwfsException;
import com.homepanel.onewire.config.Topic;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class OwfsReadRunnable implements Runnable {

    private final static int RETRIES = 5;
    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsReadRunnable.class);

    private Topic topic;

    public OwfsReadRunnable(Topic topic) {
        this.topic = topic;
    }

    @Override
    public void run() {

        try {
            int pos = this.topic.getRoute().lastIndexOf("/");
            if (pos != -1) {
                String path = this.topic.getRoute().substring(0, pos + 1) + "uncached" + this.topic.getRoute().substring(pos) + "/" + this.topic.getProperty();

                for (int retry = 1; retry <= RETRIES; retry++) {
                    try {
                        String value = OwfsClientFactory.getInstance().read(path);

                        if (!new EqualsBuilder().append(topic.getLastValue(), value).isEquals()) {
                            topic.setLastValue(value);
                            topic.setLastDateTime(LocalDateTime.now());
                            OwfsClientFactory.getInstance().getService().publishData(topic, value);
                        }

                        topic.setCurrentlyReading(false);

                        return;
                    } catch (OwfsException e) {
                        if (retry > 3) {
                            LOGGER.info("can not read route \"{}\" from ow server. retry {} of  {}", this.topic.getRoute(), retry, RETRIES, e);
                        }
                        Thread.sleep(250);
                    } catch (Exception e) {
                        LOGGER.error("can not read route \"{}\" from ow server", this.topic.getRoute(), e);
                    }
                }

                LOGGER.error("can not read route \"{}\" from ow server - all retries ({}) unsuccessful", this.topic.getRoute(), RETRIES);
            } else {
                LOGGER.error("route \"{}\" not correct", this.topic.getRoute());
            }
        } catch (Exception e) {
            LOGGER.error("can not read path \"{}\" from ow server", this.topic.getRoute(), e);
        }

        topic.setCurrentlyReading(false);
    }
}