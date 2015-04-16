package CardPickup;

import java.util.ArrayList;

/**
 * Pits Attacker and Defender agents against one another in the name of Science!
 * 
 * STUDENTS: add your defenders and attackers to the sections in main that say
 * "add defenders here" and "add attackers here" Also add your defender to the
 * method getDefenderByName() and your attacker to getAttackerByName() You may
 * also edit the rates in the Parameters class. Trust that these rates will be
 * changed when the full tournament is run.
 * 
 * @author Marcus Gutierrez and Oscar Veliz
 * @version 2014/15/14
 */
public class GameMaster {
	
	private static int gameID = 0;
	private static boolean verbose = true; //Set to false if you do not want much detail printed to console

	/**
	 * Initializes each player and each player profile with all the relevant knowledge needed to start a game
     * @param g copy of graph
     * @param p1Profile profile for player 1
     * @param p1 player 1
     * @param p2Profile profile for player 2
     * @param p2 player 2
	 */
	public static void initializePlayers(Graph g, PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2){
		//Player 1
		p1.setGraph(g.generateHiddenGraph());
		p1Profile.setCurrentHand(g.getHand(0));
		p1Profile.setCurrentLocation(0);
		//Update the player 1
		p1.setHand(g.getHand(0));
		p1.setCurrentNode(p1Profile.getCurrentLocation());
		
		//Player 2
		p2.setGraph(g.generateHiddenGraph());
		p2Profile.setCurrentHand(g.getHand(1));
		p2Profile.setCurrentLocation(1);
		//Update the player 2
		p2.setHand(g.getHand(1));
		p2.setCurrentNode(p2Profile.getCurrentLocation());
		
		//Notify players of their opponent
		p1.setOpponentNode(p2Profile.getCurrentLocation());	//subject to change
		p2.setOpponentNode(p1Profile.getCurrentLocation());	//subject to change
		
	}

