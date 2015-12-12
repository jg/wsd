package com.wsd.wsd_projekt.agents;

import java.io.IOException;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

public class BatTracker extends Agent {

	protected void setup(){
		System.out.println("TWORZENIE AGENTA");
		addBehaviour(new CyclicBehaviour() {
			
			@Override
			public void action() {
				ACLMessage message = receive();
				if(message!=null){
					System.out.println("DOSTAŁEM");
					String name = message.getContent();
					System.out.println("od " + name);
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
				m.addReceiver(new AID("Bat1", AID.ISLOCALNAME));
				m.setContent(getAID().getName());;
				send(m);
			}
		});
	}
}
