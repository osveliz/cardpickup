package CardPickup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Parser
{
	
	/**
	 * Given a .graph file, a network is generated following a predetermined format
	 * 
	 * @param filename
	 * @return a Network object based on the .graph file given
	 */
	public static Graph parseGraph(String filename)
	{
		boolean deleteJunkFile = false;
        boolean usePossibleCardSet = false;
		try 
		{
            if(filename.endsWith(".hidden"))
                usePossibleCardSet = true;
			File gFile = new File(filename);
			if(!gFile.exists()){
				gFile.createNewFile();
				deleteJunkFile = true;
			}
			CSVParser parser = CSVParser.parse(gFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			CSVParser parseRecords= CSVParser.parse(gFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			int neighborsCounter = 0;
			int numNodes = parseRecords.getRecords().size()/2;
            //Graph graph = new Graph(0,numNodes);
            Graph graph = new Graph(-1);
            graph.setName(filename.substring(0,filename.indexOf(".")));
			boolean flag = false;
			for (CSVRecord csvRecord : parser) 
			{
				Iterator<String> itr = csvRecord.iterator();
				if((neighborsCounter<numNodes) && flag==false)
				{
					Node node = graph.getNode(neighborsCounter);
					while(itr.hasNext())
					{
						int x = Integer.parseInt(itr.next());
						if(x >= 0){
							Node neighbor = graph.getNode(x);
							node.addNeighbor(neighbor);
						}
					}
					if(neighborsCounter==numNodes-1)
					{
						flag = true;
						neighborsCounter=0;
					}
					else
						neighborsCounter++;
				}
				else if(flag && (neighborsCounter<numNodes))
				{
					Node node = graph.getNode(neighborsCounter);
					//while(itr.hasNext())
					{
						/*int x  = Integer.parseInt(itr.next());
						node.setPv(x);
						int y = Integer.parseInt(itr.next());
						node.setSv(y);
						int z = Integer.parseInt(itr.next());
						node.setHoneyPot(z);*/
                        if(usePossibleCardSet)
                            for(int i = 0; i < Parameters.NUM_POSSIBLE_CARDS; i++)
                                node.addPossible(new Card(itr.next()));
                        else
                            node.setCard(new Card(itr.next()));
					}
					neighborsCounter++;
				}
			}
			if(deleteJunkFile)
				gFile.delete();
			return graph;
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		return null;
	}
    /**
	 * Parses attacker's .history file and calculates and returns the attacker's network
	 * @param attackerName
	 * @param defenderName
	 * @param graphName
	 * @return attacker's visible graph
	 */
	public static Graph parseAttackerHistory(String attackerName, String defenderName, String graphName)
	{
		Graph hidden = parseGraph(defenderName + "-" + graphName + "-hidden.graph");
		String historyFile = attackerName + "-" + defenderName + "-" + graphName + ".history";
		File csvTrainData = new File(historyFile);
		try
		{
			boolean deleteJunkFile = false;
			File hFile = new File(historyFile);
			if(!hFile.exists()){
				hFile.createNewFile();
				deleteJunkFile = true;
			}
			CSVParser parser = CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : parser)
			{
				Iterator<String> itr = csvRecord.iterator();
				int move = Integer.parseInt(itr.next());
				int id;
				CardPickup.Node node;
				boolean attackSuccess;
				int roll;
				int sv;
				int pv;
				int hp;
				switch(move){
				case 0: //attack
					//budget -= Parameters.ATTACK_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					attackSuccess = Boolean.parseBoolean(itr.next());
					roll = Integer.parseInt(itr.next());
					if(roll > node.getBestRoll())
						node.setBestRoll(roll);
					if(attackSuccess){
						pv = Integer.parseInt(itr.next());
						node.setPv(pv);
						sv = Integer.parseInt(itr.next());
						node.setSv(sv);
						hp = Integer.parseInt(itr.next());
						node.setHoneyPot(hp);
						node.setNeighborAmount(0);
						node.setCaptured(true);
						if(hp != 1){
							while(itr.hasNext())
								node.addNeighbor(hidden.getNode(Integer.parseInt(itr.next())));
						}
					}
					break;
				case 1: //superattack
					//budget -= Parameters.SUPERATTACK_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					attackSuccess = Boolean.parseBoolean(itr.next());
					roll = Integer.parseInt(itr.next());
					if(roll > node.getBestRoll())
						node.setBestRoll(roll);
					if(attackSuccess){
						pv = Integer.parseInt(itr.next());
						node.setPv(pv);
						sv = Integer.parseInt(itr.next());
						node.setSv(sv);
						hp = Integer.parseInt(itr.next());
						//System.out.println("(" + pv + "," + sv + "," + hp + ")");
						node.setHoneyPot(hp);
						node.setNeighborAmount(0);
						node.setCaptured(true);
						if(hp != 1){
							while(itr.hasNext())
								node.addNeighbor(hidden.getNode(Integer.parseInt(itr.next())));
						}
					}
					break;
				case 2: //probe security value
					//budget -= Parameters.PROBE_SECURITY_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					sv = Integer.parseInt(itr.next());
					node.setSv(sv);
					break;
				case 3: //probe point value
					//budget -= Parameters.PROBE_POINT_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					pv = Integer.parseInt(itr.next());
					node.setPv(pv);
					break;
				case 4: //probe connections
					//budget -= Parameters.PROBE_CONNECTIONS_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					int connAmt = Integer.parseInt(itr.next());
					node.setNeighborAmount(connAmt);
					break;
				case 5: //probe honeypot
					//budget -= Parameters.PROBE_HONEY_RATE;
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					hp = Integer.parseInt(itr.next());
					node.setHoneyPot(hp);
					break;
				case 6: //public node
					id = Integer.parseInt(itr.next());
					node = hidden.getNode(id);
					node.setCaptured(true);
					node.setNeighborAmount(0);
					while(itr.hasNext())
						node.addNeighbor(hidden.getNode(Integer.parseInt(itr.next())));
					break;
				case -1: default:
					//budget -= Parameters.INVALID_RATE;
					break;
				}
			}
			if(deleteJunkFile)
				hFile.delete();
			return hidden;
		}
		catch(NumberFormatException nfe){ nfe.printStackTrace(); }
		catch (IOException e) { e.printStackTrace();}
		catch (Exception e) { e.printStackTrace(); }
		return hidden;
	}
	
	/**
	 * Parses attacker's .history file and calculates and returns the attacker's budget
	 * 
	 * @param attackerName
	 * @param defenderName
	 * @param graphName
	 * @return budget i.e. integer value
	 */
	public static int parseAttackerBudget(String attackerName, String defenderName, String graphName){;
		String historyFile = attackerName + "-" + defenderName + "-" + graphName + ".history";
		int budget = CardPickup.Parameters.ATTACKER_BUDGET;
		try
		{
			File csvTrainData = new File(historyFile);
			CSVParser parser = CSVParser.parse(csvTrainData, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
			for (CSVRecord csvRecord : parser)
			{
				Iterator<String> itr = csvRecord.iterator();
				int move = Integer.parseInt(itr.next());
				
				switch(move){
				case 0: //attack
					budget -= CardPickup.Parameters.ATTACK_RATE;
					break;
				case 1: //superattack
					budget -= CardPickup.Parameters.SUPERATTACK_RATE;
					break;
				case 2: //probe security value
					budget -= CardPickup.Parameters.PROBE_SECURITY_RATE;
					break;
				case 3: //probe point value
					budget -= CardPickup.Parameters.PROBE_POINT_RATE;
					break;
				case 4: //probe connections
					budget -= CardPickup.Parameters.PROBE_CONNECTIONS_RATE;
					break;
				case 5: //probe honeypot
					budget -= CardPickup.Parameters.PROBE_HONEY_RATE;
					break;
				case -1: //public node
					budget -= CardPickup.Parameters.INVALID_RATE;
					break;
				case 6: default:
					break;
				}
			}
			return budget;
		}
		catch(NumberFormatException nfe){ nfe.printStackTrace(); }
		catch (IOException e) { e.printStackTrace();}
		return budget;
	}
}
