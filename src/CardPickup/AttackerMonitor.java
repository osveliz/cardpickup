package CardPickup;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Auxiliary class for creating and parsing .attack files.
 * Attacker will use the three parameter constructor and combination of the two attack methods and the four probe methods
 * to generate an .attack file. Have Attacker remember to call close() when finished for safety.
 * Game Master will use the two parameter constructor for parsing original network and attacker file to generate the attacker's visible network and history.
 *
 * Actions deemed invalid will be charged the Parameters.INVALID_RATE value.
 *
 * @author      Marcus Gutierrez
 * @version		2014/11/14
 */

public class AttackerMonitor
{
    private Graph net;
    private Graph visibleNet;
    
    private String attackerName;
    private String defenderName;
    private String graphName;
    private PrintWriter history;
    
    private int budget;
    private int points;
    private Random r;
    private ArrayList<CardPickup.Node> availableNodes;

    /**
     * Constructor
     * @param attackerName attacker name
     * @param defenderName defender name
     * @param graphName graph name
     */
    public AttackerMonitor(String attackerName, String defenderName, String graphName){
        budget = Parameters.ATTACKER_BUDGET;
        //System.out.println("Initial Budget1: " + budget);
        r = new Random();
        this.attackerName = attackerName;
        this.defenderName = defenderName;
        this.graphName = graphName;
        try {
        net = Parser.parseGraph(defenderName + "-" + graphName + ".graph");
        visibleNet = Parser.parseGraph(defenderName + "-" + graphName + "-hidden.graph");
        availableNodes = visibleNet.getAvailableNodes();

        //clears history and adds the public nodes to the history
        history = new PrintWriter(new FileWriter(attackerName + "-" + defenderName + "-" + graphName + ".history", false));
        ArrayList<CardPickup.Node> publicNodes = visibleNet.getCapturedNodes();
        //System.out.println("PUBLIC NODES: " + publicNodes.size());
        for(int i = 0; i < publicNodes.size(); i++){
            String publicString = "6," + publicNodes.get(i).getNodeID()+",";
            ArrayList<CardPickup.Node> neighbors = publicNodes.get(i).getNeighborList();
            for(int j = 0; j < neighbors.size(); j++)
                publicString += neighbors.get(j).getNodeID() + ",";
                publicString = publicString.substring(0, publicString.length() - 1);
                history.println(publicString);
            }
            history.close();
        } catch (IOException e) {e.printStackTrace();}
        history.close();
    }

