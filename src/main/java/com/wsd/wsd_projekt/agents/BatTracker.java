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
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import com.wsd.wsd_projekt.agents.bat_tracker.Battery;
import com.wsd.wsd_projekt.agents.bat_tracker.Proposal;
import com.wsd.wsd_projekt.agents.bat_tracker.SendGpsDataBehaviour;
import com.wsd.wsd_projekt.agents.bat_tracker.HandleIncomingMessagesBehaviour;
import com.wsd.wsd_projekt.agents.bat_tracker.SendHelloBehaviour;
import com.wsd.wsd_projekt.agents.bat_tracker.SendAgentStateBehaviour;
import com.wsd.wsd_projekt.agents.bat_tracker.State;

public class BatTracker extends Agent {
	ArrayList<GPSEntry> gps;
	ArrayList<GPSPackage> packages;
	ArrayList<String> neighbors;
	Float x,y;
	Random generator;
	Battery battery;
	
	public BatTracker(){
		generator = new Random();
		gps = new ArrayList<GPSEntry>();
		neighbors = new ArrayList<String>();
		packages = new ArrayList<GPSPackage>();
		battery = new Battery(generator.nextInt(Battery.MAX_LEVEL));
		x = generator.nextFloat()*100;
		y = generator.nextFloat()*100;
	}

    public State getCustomState() {
    	return new State(getName(), this.x, this.y, this.battery, this.neighbors, this.gps, this.packages);
    }

    /** Decrease battery level */
    public void batteryTick() {
        battery.timeTick();
    }

    public AID pickNeighbour() {
        String receiver = neighbors.get(generator.nextInt(4));
        return new AID(receiver, AID.ISGUID);
    }

    /** True if enough gps entries are present in gps package */
    public boolean isGpsPackReady() {
        return gps.size() > 10;
    }

    public int gpsPackageCount() {
        return packages.size();
    }
    
    public Float getX() {
		return x;
	}

	public Float getY() {
		return y;
	}
	
	public ArrayList<String> getNeighbors(){
    	return neighbors;
    }

    /** Generates and adds new gps entry/package
     *  @return {Boolean} true if new package was created
     */
    public boolean gpsTick() {
        x += generator.nextFloat()- new Float(0.5);
        y += generator.nextFloat()- new Float(0.5);
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        gps.add(new GPSEntry(x, y, currentTime));

        if ( isGpsPackReady() ) {
            createGpsPackage();
            return true;
        } else {
            return false;
        }
    }

    /** Collects entries into a new gps package, clear entry array list */
    private void createGpsPackage() {
        GPSPackage pack = new GPSPackage(getName(), gps);
        packages.add(pack);
        gps.clear();
    }

    public void addPackage(GPSPackage pkg) {
        packages.add(pkg);
    }

    public GPSPackage getPackage(int index) {
        return packages.get(index);
    }

    public boolean isKnownNeighbour(String name) {
        return neighbors.contains(name);
    }

    public void addNeighbor(String name) {
        neighbors.add(name);
    }

	protected void setup(){
		System.out.println("Tworzenie agenta: " + getName());

        /** Send agent state to Logger agent so it can display it to the user */
        addBehaviour(new SendAgentStateBehaviour(this, new Long(1000)));

		//symulacja dzialania nadajnika - rejestrowanie zmieniajacej sie pozycji
        addBehaviour(new SendGpsDataBehaviour(this, new Long(1000)));

        //zachowanie odpowiedzialne za obsluge wiadomosci 'Hello' i odbieranie danych
        addBehaviour(new HandleIncomingMessagesBehaviour(this));

		//cykliczne wysylanie wiadmosci 'Hello'
		addBehaviour(new SendHelloBehaviour(this, new Long(1000)));
        
        MessageTemplate template = MessageTemplate.and(
        		MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        		MessageTemplate.MatchPerformative(ACLMessage.CFP) );
        		addBehaviour(new ContractNetResponder(this, template){
        			protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        				System.out.println("Agent "+getLocalName()+": CFP received from "+cfp.getSender().getName()+". Action is "+cfp.getContent());
        				if (!battery.isBatteryLow() ) {
        					// We provide a proposal
        					Proposal proposal = new Proposal(battery.getLevel(),x,y);
        					System.out.println("Agent "+getLocalName()+": Proposing "+proposal);
        					ACLMessage propose = cfp.createReply();
        					propose.setPerformative(ACLMessage.PROPOSE);
        					try {
        						propose.setContentObject(propose);
        					} catch (IOException e) {
        						// TODO Auto-generated catch block
        						e.printStackTrace();
        					}
        					return propose;
        				}
        				else{
        					System.out.println("Agent "+getLocalName()+": Refuse");
        					ACLMessage refuse = cfp.createReply();
        					refuse.setPerformative(ACLMessage.REFUSE);
        					return refuse;
        					
        				}
        			
        			}
        			
        			protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
        				System.out.println("Agent "+getLocalName()+": Proposal accepted");
        				GPSPackage pack;
        				ACLMessage inform = accept.createReply();
        				try {
        					pack = (GPSPackage)accept.getContentObject();
        					addPackage(pack);
        					
        					inform.setPerformative(ACLMessage.INFORM);
        					
        				} catch (UnreadableException e) {
        					// TODO Auto-generated catch block
        					e.printStackTrace();
        				}
        				return inform;
        				
        			}
        			
        			protected void handleRejectProposal(ACLMessage reject) {
        				System.out.println("Agent "+getLocalName()+": Proposal rejected");
        			}
        		});
	}
}
