package com.wsd.wsd_projekt.agents.bat_tracker;

import jade.core.behaviours.TickerBehaviour;
import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import com.wsd.wsd_projekt.agents.BatTracker;

public class SendHelloBehaviour extends TickerBehaviour {
    BatTracker agent;

    public SendHelloBehaviour(BatTracker agent_,
                              Long time) {
        super(agent_, time);
        agent = agent_;
    }

    public void onTick() {
        //System.out.println("BIP "+getAID().getName());
        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        //obecnie wysylane do wszystkich
        for(int i=0;i<4;i++){
            String name = "agent"+i+"@192.168.122.1:1099/JADE";
            if (name != agent.getName()) {
                m.addReceiver(new AID(name, AID.ISGUID));
            }
        }
        m.setContent(agent.getName());;
        agent.send(m);
    }
}
