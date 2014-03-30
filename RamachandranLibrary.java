import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;
import com.google.common.collect.*;
import com.google.common.primitives.*;

public class RamachandranLibrary
{
    /** the singleton instance */
    public static final RamachandranLibrary INSTANCE = new RamachandranLibrary();
    
    /**
     * stores the Ramachandran data
     */
    private final Map<CustomKey,PreDistribution> database;

    /** read all the Ramachandran data */
    private RamachandranLibrary()
    {
        if (INSTANCE != null)
            throw new IllegalStateException("this should be a singleton!");
        
        // temporary copy of the database
        Map<CustomKey,PreDistribution> tempDatabase = new HashMap<>();
        
        // read data from zipped file
        GZIPInputStream gzip = null;
        BufferedReader br = null;
        try
            {
                gzip = new GZIPInputStream(new FileInputStream(Settings.RAMACHANDRAN_DATA_FILENAME));
                br = new BufferedReader(new InputStreamReader(gzip));

                // keep track of the last line so we know if we've changed blocks
                AminoAcid lastCentralAminoAcid  = AminoAcid.ALA;        // the amino acid in field 0
                Direction lastDirection         = Direction.LEFT;       // left or right in field 1
                AminoAcid lastAdjacentAminoAcid = AminoAcid.ALL;        // the amino acid in field 2
                
                // temporary storage while reading a block
                List<Double> tempPhis                = new LinkedList<>();  // backbone angle phi
                List<Double> tempPsis                = new LinkedList<>();  // backbone angle psi
                List<Double> tempProbabilities       = new LinkedList<>();  // log probabilities

                System.out.println("Reading Ramachandran database...");
                while (true)
                    {
                        String currentLine = br.readLine();
                        
                        // break out when we have reached the end of the file
                        if ( currentLine == null )
                            break;
                        
                        // ignore comments and blank lines
                        String[] fields = currentLine.split("\\s+");
                        if ( currentLine.startsWith("#") || fields.length != 8)
                            continue;
                        
                        // ignore proline, as we will be using cis and trans proline instead
                        if ( fields[0].equals("PRO") || fields[2].equals("PRO") )
                            continue;

                        // parse to enum constants
                        AminoAcid currentCentralAminoAcid = AminoAcid.valueOf(fields[0]);
                        Direction currentDirection = Direction.valueOf(fields[1].toUpperCase());
                        AminoAcid currentAdjacentAminoAcid = AminoAcid.valueOf(fields[2]);

                        // for debugging only -- shortens runtime
                        //if ( ! fields[0].equals("ALA") )
                        //    break;

                        // detect a change in data block
                        if ( lastCentralAminoAcid  != currentCentralAminoAcid ||
                             lastDirection         != currentDirection        ||
                             lastAdjacentAminoAcid != currentAdjacentAminoAcid )
                            {
                                // print status
                                System.out.print( String.format("%5s %5s %5s\r", lastCentralAminoAcid.toString(),
                                                                                 lastDirection.toString(),
                                                                                 lastAdjacentAminoAcid.toString() ) );

                                // create CustomKey object
                                CustomKey customKey = new CustomKey(lastCentralAminoAcid, lastDirection, lastAdjacentAminoAcid);

                                // create PreDistribution object
                                short[] phiArray = Shorts.toArray(tempPhis);
                                short[] psiArray = Shorts.toArray(tempPsis);
                                float[] logProbabilityArray = Floats.toArray(tempProbabilities);
                                PreDistribution preDistribution = new PreDistribution(phiArray, psiArray, logProbabilityArray);

                                // add to database
                                tempDatabase.put(customKey, preDistribution);

                                // reset lists
                                tempPhis = new LinkedList<>();
                                tempPsis = new LinkedList<>();
                                tempProbabilities = new LinkedList<>();
                            }

                        // parse fields and add to temporary lists
                        tempPhis.add(Double.valueOf(fields[3]));
                        tempPsis.add(Double.valueOf(fields[4]));
                        tempProbabilities.add(Double.valueOf(fields[6]));

                        // remember for the next line
                        lastCentralAminoAcid = currentCentralAminoAcid;
                        lastDirection = currentDirection;
                        lastAdjacentAminoAcid = currentAdjacentAminoAcid;
                    }

                // deal with edge case
                // create CustomKey object
                CustomKey customKey = new CustomKey(lastCentralAminoAcid, lastDirection, lastAdjacentAminoAcid);

                // create PreDistribution object
                short[] phiArray = Shorts.toArray(tempPhis);
                short[] psiArray = Shorts.toArray(tempPsis);
                float[] logProbabilityArray = Floats.toArray(tempProbabilities);
                PreDistribution preDistribution = new PreDistribution(phiArray, psiArray, logProbabilityArray);

                // add to database
                tempDatabase.put(customKey, preDistribution);

                br.close();
                gzip.close();
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        // make dataset immutable
        database = ImmutableMap.copyOf(tempDatabase);
    }

    private enum Direction
    {
        LEFT,
        RIGHT;
    }

    private static class CustomKey
    {
        private AminoAcid centralAminoAcid;
        private Direction direction;
        private AminoAcid adjacentAminoAcid;

        public CustomKey(AminoAcid centralAminoAcid, Direction direction, AminoAcid adjacentAminoAcid)
        {
            this.centralAminoAcid = centralAminoAcid;
            this.direction = direction;
            this.adjacentAminoAcid = adjacentAminoAcid;
        }

        public String toString()
        {
            return String.format("%5s %5s %5s\n", centralAminoAcid.toString(),
                                                       direction.toString(),
                                                       adjacentAminoAcid.toString() );
        }

        public int hashCode()
        {
            return Objects.hash(centralAminoAcid, direction, adjacentAminoAcid);
        }

        public boolean equals(Object obj)
        {
            if ( obj == null )
                return false;
            if ( obj == this )
                return true;
            if ( !(obj instanceof CustomKey) )
                return false;

            CustomKey another = (CustomKey)obj;
            if ( this.centralAminoAcid == another.centralAminoAcid &&
                 this.direction == another.direction &&
                 this.adjacentAminoAcid == another.adjacentAminoAcid )
                return true;
            return false;
        }
    }

    private static class PreDistribution
    {
        private final short[] phis;
        private final short[] psis;
        private final float[] logProbabilities;

        public PreDistribution(short[] phis, short[] psis, float[] logProbabilities)
        {
            this.phis = phis;
            this.psis = psis;
            this.logProbabilities = logProbabilities;
        }

        public String toString()
        {
            String returnString = "[";
            int n = phis.length;
            for (int i=0; i < n - 1; i++)
                returnString = returnString + String.format("%5d %5d %.6f,\n", phis[i], psis[i], logProbabilities[i]);
            returnString = returnString + String.format("%5d %5d %.6f]", phis[n-1], psis[n-1], logProbabilities[n-1]);
            return returnString;
        }

        public int hashCode()
        {
            return Objects.hash(phis,psis,logProbabilities);
        }

        public boolean equals(Object obj)
        {
            if ( obj == null )
                return false;
            if ( obj == this )
                return true;
            if ( !(obj instanceof PreDistribution) )
                return false;

            PreDistribution another = (PreDistribution)obj;
            if ( Arrays.equals(phis, another.phis) &&
                 Arrays.equals(psis, another.psis) &&
                 Arrays.equals(logProbabilities, another.logProbabilities) )
                return true;
            return false;
        }
    }

    public String toString()
    {
        return String.format("Ramachandran database with %d entries", database.size());
    }

    public static PreDistribution locate(AminoAcid centralAminoAcid, Direction direction, AminoAcid adjacentAminoAcid)
    {
        CustomKey thisKey = new CustomKey(centralAminoAcid, direction, adjacentAminoAcid);
        PreDistribution result = null;
        for (CustomKey k : INSTANCE.database.keySet())
            {
                if ( k.equals(thisKey) )
                    {
                        result = INSTANCE.database.get(k);
                        break;
                    }
            }
        return result;
    }

    public static void main(String[] args)
    {
        System.out.println("\nOperation complete.");
        System.out.println(locate(AminoAcid.ALA, Direction.LEFT, AminoAcid.ALL));
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to continue.");
        scanner.nextLine();
    }
}
