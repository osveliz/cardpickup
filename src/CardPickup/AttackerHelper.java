package CardPickup;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Auxiliary class for creating .attack files.
 * Attacker will use the three parameter constructor and combination of the two attack methods and the four probe methods
 * to generate an .attack file. Have Attacker remember to call close() when finished for safety.
 * Game Master will use the two parameter constructor for parsing original network and attacker file to generate the attacker's visible network and history.
 *
 * Actions deemed invalid will be charged the Parameters.INVALID_RATE value.
 *
 * @author      Marcus Gutierrez
 * @version     Nov 7, 2014
 */

public class AttackerHelper
{
    private Graph net;
    private PrintWriter apw;
    private int budget;
    private ArrayList<Node> availableNodes;

    /**
     * Constructor used by Attacker to initialize visibility file and keep track of attacker history.
     * @param net Graph being secured given a budget
     * @param budget current budget
     * @param attackerName name of the attacker
     * @param defenderName name of the defender
     * @param graphName name of the graph
     */
    public AttackerHelper(Graph net, int budget, String attackerName, String defenderName, String graphName)
    {
        this.net = net;
        this.budget = budget;
        availableNodes = net.getAvailableNodes();
        try{
            apw = new PrintWriter(attackerName + "-" + defenderName + "-" + graphName + ".attack", "UTF-8");
        }
        catch (Exception e){ e.printStackTrace();}
    }

    /**
     * Attacker should call this method when done adding actions.
     */
    public void close()
    {
    	apw.close();
    }

    /**
     * Action has been deemed invalid
     */
    public void invalid()
    {
        budget -= Parameters.INVALID_RATE;
        apw.write("-1");
        apw.println();
    }
    
    /**
     * Attacks a node
     * @param id The id of the node being attacked
     */
    public void attack(int id)
    {
        if(isAvailableNode(id) && isValidAttack(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.ATTACK_RATE;
            apw.write("0," + id);
            apw.println();
        }
        else
            invalid();
    }
    
   
    /**
     * Attacks a node with better chances
     * @param id The id of the node being attacked
     */
    public void superAttack(int id)
    {
        if(isAvailableNode(id) && isValidSuperAttack(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.SUPERATTACK_RATE;
            apw.write("1," + id);
            apw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its security value
     * @param id The id of the node being probed
     */
    public void probeSecurity(int id)
    {
        if(isAvailableNode(id) && isValidProbeSV(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_SECURITY_RATE;
            apw.write("2," + id);
            apw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its point value
     * @param id The id of the node being probed
     */
    public void probePoint(int id)
    {
        if(isAvailableNode(id) && isValidProbePV(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_POINT_RATE;
            apw.write("3," + id);
            apw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover its number of connections
     * @param id The id of the node being probed
     */
    public void probeConnections(int id)
    {
        if(isAvailableNode(id) && isValidProbeConn(id))
        {
            Node n = net.getNode(id);
            budget -= Parameters.PROBE_CONNECTIONS_RATE;
            apw.write("4," + id);
            apw.println();
        }
        else
            invalid();
    }
    
    /**
     * Probes a node to discover if it is a honey pot or not
     * @param id The id of the node being probed
     */
    public void probeHoney(int id)
    {
        if(isAvailableNode(id) && isValidProbeHP(id))
        {
            budget -= Parameters.PROBE_HONEY_RATE;
            apw.write("5," + id);
            apw.println();
        }
        else
            invalid();
    }

    /**
     * Returns current budget.
     * @return current budget
     */
    public int getBudget()
    {
        return budget;
    }

    public boolean isValidAttack(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.ATTACK_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidSuperAttack(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.SUPERATTACK_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeSV(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_SECURITY_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbePV(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_POINT_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeConn(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_CONNECTIONS_RATE || n == null)
            return false;
        return true;
    }
    
    public boolean isValidProbeHP(int id){
    	Node n = net.getNode(id);
        if(budget < Parameters.PROBE_HONEY_RATE || n == null)
            return false;
        return true;
    }
    
    private boolean isAvailableNode(int id){
    	Node n = new Node(id);
    	return availableNodes.contains(n);
    }
}
