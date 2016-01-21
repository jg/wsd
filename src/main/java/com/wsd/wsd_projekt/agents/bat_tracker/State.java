package com.wsd.wsd_projekt.agents.bat_tracker;

/** Represents bat tracke ragent state, can be sent around to another agent */
public class State implements java.io.Serializable {
    Battery battery;
    String agentName;

    public State(String agentName_, Battery battery_) {
        agentName = agentName_;
        battery = battery_;
    }

    public String toString() {
        return agentName + ": " + "battery: " + battery.getLevel();
    }
}
