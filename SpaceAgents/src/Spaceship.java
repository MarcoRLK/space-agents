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

public class Spaceship extends Agent {
		Random generator = new Random(System.currentTimeMillis());
		private AID[] astronauts;
		private int random;
		public int spaceshipCondition;
		public int oxygenLevel;
	
	protected void setup() {
		spaceshipCondition = 10;
		oxygenLevel = 10;
		addBehaviour(new SpaceShipIssues(this, 5000));
	}
	
	
	class SpaceShipIssues extends TickerBehaviour{
		public SpaceShipIssues(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			random = generator.nextInt(3); // 0, 1, 2
			System.out.println("AAAAAAAAAAAAAAAAAAAAA: " + random);
			DFAgentDescription astronaut = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			System.out.println(
							"\n//////////NEW ROUND!///////////////" +
							"\nSpaceship condition: " + spaceshipCondition +
							"\nOxygen level: " + oxygenLevel
							);
			
			switch(random) {
				case 0:
					System.out.println("Another day, another dollar... everything is good!");
					sd.setType("none");
					astronaut.addServices(sd);
					msg.setContent("Another day, another dollar... everything is good!");
					break;
				case 1:
					spaceshipCondition -= 2;
					System.out.println("Looks like we hit something...\nSpaceship condition: " + spaceshipCondition);
					if	(spaceshipCondition > 6) {
						sd.setType("mechanic");
						astronaut.addServices(sd);
						msg.setContent("everything working fine! almost...");
						msg.setConversationId("mechanic");

					} else if(spaceshipCondition > 3){
						sd.setType("mechanic");
						astronaut.addServices(sd);
						msg.setContent("little issue here");	
						msg.setConversationId("mechanic");
						
					} else if(spaceshipCondition > 0) {
						sd.setType("mechanic");
						astronaut.addServices(sd);
						msg.setContent("Houston, we have a problem...");
						msg.setConversationId("mechanic");
					}else {
						// implementar fim
					}
					break;
				case 2:
					oxygenLevel -= 2;
					System.out.println("Oxygen levels going low...\n Oxygel levels: " + oxygenLevel);
					if( oxygenLevel > 6) {
						sd.setType("engineer");
						astronaut.addServices(sd);
						msg.setContent("Still good...");
						msg.setConversationId("engineer");
					}else {
						sd.setType("engineer");
						astronaut.addServices(sd);
						msg.setContent("Oxygen hitting critical levels!");
						msg.setConversationId("engineer");
					}
					
			}

			try {
				DFAgentDescription[] result = DFService.search(myAgent, astronaut); 
//				System.out.println("Found the following astronauts:");
				astronauts = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					astronauts[i] = result[i].getName();
//					System.out.println(astronauts[i].getName());
				}
				ACLMessage response;
				for(int z = 0; z < result.length; ++z) {
					msg.clearAllReceiver();
					msg.addReceiver(astronauts[z]);
					send(msg);
					try {
						Thread.sleep(1000);
					}catch(InterruptedException e) {
						System.out.println("erro:" + e);
					};
					
					response = myAgent.receive();
					if(response.getPerformative() == ACLMessage.CONFIRM) {
						switch(random) {
							case 1: 
								spaceshipCondition += 3;
							case 2:
								oxygenLevel +=3;
						}
						break;
					}else if(response.getPerformative() == ACLMessage.INFORM) {
						break;
					}
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			
		}
	}
	
}
