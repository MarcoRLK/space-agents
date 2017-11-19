import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Medic extends Agent {
	
	private AID[] astronautAgents;

	protected void setup() {
		AID id = new AID("medic", AID.ISLOCALNAME);
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(this.getAID());
		ServiceDescription sd = new ServiceDescription(); 
		sd.setType("medic");
		sd.setName(getName());
		dfd.addServices(sd); 
		try {
			DFService.register(this, dfd);
			TreatingCrewDiseases tcd = new TreatingCrewDiseases(this);
			CheckCrewHealth cch = new CheckCrewHealth(this,10000);
//			addBehaviour(tcd);
			addBehaviour(cch);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
			doDelete();
		}	
	}
	
	class TreatingCrewDiseases extends CyclicBehaviour{ 
		
		private static final long serialVersionUID = 1L;
		
		public TreatingCrewDiseases(Agent medic) {
			super(medic);
		}
		
		@Override
		public void action() {	
			ACLMessage msg = myAgent.receive();
			
			if (msg!= null) {
				ACLMessage reply = msg.createReply();
					
				if(msg.getPerformative() == ACLMessage.REQUEST) {
					String content = msg.getContent();
					
					if ((content != null) && msg.getConversationId() == "calling-for-medic") {
						System.out.println("I am " + "Medic " + getLocalName() + ", and i received a call for help!");
						ACLMessage doTreatment = new ACLMessage(ACLMessage.CONFIRM);
						doTreatment.setContent("I can treat you now.");
						doTreatment.clearAllReceiver();
						doTreatment.addReceiver(msg.getSender());
						doTreatment.setConversationId("calling-for-medic");
						send(doTreatment);
						
						System.out.println("Medic " + getLocalName() + ": I am busy treating " + msg.getSender().getLocalName() + "'s health.");
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else{
						System.out.println("REFUSE");
						reply.setPerformative(ACLMessage.REFUSE);
						reply.setContent("Médico "+ getLocalName() +": the message asking for help was not clear enough!");
						System.out.println("REPLY: " + reply.getContent());
					}
					
					send(reply);
					
					
				} else {
					
					block();
				}
			}
		}
	
	}
	
	class CheckCrewHealth extends TickerBehaviour {                                  
        
		  public CheckCrewHealth(Agent a, long period) {                                 
		    super(a, period);                                                                                                      
		  }                                                                                
		  
		  private static final long serialVersionUID = 1L;                                 
		                                                                                   
			protected void onTick() {
				System.out.println("Medic " + getLocalName() + "Trying to find health problems on crew.");                                   
				DFAgentDescription mechanic = new DFAgentDescription();               
				ServiceDescription sd = new ServiceDescription();                     
				sd.setType("mechanic");   
				mechanic.addServices(sd);
				
				DFAgentDescription engineer = new DFAgentDescription();               
				ServiceDescription sd2 = new ServiceDescription();                     
				sd2.setType("engineer");   
				engineer.addServices(sd2);

				
				ACLMessage offerHelp = new ACLMessage(ACLMessage.REQUEST);
				offerHelp.setContent("Do you need help?");
				offerHelp.setConversationId("medic");
				ACLMessage response;
				
				try {                                                                 
					DFAgentDescription[] result = DFService.search(myAgent,mechanic);
					DFAgentDescription[] result2 = DFService.search(myAgent, engineer);
			
					astronautAgents = new AID[result.length + result2.length];
					for (int i= 0; i < result.length; ++i) {                           		            	                              
						astronautAgents[i] = result[i].getName();
						System.out.println("Found the astronaut " + astronautAgents[i].getName());
					}
					for(int z = 0; z < result2.length; z++) {
						astronautAgents[z + result.length] = result2[z].getName();
						System.out.println("Found the astronaut " + astronautAgents[z + result.length].getName());
					}
					for(int j = 0;j < (result.length+result2.length);++j){
						offerHelp.clearAllReceiver();
						offerHelp.addReceiver(astronautAgents[j]);
						System.out.println("Medic " + getLocalName() + ": is astronaut " + astronautAgents[j].getName() + " needing some help?");
						send(offerHelp);
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						response = myAgent.receive();
						
						if(response.getPerformative() == ACLMessage.CONFIRM) {
							ACLMessage doTreatment = new ACLMessage(ACLMessage.REQUEST);
							doTreatment.setContent("treating you...");
							doTreatment.clearAllReceiver();
							doTreatment.addReceiver(astronautAgents[j]);
							doTreatment.setConversationId("medic");
							send(doTreatment);
							System.out.println("Medic " + getLocalName() + ": I am busy treating " + astronautAgents[j].getLocalName() + "'s health.");
//							try {
//								Thread.sleep(5000);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
							break;
						}else if (response.getPerformative() == ACLMessage.DISCONFIRM){
							System.out.println("Medic " + getLocalName() + ": Ok...");
						}
						
					}
				}                                                                     
				catch (FIPAException fe) {                                            
					fe.printStackTrace();                                               
				}
			}                                                                              
	}     
	
}
