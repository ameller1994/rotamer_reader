import java.util.*;
import java.lang.*;

public class RotamericLibrary extends SideChainRotamerLibrary {
    
    //data storage is accomplished using linked hash map from back bone angles to each rotamer (list of chis) 
    LinkedHashMap<BackBoneAngles, DiscreteProbabilityDistribution> dataset; 
    
    public RotamericLibrary(String fileName)
    {
	//read in library for specific amino acid
	//creates LinkedHashMap between backbone angles and discrete probability distribution 
    }

    public DiscreteProbabilityDistribution map(Double psi, Double phi)
    {
	//return discrete probability distribution for psi and phi
	//allows user to then get random rotamer from that discrete probability distribution
    }

}
