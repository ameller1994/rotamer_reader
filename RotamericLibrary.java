import java.util.*;
import java.io.*;
import com.google.common.collect.*;

// notes:
// probability threshold for rotamers ---> control in settings?
// what happens if the rotamer library is missing data for some psi, phi?  maybe
// there aren't any such cases

/** A class used to read in rotamer data from the <a href="http://dunbrack.fccc.edu/bbdep2010/">Dunbrack library</a>
 * It allows you to access the Discrete Probability Distribution associated with a set of backbone angles, phi and psi.
 * This class is effectively immutable.
 */

public class RotamericLibrary extends SideChainRotamerLibrary
{
    /**
      * Data storage is accomplished using map from back bone angles to each rotamer (list of chis)
      * (phi, psi) ---> [[ X1, X2, ..., Xn ], probability of this rotamer]
      */ 
    private Map<SideChainRotamerLibrary.BackboneAngles, DiscreteProbabilityDistribution<List<Double>>> dataset; 
    private Integer numberOfSidechainTorsions = null;
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
		
		//Include edge case
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
        dataset = ImmutableMap.copyOf(dataset);
    }

    /**
     * returns the DiscreteProbabilityDistribution associated with phi and psi
     * rounds psi and phi to nearest multiple of 10
     * rounding algorithm based on <a href="http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5">StackOverflow</a>
     * @param phi a Double with the corresponding phi from the backbone
     * @param psi a Double with the corresponding psi from the backbone
     * @return a DPD object with list of chis as outcomes and associated probabilities for a phi and psi
     */
    public DiscreteProbabilityDistribution<List<Double>> get(Double phi, Double psi)
    {
	if (phi > 180.0 || phi < -180.0 || psi > 180.0 || psi < -180.0)
	    throw new IllegalArgumentException("psi and phi must be between -180 and 180");
	
	// round to the neaerest multiple of 10
	Double rounder_psi = 5.0;
	Double rounder_phi = 5.0;
	
        //Need to subtract 5 to round to nearest multiple of 10 if psi or phi are negative
	if (psi < 0)
	    rounder_psi = -5.0;
	if (phi < 0)
	    rounder_phi = -5.0;
	
	// find the nearest multiples of 5
	Double psi_rounded = Math.round((psi + rounder_psi)/ 10.0) * 10.0;
	Double phi_rounded = Math.round((phi + rounder_phi)/ 10.0) * 10.0;
	
	// return the appropriate data
        DiscreteProbabilityDistribution<List<Double>> dpd = dataset.get(new SideChainRotamerLibrary.BackboneAngles(psi_rounded,phi_rounded));
        if ( dpd == null )
            throw new NullPointerException("data not found!");
        return dpd;
    }
    
    public String toString() {
	return Integer.toString(dataset.size());
    }
    
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

	    
    //for testing purposes
    public static void main(String[] args)
    {
	    RotamericLibrary rotLib1 = new RotamericLibrary(AminoAcid.MET);
	    System.out.println(rotLib1.get(177.6,179.2).toString());
	    RotamericLibrary rotLib2 = new RotamericLibrary(AminoAcid.ASP);
	    System.out.println(rotLib1.equals(rotLib2));

	    //System.out.println(rotLib.get(-180.0,-60.0).toString());
    }
}
