package com.wsd.wsd_projekt.agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.wsd.wsd_projekt.GPSEntry;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class BatTracker extends Agent {
	
	ArrayList<GPSEntry> gps;
	ArrayList<String> neighbors;
	float x,y;
	Random generator;
	
	public BatTracker(){
		generator = new Random();
		gps = new ArrayList<GPSEntry>();
		neighbors = new ArrayList<String>();
		x = generator.nextFloat()*100;
		y = generator.nextFloat()*100;
	}

	protected void setup(){
		System.out.println("TWORZENIE AGENTA");
		addBehaviour(new TickerBehaviour(this,100) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				x += generator.nextFloat()-0.5;
				y += generator.nextFloat()-0.5;
				Calendar cal = Calendar.getInstance();
				Date currentTime = cal.getTime();
				gps.add(new GPSEntry(x, y, currentTime));
			}
		});
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage message = receive();
				if(message!=null){
					System.out.println("DOSTAŁEM");
					String name = message.getContent();
					System.out.println("od " + name);
					if(!neighbors.contains(name)){
						neighbors.add(name);
						ACLMessage m = new ACLMessage(ACLMessage.INFORM);
						m.addReceiver(new AID(name, AID.ISGUID));
						m.setContent(getAID().getName());;
						send(m);
					}
				}
				else{
					block();
				}
			}
		});
		addBehaviour(new TickerBehaviour(this,1000) {
			
			@Override
			protected void onTick() {
				// TODO Auto-generated method stub
				System.out.println("BIP "+getAID().getName());
				ACLMessage m = new ACLMessage(ACLMessage.INFORM);
				for(int i=0;i<4;i++){
					m.addReceiver(new AID("Bat"+i+"@192.168.0.12:1099/JADE", AID.ISGUID));
				}
				m.setContent(getAID().getName());;
				send(m);
			}
		});
	}
}
