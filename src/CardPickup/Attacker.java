package CardPickup;

import java.util.ArrayList;

/**
 * Attacker agent. The actions for the attacker in this game include attacking a node, super attacking a node, 
 * probing for security values of a node, probing for the point value of a node, probing number of connections, and probing for honey pots.
 * All logic/equations/formulas/etc for how your attacker decides to select actions should be included in run()
 * @author Marcus Gutierrez
 * @version 2014/11/14
 */
public abstract class Attacker implements Runnable
{
    private CardPickup.AttackerHelper ah;
    private String attackerName = "defaultAttacker"; //Overwrite this variable in your attacker subclass
    private String graph;
    private Graph netVisible;
    protected ArrayList<Node> capturedNodes;
    protected ArrayList<Node> availableNodes;
    protected int budget;
    private volatile boolean isAlive = true;
    /**
     * Constructor.
     * Parses Network stored in graphFile.
     * Performs Attacker logic to select actions.
     * Outputs [agentName]-[graphFile].attack with selected actions
     * @param agentName Attacker agent's name i.e. "Sharks"
     * @param defenderName Defender agent's name i.e. "Jets"
     * @param graphName String containing number of visibility network i.e. "1914"
     */
    public Attacker(String agentName, String defenderName, String graphName)
    {
        attackerName = agentName;
        graph = graphName;
        netVisible = Parser.parseAttackerHistory(agentName, defenderName, graphName);
        capturedNodes = netVisible.getCapturedNodes();
        availableNodes = netVisible.getAvailableNodes();
        budget = Parser.parseAttackerBudget(attackerName, defenderName, graphName);
        ah = new CardPickup.AttackerHelper(netVisible, budget, agentName, defenderName, graphName);
        initialize();
    }
    
    public Attacker(String attackerName){
    	this.attackerName = attackerName;
    }
    
    protected abstract void initialize();

    /**
     * Attacker selects to perform a regular attack on a node.
     *
     * @param id Node's ID number
     */
    private final void attack(int id)
    {
        ah.attack(id);
    }
    
    protected boolean isValidAttack(int id){
    	return ah.isValidAttack(id);
    }
    
    /**
     * Attacker selects to perform a strong attack on a node.
     *
     * @param id Node's ID number
     */
    private final void superAttack(int id)
    {
        ah.superAttack(id);
    }
    
    protected boolean isValidSuperAttack(int id){
    	return ah.isValidSuperAttack(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its security value.
     *
     * @param id Node's ID number
     */
    private final void probeSecurity(int id)
    {
        ah.probeSecurity(id);
    }
    
    protected boolean isValidProbeSecurity(int id){
    	return ah.isValidProbeSV(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its point value.
     *
     * @param id Node's ID number
     */
    private void probePoints(int id)
    {
        ah.probePoint(id);
    }
    
    protected boolean isValidProbePV(int id){
    	return ah.isValidProbePV(id);
    }
    
    /**
     * Attacker selects to probe a node to learn its number of connections.
     *
     * @param id Node's ID number
     */
    private final void probeConnections(int id)
    {
        ah.probeConnections(id);
    }
    
    protected boolean isValidProbeConn(int id){
    	return ah.isValidProbeConn(id);
    }
    
    /**
     * Attacker selects to probe a node to learn if the node is a honey pot.
     *
     * @param id Node's ID number
     */
    private final void probeHoneypot(int id)
    {
        ah.probeHoney(id);
    }
    
    protected boolean isValidHP(int id){
    	return ah.isValidProbeSV(id);
    }
    
    /**
     * Attacker performs an invalid move
     */
    private final void invalid()
    {
        ah.invalid();
    }

    /**
     * Executes one action for the attacker
     */
    public final void run()
    {
    	while(isAlive)
        {
    		/*int i;
    		System.out.print("Available Nodes: ");
    		if(availableNodes.size() > 1){
    			for(i = 0; i < availableNodes.size() - 1; i++)
    				System.out.print(availableNodes.get(i).getNodeID() + ",");
    			System.out.println(availableNodes.get(i).getNodeID());
    		} else if(availableNodes.size() == 1) {
    			System.out.println(availableNodes.get(0).getNodeID());
    		} else {
    			System.out.println(-1);
    		}
    		
    		int j;
    		System.out.print("Captured Nodes: ");
    		if(capturedNodes.size() > 1){
                for(j = 0; j < capturedNodes.size() - 1; j++)
                    System.out.print(capturedNodes.get(j).getNodeID() + ",");
                System.out.println(capturedNodes.get(j).getNodeID());
    		} else if(capturedNodes.size() == 1) {
    			System.out.println(capturedNodes.get(0).getNodeID());
    		} else {
    			System.out.println(-1);
    		}*/

            AttackerAction attack = new AttackerAction(CardPickup.AttackerActionType.INVALID, -1);
            CardPickup.AttackerActionType type = CardPickup.AttackerActionType.INVALID;
            try{
                attack = makeSingleAction();
                type = attack.move;
            }
            catch (Exception e){
                System.out.println("Error with "+attackerName);
                e.printStackTrace();
            }

            switch(type){
            case ATTACK:
            	attack(attack.nodeID);
            	break;
            case SUPERATTACK:
            	superAttack(attack.nodeID);
            	break;
            case PROBE_SECURITY:
            	probeSecurity(attack.nodeID);
            	break;
            case PROBE_POINTS:
            	probePoints(attack.nodeID);
            	break;
            case PROBE_CONNECTIONS:
            	probeConnections(attack.nodeID);
            	break;
            case PROBE_HONEYPOT:
            	probeHoneypot(attack.nodeID);
            	break;
            case INVALID:
            	invalid();
            	break;
            }
            isAlive = false;
        }
        ah.close();
    }
    
    public abstract AttackerAction makeSingleAction();

    /**
     * Get Agent Name used by GameMaster.
     * @return Name of defender
     */
    public String getName()
    {
        return attackerName;
    }

    /**
     * Get Game used by GameMaster
     * @return graph number
     */
    public final String getGraph()
    {
        return graph;
    }
    /**
     * kills long running defenders
     */
    public final void kill()
    {
        isAlive = false;
    }
    public boolean keepRunning(){
		return isAlive;
	}
}
