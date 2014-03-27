import java.util.*;

/**
 * Represents an actual amino acid residue.
 * This class may eventually become mutable.
 */
public class Residue
{
    /** The type of amino acid this residue is. */
    private AminoAcid aminoAcid;

    /** The dihedral angle between the nitrogen of the amine and the alpha carbon. */
    private Double phi;

    /** The diheral angle between the alpha carbon and the carbonyl carbon. */
    private Double psi;

    /**
     * Creates the residue.
     * @param aminoAcid the type of amino acid
     * @param phi the phi dihedral angle in degrees
     * @param psi the psi dihedral angle in degrees
     */
    public Residue(AminoAcid aminoAcid, double phi, double psi)
    {
        this.aminoAcid = aminoAcid;
        this.phi = phi;
        this.psi = psi;
    }

    /**
     * Returns the type of amino acid.
     * @return the type of amino acid
     */
    public AminoAcid getAminoAcid()
    {
        return aminoAcid;
    }

    /**
     * Returns the phi angle.
     * @return phi the phi angle in degrees
     */
    public double getPhi()
    {
        return phi;
    }

    /**
     * Returns the phi angle.
     * @return psi the psi angle in degrees
     */
    public double getPsi()
    {
        return psi;
    }

    /**
     * Returns a brief textual description of this residue.
     * @return amino acid type (phi angle, psi angle)
     */
    public String toString()
    {
        return String.format("%s (phi=%.1f, psi=%.1f)", aminoAcid.toString(), phi, psi);
    }

    /**
     * Tests all fields for equality.
     * @return whether the two Residues are the same in a logical sense
     */
    public boolean equals(Object obj)
    {
        if ( obj == null )
            return false;
        if ( obj == this )
            return true;
        if ( !(obj instanceof Residue) )
            return false;
        
        Residue anotherResidue = (Residue)obj;
        if ( aminoAcid != anotherResidue.aminoAcid ||
             !phi.equals(anotherResidue.phi) ||
             !psi.equals(anotherResidue.psi)          )
            return false;
        return true;
    }

    /**
     * Returns the hash code.
     * @return the hash code
     */
    public int hashCode()
    {
        return Objects.hash(aminoAcid, phi, psi);
    }
}
