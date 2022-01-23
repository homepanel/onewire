package com.homepanel.onewire.config;

import com.homepanel.core.config.ConfigTopic;
import com.homepanel.onewire.type.ReverseSwitch;
import com.homepanel.onewire.type.Switch;
import com.homepanel.onewire.type.TimedSwitch;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "config")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Config extends ConfigTopic<Topic> {

    static {
        addTypes(new Switch(), new ReverseSwitch(), new TimedSwitch());
    }

    private Onewire onewire;
    private List<Topic> topics;

    public Onewire getOnewire() {
        return onewire;
    }

    private void setOnewire(Onewire onewire) {
        this.onewire = onewire;
    }

    @XmlElementWrapper(name = "topics")
    @XmlElement(name = "topic")
    public List<Topic> getTopics() {
        return topics;
    }

    @Override
    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }
}