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

public class SendGpsDataBehaviour extends TickerBehaviour {
    BatTracker agent;

    public SendGpsDataBehaviour(BatTracker agent_,
                                Long time) {
        super(agent_, time);
        agent = agent_;
    }

    public void onTick() {
        agent.batteryTick();
        agent.gpsTick();

        // jesli zbiora sie dane - utworz paczke i dystrybuuj ja
        if ( agent.gpsTick() ){
            // tworzenie requesta odebrania paczki
            ACLMessage m = new ACLMessage(ACLMessage.REQUEST);
            AID receiver = agent.pickNeighbour();
            m.addReceiver(receiver);
            try {
                // indeks paczki
                m.setContentObject(agent.gpsPackageCount() - 1);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            System.out.println("REQUEST OD "+ agent.getName() + " DO " + receiver.getName());
            agent.send(m);
        }
    }
}
