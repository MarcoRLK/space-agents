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
import jade.core.behaviours.CyclicBehaviour;
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
		AID id = new AID("spaceship", AID.ISLOCALNAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("spaceship");
		sd.setName(getName());
		dfd.addServices(sd); 
		CheckinSpaceConditions csc = new CheckinSpaceConditions(this);
		addBehaviour(new SpaceShipIssues(this, 15000));
		addBehaviour(csc);
	}
	
	
	class SpaceShipIssues extends TickerBehaviour{
		public SpaceShipIssues(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			random = generator.nextInt(3); // 0, 1, 2
			DFAgentDescription astronaut = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			System.out.println(
							"\n-------------------- New round! -------------------------" +
							"\nSpaceship info:" +
							"\nSpaceship condition: " + spaceshipCondition +
							"\nOxygen level: " + oxygenLevel + "\n"
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
					System.out.println("Oxygen levels going low...\nOxygen levels: " + oxygenLevel);
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
								break;
							case 2:
								oxygenLevel +=3;
								break;
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
	
	class CheckinSpaceConditions extends CyclicBehaviour{ 
	
		private static final long serialVersionUID = 8383983886609432248L;

		public CheckinSpaceConditions(Agent spaceship) {
			super(spaceship);
		}
		
		@Override
		public void action() {	
			ACLMessage msg = myAgent.receive();
			
			if (msg!= null) {
				ACLMessage reply = msg.createReply();
				if(msg.getPerformative() == ACLMessage.INFORM) {
					String content = msg.getContent();
					if ((content != null)) {
						switch(msg.getConversationId()) {
							case "It's everything ok in this galaxy...":
								System.out.println("It's everything ok in this galaxy...");
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent("It's all good");
								break;
							case "Ooops... things got ugly over here":
								switch(content) {
									case "meteor shower":
										spaceshipCondition -= 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("1");
										System.out.println("Oh no, we're facing a meteor shower, new Spaceship condition is: " + spaceshipCondition);
										break;
									case "cosmic storm":
										spaceshipCondition -= 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("2");
										System.out.println("Crap! There's a cosmic storm ahead, new Spaceship condition is: " + spaceshipCondition);
										break;
									case "black hole":
										spaceshipCondition -= 3;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("3");
										System.out.println("F*** a black hole! Thigs got pretty ugly, new Spaceship condition is: " + spaceshipCondition);
										break;
								}
								break;
							case "Things got better, yeah!":
								switch(content) {
									case "visibility improved":
										spaceshipCondition += 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("4");
										System.out.println("Nice, visibility improved, new Spaceship condition is: " + spaceshipCondition);
										break;
									case "gas station found":
										spaceshipCondition += 2;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("5");
										System.out.println("Thar's werid, a gas station around here. Well, let's fill up the tank, new Spaceship condition is: " + spaceshipCondition);
										break;
									case "galaxy good humor":
										spaceshipCondition += 1;
										oxygenLevel += 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent("6");
										System.out.println("Oh Galaxy, are you smiling at me? Thanks my little Milky Way! New Spaceship condition is: " + spaceshipCondition);
										System.out.println("Oxigen level is now at: " + oxygenLevel);
										break;
								}
								break;
						}
						System.out.println("\n");
					}
					
				}
			}
			
			
		}
	}
	
}
