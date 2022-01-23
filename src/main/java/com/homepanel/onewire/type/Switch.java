package com.homepanel.onewire.type;

import com.homepanel.core.type.DefaultSwitch;
import com.homepanel.core.state.State;

public class Switch extends DefaultSwitch {

    public Switch() {
        super(
            new State("1", DefaultSwitch.SWITCH.ON.name()),
            new State("0", DefaultSwitch.SWITCH.OFF.name())
        );
    }
}