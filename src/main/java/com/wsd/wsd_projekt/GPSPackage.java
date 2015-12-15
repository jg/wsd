package com.wsd.wsd_projekt;

import java.io.Serializable;
import java.util.ArrayList;

import com.wsd.wsd_projekt.GPSEntry;

public class GPSPackage implements Serializable {
	public String getOwner() {
		return owner;
	}
	public ArrayList<GPSEntry> getEntries() {
		return entries;
	}
	public GPSPackage(String owner, ArrayList<GPSEntry> entries) {
		super();
		this.owner = owner;
		this.entries = new ArrayList<GPSEntry>();
	}
	protected String owner;
	protected ArrayList<GPSEntry> entries;
}


