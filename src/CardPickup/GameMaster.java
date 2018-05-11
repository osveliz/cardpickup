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

	private static boolean verbose = true; //Set to false if you do not want much detail printed to console
	private static int numGames = 1; //use a small number for quick tests, a large one to be comprehensive
	private static int parameterSetting = 1; //see changeParameters()
	private static Parameters param;
	private static Graph currentGame;
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
		param = new Parameters();
		changeParameters(parameterSetting,param);		
		Graph[] graphs = generateGraphs(numGames);
		
		ArrayList<Player> players = new ArrayList<Player>();
		players.add(new TestPlayer());
		//players.add(new TestPlayer());
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
						currentGame = graphs[game].clone();
						if(verbose)System.out.println("Game " + Integer.toString(game) + " starting...");
						Hand[] hands = runMatches(players.get(p1).getName(), players.get(p2).getName());
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
			timeLimit = param.INIT_TIME;
		else if(pDriver.state == PlayerState.RESULT)
			timeLimit = param.RESULT_TIME;
		else if(pDriver.state == PlayerState.OPP_RESULT)
			timeLimit = param.OPP_RESULT_TIME;
		else
			timeLimit = param.ACTION_TIME;

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
	 * @param p1Profile profile for player 1
	 * @param p1 player 1
	 * @param p2Profile profile for player 2
	 * @param p2 player 2
	 * @param g copy of graph
	 */
	public static void initializePlayers(PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2, Graph g){
		//Player 1
		//p1.setGraph(Parser.parseGraph(gameSeed+".hidden").generateHiddenGraph());
		Graph hidden1 = g.clone();
		Graph hidden2 = g.clone();
		hidden1.hide();
		p1.setGraph(hidden1);
		p1Profile.setCurrentHand(g.getHand(1));//player 1
		p1Profile.setCurrentLocation(0);
		p1.setHand(g.getHand(1));
		p1.setCurrentNode(p1Profile.getCurrentLocation());
		p1Profile.setBudget(g.getInitalBudget());
		p1.setBudget(p1Profile.getBudget());

		//Player 2
		hidden2.hide();
		p2.setGraph(hidden2);
		p2Profile.setCurrentHand(g.getHand(2));
		p2Profile.setCurrentLocation(1);
		p2.setHand(g.getHand(2));
		p2.setCurrentNode(p2Profile.getCurrentLocation());
		p2Profile.setBudget(g.getInitalBudget());
		p2.setBudget(p1Profile.getBudget());

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
	private static void registerTurn(PlayerProfile cpProfile, Player currentPlayer, Player opponent){
		//currentGame.printMatrix();
		Node[] graph = currentGame.getNodes();
		Action a = currentPlayer.getLastAction();

        if(a == null)//just in case the player couldn't get a move in.
            a = new Action(ActionType.MOVE,currentPlayer.getCurrentNode());
        if(a.move == ActionType.END){
			if(verbose)System.out.println(currentPlayer.getName() + " Ended");
			cpProfile.setBudget(-1);
			currentPlayer.setBudget(-1);
			return;
		}
		if(a.move == ActionType.BURN){
			if(verbose)System.out.println(currentPlayer.getName() + " removed "+a.card);
			cpProfile.remove(a.card);
			currentPlayer.remove(a.card);
			cpProfile.pay(5);
			currentPlayer.setBudget(cpProfile.getBudget());
			return;
		}
        int di1,di2;
		if(isValidMove(cpProfile.getCurrentLocation(), a.nodeID) && cpProfile.getBudget()>0){
			switch(a.move){
			case MOVE:
				di1 = cpProfile.getCurrentLocation();
				di2 = a.nodeID;
				if(di1 != di2){
					int[][] matrix = currentGame.getWeights();
					int cost = matrix[di1][di2];
					if(verbose)System.out.println("Cost from "+di1+" to "+di2+": "+cost);
					//currentGame.printMatrix();
					if(cpProfile.getBudget()<cost){
						cpProfile.setBudget(-1);
						currentPlayer.setBudget(cpProfile.getBudget());
						break;
					}
					else{
						cpProfile.pay(cost);
						currentPlayer.setBudget(cpProfile.getBudget());
					}
				}
				else{//why move to same node?
					cpProfile.pay(5);
					currentPlayer.setBudget(cpProfile.getBudget());
				}
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
				tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, null)); //Try to notify player of result
				if(verbose)System.out.println(currentPlayer.getName() + " moved to node " + a.nodeID);
				break;
			case PICKUP:
				di1 = cpProfile.getCurrentLocation();
				di2 = a.nodeID;
				if(di1 != di2){
					int[][] matrix = currentGame.getWeights();
					int cost = matrix[di1][di2];
					if(verbose)System.out.println("Cost from "+di1+" to "+di2+": "+cost);
					if(cpProfile.getBudget()<cost){
						cpProfile.setBudget(-1);
						currentPlayer.setBudget(cpProfile.getBudget());
						break;
					}
					else{
						cpProfile.pay(cost);
						currentPlayer.setBudget(cpProfile.getBudget());
					}
				}
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				//If the node's card has not been picked up yet
				if(graph[a.nodeID].getPossibleCards().size() > 0){
					Card c = new Card(graph[a.nodeID].getCard().toString());
					cpProfile.addCardToHand(c);
					currentPlayer.addCardToHand(new Card(c.toString()));
					tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, true, c)); //Try to notify opponent of result
					tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, c)); //Try to notify player of result
					//graph[a.nodeID].clearPossibleCards(); //remove all possible cards
					currentPlayer.getNode().clearPossibleCards();
					opponent.getNode(currentPlayer.getCurrentNode()).clearPossibleCards();
					currentGame.getNode(a.nodeID).clearPossibleCards();
					if(verbose)System.out.println(currentPlayer.getName() + " picked up a " + graph[a.nodeID].getCard().toString() + " at node " + a.nodeID);
				}else{ //The node's card has already been picked up
					tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
					tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, a.nodeID, null)); //Try to notify player of result
					if(verbose)System.out.println(currentPlayer.getName() + " attempted to pick up a card at node " + a.nodeID + ", but nothing was there");
					cpProfile.pay(5);//invalid cost
					currentPlayer.setBudget(cpProfile.getBudget());
				}
				break;
			default:
				tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
				tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, cpProfile.getCurrentLocation(), null)); //Try to notify player of result
				if(verbose)System.out.println(currentPlayer.getName() + " performed an invalid action default case hit");
				cpProfile.pay(5);//invalid cost
				currentPlayer.setBudget(cpProfile.getBudget());
				break;
			}
		}else{
			tryPlayer(new PlayerDriver(PlayerState.OPP_RESULT, opponent, a.nodeID, false, null)); //Try to notify opponent of result
			tryPlayer(new PlayerDriver(PlayerState.RESULT, currentPlayer, cpProfile.getCurrentLocation(), null)); //Try to notify player of result
			if(verbose)System.out.println(currentPlayer.getName() + " performed an invalid action");
			cpProfile.pay(5);//invalid cost
				currentPlayer.setBudget(cpProfile.getBudget());
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
	private static boolean isValidMove(int playerLocation, int playerDestination){
		/*Node[] graph = currentGame.getNodes();
		if(playerLocation == playerDestination)
			return true;
		for(int i = 0; i < graph[playerLocation].getNeighborAmount(); i++){
			if(graph[playerLocation].getNeighbor(i).getNodeID() == playerDestination)
				return true;
		}
		return false;*/
		return currentGame.cost(playerLocation, playerDestination) > 0;
	}

	/**
	 * Should only be called by runMatches() as that method has the necessary initializations to run this method
	 * It expects generateGraph() to have been called before running this method
	 * @param p1Profile game master's profile of p1
	 * @param p1 The player that will move first
	 * @param p2Profile game master's profile of p2
	 * @param p2 The player that will move second
	 * @param g graph
	 */
	private static void oneRound(PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2){
		Graph g = currentGame.clone();
		Node[] graph = g.getNodes();
		g.setParameters(param);
		initializePlayers(p1Profile, p1, p2Profile, p2,g);
		boolean p1Finished = false;
		boolean p2Finished = false;
		int i = 0;
		while(!p1Finished || !p2Finished){
			if(p1Profile.getBudget()>0){
				tryPlayer(new PlayerDriver(PlayerState.MAKE_ACTION, p1)); //Try to have player 1 make an action
				registerTurn(p1Profile, p1, p2);
			}
			else
				p1Finished = true;

			if(p2Profile.getBudget()>0){
				tryPlayer(new PlayerDriver(PlayerState.MAKE_ACTION, p2)); //Try to have player 1 make an action
				registerTurn(p2Profile, p2, p1);
			}
			else
				p2Finished = true;

			if(verbose)System.out.println("Player 1's Budget: " + p1Profile.getBudget());
			if(verbose)System.out.println("Player 2's Budget: " + p2Profile.getBudget());
			if(verbose)System.out.println("Player 1's Hand: " + p1Profile.getCurrentHand());
			if(verbose)System.out.println("Player 2's Hand: " + p2Profile.getCurrentHand());
			if(verbose)System.out.println("Round " + (i++) + " finished");
			if(verbose)System.out.println();
		}
	}

	/**
	 * Runs a specified number of matches on the same graph against two players. One match is
	 * considered 2 rounds where the second round reverses the locations and hands of the 2 players.
	 * @param p1Name The player that will act first in the first round of a match (will act as player 2 in the second round)
	 * @param p2Name The player that will act second in the first round of a match (will act as player 1 in the first round)
	 * @return the hands
	 */
	public static Hand[] runMatches(String p1Name, String p2Name){
		Graph backup = currentGame.clone();
		Hand[] hands = new Hand[4];//4 hands p1 & p2 vs p2 and p1
		if(getPlayer(p1Name) == null || getPlayer(p2Name) == null){//make sure players are valid
			System.out.println("ERROR: CHECK THAT PLAYER NAMES ARE VALID");
			System.out.println("Ensure that you altered GameMaster.getPlayer() properly to add your agent and that the names match");
			return null;
		}

		//Game initialization
		Player p1 = getPlayer(p1Name);
		Player p2 = getPlayer(p2Name);

		//Profile initialization
		PlayerProfile p1Profile = new PlayerProfile(p1.getName());
		PlayerProfile p2Profile = new PlayerProfile(p2.getName());

		oneRound(p1Profile, p1, p2Profile, p2);
		hands[0] = p1Profile.getCurrentHand();
		hands[1] = p2Profile.getCurrentHand();
		/////////////////////////////////////////////////////////////////////////////
		//Exact same code as above, but reverses the roles of player 1 and player 2//
		/////////////////////////////////////////////////////////////////////////////
		//Game initialization
		currentGame = backup;
		p1 = getPlayer(p2Name);
		p2 = getPlayer(p1Name);

		//Profile initialization
		p1Profile = new PlayerProfile(p1.getName());
		p2Profile = new PlayerProfile(p2.getName());

		oneRound(p1Profile, p1, p2Profile, p2);
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
		if (hand1.size() != 5 && hand2.size() != 5) {
			if(verbose)System.out.println("No player was able to get a full hand.");
		}
		else if (hand1.size() != 5) {//p2 made it to 5 but p1 didn't
			wins[p2]++;
			ranks[p2] += hEval.rankHand(hand2);
			if(verbose)System.out.println(players.get(p2).getName()+" wins by default with "+HandEvaluator.nameHand(hEval.rankHand(hand2)));
		} else if (hand2.size() != 5) {//p1 made it to 5 but p2 didn't
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
	 * @param numGraphs the number of graphs to generate
	 * @return the generated graphs stored in an array
	 */
	public static Graph[] generateGraphs(int numGraphs) {
		Graph[] graphs = new Graph[numGraphs];
		for (int i = 0; i < numGraphs; i++) {
			Graph n = new Graph(i,param);
			n.generateGraph();
			n.saveGraph();
			graphs[i] = n;
			n.computeWeights();
			//n.printMatrix();
			n.saveGraph(true);
		}
		return graphs;
	}

	/**
	 * Sets new parameters based on a case x
	 * @param x the case to use
	 */
	private static void changeParameters(int x, Parameters p){
		switch (x) {
			case 0://smallish graph
				p.NUMBER_OF_NODES = 8;
				p.MAX_NEIGHBORS = 3;
				p.MIN_NEIGHBORS = 1;
				p.NUM_POSSIBLE_CARDS = 4;
				p.BUDGET = 50;
				break;
			case 1://slightly larger
				p.NUMBER_OF_NODES = 12;
				p.MAX_NEIGHBORS = 3;
				p.MIN_NEIGHBORS = 2;
				p.NUM_POSSIBLE_CARDS = 4;
				p.BUDGET = 80;
				break;
			case 2: //less uncertainty
				p.NUMBER_OF_NODES = 14;
				p.MAX_NEIGHBORS = 5;
				p.MIN_NEIGHBORS = 2;
				p.NUM_POSSIBLE_CARDS = 2;
				p.BUDGET = 100;
				break;
			case 3://no uncertainty
				p.NUMBER_OF_NODES = 12;
				p.MAX_NEIGHBORS = 5;
				p.MIN_NEIGHBORS = 2;
				p.NUM_POSSIBLE_CARDS = 1;
				p.BUDGET = 200;
				break;
			case 4://large and complex
				p.NUMBER_OF_NODES = 20;
				p.MAX_NEIGHBORS = 5;
				p.MIN_NEIGHBORS = 3;
				p.NUM_POSSIBLE_CARDS = 4;
				p.BUDGET = 100;
				break;
			case 5://even more complexity
				p.NUMBER_OF_NODES = 20;
				p.MAX_NEIGHBORS = 5;
				p.MIN_NEIGHBORS = 3;
				p.NUM_POSSIBLE_CARDS = 5;
				p.BUDGET = 100;
				break;
			default://whatever the default p are
				p = new Parameters();
				break;
		}
	}
}
