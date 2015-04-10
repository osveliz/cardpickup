package CardPickup;

/**
 * Defender agent. The actions for the defender in this game include strengthening nodes, adding firewalls, and adding honeypots.
 * All logic/equations/formulas/etc for how your defender decides to select actions should be included in run()
 */
public abstract class Defender implements Runnable
{
    protected Graph net;
    protected DefenderHelper dh;
    protected String defenderName;
    protected String graph;
    private volatile boolean isAlive = true;
    /**
     * Constructor.
     * Parses Network stored in graphFile.
     * Performs Defender logic to select actions.
     * Outputs [agentName]-[graphFile].defense with selected actions
     * @param agentName Defender agent's name i.e. "Miners"
     * @param graphFile String containing number of network i.e. "1914"
     */
    public Defender(String agentName, String graphFile)
    {
        defenderName = agentName;
        graph = graphFile;
        net = Parser.parseGraph(graphFile+".graph");
        dh = new DefenderHelper(net,graphFile, agentName);
    }

    /**
     * Defender selects to strengthen a node by 1 sv.
     * Only works on non-public nodes that are not already at maximum strength (20).
     *
     * Recommend using isValidStrengthen() in DefenderHelper before committing to this action
     *
     * @param id Node's ID number
     */
    public final void strengthen(int id)
    {
        dh.strengthen(id);
    }

    /**
     * Defender selects to remove the edge between two nodes (firewalling them).
     * Only works when doing so would not create island nodes and if there actually is an edge between them.
     *
     * Recommend using isValidFirewall() in DefenderHelper before committing to this action.
     *
     * @param id1 ID number of first node
     * @param id2 ID number of second node
     */
    public final void firewall(int id1, int id2)
    {
        dh.firewall(id1,id2);
    }

    /**
     * Defender selects to add a honeypot and gets to pick the Security Value, Point Value, and neighboring connected nodes.
     * Attacker will not get the Point Value for this node after a successful attack.
     * Security Value cannot be 0 (reserved for public nodes) and not greater than 20 (maximum). Points cannot be more than 20.
     * Cannot connect to nodes not in graph.
     *
     * Recommend using isValidHoneypot() in DefenderHelper before committing to this action.
     *
     * @param sv Desired Security Value should be > 0 and <= 20
     * @param pv Desired Point Value should be >= 0 and <= 20
     * @param newNeighbors array of Node IDs to connect this honeypot to.
     */
    public final void honeypot(int sv, int pv, int[] newNeighbors)
    {
        dh.honeypot(sv,pv,newNeighbors);
    }

    /**
     * Calls makeMoves()
     */
    public final void run()
    {
        while(isAlive){
            try{
            makeMoves();
            }catch (Exception e){
                System.out.println("Error with "+defenderName);
                e.printStackTrace();
            }
            isAlive = false;
        }
        dh.close();
    }

    /**
     * Get Agent Name used by GameMaster.
     * @return Name of defender
     */
    public final String getName()
    {
        return defenderName;
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

    /**
     * Defender logic goes here
     */
    public abstract void makeMoves();

}
