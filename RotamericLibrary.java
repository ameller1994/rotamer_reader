import java.util.*;
import java.lang.*;

public class RotamericLibrary extends SideChainRotamerLibrary
{
    
    //data storage is accomplished using linked hash map from back bone angles to each rotamer (list of chis) 
    LinkedHashMap<BackboneAngles, DiscreteProbabilityDistribution> dataset; 
    
    public RotamericLibrary(AminoAcid aminoAcid)
    {
	//read in library for specific amino acid
	//creates LinkedHashMap between backbone angles and discrete probability distribution

	
        List<String> list = new ArrayList<String>();
        String filenameString = "rotamer_library/" + aminoAcid.getName().toLowerCase() + ".bbdep.rotamers.lib";
        //System.out.println(System.getProperty("user.dir"));

        //Read in entire file
        Scanner thisFile = null;
	    dataset = new LinkedHashMap<BackBoneAngles,DiscreteProbabiltiyDistribution>(); 

        try {
            thisFile = new Scanner(new FileReader(filenameString));
	    
	    double currPhi = 0.0;
	    double currPsi = 0.0;

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
			   double nextPhi = parts.get(1);
			   double nextPsi = parts.get(2);
			   
			   if (currPsi == nextPsi && currPhi == nextPhi) {
			       //add to temporary list of list of chis and probabilites
			       
			   }
			   else {
			       //create new linked hash map entry
			       
			       
			   }
			   //put new entry into Linked Hash Map for those BackBone angles
			   //create DiscreteProbabilityDataSet object
			   //Chi values are in columns 9, 10, 11, 12 and probability is in column 8
                }
                }
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }

        
    }

    public DiscreteProbabilityDistribution map(Double psi, Double phi)
    {
        return null;
	//return discrete probability distribution for psi and phi
	//allows user to then get random rotamer from that discrete probability distribution
    }

}
