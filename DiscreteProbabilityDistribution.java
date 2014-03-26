import java.util.*;
import com.google.common.collect.*;

/**
 * A utility class for drawing random numbers from an arbitrary
 * discrete probability distribution using the alias method.  Please
 * see <a href="https://en.wikipedia.org/wiki/Alias_method">Wikipedia</a>
 * for more details.  This is a generic class that is parameterized by
 * the type of outcome that the distribution is meant to represent.  For
 * example, the outcome might be a list of torsion angles.  This class is
 * immutable.
 */
public class DiscreteProbabilityDistribution<E>
{

    /**
     * Contains the discrete probability distribution itself.  Maps each
     * outcome to a probability in the range 0.0 - 1.0.
     */
    private final Map<E, Double> distribution;

    /**
     * Populates the distribution and verifies invariants.  Lists must be
     * the same size and the sum of the probabilities must equal 1.
     * @param outcomes possible outcomes
     * @param probabilities probability of each outcome
    */
    public DiscreteProbabilityDistribution(List<E> outcomes, List<Double> probabilites)
    {
        // check invariants	
    }

    /**
     * Draws a random outcome based on the given distribution using the alias method.
     * @return E the outcome of the random weighted draw 
     */
    public E getRandom()
    {
    }
	
}
