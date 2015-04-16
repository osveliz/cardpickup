package CardPickup;

/**
 * Contains move type and node ID of an attacker action
 * 
 * @author Marcus Gutierrez
 */
public class Action {

	public ActionType move;
	public int nodeID;
	
	public Action(ActionType move, int nodeID){
		this.move = move;
		this.nodeID = nodeID;
	}
	
	public String toString(){
		return move.toString()+ " " + nodeID;
	}
	
}
