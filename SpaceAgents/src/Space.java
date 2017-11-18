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


public class Space extends Agent {

		private static final long serialVersionUID = -1133856395135449695L;
		private AID[] spaceships;
		int visibility;
		
	protected void setup() {
		visibility = 10;
		
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
			// TODO Auto-generated method stub
			
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









