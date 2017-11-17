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

import java.util.ArrayList;
import java.util.Hashtable;

import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;


@SuppressWarnings("unused")
public class Astronaut extends Agent {
	private static final long serialVersionUID = 1L;
	private int health;
	private String job;
//	private Hashtable <String, Integer> relationships;
//	private ArrayList <String> advantages;
//	private ArrayList <String> disadvantages;
	
	protected void setup() {
//		relationships = new Hashtable<String,Integer>();
//		advantages = new ArrayList<String>();
//		disadvantages = new ArrayList<String>();
		Object[] args = getArguments();
		health = 10;
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
			addBehaviour(fssi);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
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
					
				if(msg.getPerformative() == ACLMessage.INFORM) {
//					System.out.println("Recebi o request!!");
					String content = msg.getContent();
					System.out.println(job + " " + getLocalName() + "\nHealth:" + health + "\nreceived a inform: " + content);
					
					if ((content != null)) {
						switch(content) {
						case "little issue here":
//							System.out.println("RECEBI");
							if(health > 6) {
								health -= 1;
								reply.setPerformative(ACLMessage.CONFIRM);
								reply.setContent(job + " "+ getLocalName() + " to Ground Control, going to perform an extravehicular activity");
								System.out.println("REPLY: " + reply.getContent());
								System.out.println("New health: " + health);
							}else {
								reply.setPerformative(ACLMessage.DISCONFIRM);
								reply.setContent(job + " "+ getLocalName() + " to Ground Control, i'm too tired too do this");
								System.out.println("REPLY: " + reply.getContent());	
							}
							break;
						case "everything working fine! almost...":
//							System.out.println("RECEBI");
							reply.setPerformative(ACLMessage.INFORM);
							reply.setContent(job + " "+ getLocalName() + " to Ground Control, everything is fine!");
							System.out.println("REPLY: " + reply.getContent());
							break;
						case "Do you need help?":
							System.out.println("Im on treatment right now.");
							reply.setPerformative(ACLMessage.CONFIRM);
							reply.setContent("Lets start the treatment");
							System.out.println("REPLY: " + reply.getContent());
							//send(reply);
							//try {
							//	Thread.sleep(1000);
							//} catch (InterruptedException e) {
							//	e.printStackTrace();
							//}
							break;
						}
					} else{
						System.out.println("REFUSE");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent(job + " "+ getLocalName() +" to Ground Control, I'm cant understand, please repeat");
						System.out.println("REPLY: " + reply.getContent());
					}
					
					send(reply);
//					System.out.println("Mensagem enviada");
					
					
				} else {
					
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
