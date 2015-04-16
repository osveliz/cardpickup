package CardPickup;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
/**
 * Network class is use for generating a network.
 * Game master will use this class to generate a network. 
 *
 * @author      Porag Chowdhury, Anjon Basak, Oscar Veliz
 * @version     2015/04/12
 */

public class Graph {
	private int name;
	private String fullGraphName;//for when the graph is modified by an agent i.e. Miners-1
	private Node[] nodes = new Node[Parameters.NUMBER_OF_NODES];
	private Hand[] hand;
	private boolean graphGenerated;

	public Graph(){}

	/**
	 * Constructor used by Game master to initialize network.
	 * @param graphName An integer indicates network name
	 */
	public Graph(int graphName)
	{
		name = graphName;
		fullGraphName = ""+name;//for now
		graphGenerated = false;
		for(int i=0; i<Parameters.NUMBER_OF_NODES; i++)
		{
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
		//generateNetwork();
	}

	/**
	 * Constructor used by Game master to initialize network.
	 * @param networkName An integer indicates network name
	 * @param numNodes An integer indicates number of nodes in the network
	 */
	/*public Graph(int networkName, int numNodes)
	{
		name = networkName;
		fullGraphName = "" + name;
        nodes = new Node[numNodes];
		for(int i=0; i<numNodes; i++){
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
	}*/

	/**
	 * Returns network name.
	 * @return network name
	 */
	public int getName() {
		return name;
	}

	/**
	 * Sets network full name.
	 * @param name network name
	 */
	public void setName(String name) {
		fullGraphName = name;
	}

	/**
	 * Sets network name.
	 * @param name network name
	 */
	public void setName(int name) {
		this.name = name;
	}

	/**
	 * Returns node
	 * @param nodeId An integer indicates nodeId
	 * @return returns node.
	 */
	public Node getNode(int nodeId)
	{
		if(nodeId >= nodes.length || nodeId < 0)
			return null;
		for (Node node : nodes)
		{
			if (node.getNodeID() == nodeId)
				return node;
		}
		return null;
	}
	
	public Hand getHand(int playerNum){
		Hand h = new Hand();
		for(int i = 0; i < hand[playerNum].getNumHole(); i++)
			h.addHoleCard(new Card(hand[playerNum].getHoleCard(i).toString()));
		return h;
	}

	/**
	 * Adds edges to the node.
	 * @param routerIndex An integer indicates router id
	 * @param adjacencyMatrix A two dimensional array for adjacency
	 */
	/*public void addMoreEdges(int routerIndex, int [][] adjacencyMatrix)
	{
		ArrayList<Integer> routerNeighbors = new ArrayList<Integer>();
		Random r = new Random(name);
		int neighbourCount = 0;
		for (int i = 0; i < nodes.length; i++)
		{
			if(adjacencyMatrix[routerIndex][i] == 1){
				routerNeighbors.add(i);
				neighbourCount++;
			}
		}
		if(neighbourCount >= Parameters.MAX_ROUTER_EDGES)
			return;
		while(neighbourCount < Parameters.MAX_ROUTER_EDGES)
		{
			int neighborindex= r.nextInt(nodes.length);
			if(neighborindex != routerIndex){
				if(!routerNeighbors.contains(neighborindex))
				{
					adjacencyMatrix[routerIndex][neighborindex]=1;
					adjacencyMatrix[neighborindex][routerIndex]=1;
					routerNeighbors.add(neighborindex);
					neighbourCount++;
				}
			}
		}
	}*/

	/**
	 * Returns boolean validating a node to be eligible for Neighbor or not
	 * @param currentIndex An integer indicates current node id
	 * @param neighborIndex An integer indicates neighbor node id
	 * @param adjacencyMatrix A two dimensional array for adjacency
	 * @return boolean True/False validating a node to be eligible for Neighbor or not
	 */
	public boolean isAllowedToBeNeighbor(int currentIndex, int neighborIndex, int [][] adjacencyMatrix)
	{
		if (currentIndex == neighborIndex)
			return false;
		int neighborCount = 0;
		for(int i=0; i < adjacencyMatrix[neighborIndex].length; ++i)
		{
			if (adjacencyMatrix[neighborIndex][i] == 1)
				neighborCount++;
		}
		return neighborCount < Parameters.MAX_NEIGHBORS;

	}

	/**
	 * Returns size of the network
	 * @return size of the network i.e. number of total nodes
	 */
	public int getSize()
	{
		return nodes.length;
	}

	public void printHand(int player, Card c1, Card c2) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(fullGraphName +"-"+player+".cards", "UTF-8");
			writer.println(c1.toString());
			writer.println(c2.toString());
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print graph in a file
	 * @param usePossibleCardSet when true saves to name.hidden and saves the possible card set instead of the card
	 */
	public void saveGraph(boolean usePossibleCardSet)
	{
		PrintWriter writer;
		try {
			if(usePossibleCardSet)
				writer = new PrintWriter(fullGraphName + ".hidden", "UTF-8");
			else
				writer = new PrintWriter(fullGraphName + ".graph", "UTF-8");
			for (int i = 0; i < nodes.length; i++) {
				Node node = getNode(i);
				int neighborSize = node.neighbor.size();
				int neighborCounter = 0;
				if (node.neighbor.get(0) == null)
					writer.print("-1");
				else {
					for(Node neighbor: node.neighbor) {
						if(neighbor.getNodeID()!=node.getNodeID()) {
							if(neighborCounter==neighborSize-1)
								writer.print(neighbor.getNodeID());
							else 
								writer.print(neighbor.getNodeID()+",");
						}
						neighborCounter++;
					}
				}
				writer.println();
			}
			for (int i = 0; i < nodes.length; i++) {
				Node node = getNode(i);
				//writer.println(node.getPv()+","+node.getSv()+","+node.getHoneyPot());
				if(usePossibleCardSet){
					ArrayList<Card> cards = node.getPossibleCards();
					writer.print(cards.get(0));
					for(int c = 1; c < cards.size();c++)
						writer.print(","+cards.get(c));
					writer.println();
				}
				else
					writer.println(node.getCard());
			}
			writer.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public void saveGraph(){
		saveGraph(false);
	}

	/**
	 * Shuffles all the nodes in the network
	 */
	public void shuffleNetwork()
	{
		ArrayList<Integer> assigned = new ArrayList<Integer>();
		Random rand = new Random();
		for(int i = 0; i< this.nodes.length; i++)
		{

			while(true)
			{
				int id = rand.nextInt(nodes.length);
				if((assigned.size()==0) || (!assigned.contains(id)))
				{
					this.nodes[i].setNodeID(id);
					assigned.add(id);
					break;
				}

			}
		}
	}

	/**
	 * Generates a random network based on the parameter class and prints it in a file
	 */
	public void generateNetwork()
	{
		//Network network = new Network(networkName, numNodes);
		Random r = new Random(name);
		int [][] adjacencyMatrix = new int[Parameters.NUMBER_OF_NODES][Parameters.NUMBER_OF_NODES];
		for(int i =0; i<nodes.length; i++)
			Arrays.fill(adjacencyMatrix[i], 0);
		ArrayList<Integer> completedNodes = new ArrayList<Integer>();
		ArrayList<Integer> tmpNodeStack = new ArrayList<Integer>();
		int currentIndex = 0;
		for (int i = 0; i < nodes.length; i++)
		{
			int localMax = r.nextInt(Parameters.MAX_NEIGHBORS - Parameters.MIN_NEIGHBORS) + Parameters.MIN_NEIGHBORS;
			int neighborCounter = 0;
			ArrayList<Integer> tmpNeighbors = new ArrayList<Integer>();
			ArrayList<Integer> rejectedNeighbors = new ArrayList<Integer>();
			while(true)
			{
				int nodeIndex= r.nextInt(nodes.length);
				int totalNeighbors = 0;
				for(int k=0; k<nodes.length; k++)
					if(adjacencyMatrix[currentIndex][k]==1)
						totalNeighbors++;
				if(totalNeighbors==Parameters.MAX_NEIGHBORS)
					break;
				if(rejectedNeighbors.size()>0)
					if(rejectedNeighbors.size()==(nodes.length-tmpNeighbors.size()-1))
						break;

				if (isAllowedToBeNeighbor(currentIndex, nodeIndex, adjacencyMatrix))
				{
					if((tmpNeighbors.size()>0 && !tmpNeighbors.contains(nodeIndex) && tmpNeighbors.size() < Parameters.MAX_NEIGHBORS) || tmpNeighbors.size()==0)
					{
						//System.out.println("Current Index  " + currentIndex + "neighbor counter "+ neighborCounter + " Num Neighbors " + localMax);
						//System.out.println("Got Neighbor " + nodeIndex);
						adjacencyMatrix[currentIndex][nodeIndex] = 1;
						adjacencyMatrix[nodeIndex][currentIndex] = 1;
						tmpNeighbors.add(nodeIndex);
						if (!tmpNodeStack.contains(nodeIndex)){
							tmpNodeStack.add(nodeIndex);
							//System.out.println("Adding to stack: " + nodeIndex);
						}
						neighborCounter++;
						if(neighborCounter==localMax)
						{	
							//System.out.println("Neighbour Count for " + currentIndex + " : " +neighborCounter);
							break;
						}
					}
					else
						if(rejectedNeighbors.size()>=0 && !rejectedNeighbors.contains(nodeIndex))
							rejectedNeighbors.add(nodeIndex);
				}
				else
					if(rejectedNeighbors.size()>=0 && !rejectedNeighbors.contains(nodeIndex))
						rejectedNeighbors.add(nodeIndex);
			}
			completedNodes.add(currentIndex);
			while(true)
			{
				if (tmpNodeStack.size() == 0)
					break;
				// pick a node from the stack
				currentIndex = tmpNodeStack.get(0);
				//System.out.println("Current index: " + currentIndex);
				tmpNodeStack.remove(0);
				if (!completedNodes.contains(currentIndex))
					break;
			}
		}
		for(int i = 0; i < nodes.length; ++i)
			adjacencyMatrix[i][i] = 0;

		for (int i = 0; i < nodes.length; ++i)
		{
			Node tempNode = getNode(i);
			for(int j = 0; j < nodes.length; ++j)
			{
				if (adjacencyMatrix[i][j] == 1)
				{
					Node tempNeighbor = getNode(j);
					tempNode.addNeighbor(tempNeighbor);
				}
			}
		}
		//create deck of cards
		ArrayList<Card> deck = new ArrayList<Card>();
		for(int rank = 1; rank<=13; rank++)
			for(int suit = 1; suit <= 4; suit++)
				deck.add(new Card(rank,suit));
		//shuffle
		for(int i = 0; i < 100; i++){
			int index = r.nextInt(52);
			deck.add(deck.remove(index));
		}
		//deal two cards to each player (save to file)
		Card p1c1 = deck.remove(0);
		Card p1c2 = deck.remove(0);
		Card p2c1 = deck.remove(0);
		Card p2c2 = deck.remove(0);
		hand = new Hand[2];
		hand[0] = new Hand();
		hand[1] = new Hand();
		hand[0].addHoleCard(new Card(p1c1.toString()));
		hand[0].addHoleCard(new Card(p1c2.toString()));
		hand[1].addHoleCard(new Card(p2c1.toString()));
		hand[1].addHoleCard(new Card(p2c2.toString()));
		printHand(1,p1c1,p1c2);
		printHand(2,p2c1,p2c2);

		//deal as many cards as possible to network
		for(int i = 0; i < nodes.length; i++){
			nodes[i].setCard(deck.remove(0));
			for(int p = 1; p < Parameters.NUM_POSSIBLE_CARDS; p++)
				nodes[i].addPossible(new Card(r.nextInt(13)+1,r.nextInt(4)+1));
			nodes[i].shufflePossibleCards();
		}

		//I think this code was a failsafe to make sure that nodes were connected.
		for(int i=0; i<nodes.length; i++)
		{
			if(nodes[i].neighbor.size()==0)
			{
				//add some random neighbor
				int neighborcounter = 0;
				//System.out.println("Node "+ i +" has no neighbor");
				Random rand = new Random(name);
				while(true)
				{
					int nodeid = rand.nextInt(nodes.length-1);
					if(i!=nodeid)
					{

						if(neighborcounter==2)
						{
							break;
						}
						if(nodes[i].neighbor.size()==0)
						{
							nodes[i].neighbor.add(nodes[nodeid]);
							nodes[nodeid].neighbor.add(nodes[i]);
							neighborcounter++;
						}
						else if((nodes[i].neighbor.size()>0) && !(nodes[i].neighbor.contains(nodes[nodeid])))
						{
							nodes[i].neighbor.add(nodes[nodeid]);
							nodes[nodeid].neighbor.add(nodes[i]);
							neighborcounter++;
						}

					}
				}

			}
		}
		graphGenerated = true;
	}

	/**
	 * This method should never be called before generateNetwork()
	 * @return a copy of the graph with the true card and possible cards on each node
	 */
	public Node[] generateCopyGraph(){
		if(graphGenerated){
			Node[] graph = new Node[nodes.length];
			for(int i = 0; i < nodes.length; i++){
				graph[i] = nodes[i].clone();
			}
			for(int i = 0; i < nodes.length; i++){
				for(int j = 0; j < nodes[i].getNeighborAmount(); j++){
					int neighborID = nodes[i].getNeighbor(j).getNodeID();
					graph[i].addNeighbor(graph[neighborID]);
				}
			}
			return graph;
		}
		return null;
	}

	/**
	 * This method should never be called before generateNetwork()
	 * @return a copy of the graph without the true card on each node
	 */
	public Node[] generateHiddenGraph(){
		if(graphGenerated){
			Node[] playerGraph = new Node[nodes.length];
			for(int i = 0; i < nodes.length; i++)
				playerGraph[i] = nodes[i].getHiddenNode();
			for(int i = 0; i < nodes.length; i++){
				for(int j = 0; j < nodes[i].getNeighborAmount(); j++){
					int neighborID = nodes[i].getNeighbor(j).getNodeID();
					playerGraph[i].addNeighbor(playerGraph[neighborID]);
				}
			}
			return playerGraph;
		}
		return null;
	}
}
