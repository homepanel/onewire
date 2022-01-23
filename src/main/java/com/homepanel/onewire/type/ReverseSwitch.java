package com.homepanel.onewire.type;

import com.homepanel.core.type.DefaultReverseSwitch;
import com.homepanel.core.type.DefaultSwitch;
import com.homepanel.core.state.State;

public class ReverseSwitch extends DefaultReverseSwitch<String> {

    public ReverseSwitch() {
        super(
            new State("0", DefaultSwitch.SWITCH.ON.name()),
            new State("1", DefaultSwitch.SWITCH.OFF.name())
        );
    }
}