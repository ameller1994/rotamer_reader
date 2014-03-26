import java.util.*;
import java.lang.*;

public class RotamericLibrary extends SideChainRotamerLibrary
{
    
    //data storage is accomplished using linked hash map from back bone angles to each rotamer (list of chis) 
    LinkedHashMap<BackboneAngles, DiscreteProbabilityDistribution> dataset; 
    
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
	    dataset = new LinkedHashMap<BackBoneAngles,DiscreteProbabiltiyDistribution>(); 
	    
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
		    List<String> parts = new ArrayList<String>();
		    StringTokenizer st = new StringTokenizer(s, " ", false);
		    while(st.hasMoreTokens())
			parts.add(st.nextToken());
		    
		    //check to see if first entry is an amino acid name
		    if (parts.get(0).equals(aminoAcid.getName().toUpperCase()))
		       {
			   //create BackBone angles with parts.get(1) and parts.get(2)
			   double nextPhi = Double.parseDouble(parts.get(1));
			   double nextPsi = Double.parseDouble(parts.get(2));
			   
			   if (currPsi == nextPsi && currPhi == nextPhi) {
			       //add to temporary list of list of chis and probabilites
			       List<Double> chis = new ArrayList<Double>();
			       //Chi values are in columns 9, 10, 11, 12 and probability is in column 8
			       chis.add(parts.get(9));
			       chis.add(parts.get(10));
			       chis.add(parts.get(11));
			       chis.add(parts.get(12));
			       tempChis.add(chis);
			       tempProbabilites.add(parts.get(8));
			   }
			   else {
			       //create new backboneAngles object
			       BackboneAngles backboneAngles = new BackboneAngles(currPsi, currPhi);
			       //create DiscreteProbabilityDataSet object
			       DiscreteProbilityDistribution dpd = new DiscreteProbabilityDistribution(tempChis, tempProbabilities);
			       //put new entry into Linked Hash Map for those BackBone angles
			       dataset.put(backboneAngles, dpd);
			       //reset for next round 
			       currPsi = nextPsi;
			       currPhi = nextPhi;
			       tempChis = null;
			       tempProbabilities = null;
			       
			   }
			   
                }
                }
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        
    }

    /** returns the DiscreteProbabilityDistribution associated with psi and phi
     * @return a DPD object with list of chis as outcomes and associated probabilities for a psi and phi
     */
    public DiscreteProbabilityDistribution get(Double psi, Double phi)
    {
         return dataset.get(BackboneAngles(psi,phi));
	//return discrete probability distribution for psi and phi
	//allows user to then get random rotamer from that discrete probability distribution
    }

    //for testing purposes
    public static void main(String[] args) {
	RotamericLibrary rotLib = new RotamericLibrary(AminoAcid.ASN);
	System.out.println(dataset);
    }
}
