import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Launcher extends Agent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected void setup() {
		addBehaviour(new ProjectLaunch());
		
	}
		
		
	class ProjectLaunch extends OneShotBehaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void action() {
			
			
			Object [] argsMechanic = new Object[1];
			argsMechanic[0] = "mechanic";
			
			Object [] argsEngineer = new Object[1];
			argsEngineer[0] = "engineer";
			
		
	        ContainerController cc = getContainerController();
	        AgentController spaceshipController;
	        AgentController spaceController;
	        AgentController medicController;
	        AgentController astronautController1;
	        AgentController astronautController2;
	        try {
	            spaceshipController = cc.createNewAgent("spaceship", "Spaceship", null);
	            spaceController = cc.createNewAgent("Milky Way", "Space", null);
	            astronautController1 = cc.createNewAgent("Tom", "Astronaut", argsMechanic);
	            astronautController2 = cc.createNewAgent("Robert", "Astronaut", argsEngineer);
	            medicController = cc.createNewAgent("House", "Medic", null);
	            
	            astronautController1.start();
	            astronautController2.start();
	            spaceshipController.start();
	            spaceController.start();
	            medicController.start();
	            
	        } catch (StaleProxyException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        
			try {
				printLaunchCountDown();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

		private void printLaunchCountDown() throws InterruptedException {
			System.out.println("Commencing countdown, engines on");
			System.out.println("Take your protein pills and put your helmet on");
			System.out.println("Check ignition and may God's love be with you");
			
			for(int i = 5; i > 0; i--) {
				System.out.println(i);
				Thread.sleep(1000);
			}
			
			System.out.println("LIFTOFF");
			
		}
	}// end of inner class
	

}// end of class
