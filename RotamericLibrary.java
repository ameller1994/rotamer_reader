import java.util.*;
import java.io.*;
import com.google.common.collect.*;

/** A class used to read in rotamer data from the <a href="http://dunbrack.fccc.edu/bbdep2010/">Dunbrack library</a>
 * It allows you to access the DiscreteProbabilityDistribution associated with a set of backbone angles, phi and psi.
 * This class is effectively immutable.  Rotamers below the threshold value Settings.ROTAMER_LIBRARY_THRESHOLD will
 * be ignored.  We assume that phi and psi span the interval [-180.0, 180.0] with step 10.0. <p>
 * This library is for standard rotameric sidechains (i.e., those without terminal sp2-sp3 torsions).
 */
public class RotamericLibrary extends SideChainRotamerLibrary
{
    /**
      * Data storage is accomplished using map from back bone angles to each rotamer (list of chis)
      * (phi, psi) ---> [[ X1, X2, ..., Xn ], probability of this rotamer]
      */ 
    private Map<SideChainRotamerLibrary.BackboneAngles, DiscreteProbabilityDistribution<List<Double>>> dataset; 
    
    /** The number n of chi angles: X1, X2, ..., Xn */
    private Integer numberOfSidechainTorsions = null;

    /** The type of amino acid these data are for. */
    private AminoAcid aminoAcid;

