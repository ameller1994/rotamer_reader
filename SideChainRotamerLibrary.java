import java.util.*;

/**
 * Represents backbone-dependent rotamer data for an amino acid.
 */
public abstract class SideChainRotamerLibrary {
    
    abstract DiscreteProbabilityDistribution get(Double psi, Double phi);

    class BackboneAngles {
	
	private final Pair<Double,Double> bba;
	
	public BackboneAngles(Double psi, Double phi) {
	    bba = new Pair<Double,Double>(psi,phi);
	}
	
	public Double getPsi(){
	    return Pair.getFirst();
	}
	
	public Double getPhi(){
	    return Pair.getSecond();
	}
    }   
       
}
