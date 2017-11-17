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

public class Spaceship extends Agent {
	
		private AID[] astronauts;
		private int random = 1; // deixar random depois!
		public int spaceshipCondition; 
	
	protected void setup() {
		spaceshipCondition = 10; 
		addBehaviour(new SpaceShipIssues(this, 5000));
	}
	
	class SpaceShipIssues extends TickerBehaviour{
		public SpaceShipIssues(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			DFAgentDescription astronaut = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			System.out.println("//////////NOVA RODADA!///////////////\n" + "Condição da nave: "+ (spaceshipCondition));
			switch(random) {
				case 1:
					spaceshipCondition -= 2;
					System.out.println("Parece que batemos em algo...\nCondição da nave: " + spaceshipCondition);
					if	(spaceshipCondition > 6) {
						sd.setType("mechanic");
						msg.setContent("everything working fine! almost...");

					} else if(spaceshipCondition > 3){
						sd.setType("mechanic");
						astronaut.addServices(sd);
						// não faz nada /\, mas caso quisermos mudar depois...
						msg.setContent("little issue here");	
						
					} else if(spaceshipCondition > 0) {
						sd.setType("mechanic");
						astronaut.addServices(sd);
						msg.setContent("Houston, we have a problem...");	
					}else {
						// implementar fim
					}
					break;
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
						}
						break;
					}else if(response.getPerformative() == ACLMessage.INFORM && response.getContent().contains("everything is fine")) {
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
