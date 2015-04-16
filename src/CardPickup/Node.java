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
     * @param id node id
     * @param cards possible cards
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
        return neighbor.size();
    }
	
	/**
     * Add Neighbor to the current node
     * @param neighborNode neighbor node which will the added as a neighbor to the current node
     */
	public void addNeighbor(Node neighborNode)
	{
		if(!neighbor.contains(neighborNode)){
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
     * @param o Checks if two id's are the same when o is a Node type
	 */
	public final boolean equals(Node o){
        return o.getNodeID() == nodeID;
    }

    /**
     * Stores card c as the true card. Adds c to the set of possible cards.
     * @param c card to set
     */
    public void setCard(Card c)
    {
        trueCard = c;
        addPossible(c);
    }

    /**
     * Returns the true card. Not useful if you're not supposed to know what the card is.
     * @return the true card if you're in the know
     */
    public Card getCard(){
        return new Card(trueCard.toString());
    }

    /**
     * Returns the set of possible cards
     * @return the set of possible cards
     */
    public ArrayList<Card> getPossibleCards(){
        return possibleCards;
    }

    /**
     * Sets the set of possible cards to null
     */
    public void clearPossibleCards(){
    	possibleCards = new ArrayList<Card>(); //resets size to 0
    }

    /**
     * A version of clone but named so your IDE doesn't complain about it not being a traditional clone method.
     * @return a clone of the Node
     */
    public Node copyNode(){
    	Node n = new Node(nodeID, new Card(trueCard.toString()));
        for (Card possibleCard : possibleCards)
            n.addPossible(new Card(possibleCard.toString()));
    	return n;
    }
    
    /**
     * This method will be used to show the players a node without revealing the true card
     * @return a new node with just the possible cards
     */
    public Node getHiddenNode(){
    	Node n = new Node(nodeID);
        for (Card possibleCard : possibleCards)
            n.addPossible(new Card(possibleCard.toString()));
    	return n;
    }

    /**
     * Adds card c to the set of possible cards
     * @param c the card to be added to the set of possible cards
     */
    public void addPossible(Card c) {
        if(possibleCards == null)
            possibleCards = new ArrayList<Card>();
        possibleCards.add(new Card(c.toString()));
    }

    /**
     * Method that will shuffle the possible cards otherwise the true card would always be the first one
     */
    public void shufflePossibleCards(){
        Random r = new Random();
        for(int i = 0; i < 100; i++){
            int index = r.nextInt(possibleCards.size());
            possibleCards.add(possibleCards.remove(index));
        }
    }

}