package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityCallable;
import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.onewire.client.OwfsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class OwfsDirectoryListingCallable extends PriorityCallable<List<String>> {

    private final static Logger LOGGER = LoggerFactory.getLogger(OwfsDirectoryListingCallable.class);

    private String route;

    public OwfsDirectoryListingCallable(PriorityThreadPoolExecutor.PRIORITY priority, String route) {
        super(priority);
        this.route = route;
    }

    @Override
    public List<String> call() throws Exception {

        List<String> result = new ArrayList<>();

        int pos = route.lastIndexOf("/");
        if (pos != -1) {
            String path = route.substring(0, pos + 1) + "uncached" + route.substring(pos) + "/";

            for (String entry : OwfsClientFactory.getInstance().listDirectory(path)) {
                result.add(entry.substring(path.length()));
            }
        }

        return result;
    }
}