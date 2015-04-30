package CardPickup;

public class PlayerDriver implements Runnable {
	
	/**Used to know which Player method to call*/
	public final PlayerState state;
	/**Used to know which Player subclass to communicate with*/
	private Player player;
	
	/**Various variables needed to call the Player's methods*/
	private int playerNode;
	private int opponentNode;
	private boolean opponentPickedUp;
	private Card c;

	/**
	 * Constructor used for Player's initialize() and makeAction() methods
	 * @param state
	 * @param player
	 */
	public PlayerDriver(PlayerState state, Player player){
		this.state = state;
		this.player = player;
	}
	
	/**
	 * Constructor used for the Player's actionResult() method
	 * @param state
	 * @param player
	 * @param playerNode
	 * @param c
	 */
	public PlayerDriver(PlayerState state, Player player, int playerNode, Card c){
		this.state = state;
		this.player = player;
		this.playerNode = playerNode;
		this.c = c;
	}
	
	/**
	 * Constructor used for the Player's opponentAction() method
	 * @param state
	 * @param player
	 * @param opponentNode
	 * @param opponentPickedUp
	 * @param c
	 */
	public PlayerDriver(PlayerState state, Player player, int opponentNode, boolean opponentPickedUp, Card c){
		this.state = state;
		this.player = player;
		this.opponentNode = opponentNode;
		this.opponentPickedUp = opponentPickedUp;
		this.c = c;
	}
	
	/**
	 * GameMaster will create a thread to run this class that will call a Player subclass'
	 * methods. Any exceptions or time outs will only harm this thread and will not affect GameMaster
	 */
	public void run() {
		try{
			switch(state){
			case INIT:
				player.initialize();
				break;
			case RESULT:
				player.actionResult(playerNode, c);
				break;
			case OPP_RESULT:
				player.opponentAction(opponentNode, opponentPickedUp, c);
				break;
			case MAKE_ACTION:
				player.handleAction();
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
