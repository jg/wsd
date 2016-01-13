package com.wsd.wsd_projekt.agents.bat_tracker;

public class Battery {
	int level;

	public Battery() {
		level = 100;
	}

	public boolean isBatteryLow() {
		return level < 25;
	}

	public void timeTick() {
		level -= 1;
	}
}
