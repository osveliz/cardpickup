package CardPickup;

import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Calculates stats and performance of Defenders and Attackers
 * @author Oscar Veliz
 * @version 2014/11/07
 */
public class Analyzer
{
    private int[][] points;
    private String[] defenderNames;
    private String[] attackerNames;
    private int numAttackers;
    private int numDefenders;
    private double[] aAverage;
    private double[] dAverage;
    private double[] aMedian;
    private double[] dMedian;
    private double[] aStd;
    private double[] dStd;
    private double[] aRegret;
    private double[] dRegret;
    private double[] aBestOf;
    private double[] dBestOf;

    /**
     * Calculates important values for comparing attackers and defenders
     * @param results point value 2dArray with row as defenders and columns as attackers
     * @param aNames the names of the attackers as they are in the results
     * @param dNames the names of the defenders as they are in the results
     */
    public Analyzer(int[][] results, String[] aNames, String[] dNames)
    {
        points = results;
        defenderNames = dNames;
        attackerNames = aNames;
        numDefenders = dNames.length;
        numAttackers = aNames.length;

        int[] aTotals = new int[numAttackers];
        int[] dTotals = new int[numDefenders];
        //calculate totals
        for(int d = 0; d < points.length; d++)
            for(int a = 0; a < points[d].length; a++)
            {
                dTotals[d] += points[d][a];
                aTotals[a] += points[d][a];
            }

        //calculate averages
        //assumes arrays may not be the same length
        aAverage = new double[aTotals.length];
        dAverage = new double[dTotals.length];
        for(int a = 0; a < numAttackers; a++)
            aAverage[a] = (double) aTotals[a] / numDefenders;
        for(int d = 0; d < dAverage.length; d++)
            dAverage[d] = (double) dTotals[d] / numAttackers;

        //calculate medians
        aMedian = new double[numAttackers];
        dMedian = new double[numDefenders];
        for(int i = 0; i < dMedian.length; i++)
            dMedian[i] = median(points[i]);
        for(int j = 0; j < aMedian.length;j++)
        {
            int[] a  = new int[points.length];
            for(int i = 0; i < a.length; i++)
                a[i] = points[i][j];
            aMedian[j] = median(a);
        }

        //calculate standard deviations
        aStd = new double[numAttackers];
        dStd = new double[numDefenders];
        for(int d = 0; d < numDefenders; d++)
            dStd[d] = std(points[d],dAverage[d]);
        for(int a = 0; a < numAttackers; a++)
        {
            int[] x = new int[numDefenders];
            for(int d = 0; d < numDefenders; d++)
                x[d] = points[d][a];
            aStd[a] = std(x,aAverage[a]);
        }

        //calculate regrets and best of
        aRegret = new double[numAttackers];
        aBestOf = new double[numAttackers];
        dRegret = new double[numDefenders];
        dBestOf = new double[numDefenders];
        //for attacker
        for (int[] point : points) {
            int max = maximum(point);
            for (int a = 0; a < point.length; a++) {
                aRegret[a] += (max - point[a]) / (double) numDefenders;
                if (point[a] == max)
                    aBestOf[a]++;
            }
        }
        //for defender
        for(int a = 0; a < numAttackers; a++)
        {
            int[] p = new int[numDefenders];
            for(int i = 0; i < numDefenders; i++)
                p[i] = points[i][a];
            int min = minimum(p);
            for(int d = 0; d < numDefenders; d++)
            {
                dRegret[d] += (p[d] - min) / (double) numAttackers;
                if(p[d] == min)
                    dBestOf[d]++;
            }
        }
        //print out results
        printResults();
        printAverages();
        printMedians();
        printStandardDev();
        printRegret();
        printBestOf();
    }

    /**
     * Prints out the contents of array a and the names in array s
     * @param a general array of values
     * @param s names that correspond to array a
     */
    public void print(double[] a, String[] s)
    {
        double[] ac = Arrays.copyOf(a,a.length);
        String[] sc = Arrays.copyOf(s,s.length);
        sort(ac,sc);
        System.out.println(Arrays.toString(sc));
        System.out.println(Arrays.toString(ac));
    }

