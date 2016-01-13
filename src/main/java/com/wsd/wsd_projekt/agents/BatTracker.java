package com.wsd.wsd_projekt.agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import com.wsd.wsd_projekt.GPSEntry;
import com.wsd.wsd_projekt.GPSPackage;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import com.wsd.wsd_projekt.agents.bat_tracker.Battery;

public class BatTracker extends Agent {
	ArrayList<GPSEntry> gps;
	ArrayList<GPSPackage> packages;
	ArrayList<String> neighbors;
	float x,y;
	Random generator;
	Battery battery;
	
	public BatTracker(){
		generator = new Random();
		gps = new ArrayList<GPSEntry>();
		neighbors = new ArrayList<String>();
		packages = new ArrayList<GPSPackage>();
		battery = new Battery();
		x = generator.nextFloat()*100;
		y = generator.nextFloat()*100;
	}

	protected void setup(){
		System.out.println("TWORZENIE AGENTA");
		//symulacja dzialania nadajnika - rejestrowanie zmieniajacej sie pozycji
		addBehaviour(new TickerBehaviour(this,100) {
			
			@Override
			protected void onTick() {
				//zachowanie lokalizacji i timestampa
				x += generator.nextFloat()-0.5;
				y += generator.nextFloat()-0.5;
				Calendar cal = Calendar.getInstance();
				Date currentTime = cal.getTime();
				gps.add(new GPSEntry(x, y, currentTime));
				//jesli zbiora sie dane - utworz paczke i dystrybuuj ja
				if(gps.size()>10){
					GPSPackage pack = new GPSPackage(getAID().getName(), gps);
					packages.add(pack);
					gps.clear();
					//tworzenie requesta odebrania paczki
					ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
					String receiver = neighbors.get(generator.nextInt(4));
					//System.out.println("WYBRAŁEM "+receiver);
					m.addReceiver(new AID(receiver,AID.ISGUID));
					try {
						//indeks paczki
						m.setContentObject(packages.size()-1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("REQUEST OD "+getAID().getName() + " DO " + receiver);
					send(m);
				}
			}
		});
		//zachowanie odpowiedzialne za obsluge wiadomosci 'Hello' i odbieranie danych
		addBehaviour(new CyclicBehaviour() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			@Override
			public void action() {
				ACLMessage message = receive(mt);
				if(message!=null){
					//jesli odebrano dane
					if(message.getLanguage()=="data"){
						try {
							//zapisz odebrana paczke do swojego zbioru
							GPSPackage pack = (GPSPackage)message.getContentObject();
							packages.add(pack);
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					//jesli wiadmosc Hello
					else{
						System.out.println(getAID().getName() + " OTRZYMAŁ HELLO OD " + message.getSender().getName() );
						String name = message.getContent();
						//System.out.println("od " + name);
						//jezeli nie jest jeszcze w znanych sasiadach odpowiedz hello
						if(!neighbors.contains(name)){
							neighbors.add(name);
							ACLMessage m = message.createReply();
							m.setPerformative(ACLMessage.INFORM);
							m.setContent(getAID().getName());;
							send(m);
						}
					}
				}
				else{
					block();
				}
			}
		});
		//cykliczne wysylanie wiadmosci 'Hello'
		addBehaviour(new TickerBehaviour(this,1000) {
			
			@Override
			protected void onTick() {
				//System.out.println("BIP "+getAID().getName());
				ACLMessage m = new ACLMessage(ACLMessage.INFORM);
				//obecnie wysylane do wszystkich
				for(int i=0;i<4;i++){
					String name = "Bat"+i+"@192.168.0.12:1099/JADE";
					if(name!=getAID().getName())
						m.addReceiver(new AID(name, AID.ISGUID));
				}
				m.setContent(getAID().getName());;
				send(m);
			}
		});
		
		//negocjacja przeslania danych
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchPerformative(ACLMessage.AGREE));
				ACLMessage message = receive(mt);
				if(message!=null){
					//jesli otrzymano requesta
					if(message.getPerformative()==ACLMessage.REQUEST){
						System.out.println(getAID().getName() + " OTRZYMAŁ REQUEST OD "+message.getSender().getName());
						ACLMessage reply = message.createReply();
						//na razie zawsze sie zgadza
						reply.setPerformative(ACLMessage.AGREE);
						try {
							reply.setContentObject(message.getContentObject());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (UnreadableException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						send(reply);
					}
					//jesli otrzymano akceptacje
					else if(message.getPerformative()==ACLMessage.AGREE){
						ACLMessage reply = message.createReply();
						try {
							Integer index = (Integer)message.getContentObject();
							reply.setPerformative(ACLMessage.INFORM);
							try {
								reply.setContentObject(packages.get(index));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//ustaw jezyk na data aby odroznic od Hello
							reply.setLanguage("data");
							System.out.println(getAID().getName() + " PRZESYLA DANE DO " + message.getSender().getName());
							send(reply);
						} catch (UnreadableException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
						
				}
				else{
					block();
				}
			}
		});
	}
}
