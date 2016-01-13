package com.wsd.wsd_projekt;

import java.util.Date;

public class GPSEntry {
	private float lat,lng;
	private Date time;
	
	public float getLat() {
		return lat;
	}
	public void setLat(float lat) {
		this.lat = lat;
	}
	public float getLng() {
		return lng;
	}
	public void setLng(float lng) {
		this.lng = lng;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public GPSEntry(float lat, float lng, Date time) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.time = time;
	}
	
}
