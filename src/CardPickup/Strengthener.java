package CardPickup;

import java.util.Random;

/**
 * Example Defender Agent that strengthens at random
 * @author Porag Chowdhury
 */
public class Strengthener extends Defender
{
    public Strengthener(String graphFile)
    {
        super("Strengthener",graphFile);
    }

    /**
     * Defender's logic goes here. Remember to close the defence file when done by calling dh.close()
     */
    public void makeMoves()
    {
    	Random r = new Random();
    	while(dh.getBudget()>0)
    	{
	    	int tries = 0;
	        int node = r.nextInt(net.getSize());
	        while(!dh.isValidStrengthen(node) && tries++ < 10)
	            node = r.nextInt();
	        strengthen(node);
    	}
    	dh.close();
    }
}
