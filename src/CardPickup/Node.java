package CardPickup;

import java.util.ArrayList;
import java.util.Random;

/**
 * Node class is used for creating nodes for the network.
 * Important variables information for Attacker agents:
 * 
 * nodeId - integer representing the node
 * sv - security value of a node (-1 means unknown)
 * pv - point value of a node (-1 means unknown)
 * isHoneyPot - boolean for if the node is a honeypot (-1 means unknown, 0 means false, 1 means true)
 * captured - if the node is a public entry node or has been successfully captured via attack
 * bestRoll - the highest roll on this node (if -1, the node has never been attacked), if the security value is -1, bestRoll may be used to reason what the sv might be (actual security value >= bestRoll if sv == -1)
 * neighborAmount - number of connections this node has. Will be different than neighbor.size() if the node has not been captured. (-1 means unknown)
 * neighbor - list of neighbors to this node. Will be empty if the node has not been captured
 *
 * @author      Porag Chowdhury, Anjon Basak, Marcus Gutierrez
 * @version     11/14/2014
 */

public class Node 
{
	private int nodeID;
	private int neighborAmount = -1;
	public ArrayList<Node> neighbor = new ArrayList<Node>();
    private Card trueCard;
    private ArrayList<Card> possibleCards;
    
	/**
     * Empty Constructor.
     */
	public Node(){
		possibleCards = new ArrayList<Card>();
	}
	
	/**
     * used for comparison purposes.
     */
	public Node(int id){
		nodeID = id;
		possibleCards = new ArrayList<Card>();
	}

    /**
     * Constructor for adding a single card
     * @param id node's id
     * @param c node's card
     */
    public Node(int id, Card c){
        nodeID = id;
        trueCard = c;
        possibleCards = new ArrayList<Card>();
    }

    /**
     * Constructor for adding possible cards
     * @param id
     * @param cards
     */
    public Node(int id, ArrayList<Card> cards){
        nodeID = id;
        if(cards.size()>0) {
            trueCard = cards.get(0);
            possibleCards = cards;
        }
        else{
            trueCard = new Card();
            possibleCards = new ArrayList<Card>();
        }
    }

	/**
     * Returns the nodeId
     * @return the nodeId
     */
	public int getNodeID()
	{
		return nodeID;
	}

	/**
     * Sets the nodeId
     */
	public void setNodeID(int nodeID)
	{
		this.nodeID = nodeID;
	}
	
	/**
	 * Returns neighborAmount
	 */
	public int getNeighborAmount(){
		return neighborAmount;
	}
	
	/**
	 * sets this.neighborAmount attribute
	 * @param amt new neighbor amount
	 */
	public void setNeighborAmount(int amt){
		neighborAmount = amt;
	}
	
	/**
     * Add Neighbor to the current node
     * @param neighborNode neighbor node which will the added as a neighbor to the current node
     */
	public void addNeighbor(Node neighborNode)
	{
		if(!neighbor.contains(neighborNode)){
			neighborAmount++;
			this.neighbor.add(neighborNode);
		}
	}

	/**
     * Returns the neighbor
     * @return Node of idx
     */
	public Node getNeighbor(int idx)
	{
		return neighbor.get(idx);
	}
	
	/**
     * Returns the neighbor
     * @return Node of idx
     */
	public ArrayList<Node> getNeighborList()
	{
		return neighbor;
	}
	
	/**
	 * Overridden equals method that just compares NodeID
	 */
	public final boolean equals(Object o){
		Node n = (Node)o;
		if(n.getNodeID() == nodeID)
			return true;
		return false;
	}

    public void setCard(Card c)
    {
        trueCard = c;
        addPossible(c);
    }

    public Card getCard(){
        return new Card(trueCard.toString());
    }

    public ArrayList<Card> getPossibleCards(){
        return possibleCards;
    }
    
    public void clearPossibleCards(){
    	possibleCards = new ArrayList<Card>(); //resets size to 0
    }
    
    public Node clone(){
    	Node n = new Node(nodeID, new Card(trueCard.toString()));
    	for(int i = 0; i < possibleCards.size(); i++)
    		n.addPossible(new Card(possibleCards.get(i).toString()));
    	return n;
    }
    
    /**
     * This method will be used to show the players a node without revealing the true card
     * @return
     */
    public Node getHiddenNode(){
    	Node n = new Node(nodeID);
    	for(int i = 0; i < possibleCards.size(); i++)
    		n.addPossible(new Card(possibleCards.get(i).toString()));
    	return n;
    }

    public void addPossible(Card c) {
        if(possibleCards == null)
            possibleCards = new ArrayList<Card>();
        possibleCards.add(new Card(c.toString()));
    }

    public void shufflePossibleCards(){
        Random r = new Random();
        for(int i = 0; i < 100; i++){
            int index = r.nextInt(possibleCards.size());
            possibleCards.add(possibleCards.remove(index));
        }
    }

}