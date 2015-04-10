package CardPickup;

public class GreedyAttacker extends Attacker{
	/**INSERT YOUR ATTACKER NAME HERE*/
	private final static String attackerName = "GreedyAttacker";
	
    /**
     * DO NOT CHANGE THIS CONSTRUCTOR
     * @param defenderName name of defender
     * @param graphFile file containing graph
     */
	public GreedyAttacker(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}
	
	/**DO NOT CHANGE THIS CONSTRUCTOR*/
	public GreedyAttacker(){
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
		AttackerActionType type;
		int nodeID;
		
		for(Node x: availableNodes)
		{
			if (x.getPv() == -1)
			{
				nodeID = x.getNodeID();
				type = AttackerActionType.PROBE_POINTS;
				return new AttackerAction(type, nodeID);
			}			
		}
		int nodeidwithmaxPv = -1;
		int nodeidwithminmaxPv = -1;
		int maxPv = Integer.MIN_VALUE;
		int minmaxPv = Integer.MAX_VALUE;
		for(Node x: availableNodes)
		{
			if (x.getPv() <=15 && (maxPv<x.getPv()))
			{
				maxPv = x.getPv();
				nodeidwithmaxPv = x.getNodeID();
			}
			else if(x.getPv()>15 &&  x.getPv()<minmaxPv)
			{
				nodeidwithminmaxPv = x.getNodeID();
				minmaxPv = x.getPv();
			}
		}
		
		type = AttackerActionType.ATTACK;
		if(nodeidwithmaxPv != -1)
		{
		
			return new AttackerAction(type, nodeidwithmaxPv);
		}
		else
		{
			return new AttackerAction(type, nodeidwithminmaxPv);
		}
		
	}
}
