package com.wsd.wsd_projekt.agents.logger;

import jade.core.behaviours.CyclicBehaviour;
import com.wsd.wsd_projekt.agents.BatTracker;
import com.wsd.wsd_projekt.agents.Logger;
import com.wsd.wsd_projekt.agents.bat_tracker.State;
import jade.lang.acl.*;
import java.nio.file.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReceiveLoggingDataBehaviour extends CyclicBehaviour {
    Logger agent;
    final MessageTemplate mt =
        MessageTemplate.MatchPerformative(ACLMessage.INFORM);
    BufferedWriter bufferWritter;
    
    public ReceiveLoggingDataBehaviour(Logger agent_) {
        super(agent_);
        agent = agent_;
    }

    public void action() {
        ACLMessage message = agent.receive(mt);

        if (message != null) {
            try {
                State st = (State)message.getContentObject();
                //System.out.println(st.toString());
            	try
            	{            		
            		File file =new File("battracker_log.txt");
            		
            		if(!file.exists()){
            			file.createNewFile();
            		}

            		FileWriter fileWritter = new FileWriter(file.getName(),true);
            	        bufferWritter = new BufferedWriter(fileWritter);
            	        bufferWritter.write(st.toString());
            	        bufferWritter.write(System.getProperty("line.separator"));

            	}catch(IOException e){
            		e.printStackTrace();
            	}
            	finally
            	{
        	        try {
        				bufferWritter.close();
        			} catch (IOException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			} 
        	        
            	}
            	    
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
        } else {
            block();
        }
    }
}
