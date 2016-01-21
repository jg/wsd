package com.wsd.wsd_projekt.agents.bat_tracker;

/** Represents battery levels. Level is an integer between 0 and 100 */
public class Battery implements java.io.Serializable {
	int level;
	public static final int MAX_LEVEL = 100;

	public Battery(int initialLevel) {
		if (initialLevel <= MAX_LEVEL) {
			level = initialLevel;
		} else {
			level = MAX_LEVEL;
		}
	}

	public boolean isBatteryLow() {
		return level < 25;
	}

	public void timeTick() {
		if (level > 0) {
			level -= 1;
		}
	}

    public int getLevel() {
        return level;
    }
}
