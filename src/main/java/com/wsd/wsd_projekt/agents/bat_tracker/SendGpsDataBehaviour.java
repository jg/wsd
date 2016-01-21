package com.wsd.wsd_projekt.agents.bat_tracker;

import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;

import java.lang.Float;
import java.util.Enumeration;
import java.util.Random;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.wsd.wsd_projekt.GPSEntry;
import com.wsd.wsd_projekt.GPSPackage;

import java.util.ArrayList;

import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;

import java.io.IOException;

import com.wsd.wsd_projekt.agents.BatTracker;

public class SendGpsDataBehaviour extends TickerBehaviour {
    BatTracker agent;

    public SendGpsDataBehaviour(BatTracker agent_,
                                Long time) {
        super(agent_, time);
        agent = agent_;
    }
    
    protected int evaluateProposal(Proposal p){
    	return (int)(Math.sqrt((p.getX()-agent.getX())*(p.getX()-agent.getX())+(p.getY()-agent.getY())*(p.getY()-agent.getY()))*p.getLevel());
    }

    public void onTick() {
        agent.batteryTick();
        agent.gpsTick();

        // jesli zbiora sie dane - utworz paczke i dystrybuuj ja
        if ( agent.gpsTick() ){
  
            // tworzenie requesta odebrania paczki
        	ACLMessage msg = new ACLMessage(ACLMessage.CFP);
        	for(String neighbour : agent.getNeighbors()){
        		msg.addReceiver(new AID(neighbour, AID.ISGUID));
        	}
        	msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
        	msg.setReplyByDate(new Date(System.currentTimeMillis()+10000));
        	msg.setContent("Data transfer");
        	agent.addBehaviour(new ContractNetInitiator(agent, msg){
        		int index = agent.gpsPackageCount() - 1;
        		protected void handlePropose(ACLMessage propose, Vector v) {
    				System.out.println("Agent "+propose.getSender().getName()+" proposed ");
    			}
    			
    			protected void handleRefuse(ACLMessage refuse) {
    				System.out.println("Agent "+refuse.getSender().getName()+" refused");
    			}
    			
    			protected void handleFailure(ACLMessage failure) {
    				if (failure.getSender().equals(myAgent.getAMS())) {
    					// FAILURE notification from the JADE runtime: the receiver
    					// does not exist
    					System.out.println("Responder does not exist");
    				}
    				else {
    					System.out.println("Agent "+failure.getSender().getName()+" failed");
    				}
    				// Immediate failure --> we will not receive a response from this agent
    			}
    			
    			protected void handleAllResponses(Vector responses, Vector acceptances) {
    				// Evaluate proposals.
    				int bestProposal = -1;
    				AID bestProposer = null;
    				ACLMessage accept = null;
    				Enumeration e = responses.elements();
    				while (e.hasMoreElements()) {
    					ACLMessage msg = (ACLMessage) e.nextElement();
    					if (msg.getPerformative() == ACLMessage.PROPOSE) {
    						ACLMessage reply = msg.createReply();
    						reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
    						acceptances.addElement(reply);
    						int proposal = 0;
							try {
								proposal = evaluateProposal((Proposal)msg.getContentObject());
							} catch (UnreadableException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
    						if (proposal > bestProposal) {
    							bestProposal = proposal;
    							bestProposer = msg.getSender();
    							accept = reply;
    						}
    					}
    				}
    				// Accept the proposal of the best proposer
    				if (accept != null) {
    					System.out.println("Accepting proposal "+bestProposal+" from responder "+bestProposer.getName());
    					accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
    					try {
							accept.setContentObject(agent.getPackage(index));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
    				}						
    			}
    			
    			protected void handleInform(ACLMessage inform) {
    				System.out.println("Agent "+inform.getSender().getName()+" successfully performed the requested action");
    			}
        	});
        }
    }
}
