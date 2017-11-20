/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A.

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation,
version 2.1 of the License.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class Space extends Agent {
	private static final long serialVersionUID = -1133856395135449695L;
	Random generator = new Random(System.currentTimeMillis());
	private int random;
		
	protected void setup() {
		
		AID id = new AID("Milky Way", AID.ISLOCALNAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setName(getName());
		sd.setType("Milky Way");
		dfd.addServices(sd); 
		try {
			DFService.register(this, dfd);
			SpaceEvents se = new SpaceEvents(this, 5000);
			addBehaviour(se);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}	
	
	}
	
	class SpaceEvents extends TickerBehaviour{
		
		private static final long serialVersionUID = 8137282728765530744L;

		public SpaceEvents(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			random = generator.nextInt(7); 
			//System.out.println("Destiny has choosen: " + random);
			DFAgentDescription spaceship = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			System.out.println("\n");
			System.out.println("*** Something happend on Milky Way!!! ***");
			
			switch(random) {
				case 0:
					System.out.println("No, it didn't, it's everything ok in this galaxy...");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("regular conditions");
					msg.setConversationId("It's everything ok in this galaxy...");
					break;
				case 1:
					System.out.println("Ordinary meteor shower going on");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("meteor shower");
					msg.setConversationId("Ooops... things got ugly over here");
					break;
				case 2:
					System.out.println("Bad weather: cosmic storm on the forecast");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("cosmic storm");
					msg.setConversationId("Ooops... things got ugly over here");
					break;
				case 3:
					System.out.println("Brack Hole, you can start freaking out now");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("black hole");
					msg.setConversationId("Ooops... things got ugly over here");
					break;
				case 4:
					System.out.println("Did you put your glasses on? Cause the visibility is better now");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("visibility improved");
					msg.setConversationId("Things got better, yeah!");
					break;
				case 5:
					System.out.println("Hehehe i bet you didn't expect a gas station here");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("gas station found");
					msg.setConversationId("Things got better, yeah!");
					break;
				case 6:
					System.out.println("OMG i'm so happy");
					sd.setType("spaceship");
					spaceship.addServices(sd);
					msg.setContent("galaxy good humor");
					msg.setConversationId("Things got better, yeah!");
					break;
			
			}
			
			msg.addReceiver(new AID("spaceship", AID.ISLOCALNAME));
			myAgent.send(msg);
		}
	}
	
	protected void takeDown() {
		
		try {
			System.out.println("It's the end of times...");
			DFService.deregister(this);
			
		} catch (FIPAException e) {
			e.printStackTrace();
			
		}
	} 


}






