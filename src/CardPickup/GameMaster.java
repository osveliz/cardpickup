package CardPickup;

import java.util.ArrayList;

/**
 * Play a graph variant of Poker.
 * Add your agent to the list of agents in main() AND to the getPlayer() method
 * Note you can change the parameter settings and the number of games by altering numGames and parameterSetting
 * 
 * @author Marcus Gutierrez and Oscar Veliz
 * @version 2017/15/4
 */
public class GameMaster {

	private static boolean verbose = false; //Set to false if you do not want much detail printed to console
	private static int numGames = 25; //use a small number for quick tests, a large one to be comprehensive
	private static int parameterSetting = 1; //see changeParameters()
	
	/**
	 * You should edit this method to include your player agent
	 * @param name The name of your player agent
	 * @return An instance of your Player agent
	 */
	public static Player getPlayer(String name) {
		if (name.equalsIgnoreCase("TestPlayer"))
			return new TestPlayer();
		else if(name.equalsIgnoreCase("MaxPower"))
			return new MaxPower();
		else if(name.equalsIgnoreCase("HankScorpio"))
			return new HankScorpio();
		////////////////////////////////////
		//your player here
		////////////////////////////////////
        /*else if(name.equalsIgnoreCase("YOUR AGENT HERE")
		 * 	return new StudentAgent();
		 */
		// in case your name was not added
		return null;
	}
	/**
	 * Runs the tournament
	 * 
	 * @param args not using any command line arguments
	 */
	public static void main(String[] args) {
		changeParameters(parameterSetting);
		generateGraphs(numGames);

		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new TestPlayer());
		players.add(new MaxPower());
		players.add(new HankScorpio());
		////////////////////////////////////
		//your player here
		////////////////////////////////////

