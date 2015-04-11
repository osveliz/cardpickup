package CardPickup;

/**
 * Pits Attacker and Defender agents against one another in the name of Science!
 * 
 * STUDENTS: add your defenders and attackers to the sections in main that say
 * "add defenders here" and "add attackers here" Also add your defender to the
 * method getDefenderByName() and your attacker to getAttackerByName() You may
 * also edit the rates in the Parameters class. Trust that these rates will be
 * changed when the full tournament is run.
 * 
 * @author Oscar Veliz, Porag Chowdhury, Anjon Basak, Marcus Gutierrez
 * @version 2014/11/14
 */
public class GameMaster {
	/**
	 * Runs the tournament
	 * 
	 * @param args not using any command line arguments
	 */
	public static void main(String[] args) {
		int numGames = 2;
		int p = 5;
		changeParameters(p);
		generateGraphs(numGames);

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

		// execute attackers
		for (int d = 0; d < numDefenders; d++) {
			String defenderName = defenderNames[d];
			for (int a = 0; a < numAttackers; a++) {
				String attackerName = attackerNames[a];
				for (int g = 0; g < numGames; g++) {
					String graphName = g + "";
					AttackerMonitor am = new AttackerMonitor(attackerName,
							defenderName, graphName);
					while (am.getBudget() > 0) {
						Attacker attacker = getAttacker(defenderName,attackerName, graphName);
						new Thread(attacker).start();

						for(int sleep = 0; sleep < 500; sleep+=10){
							if(attacker.keepRunning()){						
								try {Thread.sleep(10);} catch (Exception e) {e.printStackTrace();}
							}
							else{break;}
						}
						attacker.kill();
						am.readMove();
						//System.out.println("Budget after move: "+ am.getBudget());
					}
					am.close();
					points[d][a] += am.getPoints();
				}
			}
		}
		// perform analysis
		Analyzer analyzer = new Analyzer(points, attackerNames, defenderNames);
		analyzer.savePoints(p);*/
        System.exit(0);
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
			n.saveGraph();
            n.saveGraph(true);
		}
	}

	/**
	 * You should edit this method to include your defender
	 * 
	 * @param name
	 *            name of defender
	 * @param file
	 *            graph defender will read
	 * @return your defender
	 */
	public static Defender getDefender(String name, String file) {
		if (name.equalsIgnoreCase("WDTBD"))
			return new WhatDoesThisButtonDoDefender(file);
		if (name.equalsIgnoreCase("Strengthener"))
			return new Strengthener(file);
		if (name.equalsIgnoreCase("NumbDefender"))
			return new NumbDefender(file);
		if (name.equalsIgnoreCase("RationalDefender"))
			return new RationalDefender(file);
		/*if (name.equalsIgnoreCase("SteelCurtain"))
			return new SteelCurtain(file);
		if (name.equalsIgnoreCase("CentroidWallDefenderV1"))
			return new CentroidWallDefenderV1(file);
		if (name.equalsIgnoreCase("Steelix"))
			return new Steelix(file);
		if (name.equalsIgnoreCase("Bowser"))
			return new Bowser(file);
		if (name.equalsIgnoreCase("My Defender"))
			return new MyDefender(file);
		if (name.equalsIgnoreCase("OmarEdgarDefender"))
			return new OmarEdgarDefender(file);
		if (name.equalsIgnoreCase("BrickWall"))
			return new BrickWall(file);
		if (name.equalsIgnoreCase("SinglePath"))
			return new SinglePath(file);*/
		// add your defender

		// invalid defender if name could not be found
		return new Defender("", "") {
			@Override
			public void makeMoves() {
				System.out.print("check name");
			}
		};
	}

	/**
	 * You should edit this method to include your attacker
	 * 
	 * @param defName
	 *            name of defender attacker will be pit against
	 * @param atName
	 *            name of defender
	 * @param file
	 *            graph defender will attack
	 * @return your attacker
	 */
	public static Attacker getAttacker(String defName, String atName,
			String file) {
		if (atName.equalsIgnoreCase("Blitzkrieg"))
			return new Blitzkrieg(defName, file);
		if (atName.equalsIgnoreCase("GamblingAttacker"))
			return new GamblingAttacker(defName, file);
		if (atName.equalsIgnoreCase("CautiousAttacker"))
			return new CautiousAttacker(defName, file);
		if (atName.equalsIgnoreCase("GreedyAttacker"))
			return new GreedyAttacker(defName, file);
		/*if(atName.equalsIgnoreCase("Sanic"))
			return new GottaGoFast(defName,file);
		if(atName.equalsIgnoreCase("MazeSolverAttackerV5"))
			return new MazeSolverAttackerV5(defName,file);
		if(atName.equalsIgnoreCase("TinmAttack"))
			return new TinmAttack(defName,file);
		if(atName.equalsIgnoreCase("Super Mario"))
			return new SuperMario(defName,file);
		if(atName.equalsIgnoreCase("EdgarOmarAttacker"))
			return new EdgarOmarAttacker(defName,file);
		if(atName.equalsIgnoreCase("OmarBradley"))
			return new OmarBradley(defName,file);
		if(atName.equalsIgnoreCase("TheShyOne"))
			return new TheShyOne(defName,file);*/
		// add your attacker here

		// in case your name was not added
		return new Attacker("", "", "") {
			@Override
			public AttackerAction makeSingleAction() {
				System.out.println("check attacker name");
				return null;
			}

			@Override
			protected void initialize() {
			}
		};
	}

    private static void changeParameters(int x)
    {
        switch (x)
        {
            case 0://smallish graph
                Parameters.NUMBER_OF_NODES = 10;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 2;
                Parameters.MAX_NEIGHBORS = 3;
                Parameters.MIN_NEIGHBORS = 2;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 5;
                Parameters.DEFENDER_RATE = 10;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 2;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 10;
                Parameters.HONEYPOT_RATE = 50;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 8;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 20;
                Parameters.PROBE_SECURITY_RATE = 2;
                Parameters.PROBE_POINT_RATE = 2;
                Parameters.PROBE_CONNECTIONS_RATE = 1;
                Parameters.PROBE_HONEY_RATE = 1;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            case 1: //decent size graph
                Parameters.NUMBER_OF_NODES = 20;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 4;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 8;
                Parameters.DEFENDER_RATE = 10;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 2;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 10;
                Parameters.HONEYPOT_RATE = 50;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 8;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 20;
                Parameters.PROBE_SECURITY_RATE = 2;
                Parameters.PROBE_POINT_RATE = 2;
                Parameters.PROBE_CONNECTIONS_RATE = 1;
                Parameters.PROBE_HONEY_RATE = 1;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            case 2: //expensive for defender
                Parameters.NUMBER_OF_NODES = 20;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 4;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 8;
                Parameters.DEFENDER_RATE = 10;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 5;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 20;
                Parameters.HONEYPOT_RATE = 50;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 8;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 20;
                Parameters.PROBE_SECURITY_RATE = 2;
                Parameters.PROBE_POINT_RATE = 2;
                Parameters.PROBE_CONNECTIONS_RATE = 1;
                Parameters.PROBE_HONEY_RATE = 1;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            case 3://expensive to attack
                Parameters.NUMBER_OF_NODES = 20;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 4;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 8;
                Parameters.DEFENDER_RATE = 10;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 2;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 10;
                Parameters.HONEYPOT_RATE = 50;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 10;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 25;
                Parameters.PROBE_SECURITY_RATE = 2;
                Parameters.PROBE_POINT_RATE = 2;
                Parameters.PROBE_CONNECTIONS_RATE = 1;
                Parameters.PROBE_HONEY_RATE = 1;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            case 4://expensive to probe
                Parameters.NUMBER_OF_NODES = 20;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 4;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 8;
                Parameters.DEFENDER_RATE = 10;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 2;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 10;
                Parameters.HONEYPOT_RATE = 50;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 10;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 30;
                Parameters.PROBE_SECURITY_RATE = 4;
                Parameters.PROBE_POINT_RATE = 4;
                Parameters.PROBE_CONNECTIONS_RATE = 4;
                Parameters.PROBE_HONEY_RATE = 4;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            case 5://expensive for everyone
                Parameters.NUMBER_OF_NODES = 20;
                Parameters.NUMBER_OF_PUBLIC_NODES = 2;
                Parameters.NUMBER_OF_ROUTER_NODES = 4;
                Parameters.MAX_NEIGHBORS = 5;
                Parameters.MIN_NEIGHBORS = 1;
                Parameters.MAX_POINT_VALUE = 20;
                Parameters.MAX_ROUTER_EDGES = 8;
                Parameters.DEFENDER_RATE = 5;
                Parameters.DEFENDER_BUDGET = Parameters.DEFENDER_RATE * Parameters.NUMBER_OF_NODES;
                Parameters.STRENGTHEN_RATE = 2;
                Parameters.INVALID_RATE = 10;
                Parameters.FIREWALL_RATE = 10;
                Parameters.HONEYPOT_RATE = 40;
                Parameters.ATTACKER_RATE = 5;
                Parameters.ATTACK_ROLL = 20;
                Parameters.ATTACK_RATE = 10;
                Parameters.SUPERATTACK_ROLL = 50;
                Parameters.SUPERATTACK_RATE = 25;
                Parameters.PROBE_SECURITY_RATE = 2;
                Parameters.PROBE_POINT_RATE = 2;
                Parameters.PROBE_CONNECTIONS_RATE = 1;
                Parameters.PROBE_HONEY_RATE = 1;
                Parameters.ATTACKER_BUDGET = Parameters.ATTACK_RATE * Parameters.NUMBER_OF_NODES;
                break;
            default:
                break;

        }
    }
}
