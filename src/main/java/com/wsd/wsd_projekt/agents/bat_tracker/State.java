package com.wsd.wsd_projekt.agents.bat_tracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.wsd.wsd_projekt.GPSEntry;
import com.wsd.wsd_projekt.GPSPackage;

/** Represents bat tracke ragent state, can be sent around to another agent */
public class State implements java.io.Serializable {
    Battery battery;
    String agentName;
    Float agentX;
    Float agentY;
	ArrayList<GPSEntry> agentGPS;
	ArrayList<GPSPackage> agentPackages;	
	ArrayList<String> agentNeighbors;

    public State(String agentName_,Float agentX, Float agentY, Battery battery_, ArrayList<String> agentNeighbors,ArrayList<GPSEntry> agentGPS, ArrayList<GPSPackage> agentPackages) {
        agentName = agentName_;
        this.agentX = agentX;
        this.agentY = agentY;
        battery = battery_;
        this.agentNeighbors = agentNeighbors;
        this.agentGPS = agentGPS;
        this.agentPackages = agentPackages;
    }

    public String toString() {
    	
    	String newLine = System.getProperty("line.separator");
    	DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
    	
    	//wyswietl sasiadow
		String sAgentNeighbors = "";
		for (int i = 0; i < agentNeighbors.size(); i++)
		{
			if (i == 0)
			{
				sAgentNeighbors = clearAgentName(agentNeighbors.get(i));
			}
			else
			{
				sAgentNeighbors = sAgentNeighbors + ", " + clearAgentName(agentNeighbors.get(i));
			}
			
		}
		
		//wyswietl pozycje gps agenta
		String sAgentGPS = "";
		for (GPSEntry gpsEntry : agentGPS)
		{			
			sAgentGPS += "  [" + dateFormat.format(gpsEntry.getTime()) + ", " + gpsEntry.getLat() + ", " + gpsEntry.getLng() + "]" + newLine;
		}
		
		//wyswietl dane pozostalych agentow przechowywane przez agenta
		String sAgentPackages = "";
		for (GPSPackage gpsPackage : agentPackages)
		{		
			String sAgentGPS2 = "   {" + newLine;
						
			for (GPSEntry gpsEntry : gpsPackage.getEntries())
			{
				sAgentGPS2 += "     [" + dateFormat.format(gpsEntry.getTime()) + ", " + gpsEntry.getLat() + ", " + gpsEntry.getLng() + "]" + newLine;
			}
			sAgentPackages += "  [" + clearAgentName(gpsPackage.getOwner()) +" (" + gpsPackage.getEntries().size() + ") " + newLine + sAgentGPS2 + "   }" + newLine + "  ]" + newLine;
		}
		
		//timestamp
		Date date = new Date();
		
        return clearAgentName(agentName) + " TIMESTAMP: " + dateFormat.format(date) + ": " + newLine 
        		+ " x: " + this.agentX + " y: " + this.agentY + newLine
        		+ " battery: " + battery.getLevel() + newLine 
        		+ " neighbors (" + agentNeighbors.size() + "): " + sAgentNeighbors + newLine
        		+ " agent GPS [timestamp, latitude, longitude]:" + newLine
        		+ sAgentGPS
        		+ " agent GPS packeges [owner (<gps entry size>) {[timestamp, latitude, longitude]}]:" + newLine
        		+ sAgentPackages;
    }
    
    private static String clearAgentName(String name)
    {
		int index = name.indexOf('@');
		String clear = name.substring(0,index);	
		return clear;
    }
}
