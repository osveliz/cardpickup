package CardPickup;
/**
 * Defines constant variables used in simulation
 */

public class Parameters {
    /**Identifies the number of nodes in an initial graph*/
    public static int NUMBER_OF_NODES = 15;
    /**Identifies the number of public nodes in an initial graph*/
    public static int NUMBER_OF_PUBLIC_NODES = 3;
    /**Identifies the number of routers in an initial graph*/
    public static int NUMBER_OF_ROUTER_NODES = 1;
    /**Identifies the maximum number of neighbors in an initial graph for non-router nodes*/
    public static int MAX_NEIGHBORS = 4;
    /**Identifies the minimum number of neighbors in an initial graph for non-router nodes*/
    public static int MIN_NEIGHBORS = 1;
    /**The maximum PointValue*/
    public static int MAX_POINT_VALUE = 20;
    /**The maximum number of router edges*/
    public static int MAX_ROUTER_EDGES = 7;

    /**The value that the defender gets for each node in a graph*/
    public static int DEFENDER_RATE = 10;
    /**Defender's Budget*/
    public static int DEFENDER_BUDGET = DEFENDER_RATE * NUMBER_OF_NODES;
    /**The cost to strengthen a node*/
    public static int STRENGTHEN_RATE = 2;
    /**The cost for having invalid actions*/
    public static int INVALID_RATE = 10;
    /**The cost to remove an edge (add a firewall)*/
    public static int FIREWALL_RATE = 10;
    /**The cost to add a honeypot*/
    public static int HONEYPOT_RATE = 50;

    /**The value that the attacker gets for each node in a graph*/
    public static int ATTACKER_RATE = 10;
    /**The maximum value for an attacker roll*/
    public static int ATTACK_ROLL = 20;
    /**The cost for performing an attack*/
    public static int ATTACK_RATE = 8;
    /**The maximum value for a super attack*/
    public static int SUPERATTACK_ROLL = 50;
    /**The cost for performing a super attack*/
    public static int SUPERATTACK_RATE = 20;
    /**The cost to probe for the security value*/
    public static int PROBE_SECURITY_RATE = 2;
    /**The cost to probe for the point value*/
    public static int PROBE_POINT_RATE = 2;
    /**The cost to probe for the number of connections*/
    public static int PROBE_CONNECTIONS_RATE = 1;
    /**The cost to identify if a node is a honey pot*/
    public static int PROBE_HONEY_RATE = 1;
    /**Penalty for successfully capturing a honey pot*/
    public static int HONEY_PENALTY = -10;
    /**Attacker's Budget*/
    public static int ATTACKER_BUDGET = ATTACK_RATE * NUMBER_OF_NODES;
}
