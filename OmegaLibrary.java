import java.util.*;
import java.io.*;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * This singleton stores the backbone-dependent omega data.  Omega is defined as the
 * the dihedral angle of the amide bond.  Random numbers are drawn from a normal
 * distribution centered on the given mean and standard deviation in the database.
 * A new NormalDistribution and associated apache RandomGenerator are generated
 * for each draw.  To be more multithreading efficient, we could delegate the random number
 * generator to the calling class, but we're leaving it this way for now.
 *
 * The library tells you how the omega(+1) dihedral depends on the current
 * torsion, psi(0), and the following torsion, phi(+1).  Here is the diagram
 * from the library:<p>
 *
 *          omega(0)    phi(0)     psi(0)  omega(+1)   phi(+1)    psi(+1) omega(+2)<p>
 *              -  N( 0)  -  Ca( 0)  -  C( 0)  -  N(+1)  -  Ca(+1)  -  C(+1)  -    <p>
 *                       current residue               following residue           <p>
 *
 * The library contains some global data starting with "All" but that data are ignored
 * because we assume we're given the amino acids on either side of the torsion.  There's
 * a confusing convention that omega precedes an amino acid in the N to C direction, but
 * we follow it anyways.
 */
public class OmegaLibrary
{
    /** The public singleton. */
    public static final OmegaLibrary INSTANCE = new OmegaLibrary();
    
    /**
     * Stores all of the omega data.
     * @param <String> represents the type of data (ResTypeGroup: e.g., All, All_nonxpro, etc.)
     * @param <Pair<Double>,Pair<Double>> first pair is phi(+1),psi(0), second pair is the given mean and standard deviation
     */
    private Map<String,Map<Pair<Double>,Pair<Double>>> database;

