package com.wsd.wsd_projekt.agents.bat_tracker;

import jade.core.behaviours.TickerBehaviour;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import com.wsd.wsd_projekt.agents.BatTracker;
import com.wsd.wsd_projekt.MainContainer;

public class SendAgentStateBehaviour extends TickerBehaviour {
    BatTracker agent;

    public SendAgentStateBehaviour(BatTracker agent_,
                                   Long time) {
        super(agent_, time);
        agent = agent_;
    }

    public void onTick() {
        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
        m.addReceiver(new AID(MainContainer.loggerName, AID.ISLOCALNAME));
        try {
            m.setContentObject(agent.getCustomState());
        } catch (IOException e) {
            e.printStackTrace();
        }

        agent.send(m);
    }
}
