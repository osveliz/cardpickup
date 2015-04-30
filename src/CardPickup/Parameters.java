package CardPickup;
/**
 * Defines parameter variables used in simulation
 */

public class Parameters {
    /**Identifies the number of nodes in an initial graph*/
    public static int NUMBER_OF_NODES = 15;
    /**Identifies the maximum number of neighbors in an initial graph for non-router nodes*/
    public static int MAX_NEIGHBORS = 4;
    /**Identifies the minimum number of neighbors in an initial graph for non-router nodes*/
    public static int MIN_NEIGHBORS = 1;
    /**Identifies the number of possible cards in the uncertain graph*/
    public static int NUM_POSSIBLE_CARDS = 4;
    /**Identifies the number of turns (both players moving is a turn)*/
    public static int NUM_TURNS = 10;
    /**Identifies the maximum hand size for each player*/
    public static final int MAX_HAND = 5;
    /**Time limit for initialization in milliseconds*/
    public static final int INIT_TIME = 1000;
    /**Time limit for calculating an action result in milliseconds*/
    public static final int RESULT_TIME = 1000;
    /**Time limit for calculating an opponent's action result in milliseconds*/
    public static final int OPP_RESULT_TIME = 1000;
    /**Time limit for making an action in milliseconds*/
    public static final int ACTION_TIME = 500;
}
