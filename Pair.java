
public class Pair<E, E> {
    
    private E firstValue;
    private E secondValue;
    
    public Pair(E firstVal, E secondVal) {
	this.firstValue = firstVal;
	this.secondValue = secondVal;
    }
    
    public E getFirst() {
	return firstValue;
    }

    public E getSecond() {
	return secondValue;
    }
}
