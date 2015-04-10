package CardPickup;

import java.util.Random;

/**
 * BenchMark Defender Agent that takes rational decision based on the defender action cost
 * @author Porag Chowdhury, Anjan Basak
 */

public class RationalDefender extends Defender{
	
    public RationalDefender(String graphFile)
    {
        super("RationalDefender",graphFile);
    }
	
	public void makeMoves()
	{
	    int nh = (Parameters.DEFENDER_BUDGET / 2) / Parameters.HONEYPOT_RATE;
	    int nominator = Math.abs(Parameters.DEFENDER_BUDGET - (nh*Parameters.HONEYPOT_RATE) - (Parameters.STRENGTHEN_RATE*net.getSize()));
	    int denominator = Math.abs(Parameters.FIREWALL_RATE - Parameters.STRENGTHEN_RATE);
	    
	    int nf = (int)Math.floor(nominator/denominator);
	    int ns = net.getSize()- nf - Parameters.NUMBER_OF_PUBLIC_NODES;
	    int i;
	    int j;
	    int inc = 0;
	    for(i=0; i < (nf+inc); i++)
	    {
	    	int nodeID = i % net.getSize();
	    	int nodeNeighbourID = (i+1) % net.getSize();
        	if(net.getNode(nodeID).getPv() > 0 && net.getNode(nodeID).getPv() > 0)
        		firewall(nodeID, nodeNeighbourID);
	    	else 
	    		inc++;
	    }

	    inc = 0;
        for(j=i; j<(i+ns+inc); j++)
	    {
        	int nodeID = j % net.getSize();
        	if(net.getNode(nodeID).getPv() > 0 && net.getNode(nodeID).getPv() > 0)
        		strengthen(nodeID);
	    	else 
	    		inc++;
	    }

	    Random r = new Random();
	    while (nh > 0)
	    {
		    int sv = r.nextInt(19)+1;
	        int pv = r.nextInt(20);
		    int numHoneypotNeighbors = (int)(net.getSize()*0.25);
		    int[] list =  new int[numHoneypotNeighbors];
		    int nn = r.nextInt(net.getSize()); 
	        for(int k = 0; k < list.length; k++)
	        {
	        	list[k] = nn % net.getSize();
	        	nn++;
	        }
	        honeypot(sv, pv, list);
	        nh--;
	    }
        
        while(dh.getBudget() > 0)
        {
        	int nodeID = r.nextInt(net.getSize());
        	if(Parameters.FIREWALL_RATE > Parameters.STRENGTHEN_RATE && Parameters.FIREWALL_RATE > Parameters.HONEYPOT_RATE)
        	{
        		int neighbourID = (nodeID +1) % net.getSize();
        		firewall(nodeID , neighbourID);
        	}
        	else{
        		strengthen(nodeID );
        	}
        }
        dh.close();
	 }
}
