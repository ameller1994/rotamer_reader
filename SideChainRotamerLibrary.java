import java.util.*;

/**
 * Represents backbone-dependent rotamer data for an amino acid.
 */
public abstract class SideChainRotamerLibrary
{
    //DiscreteProbabilityDistribution get(Double psi, Double phi);

    public static class BackboneAngles
    {
	    private final Pair<Double> pair;
	
	    public BackboneAngles(Double psi, Double phi)
        {
	        pair = new Pair<Double>(psi,phi);
	    }
	
	    public Double getPsi()
        {
	        return pair.getFirst();
	    }
	
	    public Double getPhi()
        {
	        return pair.getSecond();
	    }
    }   
       
}
