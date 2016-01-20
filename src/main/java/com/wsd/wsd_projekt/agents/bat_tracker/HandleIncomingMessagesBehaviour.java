package com.wsd.wsd_projekt.agents.bat_tracker;

import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;
import java.lang.Float;
import java.util.Random;
import java.util.Calendar;
import java.util.Date;
import com.wsd.wsd_projekt.GPSEntry;
import com.wsd.wsd_projekt.GPSPackage;
import java.util.ArrayList;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import com.wsd.wsd_projekt.agents.BatTracker;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

public class HandleIncomingMessagesBehaviour extends CyclicBehaviour {
    BatTracker agent;
    final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    public HandleIncomingMessagesBehaviour(BatTracker agent_) {
        super(agent_);
        agent = agent_;
    }

    public void action() {
        ACLMessage message = agent.receive(mt);
        if( message != null ){
            //jesli odebrano dane
            if (message.getLanguage() == "data") {
                try {
                    //zapisz odebrana paczke do swojego zbioru
                    GPSPackage pack = (GPSPackage)message.getContentObject();
                    agent.addPackage(pack);
                } catch (UnreadableException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                        
            }
            //jesli wiadmosc Hello
            else{
                System.out.println(agent.getName() + " OTRZYMAŁ HELLO OD " + message.getSender().getName() );
                String name = message.getContent();
                //System.out.println("od " + name);
                //jezeli nie jest jeszcze w znanych sasiadach odpowiedz hello
                if (!agent.isKnownNeighbour(name)) {
                    agent.addNeighbor(name);
                    ACLMessage m = message.createReply();
                    m.setPerformative(ACLMessage.INFORM);
                    m.setContent(agent.getName());;
                    agent.send(m);
                }
            }
        }
        else{
            block();
        }
    }
}