package com.wsd.wsd_projekt.agents.logger;

import jade.core.behaviours.CyclicBehaviour;
import com.wsd.wsd_projekt.agents.BatTracker;
import com.wsd.wsd_projekt.agents.Logger;
import com.wsd.wsd_projekt.agents.bat_tracker.State;
import jade.lang.acl.*;
import java.nio.file.*;
import java.io.IOException;

public class ReceiveLoggingDataBehaviour extends CyclicBehaviour {
    Logger agent;
    final MessageTemplate mt =
        MessageTemplate.MatchPerformative(ACLMessage.INFORM);

    public ReceiveLoggingDataBehaviour(Logger agent_) {
        super(agent_);
        agent = agent_;
    }

    public void action() {
        ACLMessage message = agent.receive(mt);

        if (message != null) {
            try {
                State st = (State)message.getContentObject();
                System.out.println(st.toString());
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
