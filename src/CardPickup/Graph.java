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
 * @version     2018/04/10
 */

public class Graph {
	private int name;
	private Parameters p;
	private String fullGraphName;//for when the graph is modified by an agent i.e. Miners-1
	private Node[] nodes;
	private Hand[] hands;
	private int[][] weights;
	private int[][] adjacencyMatrix;
	
	/**
	 * empty constructor
	 */
	public Graph(){
		
	}

	/**
	 * Constructor used by Game master to initialize graph.
	 * @param graphName An integer indicates graph name
	 * @param param the parameters
	 */
	public Graph(int graphName, Parameters param){
		p = param;
		name = graphName;
		fullGraphName = ""+name;
		nodes = new Node[p.NUMBER_OF_NODES];
		weights = new int[p.NUMBER_OF_NODES][p.NUMBER_OF_NODES];
		for(int i=0; i<p.NUMBER_OF_NODES; i++){
			nodes[i] = new Node();
			nodes[i].setNodeID(i);
		}
		adjacencyMatrix = new int[p.NUMBER_OF_NODES][p.NUMBER_OF_NODES];
	}

	/**
	 * Returns graph name.
	 * @return graph name
	 */
	public int getName() {
		return name;
	}
	
	/**
	 * set the adjacency matrix
	 * @param matrix the matrix to copy
	 * @param ns the nodes to copy
	 */
	public void setMatrix(int[][] matrix, Node[] ns){
		adjacencyMatrix = new int[matrix.length][matrix.length];
		for(int i = 0; i < matrix.length; i++)
			for(int j = 0; j < matrix[i].length; j++)
				adjacencyMatrix[i][j] = matrix[i][j];
		//printMatrix();
		nodes = new Node[matrix.length];
		for(int n = 0; n < nodes.length; n++){
			nodes[n] = new Node(n);
			nodes[n].setTrueCard(new Card(ns[n].getCard().toString()));
		}
		for(int n = 0; n < nodes.length; n++)
			for(int k = 0; k < matrix.length; k++)
				if(matrix[n][k] != 0)
					nodes[n].addNeighbor(nodes[k]);
	}
	

	/**
	 * Sets full graph name.
	 * @param name graph name
	 */
	public void setName(String name) {
		fullGraphName = name;
	}
	
	/**
	 * Update the parameters
	 * @param param the parameters
	 */
	public void setParameters(Parameters param){
		p = param;
	}
	
	/**
	 * Get initial budget from parameters
	 * @return the initial budget
	 */
	public int getInitalBudget(){
		return p.BUDGET;
	}
	
