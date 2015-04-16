package CardPickup;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
/**
 * Creates graphs of nodes that contain cards and possible cards
 *
 * @author      Porag Chowdhury, Anjon Basak, Oscar Veliz, Marcus Gutierezz
 * @version     2015/04/12
 */

public class Graph {
	private int name;
	private String fullGraphName;//for when the graph is modified by an agent i.e. Miners-1
	private Node[] nodes = new Node[Parameters.NUMBER_OF_NODES];
	private Hand[] hand;

	/**
	 * Constructor used by Game master to initialize graph.
	 * @param graphName An integer indicates graph name
	 */
	public Graph(int graphName)
	{
		name = graphName;
		fullGraphName = ""+name;//for now
		for(int i=0; i<Parameters.NUMBER_OF_NODES; i++)
		{
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
	}

	/**
	 * Returns graph name.
	 * @return graph name
	 */
	public int getName() {
		return name;
	}

	/**
	 * Sets full graph name.
	 * @param name graph name
	 */
	public void setName(String name) {
		fullGraphName = name;
	}

	/**
	 * Sets graph name.
	 * @param name graph name
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

    /**
     * Hands are dealt first and need to be saved
     * @param player player number
     * @param c1 first card
     * @param c2 second card
     */
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

    /**
     * Default save of graph without possible cards
     */
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
        for (Node node : this.nodes) {
            while (true) {
                int id = rand.nextInt(nodes.length);
                if ((assigned.size() == 0) || (!assigned.contains(id))) {
                    node.setNodeID(id);
                    assigned.add(id);
                    break;
                }

            }
        }
	}

	/**
	 * Generates a random graph
	 */
	public void generateGraph()
	{
		//Network network = new Network(networkName, numNodes);
		Random r = new Random(name);
		int [][] adjacencyMatrix = new int[Parameters.NUMBER_OF_NODES][Parameters.NUMBER_OF_NODES];
		for(int i =0; i<nodes.length; i++)
			Arrays.fill(adjacencyMatrix[i], 0);
		ArrayList<Integer> completedNodes = new ArrayList<Integer>();
		ArrayList<Integer> tmpNodeStack = new ArrayList<Integer>();
		int currentIndex = 0;
		for (int i = 0; i < nodes.length; i++) {
			int localMax = r.nextInt(Parameters.MAX_NEIGHBORS - Parameters.MIN_NEIGHBORS) + Parameters.MIN_NEIGHBORS;
			int neighborCounter = 0;
			ArrayList<Integer> tmpNeighbors = new ArrayList<Integer>();
			ArrayList<Integer> rejectedNeighbors = new ArrayList<Integer>();
			while(true)
			{
				int nodeIndex = r.nextInt(nodes.length);
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
						adjacencyMatrix[currentIndex][nodeIndex] = 1;
						adjacencyMatrix[nodeIndex][currentIndex] = 1;
						tmpNeighbors.add(nodeIndex);
						if (!tmpNodeStack.contains(nodeIndex))
							tmpNodeStack.add(nodeIndex);
						neighborCounter++;
						if(neighborCounter==localMax)
							break;
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
        //code oscar added
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
		printHand(1,p1c1,p1c2);//saves hand to files
		printHand(2,p2c1,p2c2);

		//deal as many cards as possible to network
        for (Node node : nodes) {
            node.setCard(deck.remove(0));
            for (int p = 1; p < Parameters.NUM_POSSIBLE_CARDS; p++)
                node.addPossible(new Card(r.nextInt(13) + 1, r.nextInt(4) + 1));
            node.shufflePossibleCards();
        }

		//I think this code was a fail safe to make sure that nodes were connected.
		for(int i=0; i<nodes.length; i++)
		{
			if(nodes[i].neighbor.size()==0)
			{
				//add some random neighbor
				int neighborCounter = 0;
				while(true)
				{
					int nodeID = r.nextInt(nodes.length-1);
					if(i!=nodeID)
					{
						if(neighborCounter==2)//also not sure why this needs to be 2. Need to ask Porag about this.
							break;
						if(nodes[i].neighbor.size()==0)
						{
							nodes[i].neighbor.add(nodes[nodeID]);
							nodes[nodeID].neighbor.add(nodes[i]);
							neighborCounter++;
						}
						else if((nodes[i].neighbor.size()>0) && !(nodes[i].neighbor.contains(nodes[nodeID])))
						{
							nodes[i].neighbor.add(nodes[nodeID]);
							nodes[nodeID].neighbor.add(nodes[i]);
							neighborCounter++;
						}

					}
				}

			}
		}
	}

	/**
	 * This method should never be called before generateGraph()
	 * @return a copy of the graph with the true card and possible cards on each node
	 */
	public Node[] generateCopyGraph(){
        Node[] graph = new Node[nodes.length];
        for(int i = 0; i < nodes.length; i++){
            graph[i] = nodes[i].copyNode();
        }
        for(int i = 0; i < nodes.length; i++){
            for(int j = 0; j < nodes[i].getNeighborAmount(); j++){
                int neighborID = nodes[i].getNeighbor(j).getNodeID();
                graph[i].addNeighbor(graph[neighborID]);
            }
        }
        return graph;
	}

	/**
	 * This method should never be called before generateGraph()
	 * @return a copy of the graph without the true card on each node
	 */
	public Node[] generateHiddenGraph(){
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

    /**
     * Returns the nodes
     * @return the nodes
     */
    public Node[] getNodes(){
        return nodes;
    }
}
