package CardPickup;

import java.util.Random;

/**
 * Example attacker agent.
 * IMPORTANT NOTE: 	Your attacker object will be recreated for every action. Because of this,
 * 					model your Attacker to only make a decision on the current information. Do
 * 					not try to use variables that will carry on in to the next makeSingleAction()
 * 
 * Make use of the three protected variables inherited from Attacker. These variables include:
 * protected ArrayList<Node> capturedNodes - a list of the already captured nodes
 * protected ArrayList<Node> availableNodes - a list of the available nodes for attacking and probing.
 * protected int budget - the current budget of the Attacker. Be careful that your next move will not cost more than your budget.
 * 
 * @author Marcus Gutierrez
 * @version 14/14/2014
 */
public class GamblingAttacker extends Attacker {

    private final static String attackerName = "GamblingAttacker";

    /**
     * Constructor
     * @param defenderName defender's name
     * @param graphFile graph to read
     */
	public GamblingAttacker(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}
	
	public GamblingAttacker(){
		super(attackerName);
	}
	
	/**
	 * If you need to initialize anything, do it  here
	 */
	protected void initialize(){
		
	}

	/**
     * Example Logic of Attacker agent.
     * You will need to return an AttackerAction which requires the AttackerActionType
     * and the nodeID of the action.
     * 
     * Make sure to utilize 
     * 
     * For instance, if you wish to attack node 5, you will return an AttackerAction
     * object like this: 
     * return new AttackerAction(AttackerActionType.ATTACK, 5);
     */
	public AttackerAction makeSingleAction() {
		Random r = new Random();
        if(availableNodes.size()==0)
            return new AttackerAction(CardPickup.AttackerActionType.INVALID,0);
		int nodeID = availableNodes.get(r.nextInt(availableNodes.size())).getNodeID();
		return new AttackerAction(CardPickup.AttackerActionType.ATTACK, nodeID);
		
	}
}