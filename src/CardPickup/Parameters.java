package CardPickup;
/**
 * Defines parameter variables used in simulation
 */

public class Parameters {
    /**Identifies the number of nodes in an initial graph*/
    public int NUMBER_OF_NODES = 15;
    /**Identifies the maximum number of neighbors in an initial graph for non-router nodes*/
    public int MAX_NEIGHBORS = 4;
    /**Identifies the minimum number of neighbors in an initial graph for non-router nodes*/
    public int MIN_NEIGHBORS = 1;
    /**Identifies the number of possible cards in the uncertain graph*/
    public int NUM_POSSIBLE_CARDS = 4;
    /**Identifies the budget (both players)*/
    public int BUDGET = 70;
    /**Identifies the maximum weight of an edge*/
    public int MAX_WEIGHT = 10;
    /**Time limit for initialization in milliseconds*/
    public int INIT_TIME = 1000;
    /**Time limit for calculating an action result in milliseconds*/
    public int RESULT_TIME = 1000;
    /**Time limit for calculating an opponent's action result in milliseconds*/
    public int OPP_RESULT_TIME = 1000;
    /**Time limit for making an action in milliseconds*/
    public int ACTION_TIME = 500;
    
    /**
     * Default constructor
     */
    public Parameters(){}
    
    /**
     * Specify values constructor
     * @param nodes number of nodes
     * @param max max neighbors
     * @param min min neighbors
     * @param cards number of cards
     * @param bud budget
     * @param weight maximum weight
     * @param init initialization timer
     * @param result result timer
     * @param opp opponent update timer
     * @param action action timer
     */
    public Parameters(int nodes, int max, int min, int cards, int bud, int weight, int init, int result, int opp, int action){
		NUMBER_OF_NODES = nodes;
		MAX_NEIGHBORS = max;
		MIN_NEIGHBORS = min;
		NUM_POSSIBLE_CARDS = cards;
		BUDGET = bud;
		MAX_WEIGHT = weight;
		INIT_TIME = init;
		RESULT_TIME = result;
		OPP_RESULT_TIME = opp;
		ACTION_TIME = action;
	}
	/**
	 * clones the parameters
	 * @return a clone
	 */
	public Parameters clone(){
		return new Parameters(NUMBER_OF_NODES,
								MAX_NEIGHBORS,
								MIN_NEIGHBORS,
								NUM_POSSIBLE_CARDS,
								BUDGET,
								MAX_WEIGHT,
								INIT_TIME,
								RESULT_TIME,
								OPP_RESULT_TIME,
								ACTION_TIME);
	}
}
