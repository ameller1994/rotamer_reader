import java.util.*;

/**
 * Represents backbone-dependent rotamer data for an amino acid.
 */
public abstract class SideChainRotamerLibrary
{
    /** Represents a (phi, psi) pair. */
    public static class BackboneAngles
    {
	    private final Pair<Double> pair;
	
	    public BackboneAngles(Double phi, Double psi)
        {
	        pair = new Pair<Double>(phi,psi);
	    }
	
	    public Double getPhi()
        {
	        return pair.getFirst();
	    }
	
	    public Double getPsi()
        {
	        return pair.getSecond();
	    }

	    /**
	     * Returns a short description of this pair.
	     * @return [first value, second value]
	    */
	    public String toString()
	    {
	        return String.format("[%s, %s]", pair.getFirst().toString(), pair.getSecond().toString());
	    }

        /**
         * Returns the hash code for this pair.
         * @return the hash code
         */
        @Override
        public int hashCode()
        {
            return Objects.hash(pair.getFirst(), pair.getSecond());
        }
        
        /**
         * Tests for object equality.
         * @return true if the pairs are equal
         */
        @Override
        public boolean equals(Object obj)
        {
            if ( obj == null )
            return false;
            if ( obj == this )
            return true;
            if ( !(obj instanceof BackboneAngles) )
            return false;

            BackboneAngles anotherBBA = (BackboneAngles)obj;
            return pair.equals(anotherBBA.pair);
            
        }
    }   
}