	/**
	 * Has one player execute a move
	 * @param p player who will execute a single move
	 */
	private static void oneTurn(Player p){
		Thread playerThread = new Thread(p);
		playerThread.start();
		for(int sleep = 0; sleep < 500; sleep+=10){
			if(playerThread.isAlive())
				try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
			else
				return;
		}
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
		if(isValidMove(graph, cpProfile.getCurrentLocation(), a.nodeID)){
			switch(a.move){
			case MOVE:
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				opponent.opponentAction(a.nodeID, false, null);
				if(verbose)System.out.println(currentPlayer.getName() + " moved to node " + a.nodeID);
				break;
			case PICKUP:
				cpProfile.setCurrentLocation(a.nodeID);
				currentPlayer.setCurrentNode(a.nodeID);
				//If the node's card has not been picked up yet
				if(graph[a.nodeID].getPossibleCards().size() > 0){
					currentPlayer.addCardToHand(new Card(graph[a.nodeID].getCard().toString()));
					cpProfile.addCardToHand(new Card(graph[a.nodeID].getCard().toString()));
					opponent.opponentAction(a.nodeID, true, new Card(graph[a.nodeID].getCard().toString()));
					graph[a.nodeID].clearPossibleCards(); //remove all possible cards
					if(verbose)System.out.println(currentPlayer.getName() + " picked up a " + graph[a.nodeID].getCard().toString() + " at node " + a.nodeID);
				}else{ //The node's card has already been picked up
					opponent.opponentAction(a.nodeID, false, null);
					if(verbose)System.out.println(currentPlayer.getName() + " attempted to pick up a card at node " + a.nodeID + ", but nothing was there");
				}
				break;
			default:
				opponent.opponentAction(-1, false, null);
				if(verbose)System.out.println(currentPlayer.getName() + " performed an invalid action");
				break;
			}
		}else{
			opponent.opponentAction(-1, false, null);
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
		for(int i = 0; i < graph[i].getNeighborAmount(); i++){
			if(graph[playerLocation].getNeighbor(i).getNodeID() == playerDestination)
				return true;
		}
		return false;
	}

	/**
	 * Should only be called by runMatches() as that method has the necessary initializations to run this method
     * It expects g.generateNetwork() to have been called before running this method
	 * @param g The graph the players will be playing on. Should already be initialized by this point.
	 * @param p1 The player that will move first
	 * @param p2 The player that will move second
	 */
	private static void oneRound(GameProfile gp, Graph g, PlayerProfile p1Profile, Player p1, PlayerProfile p2Profile, Player p2){
		Node[] graph = g.generateCopyGraph();
		initializePlayers(g, p1Profile, p1, p2Profile, p2);
		boolean p1Finished;
		boolean p2Finished;
		//Runs until both players have a full hand or are out of turns
		if(verbose)System.out.println("Game " + Integer.toString(gameID-1) + " starting...");
		for(int i = 0; i < Parameters.NUM_TURNS; i++){
			//Checks if player 1 is finished, if not, has him/her make one move, then registers the turn the gamemaster and both players
			if(p1Profile.getHandSize() < Parameters.MAX_HAND){
				oneTurn(p1);
				registerTurn(graph, p1Profile, p1, p2);
				p1Finished = false;
			} else
				p1Finished = true;

			//Checks if player 2 is finished, if not, has him/her make one move, then registers the turn the gamemaster and both players
			if(p2Profile.getHandSize() < Parameters.MAX_HAND){
				oneTurn(p2);
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
		gp.endGame(); //Game has ended and the GameProfile calculates the winner
		if(verbose)System.out.println("Game " + Integer.toString(gameID-1) + " ended with " + gp.getWinner() + " as the winner!");
	}
	
	/**
	 * Runs a specified number of matches on the same graph against two players. One match is
	 * considered 2 rounds where the second round reverses the locations and hands of the 2 players.

	 * @param gameSeed number representing the seed used to generate the graph
	 * @param p1Name The player that will act first in the first round of a match (will act as player 2 in the second round)
	 * @param p2Name The player that will act second in the first round of a match (will act as player 1 in the first round)
	 */
	public static void runMatches(int gameSeed, String p1Name, String p2Name){
        int matchAmt = 1;//Number of matches to play. Leave as 1. Left in for future proofing.
		if(getPlayer(p1Name) == null || getPlayer(p2Name) == null){//make sure players are valid
			System.out.println("ERROR: CHECK THAT PLAYER NAMES ARE VALID");
			System.out.println("Ensure that you altered GameMaster.getPlayer() properly to add your agent and that the names match");
			return;
		}
		
		for(int i = 0; i < matchAmt; i++){
			//Game initialization
			Player p1 = getPlayer(p1Name);
			Player p2 = getPlayer(p2Name);
			Graph g = new Graph(gameSeed);
			g.generateNetwork();
			g.saveGraph();
			
			//Profile initialization
			PlayerProfile p1Profile = new PlayerProfile(p1.getName());
			PlayerProfile p2Profile = new PlayerProfile(p2.getName());
			GameProfile gp = new GameProfile(g, gameSeed, p1Profile, p2Profile, gameID++); //Establish a game profile to store all information
			
			oneRound(gp, g, p1Profile, p1, p2Profile, p2);
			
			/////////////////////////////////////////////////////////////////////////////
			//Exact same code as above, but reverses the roles of player 1 and player 2//
			/////////////////////////////////////////////////////////////////////////////
			//Game initialization
			p1 = getPlayer(p2Name);
			p2 = getPlayer(p1Name);
			g = new Graph(gameSeed);
			g.generateNetwork();
			
			//Profile initialization
			p1Profile = new PlayerProfile(p1.getName());
			p2Profile = new PlayerProfile(p2.getName());
			gp = new GameProfile(g, gameSeed, p1Profile, p2Profile, gameID++); //Establish a game profile to store all information
			
			oneRound(gp, g, p1Profile, p1, p2Profile, p2);
		}
	}

	/**
	 * Runs the tournament
	 * 
	 * @param args not using any command line arguments
	 */
	public static void main(String[] args) {
		int numGames = 5;
		int parameterSetting = 2;
		changeParameters(parameterSetting);
		generateGraphs(numGames);

        //add your agent here
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(new TestPlayer());
        players.add(new TestPlayer());//adding this player twice to play against itself

        int numPlayers = players.size();
        for(int p1 = 0; p1 < numPlayers; p1++) {
            for(int p2 = p1+1; p2 < numPlayers; p2++) {
                if(p1!=p2)
                    for (int game = 0; game < numGames; game++)
                        runMatches(game, players.get(p1).getName(), players.get(p2).getName());
            }
        }
		// add Defenders here
		/*ArrayList<Defender> defenders = new ArrayList<Defender>();
		defenders.add(new WhatDoesThisButtonDoDefender("0"));
		defenders.add(new Strengthener("0"));
		defenders.add(new NumbDefender("0"));
		defenders.add(new RationalDefender("0"));*/
		/*defenders.add(new SteelCurtain("0"));
		defenders.add(new CentroidWallDefenderV1("0"));
		defenders.add(new Steelix("0"));
		defenders.add(new Bowser("0"));
		defenders.add(new MyDefender("0"));
		defenders.add(new OmarEdgarDefender("0"));
		defenders.add(new BrickWall("0"));
		defenders.add(new SinglePath("0"));*/

		// get names of defenders
		/*String[] defenderNames = new String[defenders.size()];
		for (int i = 0; i < defenders.size(); i++)
			defenderNames[i] = defenders.get(i).getName();
		int numDefenders = defenderNames.length;
		// execute defenders
		for (int d = 0; d < numDefenders; d++) {
			for (int g = 0; g < numGames; g++) {
				Defender defender = getDefender(defenderNames[d], g + "");
				new Thread(defender).start();
				for(int sleep = 0; sleep < 500; sleep+=10){
					if(defender.keepRunning()){						
						try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
					}
					else{break;}
				}
				defender.kill();
				new DefenderHelper(defender.getName(), defender.getGraph());
			}
		}

		// add Attackers here
		ArrayList<Attacker> attackers = new ArrayList<Attacker>();
		attackers.add(new Blitzkrieg());
		attackers.add(new GamblingAttacker());
		attackers.add(new CautiousAttacker());
		attackers.add(new GreedyAttacker());*/
		/*attackers.add(new GottaGoFast());
		attackers.add(new MazeSolverAttackerV5());
		attackers.add(new TinmAttack());
		attackers.add(new SuperMario());
		attackers.add(new EdgarOmarAttacker());
		attackers.add(new OmarBradley());
		attackers.add(new TheShyOne());*/

		// get names of attackers
		/*String[] attackerNames = new String[attackers.size()];
		for (int i = 0; i < attackers.size(); i++)
			attackerNames[i] = attackers.get(i).getName();
		int numAttackers = attackerNames.length;
		// initialize point matrix
		int[][] points = new int[numDefenders][numAttackers];

		// perform analysis
		Analyzer analyzer = new Analyzer(points, attackerNames, defenderNames);
		analyzer.savePoints(p);*/

		System.exit(0);//just to make sure it exits
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
			n.generateNetwork();
			n.saveGraph();
			n.saveGraph(true);
		}
	}

	/**
	 * You should edit this method to include your player agent
	 * @param name The name of your player agent
	 * @return An instance of your Player agent
	 */
	public static Player getPlayer(String name) {
		if (name.equalsIgnoreCase("TestPlayer"))
			return new TestPlayer();
		/*else if(name.equalsIgnoreCase("YOUR AGENT HERE")
		 * 	return new StudentAgent();
		 */
		// in case your name was not added
		return null;
	}

	private static void changeParameters(int x){
		switch (x)
		{
            case 0://smallish graph
                Parameters.NUMBER_OF_NODES = 8;
                Parameters.MAX_NEIGHBORS = 3;
                Parameters.MIN_NEIGHBORS = 2;
                Parameters.NUM_POSSIBLE_CARDS = 4;
                Parameters.NUM_TURNS = 8;
                break;
            case 1://slightly larger
                Parameters.NUMBER_OF_NODES = 12;
                Parameters.MAX_NEIGHBORS = 3;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.NUM_POSSIBLE_CARDS = 4;
                Parameters.NUM_TURNS = 12;
                break;
            case 2: //less uncertainty
                Parameters.NUMBER_OF_NODES = 14;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.NUM_POSSIBLE_CARDS = 2;
                Parameters.NUM_TURNS = 8;
                break;
            case 3://no uncertainty
                Parameters.NUMBER_OF_NODES = 12;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
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
