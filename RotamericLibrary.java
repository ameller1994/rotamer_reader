
import java.util.*;
import java.lang.*;
import java.io.*;

/** A class used to read in rotamer data from the <a href="http://dunbrack.fccc.edu/bbdep2010/">Dunbrack library</a>
 * It allows you to access the Discrete Probability Distribution associated with a set of backbone angles, phi and psi.
 */

public class RotamericLibrary extends SideChainRotamerLibrary
{

    /** Data storage is accomplished using linked hash map from back bone angles to each rotamer (list of chis) */ 
    private LinkedHashMap<SideChainRotamerLibrary.BackboneAngles, DiscreteProbabilityDistribution<List<Double>>> dataset; 
    
    /**
     * creates RotamericLibrary by reading in filename associated with amino acid
     * @param is an amino acid for which to create rotameric library
     */
    public RotamericLibrary(AminoAcid aminoAcid)
    {
	//read in library for specific amino acid
	//creates LinkedHashMap between backbone angles and discrete probability distribution

	
        List<String> list = new ArrayList<String>();
        String filenameString = aminoAcid.getFilename();
        //System.out.println(System.getProperty("user.dir"));

        //Read in entire file
        Scanner thisFile = null;
	dataset = new LinkedHashMap<SideChainRotamerLibrary.BackboneAngles,DiscreteProbabilityDistribution<List<Double>>>(); 
	
	try {
            thisFile = new Scanner(new FileReader(filenameString));
	    
	    double currPhi = -180.0;
	    double currPsi = -180.0;

	    List<List<Double>> tempChis = new ArrayList<>();
	    List<Double> tempProbabilities = new ArrayList<>();

	    while (thisFile.hasNextLine())
                {
		    String currentLine = thisFile.nextLine();
		    
		    //parse currentLine to access data fields
		    String[] parts = null;
		    parts = currentLine.split("\\s+");
		    
		    //check to see if first entry is an amino acid name    
		    if (parts[0].equals(aminoAcid.toString().toUpperCase()))
		       {
			   //create BackBone angles with parts.get(1) and parts.get(2)
			   double nextPhi = Double.parseDouble(parts[1]);
			   double nextPsi = Double.parseDouble(parts[2]);

			   		   
			   if (currPsi == nextPsi && currPhi == nextPhi) {
			       //add to temporary list of list of chis and probabilites
			       List<Double> chis = new ArrayList<Double>();
			       
			       //Chi values are in columns 9, 10, 11, 12 and probability is in column 8
			       chis.add(Double.parseDouble(parts[9]));
			       chis.add(Double.parseDouble(parts[10]));
			       chis.add(Double.parseDouble(parts[11]));
			       chis.add(Double.parseDouble(parts[12]));
			       tempChis.add(chis);
			       tempProbabilities.add(Double.parseDouble(parts[8]));
			   }
			   else {
			       //create new backboneAngles object
			       BackboneAngles backboneAngles = new BackboneAngles(currPsi, currPhi);
			       //create DiscreteProbabilityDataSet object
			       DiscreteProbabilityDistribution<List<Double>> dpd = new DiscreteProbabilityDistribution<>(tempChis, tempProbabilities);
			       //put new entry into Linked Hash Map for those BackBone angles
			       dataset.put(backboneAngles, dpd);
			       
			       //reset for next round 
			       currPsi = nextPsi;
			       currPhi = nextPhi;
			       tempChis.clear();
			       tempProbabilities.clear();
			       
			       //add values for this row to new temporary variables
			       List<Double> chis = new ArrayList<Double>();

                               //Chi values are in columns 9, 10, 11, 12 and probability is in column 8
                               chis.add(Double.parseDouble(parts[9]));
                               chis.add(Double.parseDouble(parts[10]));
                               chis.add(Double.parseDouble(parts[11]));
                               chis.add(Double.parseDouble(parts[12]));
                               tempChis.add(chis);
                               tempProbabilities.add(Double.parseDouble(parts[8]));

			   }
			   
		       }
                }
	    //Add edge case (180.0, 180.0) after all other lines have been read in
	    //create new backboneAngles object
	    BackboneAngles backboneAngles = new BackboneAngles(currPsi, currPhi);
	    //create DiscreteProbabilityDataSet object
	    DiscreteProbabilityDistribution<List<Double>> dpd = new DiscreteProbabilityDistribution<>(tempChis, tempProbabilities);
	    //put new entry into Linked Hash Map for those BackBone angles
	    dataset.put(backboneAngles, dpd);


            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        
    }

    /** returns the DiscreteProbabilityDistribution associated with psi and phi
     * rounds psi and phi to nearest multiple of 10
     * rounding algorithm based on <a href="http://stackoverflow.com/questions/9303604/rounding-up-a-number-to-nearest-multiple-of-5">StackOverflow</a>
     * @param psi a Double with the corresponding psi from the backbone
     * @param phi a Double with the corresponding phi from the backbone
     * @return a DPD object with list of chis as outcomes and associated probabilities for a psi and phi
     */
    public DiscreteProbabilityDistribution<List<Double>> get(Double psi, Double phi)
    {
	if (psi > 180.0 || psi < -180.0 || phi > 180.0 || phi < -180.0)
	    throw new IllegalArgumentException("psi and phi must be between -180 and 180");

	//Round to the neaerest multiple of 10
	Double rounder_psi = 5.0;
	Double rounder_phi = 5.0;
	//Need to subtract 5 to round to nearest multiple of 10 if psi or phi are negative
	if (psi < 0)
	    rounder_psi = -5.0;

	if (phi < 0)
	    rounder_phi = -5.0;
	    
	Double psi_rounded = Math.round((psi + rounder_psi)/ 10.0) * 10.0;
	Double phi_rounded = Math.round((phi + rounder_phi)/ 10.0) * 10.0;

	//find the nearest multiples of 5
         return dataset.get(new SideChainRotamerLibrary.BackboneAngles(psi_rounded,phi_rounded));
	//return discrete probability distribution for psi and phi
	//allows user to then get random rotamer from that discrete probability distribution
    }

    //for testing purposes
    public static void main(String[] args) {
	RotamericLibrary rotLib = new RotamericLibrary(AminoAcid.MET);
	System.out.println(rotLib.get(-177.6,-179.2).toString());
    }
}
