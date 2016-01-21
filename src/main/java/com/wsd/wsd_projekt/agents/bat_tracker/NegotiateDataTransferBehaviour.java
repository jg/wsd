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

public class NegotiateDataTransferBehaviour extends CyclicBehaviour {
    BatTracker agent;
    final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    public NegotiateDataTransferBehaviour(BatTracker agent_) {
        super(agent_);
        agent = agent_;
    }

    public void action() {
        MessageTemplate mt =
            MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                               MessageTemplate.MatchPerformative(ACLMessage.AGREE));
        ACLMessage message = agent.receive(mt);
        if (message != null){
            //jesli otrzymano requesta
            if (message.getPerformative() == ACLMessage.REQUEST){
                System.out.println(agent.getName() + " OTRZYMAŁ REQUEST OD "+message.getSender().getName());
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
                agent.send(reply);
            }
            //jesli otrzymano akceptacje
            else if(message.getPerformative() == ACLMessage.AGREE){
                ACLMessage reply = message.createReply();
                try {
                    Integer index = (Integer)message.getContentObject();
                    reply.setPerformative(ACLMessage.INFORM);
                    try {
                        reply.setContentObject(agent.getPackage(index));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //ustaw jezyk na data aby odroznic od Hello
                    reply.setLanguage("data");
                    System.out.println(agent.getName() + " PRZESYLA DANE DO " + message.getSender().getName());
                    agent.send(reply);
                } catch (UnreadableException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        } else {
            block();
        }
    }
}
