package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.core.service.PollingService;
import com.homepanel.core.state.Type;
import com.homepanel.onewire.config.Config;
import com.homepanel.onewire.config.Topic;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Service extends PollingService<Config, Topic> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private ExecutorService executorService;
    private Map<String, DirectoryListing> directoryListings;

    private ExecutorService getExecutorService() {
        return executorService;
    }

    private void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    @Override
    protected void startService() throws Exception {

        this.directoryListings = new HashMap<>();
        new OwfsClientFactory.Builder(getConfig()).build();

        if (getConfig().getTopics() != null) {
            for (Topic topic : getConfig().getTopics()) {
                if (topic.getPriority() == null) {
                    topic.setPriority(PriorityThreadPoolExecutor.PRIORITY.MEDIUM);
                }
            }
        }

        setExecutorService(new PriorityThreadPoolExecutor(10, 10, 0L, TimeUnit.MILLISECONDS));
    }

    @Override
    protected void shutdownService() throws Exception {

        if (getExecutorService() != null) {
            try {
                getExecutorService().shutdown();
            } catch (Exception exception) {
                try {
                    getExecutorService().shutdownNow();
                } catch (Exception e) {}

                LOGGER.error("can not shutdown executor service", exception);
            }
            setExecutorService(null);
        }

        OwfsClientFactory.getInstance().close();
    }

    @Override
    protected void onInit() {

        try {
            for (Topic topic : getConfig().getTopics()) {

                OwfsReadCallable owfsReadCallable = new OwfsReadCallable(PriorityThreadPoolExecutor.PRIORITY.LOWEST, topic.getRoute(), topic.getProperty());
                Future<String> future = getExecutorService().submit(owfsReadCallable);

                try {
                    String value = future.get();
                    publishData(topic, value);
                } catch (Exception e) {
                }

                owfsReadCallable = null;
            }
        } catch (Exception e) {
            LOGGER.error("error reading directory listing from ow server", e);
        }
    }

    @Override
    public String getTopicNameByTopic(Topic topic) {
        return getTopicNameByParameter(topic.getRoute(), topic.getProperty());
    }

    @Override
    protected Integer getPollingExecutorServicePoolSize() {
        return getConfig().getOnewire().getPollingExecutorServicePoolSize();
    }

    @Override
    public void pollData(Topic topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds) {

        if (topic.getProperty() != null) {
            if (topic.getCurrentlyReading() == null || !topic.getCurrentlyReading()) {
                topic.setCurrentlyReading(true);
                try {
                    OwfsReadCallable owfsReadCallable = new OwfsReadCallable(topic.getPriority(), topic.getRoute(), topic.getProperty());
                    Future<String> future = getExecutorService().submit(owfsReadCallable);
                    try {
                        String value = future.get();

                        topic.setCurrentlyReading(false);

                        if (!new EqualsBuilder().append(topic.getLastValue(), value).isEquals()) {
                            topic.setLastValue(value);
                            topic.setLastDateTime(LocalDateTime.now());
                            publishData(topic, value);
                        }
                    } catch (Exception e) {
                        LOGGER.error("can not read from ow network", e);
                    }

                    owfsReadCallable = null;


                } catch (Exception e) {
                    LOGGER.error("error reading data from ow server", e);
                }
            }
        } else {
           if (topic.getCurrentlyReading() == null || !topic.getCurrentlyReading()) {

               int pos = topic.getRoute().lastIndexOf("/");

               if (pos != -1) {
                   String route = topic.getRoute().substring(0, pos);
                   String id = topic.getRoute().substring(pos + 1);

                   if (!this.directoryListings.containsKey(route)) {
                       this.directoryListings.put(route, new DirectoryListing());
                   }

                   if ((this.directoryListings.get(route).getLastJobRunningTimeInMilliseconds() == null || this.directoryListings.get(route).getLastJobRunningTimeInMilliseconds() != jobRunningTimeInMilliseconds) && !this.directoryListings.get(route).isExecuting()) {
                       this.directoryListings.get(route).setExecuting(true);
                       this.directoryListings.get(route).setLastJobRunningTimeInMilliseconds(jobRunningTimeInMilliseconds);

                       OwfsDirectoryListingCallable owfsDirectoryListingCallable = new OwfsDirectoryListingCallable(topic.getPriority(), route);
                       Future<List<String>> future = getExecutorService().submit(owfsDirectoryListingCallable);

                       try {
                           this.directoryListings.get(route).setEntries(future.get());
                       } catch (Exception e) {
                       }

                       this.directoryListings.get(route).setExecuting(false);
                   }

                   Boolean result = this.directoryListings.get(route).getEntries().contains(id);

                   Object value = null;
                   if (topic.getType().getName().equals(Type.NAME.SWITCH.name())) {
                       value = result ? "1" : "0";
                   } else {
                       value = result.toString();
                   }

                   if (!new EqualsBuilder().append(topic.getLastValue(), value).isEquals()) {
                       topic.setLastValue(value);
                       topic.setLastDateTime(LocalDateTime.now());
                       publishData(topic, value);
                   }
               }
           }
        }
    }

    @Override
    protected void onData(Topic topic, Object value, PriorityThreadPoolExecutor.PRIORITY priority) {

        try {
            OwfsWriteCallable owfsWriteCallable = new OwfsWriteCallable(priority, topic.getRoute(), topic.getProperty(), value.toString());
            Future<Void> future = getExecutorService().submit(owfsWriteCallable);
            try {
                future.get();
            } catch (Exception e) {
            }

            owfsWriteCallable = null;

        } catch (Exception e) {
            LOGGER.error("error writing data to ow server", e);
        }
    }

    @Override
    protected void updateData(Topic topic) {
        // unused
    }

    public static void main(String[] arguments) throws Exception {
        new Service().start(arguments, Config.class);
    }
}