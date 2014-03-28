import java.util.*;
import java.io.*;
import com.google.common.collect.*;

/** A class used to read in rotamer data from the <a href="http://dunbrack.fccc.edu/bbdep2010/">Dunbrack library</a>
 * It allows you to access the DiscreteProbabilityDistribution associated with a set of backbone angles, phi and psi.
 * This class is effectively immutable.  Rotamers below the threshold value Settings.ROTAMER_LIBRARY_THRESHOLD will
 * be ignored.  We assume that phi and psi span the interval [-180.0, 180.0] with step 10.0. <p>
 * This library is for non-rotameric sidechains (i.e., those with terminal sp2-sp3 torsions).
 */
public class NonRotamericLibrary extends SideChainRotamerLibrary
{

    /**
     * Utility class for the probabilistic outcomes.  Contains a list of standard torsion angles
     * and a distribution for the last non-rotameric angle.  This class is immutable.
     */
    public class NonRotamericAngles
    {
        /** X1, X2, ..., Xn-1 torsion angles in degrees */
        private final List<Double> standardTorsionAngles;

        /** a discrete distribution for Xn */
        private final DiscreteProbabilityDistribution<Double> nonRotamericTorsionAngleDistribution;

        /** constructs an immutable instance of this class */
        public NonRotamericAngles(List<Double> chis, DiscreteProbabilityDistribution<Double> dpd)
        {
            standardTorsionAngles = ImmutableList.copyOf(chis);
            nonRotamericTorsionAngleDistribution = dpd;
        }

        /**
         * returns X1, X2, ..., Xn-1 torsion angles in degrees 
         * @return X1, X2, ..., Xn-1 torsion angles in degrees
         */
	    public List<Double> getRotamericAngles()
        {
	        return standardTorsionAngles;
	    }
	
        /**
         * returns the discrete probability distribution for the non-rotameric torsion angle
         * @return the discrete probability distribution for the non-rotameric torsion angle (degrees)
         */
	    public DiscreteProbabilityDistribution<Double> getDPD()
        {
            return nonRotamericTorsionAngleDistribution;
	    }

        /**
         * returns a brief description
         * @return brief description
         */
        public String toString()
        {
            return standardTorsionAngles.toString() + "\n" + nonRotamericTorsionAngleDistribution.toString();
        }

        /**
         * returns the hash code
         * @return the hash code
         */
        public int hashCode()
        {
            return Objects.hash(standardTorsionAngles, nonRotamericTorsionAngleDistribution);
        }

        /**
         * tests for object equality
         * @return true if the object's fields are logically equivalent
         */
        public boolean equals(Object obj)
        {
            if ( obj == null )
                return false;
            if ( ! (obj instanceof NonRotamericAngles) )
                return false;
            final NonRotamericAngles other = (NonRotamericAngles)obj;
            return (Objects.equals(standardTorsionAngles, other.standardTorsionAngles) &&
                    Objects.equals(nonRotamericTorsionAngleDistribution, other.nonRotamericTorsionAngleDistribution));
        }
    }
				
    /**
      * Data storage is accomplished using map from back bone angles to each rotamer (list of chis)
      * (phi, psi) ---> [[ X1, X2, ..., Xn-1 ], probability of this rotamer]
      */ 
    private Map<SideChainRotamerLibrary.BackboneAngles, DiscreteProbabilityDistribution<NonRotamericAngles>> dataset; 
    
    /** The number n-1 of normal chi angles: X1, X2, ..., Xn-1 */
    private Integer numberOfSidechainTorsions = null;

    /** The type of amino acid these data are for. */
    private AminoAcid aminoAcid;

    /** The number of torsion angles for the last chi */
    private Integer numberOfLastChis = null;

    /**
     * creates a RotamericLibrary by reading in filename associated with amino acid
     * @param aminoAcid the amino acid for which to create rotameric library
     */
    public NonRotamericLibrary(AminoAcid aminoAcid)
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
                if ( aminoAcid.getFilename().indexOf("rotamers") > -1 )
                    throw new IllegalArgumentException("Should not be using RotamericLibrary to read standard rotameric side chains!");

                                
		List<Double> tempProbabilities = new ArrayList<>();
		List<NonRotamericAngles> tempNRA = new ArrayList<>();
                