    /**
     * Outputs in a 2D way the contents of the results
     */
    public void printResults()
    {
        System.out.print("\t");
        for (String attackerName : attackerNames)
            System.out.print(attackerName + "\t");
        System.out.println();
        for(int i = 0; i < points.length; i++)
        {
            System.out.print(defenderNames[i]+"\t");
            for(int j = 0; j < points[i].length; j++)
                System.out.print(points[i][j]+"\t\t");
            System.out.println();
        }
    }

    /**
     * Outputs Averages
     */
    public void printAverages()
    {
        System.out.println("Average Points");
        print(aAverage,attackerNames);
        print(dAverage,defenderNames);
    }

    /**
     * Outputs Medians
     */
    public void printMedians()
    {
        System.out.println("Medians");
        print(aMedian, attackerNames);
        print(dMedian,defenderNames);
    }

    /**
     * Outputs Standard Deviations
     */
    public void printStandardDev()
    {
        System.out.println("Standard Deviations");
        print(aStd,attackerNames);
        print(dStd,defenderNames);
    }

    /**
     * Outputs Regrets
     */
    public void printRegret()
    {
        System.out.println("Average Regret");
        print(aRegret,attackerNames);
        print(dRegret,defenderNames);
    }

    /**
     * Outputs the number of times an agent did the best
     */
    public void printBestOf()
    {
        System.out.println("Instances Where Agent Was The Best");
        print(aBestOf,attackerNames);
        print(dBestOf,defenderNames);
    }

    /**
     * Sorts array a and s based on array a
     * @param a general array of values
     * @param s names for the elements in a
     */
    public void sort(double[] a, String[] s)
    {
        for(int i = 0; i < a.length; i++)
        {
            for(int j = 0; j < a.length; j++)
            {
                if(a[j] < a[i])
                {
                    double tempA = a[j];
                    String tempS = s[j];
                    a[j] = a[i];
                    s[j] = s[i];
                    a[i] = tempA;
                    s[i] = tempS;
                }
            }
        }
    }

    /**
     * Computes the median of array a
     * @param a general array of values
     * @return median of a
     */
    public double median(int[] a)
    {
        int[] m = Arrays.copyOf(a,a.length);
        Arrays.sort(m);
        if(m.length%2==0)//even
            return (m[m.length/2-1]+m[m.length/2])/2.0;
        else//odd
            return m[m.length/2];
    }

    /**
     * Computes the standard deviation given an array a and an average
     * @param a general array of values
     * @param avg average of a
     * @return standard deviation of a
     */
    public double std(int[] a, double avg)
    {
        int[] m = Arrays.copyOf(a,a.length);
        double var = 0;
        for (int aM : m)
            var += Math.pow((double) aM - avg, 2);
        return Math.sqrt(var/m.length);
    }

    /**
     * Find the maximum value of a
     * @param a general array of values
     * @return maximum value in a
     */
    public int maximum(int[] a)
    {
        int max = a[0];
        for(int i = 1; i < a.length; i++)
            if(max < a[i])
                max = a[i];
        return max;
    }

    /**
     * Finds the minimum value of a
     * @param a general array of values
     * @return minimum value of a
     */
    public int minimum(int[] a)
    {
        int min = a[0];
        for(int i = 1; i < a.length; i++)
            if(min > a[i])
                min = a[i];
        return  min;
    }

    public void savePoints()
    {
        savePoints(0);
    }
    public void savePoints(int name)
    {
        if(numDefenders==0 || numAttackers==0)
            return;
        try{
            PrintWriter pw = new PrintWriter("tournament-"+name+".points", "UTF-8");
            pw.write(defenderNames[0]);
            for(int d = 1; d < numDefenders;d++)
                pw.write(","+defenderNames[d]);
            pw.println();
            pw.write(attackerNames[0]);
            for(int a = 1; a < numAttackers;a++)
                pw.write(","+attackerNames[a]);
            pw.println();
            for(int d = 0; d < numDefenders;d++)
            {
                pw.write(""+points[d][0]);
                for(int a = 1; a < numAttackers; a++)
                {
                    pw.write(","+points[d][a]);
                }
                pw.println();
            }
            pw.close();
        }catch (Exception e){e.printStackTrace();}

    }
}
