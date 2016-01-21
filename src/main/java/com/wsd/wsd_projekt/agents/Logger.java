package com.wsd.wsd_projekt.agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.wsd.wsd_projekt.GPSEntry;
import com.wsd.wsd_projekt.GPSPackage;

import jade.core.Agent;
import com.wsd.wsd_projekt.agents.logger.ReceiveLoggingDataBehaviour;

public class Logger extends Agent {
	protected void setup(){
		System.out.println("Tworzenie agenta: " + getName());

        addBehaviour(new ReceiveLoggingDataBehaviour(this));
	}
}
