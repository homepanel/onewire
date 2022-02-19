package com.homepanel.onewire.service;

import com.homepanel.core.state.Type;
import com.homepanel.onewire.client.OwfsException;
import com.homepanel.onewire.config.Topic;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OwfsDirectoryListingRunnable implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsDirectoryListingRunnable.class);

    private String route;

    public OwfsDirectoryListingRunnable(String route) {
        this.route = route;
    }

    @Override
    public void run() {

        try {
            try {
                List<String> entries = new ArrayList<>();

                int pos = this.route.lastIndexOf("/");
                if (pos != -1) {
                    String path = this.route.substring(0, pos + 1) + "uncached" + this.route.substring(pos) + "/";

                    for (String entry : OwfsClientFactory.getInstance().listDirectory(path)) {
                        entries.add(entry.substring(path.length()));
                    }
                }

                for (Topic topic : OwfsClientFactory.getInstance().getService().getDirectoryListings().get(route).getTopics()) {
                    pos = topic.getRoute().lastIndexOf("/");

                    if (pos != -1) {
                        String id = topic.getRoute().substring(pos + 1);
                        Boolean result = entries.contains(id);

                        Object value;
                        if (topic.getType().getName().equals(Type.NAME.SWITCH.name())) {
                            value = result ? "1" : "0";
                        } else {
                            value = result.toString();
                        }

                        if (!new EqualsBuilder().append(topic.getLastValue(), value).isEquals()) {
                            topic.setLastValue(value);
                            topic.setLastDateTime(LocalDateTime.now());
                            OwfsClientFactory.getInstance().getService().publishData(topic, value);
                        }
                    }
                }
            } catch (OwfsException ex) {
            }

        } catch (InterruptedException e) {
            LOGGER.error("can not read listening with route \"{}\" from ow server", this.route, e);
        }

        OwfsClientFactory.getInstance().getService().getDirectoryListings().get(route).setCurrentlyReading(false);
    }
}