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
	
	protected void setup() {
		addBehaviour(new SpaceShipIssues(this, 5000));
	}
	
	class SpaceShipIssues extends TickerBehaviour{
		public SpaceShipIssues(Agent a, long period) {
			super(a, period);
		}

		@Override
		protected void onTick() {
			DFAgentDescription mechanic = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			switch(random) {
				case 1: 
					sd.setType("mechanical-stuff");
					mechanic.addServices(sd);
					msg.setPerformative(ACLMessage.INFORM); 
					// não faz nada /\, mas caso quisermos mudar depois...
					msg.setContent("little issue here");
					break;
			}

			try {
				DFAgentDescription[] result = DFService.search(myAgent, mechanic); 
				System.out.println("Found the following astronauts:");
				astronauts = new AID[result.length];
				for (int i = 0; i < result.length; ++i) {
					astronauts[i] = result[i].getName();
					System.out.println(astronauts[i].getName());
					msg.addReceiver(astronauts[i]);
				}
			}
			catch (FIPAException fe) {
				fe.printStackTrace();
			}
			
			
			send(msg);
		}
	}
	
}
