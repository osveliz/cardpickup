package CardPickup;

/**
 * Level 0 BenchMark Defender Agent that takes invalid actions
 * @author Porag Chowdhury, Anjan Basak
 */


public class NumbDefender extends Defender{
	
	   public NumbDefender(String graphFile)
	    {
	        super("NumbDefender",graphFile);
	    }

	    /**
	     * Defender's logic goes here. Remember to close the defence file when done by calling dh.close()
	     */
	    public void makeMoves()
	    {
	    	
	    	int nodeid =-1;
	    	for(int i=0; i<net.getSize(); i++)
	    	{
	    		if(net.getNode(i).getSv()==0 && net.getNode(i).getPv()==0)
	    		{
	    			nodeid = i;
	    			break;
	    		}
	    	}
	    	while(dh.getBudget()>0)
	    	{
	           strengthen(nodeid);
	    	}
	        dh.close();
	    	
	    	
	    }
}
