package com.homepanel.onewire.service;

import com.homepanel.core.executor.PriorityThreadPoolExecutor;
import com.homepanel.core.service.PollingService;
import com.homepanel.onewire.config.Config;
import com.homepanel.onewire.config.Topic;
import org.quartz.simpl.SimpleThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Service extends PollingService<Config, Topic> {

    private final static Logger LOGGER = LoggerFactory.getLogger(Service.class);

    private Map<PriorityThreadPoolExecutor.PRIORITY, ExecutorService> executorServices;
    private Map<String, DirectoryListing> directoryListings;

    @Override
    public Config getConfig() {
        return (Config) super.getConfig();
    }

    public Map<String, DirectoryListing> getDirectoryListings() {
        return directoryListings;
    }

    @Override
    protected void startService() throws Exception {

        this.directoryListings = new ConcurrentHashMap<>();
        this.executorServices = new HashMap<>();

        new OwfsClientFactory.Builder(this).build();

        // for init and writing
        this.executorServices.put(PriorityThreadPoolExecutor.PRIORITY.LOWEST, Executors.newFixedThreadPool(1));
        this.executorServices.put(PriorityThreadPoolExecutor.PRIORITY.LOW, Executors.newFixedThreadPool(1));
        this.executorServices.put(PriorityThreadPoolExecutor.PRIORITY.MEDIUM, Executors.newFixedThreadPool(2));
        this.executorServices.put(PriorityThreadPoolExecutor.PRIORITY.HIGH, Executors.newFixedThreadPool(3));
        this.executorServices.put(PriorityThreadPoolExecutor.PRIORITY.HIGHEST, Executors.newFixedThreadPool(5));

        if (getConfig().getTopics() != null) {
            for (Topic topic : getConfig().getTopics()) {
                if (topic.getPriority() == null) {
                    topic.setPriority(PriorityThreadPoolExecutor.PRIORITY.LOW);
                }

                if (topic.getProperty() == null) {
                    int pos = topic.getRoute().lastIndexOf("/");
                    if (pos != -1) {
                        String route = topic.getRoute().substring(0, pos);
                        if (!this.directoryListings.containsKey(route)) {
                            this.directoryListings.put(route, new DirectoryListing());
                        }
                        this.directoryListings.get(route).getTopics().add(topic);
                    }
                }
            }
        }
    }

    @Override
    protected void shutdownService() throws Exception {

        if (this.executorServices != null) {
            for (ExecutorService executorService : this.executorServices.values()) {
                try {
                    executorService.shutdown();
                } catch (Exception exception) {
                    try {
                        executorService.shutdownNow();
                    } catch (Exception e) {
                    }

                    LOGGER.error("can not shutdown executor service", exception);
                }
                executorService = null;
            }
        }

        OwfsClientFactory.getInstance().close();
    }

    @Override
    protected void onInit() {

        for (Topic topic : getConfig().getTopics()) {
            if (this.executorServices.containsKey(PriorityThreadPoolExecutor.PRIORITY.LOWEST)) {
                this.executorServices.get(PriorityThreadPoolExecutor.PRIORITY.LOWEST).submit(new OwfsReadRunnable(topic));
            }
        }
    }

    @Override
    public String getTopicNameByTopic(Topic topic) {
        return getTopicNameByParameter(topic.getRoute(), topic.getProperty());
    }

    @Override
    protected SimpleThreadPool getSimpleThreadPool() {
        return new SimpleThreadPool(3, Thread.NORM_PRIORITY);
    }

    @Override
    protected Integer getPollingExecutorServicePoolSize() {
        return getConfig().getOnewire().getPollingExecutorServicePoolSize();
    }

    @Override
    public void pollData(Topic topic, Long jobRunningTimeInMilliseconds, Long refreshIntervalInMilliseconds) {

        if (topic.getProperty() != null) {
            if (this.executorServices.containsKey(topic.getPriority())) {
                if (!topic.isCurrentlyReading()) {
                    this.executorServices.get(topic.getPriority()).submit(new OwfsReadRunnable(topic));
                }
            }
        } else {
           if (topic.isCurrentlyReading()) {
               if (this.executorServices.containsKey(topic.getPriority())) {

                   int pos = topic.getRoute().lastIndexOf("/");

                   if (pos != -1) {
                       String route = topic.getRoute().substring(0, pos);

                       if ((this.directoryListings.get(route).getLastJobRunningTimeInMilliseconds() == null || this.directoryListings.get(route).getLastJobRunningTimeInMilliseconds() != jobRunningTimeInMilliseconds) && !this.directoryListings.get(route).isCurrentlyReading()) {
                           this.directoryListings.get(route).setLastJobRunningTimeInMilliseconds(jobRunningTimeInMilliseconds);
                           this.executorServices.get(topic.getPriority()).submit(new OwfsDirectoryListingRunnable(route));
                       }
                   }
               }
           }
        }
    }

    @Override
    protected void onData(Topic topic, Object value, PriorityThreadPoolExecutor.PRIORITY priority) {

        if (this.executorServices.containsKey(priority)) {
            OwfsWriteRunnable owfsWriteCallable = new OwfsWriteRunnable(topic.getRoute(), topic.getProperty(), value.toString());
            this.executorServices.get(priority).submit(owfsWriteCallable);
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