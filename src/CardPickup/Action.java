package CardPickup;

/**
 * Contains move type and node ID of an attacker action
 * 
 * @author Marcus Gutierrez
 */
public class Action {

	public ActionType move;
	public int nodeID;
	public Card card;
	
	/**
	 * End Action
	 */
	public Action(){
		move = ActionType.END;
		nodeID = -1;
	}

    /**
     *
     * @param move either pickup or move
     * @param nodeID target node id
     */
	public Action(ActionType move, int nodeID){
		this.move = move;
		this.nodeID = nodeID;
	}
	/**
	 * Burn a card - costs 5 from budget
	 * @param card card you want to remove
	 */
	public Action(Card card){
		this.move = ActionType.BURN;
		this.card = card;
	}

    /**
     * Basic toString()
     * @return action with node id as a string
     */
	public String toString(){
		return move.toString()+ " " + nodeID;
	}
	
}
