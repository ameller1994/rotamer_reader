import java.util.*;

public class DiscreteProbabilityDistribution<E> {

    //create corresponding lists of outcomes and probabilities
    private final ImmutableMap<E, Double> dpd;

    /**
       constructor method that fills Immutable Map 
       * @param List<E> which is a list of possible outcomes
       * @param List<Double> which is the corresponding list of probabilities
    */
    public DiscreteProbabilityDistribution(List<E> outcomes, List<Double> proababilites) {
	
    }

    /**
     * This is the method that draws a random outcome based on weighted probabilites 
     * @return E This returns the outcome of the random weighted draw 
     */
    public E getRandom()
    {
	//draw from distribution based on Alias Method

    }
	
}
	    
