package com.wsd.wsd_projekt.agents.bat_tracker;

public class Proposal {
	protected int batteryLevel;
	float x,y;
	public Proposal(int level, float x, float y) {
		super();
		this.batteryLevel = level;
		this.x = x;
		this.y = y;
	}
	public int getLevel() {
		return batteryLevel;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
}
