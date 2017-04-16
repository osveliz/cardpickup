package CardPickup;

/**
 * Player agent.
 *
 * NOTE TO STUDENTS: The game master will only tell the player the results of your and your opponents actions.
 * It will not update your graph for you. That is something we left you to do so that you can update your
 * graphs, opponent hand, horoscope, etc. intelligently and however you like.
 *
 * @author Marcus Gutierrez
 * @version 04/15/2015
 */
public abstract class Player
{
    protected String playerName = "defaultPlayer"; //Overwrite this variable in your player subclass
    private Action lastAction;
    
    protected Node[] graph;
    protected Hand hand;
    protected int turnsRemaining;
    protected int currentNode;
    protected int oppNode; /**Opponent's current position*/
    protected Card oppLastCard;	/**Opponent's last picked up card*/
    
    /**
     * Default Constructor
     */
    public Player()
    {
        turnsRemaining = Parameters.NUM_TURNS;
    }

    /**
     * There is code in GameMaster which will keep track of legal moves.
     * Trust it to call this function to update your location if your movie is legal.
     * @param newNode id of a neighboring node or current node.
     */
    public void setCurrentNode(int newNode){
        currentNode = newNode;
    }
    
    /**
     * Sets the opponent's current location
     * @param newNode location of opponent
     */
    public void setOpponentNode(int newNode){
        oppNode = newNode;
    }
    
    /**
     * returns your current location
     * @return current location
     */
    public int getCurrentNode(){ return currentNode; }
    
    /**
     * Sets your current Hand. This does not change your actual hand, just your copy of it.
     * The GameMaster keeps track of your actual hand.
     * @param h hand
     */
    public void setHand(Hand h){
    	hand = h;
    }
    
    /**
     * Imports a new graph
     * @param g graph
     */
    public void setGraph(Node[] g){
    	graph = g;
    }
    
    /**
     * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of the opponent's moves. 
     * GameMaster will call this to update the player on the opponent's actions.
     * 
     * @param opponentNode Opponent's current location
     * @param opponentPickedUp Notifies if the opponent picked up a card last turn
     * @param c The card that the opponent picked up, if any (null if the opponent did not pick up a card)
     */
    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
    	oppNode = opponentNode;
    	if(opponentPickedUp)
    		oppLastCard = c;
    	else
    		oppLastCard = null;
    }

    /**
     * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of your results.
     * GameMaster will call this to update you on your actions.
     *
     * @param currentNode Opponent's current location
     * @param c The card that you picked up, if any (null if you did not pick up a card)
     */
    protected void actionResult(int currentNode, Card c){
        this.currentNode = currentNode;
        if(c!=null)
            addCardToHand(c);
    }


    /**
     * There is code in GameMaster which will keep track of legal moves.
     * Trust it to call this function if your movie is legal.
     * @param card Name of card to add - ex "Six of Hearts"
     */
    public void addCardToHand(Card card){
        hand.addHoleCard(card);
    }
    
    /**
     * GameMaster will use this method to determine if a player has finished their moves
     * @return size of hand
     */
    public int getHandSize(){
    	return hand.getNumHole();
    }

    /**Allows players to make any initial computations*/
    public abstract void initialize();

    /**
     * This method forces the subclass player to make a single action (pickup or move a card).
     * Returning a null/invalid action will result in a wasted turn.
     */
    public void handleAction(){
    	lastAction = null;
        Action a = makeAction();
        if(a != null){
            switch(a.move){
            case MOVE:
            	if(isValidAction(a)){
            		move(a.nodeID);
            		lastAction = a;
            	}
            	break;
            case PICKUP:
            	if(isValidAction(a)){
            		pickup(a.nodeID);
            		lastAction = new Action(a.move, a.nodeID);
            	}
            	break;
            default:
            	lastAction = null; //indicates that the player has made an erroneous move
            	break;
            }
        }
    }


    /**
     * Get Agent Name used by GameMaster.
     * @return Name of player
     */
    public String getName(){return playerName;}

    /**
     * Player logic goes here in extended super
     * @return the action
     */
    public abstract Action makeAction();
    
    /**
     * GameMaster uses this method to determine the last action of the player
     * @return last action the player has made
     */
    public Action getLastAction(){
    	return lastAction;
    }

    /**
     * Sets the current node to the target node
     * @param targetID node id of the target node
     */
    private void move(int targetID){
        currentNode = targetID;
    }

    /**
     * Sets the current Node to the target node
     * @param targetID node id of the target node
     */
    private void pickup(int targetID){
        currentNode = targetID;
    }

    /**
     * Method that is used to determine if a move is valid. This method should be used to help players
     * determine if their actions are valid. GameMaster has a local copy of this method and all the
     * required variables (such as the true graph), so manipulating the variables to turn a previously
     * invalid action in to a "valid" one will not help you as the GameMaster will still see the action 
     * as invalid.
     * 
     * @param a A proposed action from your current position in the given graph
     * @return True if the action is valid, false otherwise
     */
    protected boolean isValidAction(Action a){
		if(currentNode == a.nodeID)
			return true;
		for(int i = 0; i < graph[currentNode].getNeighborAmount(); i++){
			if(graph[currentNode].getNeighbor(i).getNodeID() == a.nodeID)
				return true;
		}
		return false;
	}
}
