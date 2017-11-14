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


public class Astronaut extends Agent {
	private static final long serialVersionUID = 1L;
	private int humour;
	private Hashtable <String, Integer> relationships;
	private ArrayList <String> advantages;
	private ArrayList <String> disadvantages;
	
	protected void setup() {
		relationships = new Hashtable<String,Integer>();
		advantages = new ArrayList<String>();
		disadvantages = new ArrayList<String>();
		
		
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("mechanical stuff");
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
				
				if(msg.getPerformative() == ACLMessage.REQUEST) {
					System.out.println("Recebi o request!!");
					String content = msg.getContent();
					System.out.println("Recebi a mensagem:" + content);
					
					if ((content != null)) {
						System.out.println("RECEBI");
						reply.setPerformative(ACLMessage.INFORM);
						reply.setContent("Major Tom to Ground Control, going to perform an extravehicular activity");
						System.out.println("REPLY: " + reply.getContent());
					
					} else{
						System.out.println("REFUSE");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("Major Tom to Ground Control, I'm incapable of perform this extravehicular activity");
						System.out.println("REPLY: " + reply.getContent());
					}
					
					send(reply);
					System.out.println("Mensagem enviada");
					
					
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
