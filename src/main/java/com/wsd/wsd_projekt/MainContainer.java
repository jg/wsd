package com.wsd.wsd_projekt;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import com.wsd.wsd_projekt.agents.BatTracker;

public class MainContainer {
    final public static String loggerName = "logger";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Runtime rt = Runtime.instance();
		Properties p = new ExtendedProperties();
		p.setProperty("gui", "true");
		ProfileImpl impl = new ProfileImpl(p);
		AgentContainer container = rt.createMainContainer(impl);

		try {
            container.start();
            initAgents(container);
		} catch (ControllerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    public static AgentController startAgent(AgentContainer container,
                                             String agentName,
                                             Object[] args) throws StaleProxyException {
        AgentController agent1 =
            container.createNewAgent(agentName,
                                     "com.wsd.wsd_projekt.agents.BatTracker",
                                     args);
        agent1.start();
        return agent1;
    }

    public static AgentController startLogger(AgentContainer container)
        throws StaleProxyException {
        AgentController agent1 =
            container.createNewAgent(loggerName,
                                     "com.wsd.wsd_projekt.agents.Logger",
                                     new Object[0]);
        agent1.start();
        return agent1;
    }

    public static void initAgents(AgentContainer container) throws StaleProxyException {
        Object[] noArgs = new Object[0];
        startLogger(container);
        startAgent(container, "agent1", noArgs);
        startAgent(container, "agent2", noArgs);
        startAgent(container, "agent3", noArgs);
        startAgent(container, "agent4", noArgs);
    }
}
