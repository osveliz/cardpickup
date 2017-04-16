package CardPickup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * Make sure you add the CSV jar to your library. Should work already if you're using ant.
 * @author Porag (original), Oscar (updated)
 */

public class Parser
{
	/**
	 * Given a .graph file, a network is generated following a predetermined format
	 * 
	 * @param filename file name of graph
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
                    {
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
     * Parses a hand
     * @param fileNumber ie 0.graph
     * @param player 1 or 2
     * @return the hand
     */
    public static Hand parseHand(int fileNumber, int player){
        boolean deleteJunkFile = false;
        String filename = fileNumber+"-"+player+".cards";
        Hand h = new Hand();
        try
        {
            File gFile = new File(filename);
            if(!gFile.exists()){
                gFile.createNewFile();
                deleteJunkFile = true;
            }
            CSVParser parser = CSVParser.parse(gFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
            CSVParser parseRecords= CSVParser.parse(gFile, StandardCharsets.US_ASCII, CSVFormat.DEFAULT);
            for (CSVRecord csvRecord : parser) {
                Iterator<String> itr = csvRecord.iterator();
                h.addHoleCard(new Card(itr.next()));
            }
            if(deleteJunkFile)
                gFile.delete();
            parseRecords.close();
            parser.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return h;
    }
}
