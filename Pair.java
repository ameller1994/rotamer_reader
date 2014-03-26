import java.util.*;

/**
 * This is a utility generic class that represents an ordered pair.
 * This class is immutable.
 */
public class Pair<E>
{
    private E firstValue;
    private E secondValue;
    
    /**
     * Constructs an immutable pair.
     */
    public Pair(E firstVal, E secondVal)
    {
	    this.firstValue = firstVal;
	    this.secondValue = secondVal;
    }
    
    /**
     * Returns the first value of this pair.
     * @return the first value
     */
    public E getFirst()
    {
	    return firstValue;
    }

    /**
     * Returns the second value of this pair.
     * @return the second value
     */
    public E getSecond()
    {
	    return secondValue;
    }

    /**
     * Returns a short description of this pair.
     * @return [first value, second value]
     */
    public String toString()
    {
        return String.format("[%s, %s]", firstValue.toString(), secondValue.toString());
    }

    /**
     * Returns the hash code for this pair.
     * @return the hash code
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(firstValue, secondValue);
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
        if ( !(obj instanceof Pair) )
            return false;

        Pair<?> anotherPair = (Pair<?>)obj;
        if ( this.firstValue.equals(anotherPair.firstValue) &&
             this.secondValue.equals(anotherPair.secondValue)  )
            return true;
        return false;
    }
}
