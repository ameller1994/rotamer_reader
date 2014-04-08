import org.apache.commons.math3.geometry.euclidean.threed.*;

/** A class representing an atom. It includes information about the symbol, the position, and the VDW parameters for an atom.
 */
public class Atom
{
    /** A symbol (C, N, H, etc.) corresponding to the atom type */
    private String symbol;

    /** A vector representing the position of the atom */
    private Vector3D position;

    /** The Van Der Waals paramater epsilon used for force field calculations */
    private double epsilon;

    /** The Van Der Waals parameter sigma used for force field calculations */
    private double sigma;

    /** The partial charge calculated from macromodel */
    private double partialCharge;

    /** The formal charge of the atom */
    private int formalCharge;

    public Atom(String symbol, double x, double y, double z)
    {
        this.symbol = symbol;
        this.position = new Vector3D(x,y,z);
        setVDW();
        formalCharge = 0;
	partialCharge = 0.0;
    }

    public Atom(String symbol, Vector3D position)
    {
        this.symbol = symbol;
        this.position = position;
        setVDW();
        formalCharge = 0;
	partialCharge = 0.0;
    }

    /**
       @return string representation of atom
    */
    public String toString()
    {
        //return(symbol);
        String returnString = String.format("%2s %10.6f %10.6f %10.6f", symbol, position.getX(), position.getY(), position.getZ());
        return(returnString);
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    /**
       @return string with the atom symbol (C, N, O, etc.) 
    */
    public String getSymbol()
    {
        return(symbol);
    }

    /**
       allows user to change the position of an atom
    */
    public void setPosition(double x, double y, double z)
    {
        this.position = new Vector3D(x,y,z);
    }

    public void setPosition(Vector3D position)
    {
        this.position = position;
    }

    public Vector3D getPosition()
    {
        return(position);
    }

    public Atom cloneAtom()
    {
        Atom newAtom = new Atom(symbol, position);
        newAtom.setFormalCharge( formalCharge );
	newAtom.setPartialCharge( partialCharge );
        return(newAtom);
    }

    /** sets default Van Der Waals values for the atom 
     */
    private void setVDW()
    {
        if (symbol.equals("H"))
            {
                sigma = 2.50;
                epsilon = 0.03;
            }
        else if (symbol.equals("C"))
            {
                sigma = 3.75;
                epsilon = 0.105;
            }
        else if (symbol.equals("N"))
            {
                sigma = 3.25;
                epsilon = 0.17;
            }
        else if (symbol.equals("O"))
            {
                sigma = 2.96;
                epsilon = 0.21;
            }
        else if (symbol.equals("Si"))
            {
                sigma = 3.83;
                epsilon = 0.40;
            }
        else if (symbol.equals("S"))
            {
                sigma = 3.60;
                epsilon = 0.355;
            }
        else
            {
                System.out.println("Warning!  Atom symbol " + symbol + " has no VDW parameters!  Defaulting to carbon parameters.");
                sigma = 3.75;
                epsilon = 0.105;
            }
    }

    public double getSigma()
    {
	return sigma;
    }
    
    public void setSigma(Double sigma) 
    {
	this.sigma = sigma;
    }
    
    public void setEpsilon(Double epsilon)
    {
	this.epsilon = epsilon;
    }

    public double getEpsilon()
    {
	return epsilon;
    }

    public int getFormalCharge()
    {
	return formalCharge;
    }
    
    public double getPartialCharge()
    {
	return partialCharge;
    }

    public void setPartialCharge(double partialCharge) 
    {
	this.partialCharge = partialCharge;
    }

    public void setFormalCharge(int formalCharge)
    {
	this.formalCharge = formalCharge;
    }
}
