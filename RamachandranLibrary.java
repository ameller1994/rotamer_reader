import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;
import com.google.common.collect.*;

public class RamachandranLibrary
{
    /** the singleton instance */
    public static final RamachandranLibrary INSTANCE = new RamachandranLibrary();
    
    /**
     * stores the Ramachandran data
     */
    private final Set<Entry> database;

    /** read all the Ramachandran data */
    private RamachandranLibrary()
    {
        if (INSTANCE != null)
            throw new IllegalStateException("this should be a singleton!");
        
        // temporary copy of the database
        Set<Entry> tempDatabase = new HashSet<>();
        
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
                List<ShortBackboneAngles> tempAngles = new LinkedList<>();  // backbone angles phi,psi
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

                        // detect a change in data block
                        if ( lastCentralAminoAcid  != currentCentralAminoAcid ||
                             lastDirection         != currentDirection        ||
                             lastAdjacentAminoAcid != currentAdjacentAminoAcid )
                            {
                                // print status
                                System.out.print( String.format("%5s %5s %5s\r", lastCentralAminoAcid.toString(),
                                                                                 lastDirection.toString(),
                                                                                 lastAdjacentAminoAcid.toString() ) );
                                
                                // compile the probability data
                                DiscreteProbabilityDistribution<ShortBackboneAngles> lastDPD
                                    = new DiscreteProbabilityDistribution<>(tempAngles, tempProbabilities);

                                // compile the last data block into an Entry object
                                Entry newEntry = new Entry(lastCentralAminoAcid, lastDirection, lastAdjacentAminoAcid, lastDPD);

                                // add the Entry to the database
                                tempDatabase.add(newEntry);

                                // reset lists
                                tempAngles = new LinkedList<>();
                                tempProbabilities = new LinkedList<>();
                            }

                        // parse fields and add to temporary lists
                        ShortBackboneAngles theseAngles = new ShortBackboneAngles(Short.parseShort(fields[3]),
                                                                                  Short.parseShort(fields[4]));
                        tempAngles.add(theseAngles);
                        tempProbabilities.add(Double.valueOf(fields[6]));

                        // remember for the next line
                        lastCentralAminoAcid = currentCentralAminoAcid;
                        lastDirection = currentDirection;
                        lastAdjacentAminoAcid = currentAdjacentAminoAcid;
                    }

                // deal with edge case
                DiscreteProbabilityDistribution<ShortBackboneAngles> lastDPD
                    = new DiscreteProbabilityDistribution<>(tempAngles, tempProbabilities);

                Entry newEntry = new Entry(lastCentralAminoAcid, lastDirection, lastAdjacentAminoAcid, lastDPD);
                tempDatabase.add(newEntry);

                br.close();
                gzip.close();
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        // make dataset immutable
        database = ImmutableSet.copyOf(tempDatabase);

        System.out.println("\nOperation complete.");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Press enter to continue.");
        scanner.next();
    }

    private enum Direction
    {
        LEFT,
        RIGHT;
    }

    private static class Entry
    {
        private AminoAcid centralAminoAcid;
        private Direction direction;
        private AminoAcid adjacentAminoAcid;
        private DiscreteProbabilityDistribution<ShortBackboneAngles> dpd;

        public Entry(AminoAcid centralAminoAcid, Direction direction,
                     AminoAcid adjacentAminoAcid, DiscreteProbabilityDistribution<ShortBackboneAngles> dpd)
        {
            this.centralAminoAcid = centralAminoAcid;
            this.direction = direction;
            this.adjacentAminoAcid = adjacentAminoAcid;
            this.dpd = dpd;
        }

        public String toString()
        {
            return String.format("%5s %5s %5s \n%s\n", centralAminoAcid.toString(),
                                                       direction.toString(),
                                                       adjacentAminoAcid.toString(),
                                                       dpd.toString() );
        }

        public int hashCode()
        {
            return Objects.hash(centralAminoAcid, direction, adjacentAminoAcid, dpd);
        }

        public boolean equals(Object obj)
        {
            if ( obj == null )
                return false;
            if ( obj == this )
                return true;
            if ( !(obj instanceof ShortBackboneAngles) )
                return false;

            Entry another = (Entry)obj;
            if ( this.centralAminoAcid == another.centralAminoAcid &&
                 this.direction == another.direction &&
                 this.adjacentAminoAcid == another.adjacentAminoAcid &&
                 this.dpd.equals(another.dpd)                           )
                return true;
            return false;
        }
    }

    private static class ShortBackboneAngles
    {
        private short phi;
        private short psi;

        public ShortBackboneAngles(short phi, short psi)
        {
            this.phi = phi;
            this.psi = psi;
        }

        public double getPhi()
        {
            return (double)phi;
        }

        public double getPsi()
        {
            return (double)psi;
        }

        public String toString()
        {
            return String.format("%d, %d", phi, psi);
        }

        public int hashCode()
        {
            return Objects.hash(phi,psi);
        }

        public boolean equals(Object obj)
        {
            if ( obj == null )
                return false;
            if ( obj == this )
                return true;
            if ( !(obj instanceof ShortBackboneAngles) )
                return false;

            ShortBackboneAngles another = (ShortBackboneAngles)obj;
            if ( this.phi == another.phi && this.psi == another.psi )
                return true;
            return false;
        }
    }

    public String toString()
    {
        return "";
    }

    public static void main(String[] args)
    {
    }
}