		float[] ranks = new float[players.size()];
		double[] wins = new double[players.size()];
		int numPlayers = players.size();
		for(int p1 = 0; p1 < numPlayers; p1++) {
			for(int p2 = p1+1; p2 < numPlayers; p2++) {
				if(p1!=p2) {//avoid playing against yourself
					for (int game = 0; game < numGames; game++) {
						Hand[] hands = runMatches(game, players.get(p1).getName(), players.get(p2).getName());
						if(verbose)System.out.println("Result for Game "+game);
						evaluateHands(hands[0], hands[1], ranks, wins, p1, p2, players);
						evaluateHands(hands[2],hands[3],ranks,wins,p1,p2,players);
						if(verbose)System.out.println();
					}
				}
			}
		}
		System.out.println("\nTotal Wins");
		for(int i = 0; i < wins.length; i++)
			System.out.println(players.get(i).getName()+" "+wins[i]);
		System.out.println("\nCumulative Hand Ranks");
		for(int i = 0; i < ranks.length; i++)
			System.out.println(players.get(i).getName()+" "+ranks[i]);
		System.exit(0);//just to make sure it exits
	}


	/**
	 * Tries to execute a Player class' method by using threads a layer of protection in case
	 * the Player subclasses crash or time out.
	 * 
	 * @param pDriver The thread that will ask the player to execute some code
	 */
	private static void tryPlayer(PlayerDriver pDriver){
		int timeLimit;
		if(pDriver.state == PlayerState.INIT)
			timeLimit = Parameters.INIT_TIME;
		else if(pDriver.state == PlayerState.RESULT)
			timeLimit = Parameters.RESULT_TIME;
		else if(pDriver.state == PlayerState.OPP_RESULT)
			timeLimit = Parameters.OPP_RESULT_TIME;
		else
			timeLimit = Parameters.ACTION_TIME;

		Thread playerThread = new Thread(pDriver);
		playerThread.start();
		for(int sleep = 0; sleep < timeLimit; sleep+=10){
			if(playerThread.isAlive())
				try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
			else
				return;
		}
	}

	/**
	 * Initializes each player and each player profile with all the relevant knowledge needed to start a game
	 * @param g copy of graph
	 * @param p1Profile profile for player 1
	 * @param p1 player 1
	 * @param p2Profile profile for player 2
	 * @param p2 player 2
	 * @param gameSeed seed for the game
	 */
	public static void initializePlayers(Graph g, PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2, int gameSeed){
		//Player 1
		p1.setGraph(Parser.parseGraph(gameSeed+".hidden").generateHiddenGraph());
		p1Profile.setCurrentHand(Parser.parseHand(gameSeed,1));//player 1
		p1Profile.setCurrentLocation(0);
		//Update the player 1
		p1.setHand(Parser.parseHand(gameSeed,1));
		p1.setCurrentNode(p1Profile.getCurrentLocation());

		//Player 2
		p2.setGraph(g.generateHiddenGraph());
		p2Profile.setCurrentHand(Parser.parseHand(gameSeed, 2));
		p2Profile.setCurrentLocation(1);
		//Update the player 2
		p2.setHand(Parser.parseHand(gameSeed, 2));
		p2.setCurrentNode(p2Profile.getCurrentLocation());


		//Notify players of their opponent
		p1.setOpponentNode(p2Profile.getCurrentLocation());	//subject to change
		p2.setOpponentNode(p1Profile.getCurrentLocation());	//subject to change

		//initialize players
		tryPlayer(new PlayerDriver(PlayerState.INIT, p1)); //Try to initialize player 1
		tryPlayer(new PlayerDriver(PlayerState.INIT, p2)); //Try to initialize player 1
	}

	/**
	 * Makes necessary changes to local version of the graph and notifies/updates the current and opponent player
	 * @param graph vector of nodes in the graph
	 * @param cpProfile profile for the current player
	 * @param currentPlayer current player
	 * @param opponent opposing player
	 */
	private static void registerTurn(Node[] graph, PlayerProfile cpProfile, Player currentPlayer, Player opponent){
		Action a = currentPlayer.getLastAction();
        if(a == null)//just in case the player couldn't get a move in.
            a = new Action(ActionType.MOVE,currentPlayer.getCurrentNode());
		if(isValidMove(graph, cpProfile.getCurrentLocation(), a.nodeID)){
			switch(a.move){
			case MOVE:
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
				tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, null)); //Try to notify player of result
				//opponent.opponentAction(a.nodeID, false, null);
				//currentPlayer.actionResult(a.nodeID,null);
				if(verbose)System.out.println(currentPlayer.getName() + " moved to node " + a.nodeID);
				break;
			case PICKUP:
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				//If the node's card has not been picked up yet
				if(graph[a.nodeID].getPossibleCards().size() > 0){
					//currentPlayer.addCardToHand(new Card(graph[a.nodeID].getCard().toString()));
					cpProfile.addCardToHand(new Card(graph[a.nodeID].getCard().toString()));
					tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, true, new Card(graph[a.nodeID].getCard().toString()))); //Try to notify opponent of result
					tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, new Card(graph[a.nodeID].getCard().toString()))); //Try to notify player of result
					//opponent.opponentAction(a.nodeID, true, new Card(graph[a.nodeID].getCard().toString()));
					//currentPlayer.actionResult(a.nodeID, new Card(graph[a.nodeID].getCard().toString()));
					graph[a.nodeID].clearPossibleCards(); //remove all possible cards
					if(verbose)System.out.println(currentPlayer.getName() + " picked up a " + graph[a.nodeID].getCard().toString() + " at node " + a.nodeID);
				}else{ //The node's card has already been picked up
					//opponent.opponentAction(a.nodeID, false, null);
					//currentPlayer.actionResult(a.nodeID,null);
					tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
					tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, null)); //Try to notify player of result
					if(verbose)System.out.println(currentPlayer.getName() + " attempted to pick up a card at node " + a.nodeID + ", but nothing was there");
				}
				break;
			default:
				tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
				tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, cpProfile.getCurrentLocation(), null)); //Try to notify player of result
				//opponent.opponentAction(a.nodeID, false, null);
				//currentPlayer.actionResult(cpProfile.getCurrentLocation(),null);
				if(verbose)System.out.println(currentPlayer.getName() + " performed an invalid action default case hit");
				break;
			}
		}else{
			tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
			tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, cpProfile.getCurrentLocation(), null)); //Try to notify player of result
			//opponent.opponentAction(cpProfile.getCurrentLocation(), false, null);
			//currentPlayer.actionResult(cpProfile.getCurrentLocation(),null);
			if(verbose)System.out.println(currentPlayer.getName() + " performed an invalid action");
		}
	}

	/**
	 * Checks if an attempted move is valid.
	 * 
	 * @param graph The adjacency list of the graph
	 * @param playerLocation The current player location
	 * @param playerDestination The attempted destination of the player
	 * @return Rather the move is valid or not
	 */
	private static boolean isValidMove(Node[] graph, int playerLocation, int playerDestination){
		if(playerLocation == playerDestination)
			return true;
		for(int i = 0; i < graph[playerLocation].getNeighborAmount(); i++){
			if(graph[playerLocation].getNeighbor(i).getNodeID() == playerDestination)
				return true;
		}
		return false;
	}

	/**
	 * Should only be called by runMatches() as that method has the necessary initializations to run this method
	 * It expects generateGraph() to have been called before running this method
	 * @param p1Profile game master's profile of p1
	 * @param p1 The player that will move first
	 * @param p2Profile game master's profile of p2
	 * @param p2 The player that will move second
	 * @param gameSeed game seed
	 */
	private static void oneRound(PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2, int gameSeed){
		Node[] graph = Parser.parseGraph(gameSeed+".graph").getNodes();
		Graph g = Parser.parseGraph(gameSeed+".graph");
		initializePlayers(g, p1Profile, p1, p2Profile, p2,gameSeed);
		boolean p1Finished;
		boolean p2Finished;
		//Runs until both players have a full hand or are out of turns
		for(int i = 0; i < Parameters.NUM_TURNS; i++){
			//Checks if player 1 is finished, if not, has him/her make one move, then registers the turn the gamemaster and both players
			if(p1Profile.getHandSize() < Parameters.MAX_HAND){
				//oneTurn(p1);
				tryPlayer(new PlayerDriver(PlayerState.MAKE_ACTION, p1)); //Try to have player 1 make an action
				registerTurn(graph, p1Profile, p1, p2);
				p1Finished = false;
			} else
				p1Finished = true;

			//Checks if player 2 is finished, if not, has him/her make one move, then registers the turn the gamemaster and both players
			if(p2Profile.getHandSize() < Parameters.MAX_HAND){
				//oneTurn(p2);
				tryPlayer(new PlayerDriver(PlayerState.MAKE_ACTION, p2)); //Try to have player 1 make an action
				registerTurn(graph, p2Profile, p2, p1);
				p2Finished = false;
			} else
				p2Finished = true;

			if(verbose)System.out.println("Player 1's Hand: " + p1Profile.getCurrentHand());
			if(verbose)System.out.println("Player 2's Hand: " + p2Profile.getCurrentHand());
			if(verbose)System.out.println("Round " + i + " finished");
			if(verbose)System.out.println();

			if(p1Finished && p2Finished)
				break;
		}
	}

	/**
	 * Runs a specified number of matches on the same graph against two players. One match is
	 * considered 2 rounds where the second round reverses the locations and hands of the 2 players.

	 * @param gameSeed number representing the seed used to generate the graph
	 * @param p1Name The player that will act first in the first round of a match (will act as player 2 in the second round)
	 * @param p2Name The player that will act second in the first round of a match (will act as player 1 in the first round)
	 * @return the hands
	 */
	public static Hand[] runMatches(int gameSeed, String p1Name, String p2Name){
		Hand[] hands = new Hand[4];//4 hands p1 & p2 vs p2 and p1
		if(getPlayer(p1Name) == null || getPlayer(p2Name) == null){//make sure players are valid
			System.out.println("ERROR: CHECK THAT PLAYER NAMES ARE VALID");
			System.out.println("Ensure that you altered GameMaster.getPlayer() properly to add your agent and that the names match");
			return null;
		}
		if(verbose)System.out.println("Game " + Integer.toString(gameSeed) + " starting...");

		//Game initialization
		Player p1 = getPlayer(p1Name);
		Player p2 = getPlayer(p2Name);

		//Profile initialization
		PlayerProfile p1Profile = new PlayerProfile(p1.getName());
		PlayerProfile p2Profile = new PlayerProfile(p2.getName());

		oneRound(p1Profile, p1, p2Profile, p2,gameSeed);
		hands[0] = p1Profile.getCurrentHand();
		hands[1] = p2Profile.getCurrentHand();
		/////////////////////////////////////////////////////////////////////////////
		//Exact same code as above, but reverses the roles of player 1 and player 2//
		/////////////////////////////////////////////////////////////////////////////
		//Game initialization
		p1 = getPlayer(p2Name);
		p2 = getPlayer(p1Name);

		//Profile initialization
		p1Profile = new PlayerProfile(p1.getName());
		p2Profile = new PlayerProfile(p2.getName());

		oneRound(p1Profile, p1, p2Profile, p2,gameSeed);
		hands[2] = p2Profile.getCurrentHand();
		hands[3] = p1Profile.getCurrentHand();
		return hands;
	}


	/**
	 * Function to determine which agent has the better hand.
	 * @param hand1 player 1's hand
	 * @param hand2 player 2's hand
	 * @param ranks array of ranks indexed by agent number
	 * @param wins array of wins indexed by agent number (a draw counts as .5)
	 * @param p1 index of player 1
	 * @param p2 index of player 2
	 * @param players array of players used to get their names
	 */

	private static void evaluateHands(Hand hand1, Hand hand2, float[]ranks, double[]wins,int p1, int p2, ArrayList<Player> players){
		HandEvaluator hEval = new HandEvaluator();
		if (hand1.size() != Parameters.MAX_HAND && hand2.size() != Parameters.MAX_HAND) {
			if(verbose)System.out.println("No player was able to get a full hand.");
		}
		else if (hand1.size() != Parameters.MAX_HAND) {//p2 made it to 5 but p1 didn't
			wins[p2]++;
			ranks[p2] += hEval.rankHand(hand2);
			if(verbose)System.out.println(players.get(p2).getName()+" wins by default with "+HandEvaluator.nameHand(hEval.rankHand(hand2)));
		} else if (hand2.size() != Parameters.MAX_HAND) {//p1 made it to 5 but p2 didn't
			wins[p1]++;
			ranks[p1] += hEval.rankHand(hand2);
			if(verbose)System.out.println(players.get(p1).getName()+" wins by default with "+HandEvaluator.nameHand(hEval.rankHand(hand1)));
		} else {//both players finished
			float rank1 = hEval.rankHand(hand1);
			float rank2 = hEval.rankHand(hand2);
			if (rank1 > rank2)//p1 wins
			{//p1 wins
				wins[p1]++;
				if (verbose)
					System.out.println(players.get(p1).getName()+" wins with " + HandEvaluator.nameHand(rank1) +
							" against "+players.get(p2).getName()+" " + HandEvaluator.nameHand(rank2));
			} else if (rank1 < rank2) {//p2 wins
				wins[p2]++;
				if (verbose)
					System.out.println(players.get(p1).getName()+" lost with " + HandEvaluator.nameHand(rank1) +
							" against "+players.get(p2).getName()+" " + HandEvaluator.nameHand(rank2));
			} else {//draw
				wins[p1] += .5;
				wins[p2] += .5;
				if (verbose)
					System.out.println(players.get(p1).getName()+" drew with " + HandEvaluator.nameHand(rank1) +
							" against "+players.get(p2).getName()+" " + HandEvaluator.nameHand(rank2));
			}
			ranks[p1] += rank1;
			ranks[p2] += rank2;
		}
	}
	/**
	 * Generates graphs
	 * 
	 * @param numGraphs
	 *            the number of graphs to generate
	 */
	public static void generateGraphs(int numGraphs) {
		for (int i = 0; i < numGraphs; i++) {
			Graph n = new Graph(i);
			n.generateGraph();
			n.saveGraph();
			n.saveGraph(true);
		}
	}


	/**
	 * Sets new parameters based on a case x
	 * @param x the case to use
	 */
	private static void changeParameters(int x){
		switch (x) {
		case 0://smallish graph
			Parameters.NUMBER_OF_NODES = 8;
			Parameters.MAX_NEIGHBORS = 3;
			Parameters.MIN_NEIGHBORS = 1;
			Parameters.NUM_POSSIBLE_CARDS = 4;
			Parameters.NUM_TURNS = 8;
			break;
		case 1://slightly larger
			Parameters.NUMBER_OF_NODES = 12;
			Parameters.MAX_NEIGHBORS = 3;
			Parameters.MIN_NEIGHBORS = 2;
			Parameters.NUM_POSSIBLE_CARDS = 4;
			Parameters.NUM_TURNS = 12;
			break;
		case 2: //less uncertainty
			Parameters.NUMBER_OF_NODES = 14;
			Parameters.MAX_NEIGHBORS = 5;
			Parameters.MIN_NEIGHBORS = 2;
			Parameters.NUM_POSSIBLE_CARDS = 2;
			Parameters.NUM_TURNS = 8;
			break;
		case 3://no uncertainty
			Parameters.NUMBER_OF_NODES = 12;
			Parameters.MAX_NEIGHBORS = 5;
			Parameters.MIN_NEIGHBORS = 2;
			Parameters.NUM_POSSIBLE_CARDS = 1;
			Parameters.NUM_TURNS = 8;
			break;
		case 4://large and complex
			Parameters.NUMBER_OF_NODES = 20;
			Parameters.MAX_NEIGHBORS = 5;
			Parameters.MIN_NEIGHBORS = 3;
			Parameters.NUM_POSSIBLE_CARDS = 4;
			Parameters.NUM_TURNS = 15;
			break;
		case 5://even more complexity
			Parameters.NUMBER_OF_NODES = 20;
			Parameters.MAX_NEIGHBORS = 5;
			Parameters.MIN_NEIGHBORS = 3;
			Parameters.NUM_POSSIBLE_CARDS = 5;
			Parameters.NUM_TURNS = 15;
			break;
		default://whatever the default parameters are
			break;
		}
	}
}
