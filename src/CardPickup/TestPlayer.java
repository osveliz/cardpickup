package CardPickup;

import java.util.Random;

/**
 * Some important variables inherited from the Player Class:
 * protected Node[] graph; //Contains the entire graph
 * protected Hand hand; //Contains your current hand (Use the cardsHole array list)
 * protected int turnsRemaining; //Number of turns before the game ends
 * protected int currentNode; //Your current location
 * protected int oppNode; //Opponent's current position
 * protected Card oppLastCard;	//Opponent's last picked up card
 * 
 * Important methods inherited from Player Class:
 * Method that is used to determine if a move is valid. This method should be used to help players
 * determine if their actions are valid. GameMaster has a local copy of this method and all the
 * required variables (such as the true graph), so manipulating the variables to turn a previously
 * invalid action in to a "valid" one will not help you as the GameMaster will still see the action 
 * as invalid.
 * protected boolean isValidAction(Action a); //This method can be used to determine if an action is valid
 *
 * NOTE TO STUDENTS: The game master will only tell the player the results of your and your opponents actions.
 * It will not update your graph for you. That is something we left you to do so that you can update your
 * graphs, opponent hand, horoscope, etc. intelligently and however you like.

 * 
 * @author Marcus Gutierrez
 * @version 04/15/2015
 */
public class TestPlayer extends Player{
	protected final String newName = "TestPlayer"; //Overwrite this variable in your player subclass

	/**Do not alter this constructor as nothing has been initialized yet. Please use initialize() instead*/
	public TestPlayer() {
		super();
        playerName = newName;
	}
	
	public void initialize() {
		//WRITE ANY INITIALIZATION COMPUTATIONS HERE
	}
	
	/**
     * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of the opponent's moves. 
     * GameMaster will call this to update your player on the opponent's actions. This method is called
     * after the opponent has made a move.
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
     * Player logic goes here
     */
	public Action makeAction() {
		/*if(hand.size()==5)
			return new Action();//end
		Random r = new Random();
        int neighbor;
        if (nodes[currentNode].getNeighborAmount()==1)
            neighbor = nodes[currentNode].getNeighbor(0).getNodeID();
        else
		    neighbor = nodes[currentNode].getNeighbor(r.nextInt(nodes[currentNode].getNeighborAmount())).getNodeID();
		return new Action(ActionType.PICKUP, neighbor);*/
		return new Action(ActionType.END, -1);
	}

}
