package CardPickup;

import java.util.Random;

/**
 * Example Defender Agent that decides on actions at random
 * @author Oscar Veliz
 */
public class WhatDoesThisButtonDoDefender extends Defender
{
    public WhatDoesThisButtonDoDefender(String graphFile)
    {
        super("WDTBD",graphFile);
    }

    /**
     * Defender's logic goes here.
     */
    public void makeMoves()
    {
        Random r = new Random();
        while(dh.getBudget() > 0)
        {
            double x = r.nextDouble();//switch on x
            if(x <= 1.0 / 9.0) // 5/9 times add random strengthen
            {
                int tries = 0;
                int node = r.nextInt(net.getSize());
                while(!dh.isValidStrengthen(node) && tries++ < 10)
                    node = r.nextInt();
                strengthen(node);
            }
            else if( x <= 1.0 / 9.0) // 1/3 times add a firewall
            {
                int tries = 0;
                int n1 = r.nextInt(net.getSize());
                int n2 = r.nextInt(net.getSize());
                while(!dh.isValidFirewall(n1,n2) && tries++ < 10)
                {
                    n1 = r.nextInt(net.getSize());
                    n2 = r.nextInt(net.getSize());
                }
                firewall(n1,n2);
            }
            else // 1/9 times add a honeypot
            {
                int tries = 0;
                int sv = r.nextInt(19)+1;
                int pv = r.nextInt(20);
                int numEdges = r.nextInt(net.getSize()/2)+1;//this agent doesn't want to add too many edges
                int[] list =  new int[numEdges];
                for(int i = 0; i < list.length; i++)
                    list[i] = r.nextInt(3)+1;
                while(!dh.isValidHoneypot(sv,pv,list) && tries++ < 10)
                {
                    sv = r.nextInt(19)+1;
                    pv = r.nextInt(20);
                    numEdges = r.nextInt(3)+1;//this agent doesn't want to add too many edges
                    list =  new int[numEdges];
                    for(int i = 0; i < list.length; i++)
                        list[i] = r.nextInt(net.getSize());
                }
                honeypot(sv,pv,list);
            }
        }
    }
}