		List<Double> lastChi = new ArrayList<>();

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
			//access the possible values for the last chi
			else if ( currentLine.indexOf("# chi3 interval, deg") > -1)
			    {
				// parse currentLine to access data fields
				parts = currentLine.split("\\s+");
				parts[4] = parts[4].replace(",","");
				Double minAngle = Double.parseDouble(parts[4].replace("[",""));
				System.out.println(minAngle);

				Double maxAngle = Double.parseDouble(parts[5].replace("]",""));
				System.out.println(maxAngle);
				//access next line to read spacing
				currentLine = thisFile.nextLine();
				parts = currentLine.split("\\s+");
				Double spacing = Double.parseDouble(parts[4]);
				for (Double i = minAngle; i < maxAngle ; i = i+spacing)
				    lastChi.add(minAngle);

				numberOfLastChis = (int) ((maxAngle-minAngle) / spacing); //could be off by one
				System.out.println(numberOfLastChis);
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

				System.out.println(tempNRA.size());
				System.out.println(tempProbabilities.size());

                                // create DiscreteProbabilityDataSet object
                                DiscreteProbabilityDistribution<NonRotamericAngles> dpd = new DiscreteProbabilityDistribution<>(
                                                                                         ImmutableList.copyOf(tempNRA),
                                                                                         ImmutableList.copyOf(tempProbabilities));

                                // put new entry into map for this BackboneAngle
                                dataset.put(backboneAngles, dpd);

                                // reset for next round
                                tempNRA.clear();
                                tempProbabilities.clear();
			    }

			// check if this probability falls below the threshold
			if ( Double.valueOf(parts[4+numberOfSidechainTorsions]) < Settings.ROTAMER_LIBRARY_THRESHOLD )
			    continue;

			// add to temporary list of list of chis and probabilites
			List<Double> chis = new ArrayList<Double>();
			
			//add to temporary probabilities
			tempProbabilities.add(Double.valueOf(parts[4+numberOfSidechainTorsions]));
			
			// chi values are in columns 6 through the end of the discrete angles and probability is in column 5
			for (int i=6; i < 6+numberOfSidechainTorsions; i++)
			    chis.add(Double.valueOf(parts[i]));
			
			List<Double> lastChiProbabilities = new ArrayList<>();
			for (int i=5+3*numberOfSidechainTorsions; i<parts.length; i++)
			    lastChiProbabilities.add(Double.valueOf(parts[i]));

			System.out.println(lastChiProbabilities.get(lastChiProbabilities.size()-1));
			System.out.println(lastChiProbabilities.size());
			System.out.println(lastChiProbabilities.toString());

			DiscreteProbabilityDistribution<Double> lastChiDPD = new DiscreteProbabilityDistribution<>(lastChi,lastChiProbabilities); 
			tempNRA.add(new NonRotamericAngles(chis,lastChiDPD));
				    
                        // store last entries
                        lastPhi = currPhi;
                        lastPsi = currPsi;
		    }
		
		// include edge case
		BackboneAngles backboneAngles = new BackboneAngles(lastPhi, lastPsi);
		
		// create DiscreteProbabilityDataSet object
		DiscreteProbabilityDistribution<NonRotamericAngles> dpd = new DiscreteProbabilityDistribution<>(
														ImmutableList.copyOf(tempNRA),
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
     * rounding algorithm based on <a href="http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5">StackOverflow</a>
     * @param phi a Double with the corresponding phi from the backbone
     * @param psi a Double with the corresponding psi from the backbone
     * @return a DPD object with list of chis as outcomes and associated probabilities for a phi and psi
     */
    public DiscreteProbabilityDistribution<NonRotamericAngles> get(Double phi, Double psi)
    {
        if (phi > 180.0 || phi < -180.0 || psi > 180.0 || psi < -180.0)
            throw new IllegalArgumentException("psi and phi must be between -180 and 180");
        
        // round to the neaerest multiple of 10
        Double rounder_psi = 5.0;
        Double rounder_phi = 5.0;
        
        // need to subtract 5 to round to nearest multiple of 10 if psi or phi are negative
        if (psi < 0)
            rounder_psi = -5.0;
        if (phi < 0)
            rounder_phi = -5.0;
        
        // find the nearest multiples of 5
        Double psi_rounded = Math.round((psi + rounder_psi)/ 10.0) * 10.0;
        Double phi_rounded = Math.round((phi + rounder_phi)/ 10.0) * 10.0;
        
        // return the appropriate data
        DiscreteProbabilityDistribution<NonRotamericAngles> dpd = dataset.get(new SideChainRotamerLibrary.BackboneAngles(phi_rounded,psi_rounded));
        if ( dpd == null )
            throw new NullPointerException("data not found!");
        return dpd;
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

        NonRotamericLibrary rotLib = (NonRotamericLibrary) obj;

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
        return Objects.hash(dataset, aminoAcid, numberOfSidechainTorsions, numberOfLastChis);
    }

    /** Tests the functionality of this library. */
    public static void main(String[] args)
    {
	NonRotamericLibrary rotLib1 = new NonRotamericLibrary(AminoAcid.GLU);
	System.out.println(rotLib1.get(177.6,179.2).toString());
	    
	    //RotamericLibrary rotLib2 = new RotamericLibrary(AminoAcid.LYS);
	    //RotamericLibrary rotLib3 = new RotamericLibrary(AminoAcid.MET);
	    //System.out.println(rotLib1.equals(rotLib2));
	    //System.out.println(rotLib1.equals(rotLib3));

	    //System.out.println(rotLib.get(-180.0,-60.0).toString());
    }
}
