package com.homepanel.onewire.service;

import java.util.ArrayList;
import java.util.List;

public class DirectoryListing {

    private List<String> entries;
    private Long lastJobRunningTimeInMilliseconds;
    private Boolean executing;

    public List<String> getEntries() {
        return entries;
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public Long getLastJobRunningTimeInMilliseconds() {
        return lastJobRunningTimeInMilliseconds;
    }

    public void setLastJobRunningTimeInMilliseconds(Long lastJobRunningTimeInMilliseconds) {
        this.lastJobRunningTimeInMilliseconds = lastJobRunningTimeInMilliseconds;
    }

    public Boolean isExecuting() {
        return executing;
    }

    public void setExecuting(Boolean executing) {
        this.executing = executing;
    }

    public DirectoryListing() {
        this.entries = new ArrayList<>();
        this.executing = false;
    }
}