	/**
	 * 1 for player 1
	 * 2 for player 2
	 * @param player player number 1 or 2
	 * @return player's hand
	 */
	public Hand getHand(int player){
		return hands[player-1];
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
	public Node getNode(int nodeId){
		if(nodeId >= nodes.length || nodeId < 0)
			return null;
		for (Node node : nodes){
			if (node.getNodeID() == nodeId)
				return node;
		}
		return null;
	}

	/**
	 * Returns boolean validating a node to be eligible for Neighbor or not
	 * @param currentIndex An integer indicates current node id
	 * @param neighborIndex An integer indicates neighbor node id
	 * @return boolean True/False validating a node to be eligible for Neighbor or not
	 */
	public boolean isAllowedToBeNeighbor(int currentIndex, int neighborIndex){
		if (currentIndex == neighborIndex)
			return false;
		int neighborCount = 0;
		for(int i=0; i < adjacencyMatrix[neighborIndex].length; ++i)
		{
			if (adjacencyMatrix[neighborIndex][i] == 1)
				neighborCount++;
		}
		return neighborCount < p.MAX_NEIGHBORS;
	}

	/**
	 * Returns size of the network
	 * @return size of the network i.e. number of total nodes
	 */
	public int getSize(){
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
	 * print both hands
	 */
	public void printHands(){
		Card p1c1 = hands[0].getHoleCard(0);
		Card p1c2 = hands[0].getHoleCard(1);
		Card p2c1 = hands[1].getHoleCard(0);
		Card p2c2 = hands[1].getHoleCard(1);
		printHand(1,p1c1,p1c2);//saves hand to files
		printHand(2,p2c1,p2c2);
	}

	/**
	 * Print graph in a file
	 * @param usePossibleCardSet when true saves to name.hidden and saves the possible card set instead of the card
	 */
	public void saveGraph(boolean usePossibleCardSet){
		PrintWriter writer;
		try {
			if(usePossibleCardSet)
				writer = new PrintWriter(fullGraphName + ".hidden", "UTF-8");
			else
				writer = new PrintWriter(fullGraphName + ".graph", "UTF-8");
			for (int i = 0; i < nodes.length; i++){
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
	public void shuffleNetwork(){
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
	public void generateGraph(){
		Random r = new Random(name);
		//int [][] adjacencyMatrix = new int[p.NUMBER_OF_NODES][p.NUMBER_OF_NODES];
		for(int i =0; i<nodes.length; i++)
			Arrays.fill(adjacencyMatrix[i], 0);
		ArrayList<Integer> completedNodes = new ArrayList<Integer>();
		ArrayList<Integer> tmpNodeStack = new ArrayList<Integer>();
		int currentIndex = 0;
		for (int i = 0; i < nodes.length; i++) {
			int localMax = r.nextInt(p.MAX_NEIGHBORS - p.MIN_NEIGHBORS) + p.MIN_NEIGHBORS;
			int neighborCounter = 0;
			ArrayList<Integer> tmpNeighbors = new ArrayList<Integer>();
			ArrayList<Integer> rejectedNeighbors = new ArrayList<Integer>();
			while(true){
				int nodeIndex = r.nextInt(nodes.length);
				int totalNeighbors = 0;
				for(int k=0; k<nodes.length; k++)
					if(adjacencyMatrix[currentIndex][k]==1)
						totalNeighbors++;
				if(totalNeighbors==p.MAX_NEIGHBORS)
					break;
				if(rejectedNeighbors.size()>0)
					if(rejectedNeighbors.size()==(nodes.length-tmpNeighbors.size()-1))
						break;
				if (isAllowedToBeNeighbor(currentIndex, nodeIndex)){
					if((tmpNeighbors.size()>0 && !tmpNeighbors.contains(nodeIndex) && tmpNeighbors.size() < p.MAX_NEIGHBORS) || tmpNeighbors.size()==0){
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
			while(true){
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

		for (int i = 0; i < nodes.length; ++i){
			Node tempNode = getNode(i);
			for(int j = 0; j < nodes.length; ++j){
				if (adjacencyMatrix[i][j] == 1){
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
		hands = new Hand[2];
		hands[0] = new Hand();
		hands[1] = new Hand();
		hands[0].addHoleCard(new Card(p1c1.toString()));
		hands[0].addHoleCard(new Card(p1c2.toString()));
		hands[1].addHoleCard(new Card(p2c1.toString()));
		hands[1].addHoleCard(new Card(p2c2.toString()));
		printHand(1,p1c1,p1c2);//saves hand to files
		printHand(2,p2c1,p2c2);

		//deal as many cards as possible to network
        for (Node node : nodes) {
            node.setCard(deck.remove(0));
            for (int c = 1; c < p.NUM_POSSIBLE_CARDS; c++)
                node.addPossible(new Card(r.nextInt(13) + 1, r.nextInt(4) + 1));
            node.shufflePossibleCards();
        }

		//Porag - think this code was a fail safe to make sure that nodes were connected.
		for(int i=0; i<nodes.length; i++){
			if(nodes[i].neighbor.size()==0){
				//add some random neighbor
				int neighborCounter = 0;
				while(true){
					int nodeID = r.nextInt(nodes.length-1);
					if(i!=nodeID){
						if(neighborCounter==2)//also not sure why this needs to be 2. Need to ask Porag about this.
							break;
						if(nodes[i].neighbor.size()==0){
							nodes[i].neighbor.add(nodes[nodeID]);
							nodes[nodeID].neighbor.add(nodes[i]);
							neighborCounter++;
						}
						else if((nodes[i].neighbor.size()>0) && !(nodes[i].neighbor.contains(nodes[nodeID]))){
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
	 * Obfuscate the graph for the player
	 */
	public void hide(){
		Random r = new Random();
		for(int i = 0; i < nodes.length; i++){
			nodes[i].clearCard();
			ArrayList<Card> possible = nodes[i].getPossibleCards();
			if(possible.size()>0)
				for(int j = 0; j < 17; j++){//17 shuffles
					Card c = possible.remove(0);
					possible.add(r.nextInt(possible.size()),c);
				}			
		}
		
	}

    /**
     * Returns the nodes
     * @return the nodes
     */
    public Node[] getNodes(){
        return nodes;
    }
    
    /**
     * Returns the weights
     * @return the weights
     */
    public int[][] getWeights(){
		//return weights;
		return adjacencyMatrix;
	}
	
	/**
	 * copies all of the card data from a list of nodes
	 * @param newNodes the new nodes
	 */
	public void setCards(Node[] newNodes){
		if(newNodes == null){
			System.out.println("error");
			return;
		}
			
		for(int n = 0; n < nodes.length; n++){
			if(newNodes[n] == null){
				System.out.println("danger");
				return;
			}
			if(newNodes[n].getCard()==null)
				return;
			nodes[n].setTrueCard(new Card(newNodes[n].getCard().toString()));
			ArrayList<Card> cards = newNodes[n].getPossibleCards();
			for(int k = 0; k < cards.size(); k++)
				nodes[n].addPossible(new Card(cards.get(k).toString()));
		}
	}
	/**
	 * copy hands
	 * @param newHands the new hands
	 */
	public void setHands(Hand[] newHands){
		hands = new Hand[2];
		hands[0] = new Hand();
		hands[1] = new Hand();
		if (newHands == null||newHands[0] == null || newHands[1] == null){
				return;
		}
		else{	
			for(int p = 0; p < newHands.length; p++){
				for(int c = 0; c < newHands[p].getNumHole(); c++)
					hands[p].addHoleCard(new Card((newHands[p].getHoleCard(c)).toString()));
			}
		}
	}
	
	/**
	 * Update all of the weights in the adjacency matrix
	 */
	public void computeWeights(){
		Random r = new Random(name);
		int max = p.MAX_WEIGHT;
		int n = p.NUMBER_OF_NODES;
		//reset the matrix
		for(int i = 0; i < n; i++)
			for(int j = 0; j < n; j++)
				adjacencyMatrix[i][j] = -1;
		//reconnect the matrix
		for(int i = 0; i < n; i++){
			Node x = nodes[i];
			int id = x.getNodeID();
			ArrayList<Node> neigh = x.getNeighborList();
			for(int j = 0; j < neigh.size(); j++){
				int xid = neigh.get(j).getNodeID();
				adjacencyMatrix[id][xid] = 1;
			}
		}
		for(int i = 0; i < n; i++){
			for(int j = 0; j < n; j++){
				if(adjacencyMatrix[i][j] <= 0)
					adjacencyMatrix[i][j] = -1;//cannot connect
				else{
					if(i <= j)
						adjacencyMatrix[i][j] = r.nextInt(max)+1;
					else
						adjacencyMatrix[i][j] = adjacencyMatrix[j][i];
				}
			}
		}
		resetNeighbors();
	}
	
	public void resetNeighbors(){
		int n = p.NUMBER_OF_NODES;
		for(int i = 0; i < n; i++){
			nodes[i].clearNeighbors();
			for(int j = 0; j < n; j++){
				if(adjacencyMatrix[i][j] > 0)
					nodes[i].addNeighbor(nodes[j]);
			}
		}
	}
	
	/**
	 * prints the adjacency matrix
	 */
	public void printMatrix(){
		for(int i = 0; i < adjacencyMatrix.length; i++){
			for(int j = 0; j < adjacencyMatrix[i].length; j++)
				System.out.print(adjacencyMatrix[i][j]+"\t");
			System.out.println();
		}
	}
	
	/**
	 * cloner
	 * @return clone of graph
	 */
	public Graph clone(){
		Graph copy = new Graph(name, p);
		copy.generateGraph();
		copy.setParameters(p.clone());
		copy.setName(this.fullGraphName+"");
		copy.setMatrix(this.adjacencyMatrix, this.nodes);
		copy.setCards(this.nodes);
		copy.setHands(this.hands);
		return copy;
	}
	
	/**
	 * Cost between nodes
	 * @param id1 node id 1
	 * @param id2 node id 2
	 * @return -1 if not connected, otherwise the cost
	 */
	public int cost(int id1, int id2){
		return adjacencyMatrix[id1][id2];
	}
}
