package CardPickup;

import java.util.Random;

/**
 * Example attacker agent that decides on actions at random.
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
 * @version 11/14/2014
 */
public class Blitzkrieg extends Attacker {
	
	/**INSERT YOUR ATTACKER NAME HERE*/
	private final static String attackerName = "Blitzkrieg";
	
    /**DO NOT CHANGE THIS CONSTRUCTOR*/
	public Blitzkrieg(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}
	
	/**DO NOT CHANGE THIS CONSTRUCTOR*/
	public Blitzkrieg(){
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
            return new AttackerAction(AttackerActionType.INVALID,0);
		int nodeID = availableNodes.get(r.nextInt(availableNodes.size())).getNodeID();
		int move = r.nextInt(6);
		AttackerActionType type;
		if(move == 0)
			type = AttackerActionType.ATTACK;
		else if(move == 1)
			type = AttackerActionType.SUPERATTACK;
		else if(move == 2)
			type = AttackerActionType.PROBE_SECURITY;
		else if(move == 3)
			type = AttackerActionType.PROBE_POINTS;
		else if(move == 4)
			type = AttackerActionType.PROBE_CONNECTIONS;
		else if(move == 5)
			type = AttackerActionType.PROBE_HONEYPOT;
		else
			type = AttackerActionType.INVALID;
		return new AttackerAction(type, nodeID);
	}
}