    /**
     * creates a RotamericLibrary by reading in filename associated with amino acid
     * @param aminoAcid the amino acid for which to create rotameric library
     */
    public RotamericLibrary(AminoAcid aminoAcid)
    {
	    // read in library for specific amino acid
	    // creates LinkedHashMap between backbone angles and discrete probability distribution
        this.aminoAcid = aminoAcid;

        // read in entire file
        Scanner thisFile = null;
        dataset = new LinkedHashMap<>(); 
        try
            {
                thisFile = new Scanner(new FileReader(aminoAcid.getFilename()));
                
                // make sure this is the right kind of file
                if ( aminoAcid.getFilename().indexOf("densities") > -1 )
                    throw new IllegalArgumentException("Should not be using RotamericLibrary to read non-rotameric side chains!");

                List<List<Double>> tempChis = new ArrayList<>();
                List<Double> tempProbabilities = new ArrayList<>();
                Double lastPhi = null;
                Double lastPsi = null;
                    
                while (thisFile.hasNextLine())
                    {
			            String currentLine = thisFile.nextLine();
			
    	        		// parse currentLine to access data fields
	            		String[] parts = currentLine.split("\\s+");
		    
			            // valid data is stored on lines that contain the
                        // abbreviated amino acid name as the first field
                        if ( currentLine.indexOf("Number of chi angles treated as discrete") > -1 )
                            {
                                // records the number of torsions we need to store
                                numberOfSidechainTorsions = Integer.valueOf(parts[parts.length-1]);
                                continue;
                            }
			            else if (!parts[0].equals(aminoAcid.toString().toUpperCase()))
                            continue;
			
			            // read backbone angles with parts.get(1) and parts.get(2)
			            Double currPhi = Double.parseDouble(parts[1]);
			            Double currPsi = Double.parseDouble(parts[2]);
                        if ( lastPhi == null )
                            lastPhi = currPhi;
                        if ( lastPsi == null )
                            lastPsi = currPsi;

                        // assumes entries are consecutive for one pair of (phi,psi)
			            if ( !currPhi.equals(lastPhi) || !currPsi.equals(lastPsi))  
                            {
                                // this is a new (phi,psi) pair, so create a new BackboneAngles object
                                // for all the data we've looked at so far
                                BackboneAngles backboneAngles = new BackboneAngles(lastPhi, lastPsi);

                                // create DiscreteProbabilityDataSet object
                                DiscreteProbabilityDistribution<List<Double>> dpd = new DiscreteProbabilityDistribution<>(
                                                                                         ImmutableList.copyOf(tempChis),
                                                                                         ImmutableList.copyOf(tempProbabilities));

                                // put new entry into map for this BackboneAngle
                                dataset.put(backboneAngles, dpd);

                                // reset for next round
                                tempChis.clear();
                                tempProbabilities.clear();
			                }

                        // check if this probability falls below the threshold
                        if ( Double.valueOf(parts[8]) < Settings.ROTAMER_LIBRARY_THRESHOLD )
                            continue;

			            // add to temporary list of list of chis and probabilites
			            List<Double> chis = new ArrayList<Double>();
			
	            		// chi values are in columns 9, 10, 11, 12 and probability is in column 8
	            		for (int i=9; i < 9+numberOfSidechainTorsions; i++)
			            chis.add(Double.valueOf(parts[i]));
			            tempChis.add(ImmutableList.copyOf(chis));
			            tempProbabilities.add(Double.valueOf(parts[8]));
			
                        // store last entries
                        lastPhi = currPhi;
                        lastPsi = currPsi;
		            }
		
		        // include edge case
		        BackboneAngles backboneAngles = new BackboneAngles(lastPhi, lastPsi);

		        // create DiscreteProbabilityDataSet object
		        DiscreteProbabilityDistribution<List<Double>> dpd = new DiscreteProbabilityDistribution<>(
				                    									  ImmutableList.copyOf(tempChis),
									                    				  ImmutableList.copyOf(tempProbabilities));
		        // put new entry into map for this BackboneAngle
		        dataset.put(backboneAngles, dpd);
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        finally
            {
                if (thisFile != null)
                    thisFile.close();
            }
            
        // make defensive and immutable copy
        dataset = ImmutableMap.copyOf(dataset);
    }

    /**
     * returns the DiscreteProbabilityDistribution associated with phi and psi
     * rounds psi and phi to nearest multiple of 10
     * @param phi a Double with the corresponding phi from the backbone
     * @param psi a Double with the corresponding psi from the backbone
     * @return a DPD object with list of chis as outcomes and associated probabilities for a phi and psi
     */
    public DiscreteProbabilityDistribution<List<Double>> get(Double phi, Double psi)
    {
        if (phi > 180.0 || phi < -180.0 || psi > 180.0 || psi < -180.0)
            throw new IllegalArgumentException("psi and phi must be between -180 and 180");
        
        // return the appropriate data
        double phi_rounded = roundTo10(phi);
        double psi_rounded = roundTo10(psi);
        DiscreteProbabilityDistribution<List<Double>> dpd = dataset.get(new SideChainRotamerLibrary.BackboneAngles(phi_rounded,psi_rounded));
        if ( dpd == null )
            throw new NullPointerException("data not found!");
        return dpd;
    }

    public static double roundTo10(double number)
    {
        return Math.rint(number/10)*10.0;
    }

    /**
     * Gives a short textual description of this database.
     * @return a brief sentence
     */
    public String toString()
    {
	    return String.format("RotamericDatabase for %s comprising %d entries.", aminoAcid.toString(), dataset.size());
    }
    
    /**
     * Tests two libraries for logical equivalence on all fields.
     * @return true if the libraries are equal
     */
    public boolean equals(Object obj)
    {
        if ( obj == null )
                return false;
            if ( obj == this )
                return true;
            if ( !(obj instanceof RotamericLibrary) )
                return false;

        RotamericLibrary rotLib = (RotamericLibrary) obj;

        //check for matching datasets
        if (!rotLib.dataset.equals(dataset))
            return false;

        //check for matching number of torsion angles
        if (rotLib.numberOfSidechainTorsions != numberOfSidechainTorsions)
            return false;

        //check for matching amino acid
        if (!rotLib.aminoAcid.equals(aminoAcid))
            return false;
        
        return true;
    }

    /**
     * Returns the hash code for this rotameric library
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(dataset, aminoAcid, numberOfSidechainTorsions);
    }

    /** Tests the functionality of this library. */
    public static void main(String[] args)
    {
	    RotamericLibrary rotLib1 = new RotamericLibrary(AminoAcid.MET);
	    System.out.println(rotLib1.get(-179.0,93.1).toString());
    }
}
