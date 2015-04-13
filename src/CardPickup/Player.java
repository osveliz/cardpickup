package CardPickup;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Attacker agent. The actions for the attacker in this game include attacking a node, super attacking a node, 
 * probing for security values of a node, probing for the point value of a node, probing number of connections, and probing for honey pots.
 * All logic/equations/formulas/etc for how your attacker decides to select actions should be included in run()
 * @author Marcus Gutierrez
 * @version 2014/11/14
 */
public abstract class Player implements Runnable
{
    private String playerName = "defaultPlayer"; //Overwrite this variable in your attacker subclass
    private int playerNumber;
    private Graph graph;
    private Hand hand;
    private int turns;
    private volatile boolean isAlive = true;
    private int currentNode;
    /**
     * Constructor.
     * Parses Network stored in graphFile.
     * Performs Attacker logic to select actions.
     * Outputs [agentName]-[graphFile].attack with selected actions
     * @param name Attacker agent's name i.e. "Sharks"
     * @param number Player number
     * @param g String containing number of visibility network i.e. "1914"
     */
    public Player(String name, int number , Graph g)
    {
        playerName = name;
        playerNumber = number;
        graph = g;
        turns = Parameters.NUM_TURNS;
        initialize();
    }

    public Player(String name){
    	playerName = name;
    }

    /**
     * There is code in GameMaster which will keep track of legal moves.
     * Trust it to call this function to update your location if your movie is legal.
     * @param newNode id of a neighboring node or current node.
     */
    private void setCurrentNode(int newNode){
        currentNode = newNode;
    }

    /**
     * There is code in GameMaster which will keep track of legal moves.
     * Trust it to call this function if your movie is legal.
     * @param card Name of card to add - ex "Six of Hearts"
     */
    private void addCardToHand(String card){
        hand.addHoleCard(new Card(card));
    }

    protected abstract void initialize();


    /**
     * Executes one action for the attacker
     */
    public final void run()
    {
        while(isAlive){
            try{
                makeDecision();
            }catch (Exception e){
                System.out.println("Error with "+playerName);
                e.printStackTrace();
            }
            isAlive = false;
        }
    }


    /**
     * Get Agent Name used by GameMaster.
     * @return Name of defender
     */
    public String getName()
    {
        return playerName;
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

    /**
     * Player logic goes here in extended super
     */
    public abstract void makeDecision();

    private final void doNothing(int targetID){
        PrintWriter writer;
        try {
            writer = new PrintWriter(playerName+".action", "UTF-8");
            writer.println("0");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final void move(int targetID){
        PrintWriter writer;
        try {
            writer = new PrintWriter(playerName+".action", "UTF-8");
            writer.println("1,"+targetID);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final void pickup(int targetID){
        if(isValidPickup(targetID)){
            PrintWriter writer;
            try {
                writer = new PrintWriter(playerName+".action", "UTF-8");
                writer.println("2,"+targetID);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private final boolean isValidPickup(int targetID){
        if(isValidMove(targetID) && graph.getNode(targetID).getPossibleCards().size()!=0)
            return true;
        return false;
    }

    private final boolean isValidMove(int targetID){
        if(targetID==currentNode)
            return true;
        ArrayList<Node> n = graph.getNode(currentNode).getNeighborList();
        for(int i = 0; i < n.size(); i++)
            if(targetID == n.get(i).getNodeID())
                return true;
        return false;
    }
}