    /** Private constructor should only be called once! */
    private OmegaLibrary()
    {
        if ( INSTANCE != null )
            throw new IllegalArgumentException("OmegaLibrary is a singleton!");
        database = new HashMap<>();
        String filename = Settings.OMEGA_DATA_FILENAME;

        // read data from file
        Scanner thisFile = null;
        try
            {
                thisFile = new Scanner(new FileReader(filename));
                String lastResidue = null;
                Map<Pair<Double>,Pair<Double>> currentData = new LinkedHashMap<>();
                while (thisFile.hasNextLine())
                    {
                        String currentLine = thisFile.nextLine().trim();

                        // ignore header lines and "all-residue" lines
                        if ( currentLine.startsWith("@") || currentLine.startsWith("#") ||
                             currentLine.startsWith("All") )
                            continue;
                        
                        // parse fields
                        String[] fields = currentLine.split("\\s+");
                        String currentResidue = fields[0];
                        if ( lastResidue == null )
                            lastResidue = currentResidue;
                        
                        // if this is a new residue type, load the stored data into the database
                        // assumes file ends with a line of data
                        if ( ! lastResidue.equals(currentResidue) )
                            {
                                database.put(lastResidue,currentData);
                                currentData = new LinkedHashMap<>();
                            }
                        lastResidue = currentResidue;

                        // record the current data
                        Double phi = Double.valueOf(fields[1]);
                        Double psi = Double.valueOf(fields[2]);
                        Pair<Double> anglePair = new Pair<>(phi,psi);
                        Double mean = Double.valueOf(fields[5]);
                        Double stdev = Double.valueOf(fields[6]);
                        Pair<Double> distPair = new Pair<>(mean,stdev);
                        currentData.put(anglePair,distPair);
                    }

                // load last entry into database
                database.put(lastResidue, currentData);
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        finally
            {
                if ( thisFile != null )
                    thisFile.close();
            }

    }

    /**
     * Returns a normal distribution for the omega torsion of residue2, based on the
     * identities of residue1 and residue2 and the enclosing phi angles, psi0 and
     * phi1.
     * @param residue1 the preceding residue (N to C direction)
     * @param residue2 the following residue (N to C direction)
     * @return the normal distribution for the residue2 omega torsion
     */
    private static NormalDistribution getNormalDistribution(Residue residue1, Residue residue2)
    {
        Map<String,Map<Pair<Double>,Pair<Double>>> database = INSTANCE.database;
        String key = determineKey(residue1, residue2);
        Map<Pair<Double>,Pair<Double>> thisEntry = database.get(key);
        if ( thisEntry == null )
            throw new NullPointerException("should have found something in the database!");
        double phi1 = residue2.getPhi();
        double psi0 = residue1.getPsi();
        Pair<Double> key2 = snapToGrid(new Pair<>(phi1,psi0));
        Pair<Double> dist = thisEntry.get(key2);
        if ( dist == null )
            throw new NullPointerException("unexpected error in OmegaLibrary!");
        return new NormalDistribution(dist.getFirst(), dist.getSecond());
    }

    /**
     * Returns a random omega given psi0 of the preceding residue1 and the phi1 of the
     * following residue2.  Nulls are not allowed.
     * @param residue1 the preceding residue (N to C direction)
     * @param residue2 the following residue (N to C direction)
     * @return a random, normally-distributed value for the omega of residue2 based on the database values
     */
    public static double getOmega(Residue residue1, Residue residue2)
    {
        if ( residue1 == null || residue2 == null )
            throw new IllegalArgumentException("nulls are not allowed");
        NormalDistribution dist = getNormalDistribution(residue1,residue2);
        return dist.sample();
    }

    /**
     * Returns the database key for this combination of residues.
     * Residue 1 precedes residue 2 in the N to C direction.
     * @param residue1 the residue before the omega1 torsion
     * @param residue2 the residue after the omega1 torsion
     * @return the database key
     */
    private static String determineKey(Residue residue1, Residue residue2)
    {
        AminoAcid aa1 = residue1.getAminoAcid();
        AminoAcid aa2 = residue2.getAminoAcid();
        String key = "";

        if ( aa1 == AminoAcid.ILE || aa1 == AminoAcid.VAL )
            key = "IleVal";
        else if ( aa1 == AminoAcid.GLY )
            key = "Gly";
        else if ( aa1 == AminoAcid.CPR || aa1 == AminoAcid.TPR )
            key = "Pro";
        else
            key = "NonPGIV";

        if ( aa2 == AminoAcid.CPR || aa2 == AminoAcid.TPR )
            {
                // the torsion precedes a proline
                key = key + "_xpro";
            }
        else
            {
                // the torsion does not precede a proline
                key = key + "_nonxpro";
            }

        return key;
    }

    /**
     * rounds phi and psi to nearest multiple of 10
     * @param inputPair e.g. phi1,psi0 (-179.0, -178.0) 
     * @return the nearest pair on the grid e.g., phi, psi (-180.0, 180.0)
     */
    private static Pair<Double> snapToGrid(Pair<Double> inputPair)
    {
        double phi = inputPair.getFirst();
        double psi = inputPair.getSecond();
        if (phi > 180.0 || phi < -180.0 || psi > 180.0 || psi < -180.0)
            throw new IllegalArgumentException("psi and phi must be between -180 and 180");

        Double phi_rounded = Math.rint((phi/10)*10.0);
        Double psi_rounded = Math.rint((psi/10)*10.0);

        // return the appropriate data
        return new Pair<Double>(phi_rounded,psi_rounded);
    }

    /**
     * Returns the number of entries in the database.
     * @return the short textual representation of this database
     */
    public String toString()
    {
        // counts how many lines of data were read
        int entries = 0;
        for (String s : database.keySet())
            {
                for (Pair<Double> p : database.get(s).keySet())
                    entries++;
            }
        return String.format("An omega dihedral angle database with %d entries.", entries);
    }

    /** Tester class. */
    public static void main(String[] args)
    {
        System.out.println(OmegaLibrary.INSTANCE);
        // phi0, _psi0_
        Residue residue1 = new Residue(AminoAcid.ILE, -179.0, 0.1);
        System.out.println(residue1);
        // _phi1_, psi1
        Residue residue2 = new Residue(AminoAcid.PHE, 21.0, -179.0);
        System.out.println(residue2);
        // read on phi1, psi0
        NormalDistribution dist = getNormalDistribution(residue1, residue2);
        System.out.println(dist.getMean());
        System.out.println(dist.getStandardDeviation());
        System.out.println(getOmega(residue1,residue2));
    }
}