    /**
     * Parses attacker's move from .attack file
     */
    public void readMove(){
		try{
			visibleNet = Parser.parseAttackerHistory(attackerName, defenderName, graphName);
			history = new PrintWriter(new FileWriter(attackerName + "-" + defenderName + "-" + graphName + ".history", true));
			File csv = new File(attackerName + "-" + defenderName + "-" + graphName + ".attack");
			CSVParser parser = CSVParser.parse(csv, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			
			for(CSVRecord csvRecord : parser){
				Iterator<String> itr = csvRecord.iterator();
                int mode = Integer.parseInt(itr.next());
                int id;
                String neighbors = ",";
                switch (mode){
                	case 0://attack node
                    id = Integer.parseInt(itr.next());
                    if(isAvailableNode(id) && isValidAttack(id))
                    {
                        budget -= Parameters.ATTACK_RATE;
                        CardPickup.Node n = net.getNode(id);
                        int attackRoll = r.nextInt(Parameters.ATTACK_ROLL) + 1; 
                        if(attackRoll >= n.getSv()){
                        	visibleNet.getNode(id).setPv(n.getPv());
                        	visibleNet.getNode(id).setSv(n.getSv());
                        	visibleNet.getNode(id).setHoneyPot(n.getHoneyPot());
                        	visibleNet.getNode(id).setCaptured(true);
                        	if(visibleNet.getNode(id).isHoneyPot())
                        		points += Parameters.HONEY_PENALTY;
                        	else{
                        		points += visibleNet.getNode(id).getPv();
                        		for(int j = 0; j < n.neighbor.size(); j++){
                            		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                            		neighbors += n.neighbor.get(j).getNodeID() + ",";
                            	}
                        	}
                        	neighbors = neighbors.substring(0, neighbors.length()-1);
                        	//System.out.println("attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                        	history.println("0," + id + ",true," + attackRoll + "," + n.getPv() + "," + n.getSv() + "," + n.getHoneyPot() + neighbors);
                        }else{
                        	//System.out.println("attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                        	history.println("0," + id + ",false," + attackRoll);
                        }
                    }
                    else{
                    	//System.out.println("Invalid attack on node "+ id + "! " + isAvailableNode(id));
                    	history.println("-1");
                        budget -= Parameters.INVALID_RATE;
                    }
                    break;
                	case 1://super attack node
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidSuperAttack(id))
                        {
                            budget -= Parameters.SUPERATTACK_RATE;
                            CardPickup.Node n = net.getNode(id);
                            
                            int attackRoll = r.nextInt(Parameters.SUPERATTACK_ROLL) + 1; 
                            if(attackRoll >= n.getSv()){
                            	visibleNet.getNode(id).setPv(n.getPv());
                            	visibleNet.getNode(id).setSv(n.getSv());
                            	visibleNet.getNode(id).setHoneyPot(n.getHoneyPot());
                            	visibleNet.getNode(id).setCaptured(true);
                            	if(visibleNet.getNode(id).isHoneyPot())
                            		points += Parameters.HONEY_PENALTY;
                            	else{
                            		points += visibleNet.getNode(id).getPv();
                            		for(int j = 0; j < n.neighbor.size(); j++){
                                		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(n.neighbor.get(j).getNodeID()));
                                		neighbors += n.neighbor.get(j).getNodeID() + ",";
                                	}
                            	}
                            	neighbors = neighbors.substring(0, neighbors.length()-1);
                            	//System.out.println("super attack on node " + id + " was successful with a roll of " + attackRoll + "!");
                            	history.println("1," + id + ",true," + attackRoll + "," + n.getPv() + "," + n.getSv() + "," + n.getHoneyPot() + neighbors);
                            }else{
                            	//System.out.println("super attack on node " + id + " was unsuccessful with a roll of " + attackRoll);
                            	history.println("1," + id + ",false," + attackRoll);
                            }
                        }
                        else{
                        	//System.out.println("Invalid superattack on node "+ id + "! " + isAvailableNode(id));
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 2://probe security value
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeSV(id))
                        {
                            budget -= Parameters.PROBE_SECURITY_RATE;
                            CardPickup.Node n = net.getNode(id);
                            
                            int sv = n.getSv();
                            visibleNet.getNode(id).setSv(sv);
                            //System.out.println("probed node " + id + "'s security value: " + sv);
                            history.println("2," + id + "," + sv);
                        }
                        else{
                        	//System.out.println("Invalid probing of security value on node "+ id + "! " + isAvailableNode(id));
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 3://probe point value
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbePV(id))
                        {
                            budget -= Parameters.PROBE_POINT_RATE;
                            CardPickup.Node n = net.getNode(id);
                            
                            int pv = n.getPv();
                            visibleNet.getNode(id).setPv(n.getPv());
                            //System.out.println("probed node " + id + "'s point value: " + pv);
                            history.println("3," + id + "," + pv);
                        }
                        else{
                        	//System.out.println("Invalid probing of point value on node "+ id + "! " + isAvailableNode(id));
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 4://probe connections
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeConn(id))
                        {
                            budget -= Parameters.PROBE_CONNECTIONS_RATE;
                            CardPickup.Node n = net.getNode(id);
                            
                            int[] nodes = new int[n.neighbor.size()];
                            for(int j = 0; j < n.neighbor.size(); j++){
                        		nodes[j] = n.neighbor.get(j).getNodeID();
                        		visibleNet.getNode(id).addNeighbor(visibleNet.getNode(nodes[j]));
                            }
                            //System.out.println("probed node " + id + "'s connections: " + n.neighbor.size());
                            history.println("4," + id + "," + n.neighbor.size());
                        }
                        else{
                        	//System.out.println("Invalid probing of connections on node "+ id + "! " + isAvailableNode(id));
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	case 5://probe honey pot
                        id = Integer.parseInt(itr.next());
                        if(isAvailableNode(id) && isValidProbeHP(id))
                        {
                            budget -= Parameters.PROBE_HONEY_RATE;
                            CardPickup.Node n = net.getNode(id);
                            
                            visibleNet.getNode(id).setHoneyPot(n.isHoneyPot());
                            //System.out.println("probed node " + id + "'s honey pot: " + n.isHoneyPot());
                            history.println("5," + id + "," + n.getHoneyPot());
                        }
                        else{
                        	//System.out.println("Invalid probing of honey pot on node "+ id + "! " + isAvailableNode(id));
                        	history.println("-1");
                            budget -= Parameters.INVALID_RATE;
                        }
                        break;
                	default://some other case not defined
                		//System.out.println("Invalid Move!");
                		history.println("-1");
                        budget -= Parameters.INVALID_RATE;
                        break;
                }
            }
            parser.close();
            history.close();
            availableNodes = visibleNet.getAvailableNodes();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Attacker should call this method when done adding actions.
     */
    public void close()
    {
        history.close();
    }

    /**
     * Returns current budget.
     * @return current budget
     */
    public int getBudget()
    {
        return budget;
    }

    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
     */
    public boolean isValidAttack(int id){
        if(budget < Parameters.ATTACK_RATE)
            return false;
        return true;
    }

    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
    */
    public boolean isValidSuperAttack(int id){
        if(budget < Parameters.SUPERATTACK_RATE)
            return false;
        return true;
    }

    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
    */
    public boolean isValidProbeSV(int id){
        if(budget < Parameters.PROBE_SECURITY_RATE)
            return false;
        return true;
    }
    
    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
     */
    public boolean isValidProbePV(int id){
        if(budget < Parameters.PROBE_POINT_RATE)
            return false;
        return true;
    }

    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
     */
    public boolean isValidProbeConn(int id){
        if(budget < Parameters.PROBE_CONNECTIONS_RATE)
            return false;
        return true;
    }

    /**
     * Actually doesn't check if move is valid but rather if there is enough budget for this move
     * Ping Marcus to fix this
     * @param id node id
     * @return if there is enough budget for this move
    */
    public boolean isValidProbeHP(int id){
        if(budget < Parameters.PROBE_HONEY_RATE)
            return false;
        return true;
    }

    /**
     * Checks if node is available based on id
     * @param id ID of node you want to check
     * @return if node is available
     */
    private boolean isAvailableNode(int id){
    	CardPickup.Node n = new CardPickup.Node(id);
    	return availableNodes.contains(n);
    }

    /**
     * Gets total points
     * @return total points
     */
    public int getPoints()
    {
        return points;
    }
}
