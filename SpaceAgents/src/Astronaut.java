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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


@SuppressWarnings("unused")
public class Astronaut extends Agent {
	private static final long serialVersionUID = 1L;
	Random generator = new Random(System.currentTimeMillis());
	private int health;
	private String job;
	private int random;
	private boolean onTreatment;
//	private Hashtable <String, Integer> relationships;
//	private ArrayList <String> advantages;
//	private ArrayList <String> disadvantages;
	
	protected void setup() {
//		relationships = new Hashtable<String,Integer>();
//		advantages = new ArrayList<String>();
//		disadvantages = new ArrayList<String>();
		Object[] args = getArguments();
		health = 10;
		onTreatment = false;
		job = args[0].toString();
		AID id = new AID(job, AID.ISLOCALNAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType(job);
		sd.setName(getName());
		dfd.addServices(sd); 
		try {
			DFService.register(this, dfd);
			FindingSpaceshipIssues fssi = new FindingSpaceshipIssues(this);
			ImOnlyHumanBehaviour humanBehaviour = new ImOnlyHumanBehaviour(this, 5000);
			addBehaviour(fssi);
			addBehaviour(humanBehaviour);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}	
	
	}
	
	class ImOnlyHumanBehaviour extends TickerBehaviour {

		public ImOnlyHumanBehaviour(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			random = generator.nextInt(4); // 0, 1, 2, 3
			if(random == 0) {
				System.out.println(job + " " + getLocalName() + ": " + "I'm not feeling very well...");
				health--;
				System.out.println("New health: " + health);
			}
			
		if(health <= 0) {
			takeDown();
		}	
		}	
	}
	
	class FindingSpaceshipIssues extends CyclicBehaviour{ 
		
		private static final long serialVersionUID = 1L;
		
		public FindingSpaceshipIssues(Agent astronaut) {
			super(astronaut);
		}
		
		@Override
		public void action() {	
			ACLMessage msg = myAgent.receive();
			
			if (msg!= null) {
				ACLMessage reply = msg.createReply();
				if(msg.getPerformative() == ACLMessage.REQUEST) {
					String content = msg.getContent();
					if(content != null) {
						switch(msg.getConversationId()) {
							case "medic":
								switch(content) {
									case "Do you need help?":
										if(health <= 6) {
											reply.setPerformative(ACLMessage.CONFIRM);
											reply.setContent("Please, i need help!");
											System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										} else {
											reply.setPerformative(ACLMessage.DISCONFIRM);
											reply.setContent("There's no need, im fine!");
											System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										}
										break;
									case "treating you...":
										onTreatment = true;
										System.out.print(job + " " + getLocalName() + " receiving treatment!!\n");
										try {
												Thread.sleep(1000);
										} catch (InterruptedException e) {
												e.printStackTrace();
										}
										health += 3;
										onTreatment = false;
								}
								break;				
						}
						send(reply);
					}
				}	
				if(msg.getPerformative() == ACLMessage.INFORM && onTreatment == false) {
//					System.out.println("Recebi o request!!");
					String content = msg.getContent();
					System.out.println(" ---------------- ");
					System.out.println(job + " " + getLocalName() + "\nHealth:" + health + "\nreceived a inform: " + content);
					if(health <= 6) {
						reply.setPerformative(ACLMessage.DISCONFIRM);
						reply.setContent(job + " "+ getLocalName() + " to Ground Control, i'm too tired to do this");
						System.out.println(job + " " + getLocalName() + ": " + reply.getContent());	
					}
					else if ((content != null)) {
						switch(msg.getConversationId()) {
							case "mechanic":
								switch(content) {
									case "little issue here":
										health -= 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent(job + " "+ getLocalName() + " to Ground Control, going to perform an extravehicular activity");
										System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										System.out.println("New health: " + health);
										break;
									case "everything working fine! almost...":
										reply.setPerformative(ACLMessage.INFORM);
										reply.setContent(job + " "+ getLocalName() + " to Ground Control, everything is fine!");
										System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										break;
								}
								break;
							case "engineer":
								switch(content) {
									case "Still good...":
										reply.setPerformative(ACLMessage.INFORM);
										reply.setContent(job + " "+ getLocalName() + " to Ground Control, everything is fine!");
										System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										break;
									case "Oxygen hitting critical levels!":
										health -= 1;
										reply.setPerformative(ACLMessage.CONFIRM);
										reply.setContent(job + " "+ getLocalName() + " to Ground Control, going to get some air");
										System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
										System.out.println("New health: " + health);
										break;
								}
								break;
							default:
								System.out.println(job + " " + getLocalName() + ": " + "Time to take some rest...");
								reply.setPerformative(ACLMessage.INFORM);
								reply.setContent(job + " " + getLocalName() + ": " + "Time to take some rest...");
						}
						
					} else{
						System.out.println("REFUSE");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent(job + " "+ getLocalName() +" to Ground Control, I'm cant understand, please repeat");
						System.out.println(job + " " + getLocalName() + ": " + reply.getContent());
					}
					
					send(reply);
//					System.out.println("Mensagem enviada");
					
					
				}
//				if(health <= 6){
//					DFAgentDescription medic = new DFAgentDescription();               
//					ServiceDescription sd = new ServiceDescription();                     
//					sd.setType("medic");   
//					medic.addServices(sd);
//					System.out.println(job + " " + getLocalName() + ": I need help, im sick!");
//					ACLMessage askForTreatment = new ACLMessage(ACLMessage.REQUEST);
//					askForTreatment.setContent("I need help, im sick.");
//					askForTreatment.setConversationId("calling-for-medic");
//					ACLMessage response;
//					
//					try {
//						DFAgentDescription[] result = DFService.search(myAgent,medic);
//						
//						AID[] medicAgents = new AID[result.length];
//						
//						for (int i= 0; i < result.length; ++i) {                           		            	                              
//							medicAgents[i] = result[i].getName();
//							System.out.println("Found the medic " + medicAgents[i].getName());
//						}
//						
//						for (int j = 0;j < result.length;++j){
//						
//							askForTreatment.clearAllReceiver();
//							askForTreatment.addReceiver(medicAgents[j]);				
//							send(askForTreatment);			
//						
//							try {
//								Thread.sleep(1000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//						
//							response = myAgent.receive();
//							
//							if(response.getPerformative() == ACLMessage.CONFIRM && response.getConversationId() == "calling-for-medic") {
//								System.out.println("I am in treatment!");
//								health += 1;
//								try {
//									Thread.sleep(5000);
//								} catch (InterruptedException e) {
//									e.printStackTrace();
//								}
//							}
//							break;
//						}
//									
//					} catch (FIPAException e) {
//						e.printStackTrace();
//					}
//					
//			
//				}
				else {
					block();
				}
			}
		}
	
	} // end of inner class
	
	
	protected void takeDown() {
		
		try {
			System.out.println("Morri... brinks");
			DFService.deregister(this);
			
		} catch (FIPAException e) {
			e.printStackTrace();
			
		}
	} 

}// end of class
