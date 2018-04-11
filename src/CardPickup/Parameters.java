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
    public int MAX_WEIGTH = 10;
    /**Time limit for initialization in milliseconds*/
    public int INIT_TIME = 1000;
    /**Time limit for calculating an action result in milliseconds*/
    public int RESULT_TIME = 1000;
    /**Time limit for calculating an opponent's action result in milliseconds*/
    public int OPP_RESULT_TIME = 1000;
    /**Time limit for making an action in milliseconds*/
    public int ACTION_TIME = 500;
}
