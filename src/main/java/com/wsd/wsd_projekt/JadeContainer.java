package com.wsd.wsd_projekt;

import jade.core.AID;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.JADEAgentManagement.SniffOn;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;


public class JadeContainer {

	public static void main(String[] args) {
		try {
			Runtime runtime = Runtime.instance();
			ProfileImpl impl = new ProfileImpl(false);
			impl.setParameter(ProfileImpl.MAIN_HOST, "localhost");
			AgentContainer container = runtime.createAgentContainer(impl);
			for(int i=0; i<4; i++){
				AgentController controller = container.createNewAgent("Bat"+i, "com.wsd.wsd_projekt.agents.BatTracker", new Object[]{});
				controller.start();
			}
			
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
