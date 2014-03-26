
import java.util.*;
import java.lang.*;
import java.io.*;

public class RotamericLibrary extends SideChainRotamerLibrary
{
    
    //data storage is accomplished using linked hash map from back bone angles to each rotamer (list of chis) 
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
		    //		    List<String> parts = new ArrayList<String>();
		    String[] parts = null;
		    parts = currentLine.split("\\s+");

		    //StringTokenizer st = new StringTokenizer(currentLine, " ", false);
		    //while(st.hasMoreTokens())
		    //	parts.add(st.nextToken());
		    if (parts.length > 5)
		    System.out.println(parts[1]);
		    //check to see if first entry is an amino acid name
		    if (parts[0].equals(aminoAcid.toString().toUpperCase()))
		       {

			   //create BackBone angles with parts.get(1) and parts.get(2)
			   double nextPhi = Double.parseDouble(parts[1]);
			   double nextPsi = Double.parseDouble(parts[2]);

			   System.out.println(currPhi + "   " + nextPhi);
                           System.out.println(currPsi + "    " + nextPsi);

			   
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
			       System.out.println("enters put");
			       //reset for next round 
			       currPsi = nextPsi;
			       currPhi = nextPhi;
			       tempChis.clear();
			       tempProbabilities.clear();
			       
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
    public DiscreteProbabilityDistribution<List<Double>> get(Double psi, Double phi)
    {
	System.out.println(dataset.size());
	//find the nearest multiples of 5
         return dataset.get(new SideChainRotamerLibrary.BackboneAngles(psi,phi));
	//return discrete probability distribution for psi and phi
	//allows user to then get random rotamer from that discrete probability distribution
    }

    //for testing purposes
    public static void main(String[] args) {
	RotamericLibrary rotLib = new RotamericLibrary(AminoAcid.ASN);
	System.out.println(rotLib.get(120.0,120.0).toString());
    }
}
