package CardPickup;

/**
 * Contains move type and node ID of an attacker action
 * 
 * @author Marcus Gutierrez
 */
public class AttackerAction {

	public AttackerActionType move;
	public int nodeID;
	
	public AttackerAction(AttackerActionType move, int nodeID){
		this.move = move;
		this.nodeID = nodeID;
	}
	
}
