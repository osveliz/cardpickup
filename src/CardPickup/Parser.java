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
	
}
