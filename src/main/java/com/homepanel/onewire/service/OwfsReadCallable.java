package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.onewire.client.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OwfsReadCallable extends OwfsCallable<String> {

    private final static int RETRIES = 5;

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsReadCallable.class);

    public OwfsReadCallable(PriorityThreadPoolExecutor.PRIORITY priority, String route, String property) {
        super(priority, route, property);
    }

    @Override
    public String call() throws Exception {

        int pos = getRoute().lastIndexOf("/");
        if (pos != -1) {
            String path = getRoute().substring(0, pos + 1) + "uncached" + getRoute().substring(pos) + "/" + getProperty();

            for (int retry = 1; retry <= RETRIES; retry++) {
                try {
                    return OwfsClientFactory.getInstance().read(path);
                } catch (OwfsException e) {
                    if (retry > 3) {
                        LOGGER.info(String.format("can not read path \"%s\" from ow server. retry %s of  %s", getRoute(), retry, RETRIES), e);
                    }
                    Thread.sleep(250);
                } catch (Exception e) {
                    LOGGER.error(String.format("can not read path \"%s\" from ow server", getRoute()), e);
                    throw e;
                }
            }

            throw new Exception(String.format("can not read path \"%s\" from ow server", getRoute()));
        } else {
            throw new Exception(String.format("can not read path \"%s\" from ow server", getRoute()));
        }
    }
}