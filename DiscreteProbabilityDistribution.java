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
 *
 * Algorithm copied from http://www.keithschwarz.com/darts-dice-coins/
 *
 */
public class DiscreteProbabilityDistribution<E>
{
    /**
     * Contains the discrete probability distribution itself.  Maps each
     * outcome to a probability in the range 0.0 - 1.0.
     */
    private final double[] probability;
    
    /** The outcomes, mapped 1:1 with the probability array. */
    private final ImmutableList<E> outcomes;

    /** The alias table. */
    private final int[] alias;

    /**
     * Populates the distribution and verifies invariants.  Lists must be
     * the same size and the sum of the probabilities must equal 1.
     * @param outcomes possible outcomes
     * @param probabilities probability of each outcome
    */
    public DiscreteProbabilityDistribution(List<E> outcomes, List<Double> probabilities)
    {
        // check for nulls
        if (outcomes == null || probabilities == null || outcomes.size() == 0 || probabilities.size() == 0)
            throw new NullPointerException("Cannot create a DiscreteProbabilityDistribution from a null or empty List!");

        // check lists are equal sizes
        if ( outcomes.size() != probabilities.size() )
            throw new IllegalArgumentException("Must have the same number of outcomes and probabilities!");

        // check for all positive probabilities
        double sum = 0.0;
        for (Double d : probabilities)
            {
                if ( d < 0 )
                    throw new IllegalArgumentException("Cannot have a negative probability!");
                sum += d;
            }

        // initialize arrays
        probability = new double[probabilities.size()];
        alias = new int[probabilities.size()];
        this.outcomes = new E[probabilities.size()];
        for (int i=0; i < probabilities.size(); i++)
            this.outcomes[i] = outcomes.get(i);
        
        // normalize probabilities and make a copy of the probabilities list, since we will be changing it
        for (int i=0; i < probabilities.size(); i++)
            probabilities.set(i, probabilities.get(i) / sum);
        probabilities = new ArrayList<Double>(probabilities);

        // calculate average probability
        final double average = 1.0 / probabilities.size();

        // create work lists
        Deque<Integer> small = new ArrayDeque<>();
        Deque<Integer> large = new ArrayDeque<>();

        // populate the input stacks with the input probabilities
        for (int i=0; i < probabilities.size(); ++i)
            {
                // if the probability is below avearge add it to small list
                // otherwise add it to the large list
                if ( probabilities.get(i) >= average )
                    large.add(i);
                else
                    small.add(i);
            }

        /* As a note: in the mathematical specification of the algorithm, we
         * will always exhaust the small list before the big list.  However,
         * due to floating point inaccuracies, this is not necessarily true.
         * Consequently, this inner loop (which tries to pair small and large
         * elements) will have to check that both lists aren't empty.
         */
        while ( !small.isEmpty() && !large.isEmpty() )
            {
                /* Get the index of the small and the large probabilities. */
                int less = small.removeLast();
                int more = large.removeLast();

                /* These probabilities have not yet been scaled up to be such that
                 * 1/n is given weight 1.0.  We do this here instead.
                 */
                probability[less] = probabilities.get(less) * probabilities.size();
                alias[less] = more;

                /* Decrease the probability of the larger one by the appropriate
                 * amount.
                 */
                probabilities.set( more, (probabilities.get(more) + probabilities.get(less)) - average );

                /* If the new probability is less than the average, add it into the
                 * small list; otherwise add it to the large list.
                 */
                if (probabilities.get(more) >= 1.0 / probabilities.size())
                    large.add(more);
                else
                    small.add(more);
            }

        /* At this point, everything is in one list, which means that the
         * remaining probabilities should all be 1/n.  Based on this, set them
         * appropriately.  Due to numerical issues, we can't be sure which
         * stack will hold the entries, so we empty both.
         */
        while (!small.isEmpty())
            probability[small.removeLast()] = 1.0;
        while (!large.isEmpty())
            probability[large.removeLast()] = 1.0;
    }
    /**
     * Draws a random outcome based on the given distribution using the alias method.
     * @return E the outcome of the random weighted draw 
     */
    public E getRandom()
    {
        // draw a thread-safe random number
        int column = ThreadLocalRandom.current().nextInt(probability.length);

        // generate biased coin toss
        boolean coinToss = ThreadLocalRandom.current().nextDouble() < probability[column];

        // based on the outcome, get the colum or its alias
        int result = coinToss ? column : alias[column];

        // return the corresponding result object
        return alias[result];
    }

    @Override
    public String toString()
    {
        return "";
    }

    // hashCode
    // equals

    /**
     * Tester class.
     */
    public static void main(String[] args)
    {
        // generate test distribution
        List<String> outcomes = new LinkedList<String>();
        outcomes.add("A");
        outcomes.add("B");
        outcomes.add("C");
        outcomes.add("D");
        outcomes.add("E");

        List<Double> probabilities = new LinkedList<Double>();
        probabilities.add(2.0);
        probabilities.add(1.0);
        probabilities.add(1.0);
        probabilities.add(1.0);
        probabilities.add(1.0);

        DiscreteProbabilityDistribution dist = new DiscreteProbabilityDistribution(outcomes,probabilities);
        LinkedHashMap<String,Integer> results = new LinkedHashMap<>();
        for (String s : outcomes)
            results.put(s, 0);
        System.out.println("Rolling...");
        for (int i=0; i < 100000; i++)
            {
                String thisOutcome = dist.getRandom();
                Integer numberOfHits = results.get(thisOutcome);
                numberOfHits = numberOfHits + 1;
                results.put(thisOutcome, numberOfHits);
            }
        System.out.println(results);
    }
}
