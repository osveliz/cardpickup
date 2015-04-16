package CardPickup;

/**
 * Auxiliary class for storing information about the game.
 * @author Marcus Gutierrez
 */
public class GameProfile {

	private int ID; //Game ID in case multiple games of the same match up happen (This number should be unique)
	private HandEvaluator hEval;
	private int gameSeed;
	private Graph g;
	private PlayerProfile p1; //first player
	private PlayerProfile p2; //second player
	private int turns;
	private String winner;
	
	public GameProfile(Graph g, int gameSeed, PlayerProfile p1, PlayerProfile p2,int match){
        ID = gameSeed*2 + match;
		hEval = new HandEvaluator();
		this.gameSeed = gameSeed;
		this.g = g;
		this.p1 = p1;
		this.p2 = p2;
		turns = 0;
		winner = null;
	}
	
	public void oneRound(){
		turns++;
	}
	
	public String getWinner(){
		return winner;
	}
	
	//determines winner at the end of the game
	public void endGame(){
		float p1Score = hEval.rankHand(p1.getCurrentHand());
		float p2Score = hEval.rankHand(p2.getCurrentHand());
		if(p1Score > p2Score)
			winner = p1.getName();
		else if (p2Score > p1Score)
			winner = p2.getName();
		else
			winner = "Draw";
	}
	
	//Don't forget to include Parameters as well!
	public void writeResults(){
		
	}
}