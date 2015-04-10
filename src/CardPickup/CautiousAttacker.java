package CardPickup;

public class CautiousAttacker extends Attacker {
	/**INSERT YOUR ATTACKER NAME HERE*/
	private final static String attackerName = "CautiousAttacker";
	
    /**DO NOT CHANGE THIS CONSTRUCTOR*/
	public CautiousAttacker(String defenderName, String graphFile) {
		super(attackerName, defenderName, graphFile);
	}
	
	/**DO NOT CHANGE THIS CONSTRUCTOR*/
	public CautiousAttacker(){
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
		int nodeID = -1;
		
		for(Node x: availableNodes)
		{
			if (x.getSv() == -1)
			{
				nodeID = x.getNodeID();
				type = AttackerActionType.PROBE_SECURITY;
				return new AttackerAction(type, nodeID);
			}			
		}
		int nodeidwithmaxsv = -1;
		int nodeidwithminmaxsv = -1;
		int maxsv = Integer.MIN_VALUE;
		int minmaxsv = Integer.MAX_VALUE;
		for(Node x: availableNodes)
		{
			if (x.getSv() <=10 && (maxsv<x.getSv()))
			{
				maxsv = x.getSv();
				nodeidwithmaxsv = x.getNodeID();
			}
			else if(x.getSv()>10 &&  x.getSv()<minmaxsv)
			{
				nodeidwithminmaxsv = x.getNodeID();
				minmaxsv = x.getSv();
			}
		}
		
		type = AttackerActionType.ATTACK;
		if(nodeidwithmaxsv != -1)
		{
		
			return new AttackerAction(type, nodeidwithmaxsv);
		}
		else
		{
			return new AttackerAction(type, nodeidwithminmaxsv);
		}
		
	}
}
