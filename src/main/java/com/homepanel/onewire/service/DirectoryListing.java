package com.homepanel.onewire.service;

import com.homepanel.onewire.config.Topic;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.List;

public class DirectoryListing {

    private Long lastJobRunningTimeInMilliseconds;
    public Boolean currentlyReading;
    private List<Topic> topics;

    public Long getLastJobRunningTimeInMilliseconds() {
        return lastJobRunningTimeInMilliseconds;
    }

    public void setLastJobRunningTimeInMilliseconds(Long lastJobRunningTimeInMilliseconds) {
        this.lastJobRunningTimeInMilliseconds = lastJobRunningTimeInMilliseconds;
    }

    public synchronized Boolean isCurrentlyReading() {
        if (this.currentlyReading != null && this.currentlyReading) {
            return true;
        } else {
            this.currentlyReading = true;
            return false;
        }
    }

    public void setCurrentlyReading(Boolean currentlyReading) {
        this.currentlyReading = currentlyReading;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public DirectoryListing() {
        this.topics = new ArrayList<>();
        this.currentlyReading = false;
    }
}