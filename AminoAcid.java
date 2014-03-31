
// This class represents an amino acid.

import java.util.*;
import java.io.*;
import com.google.common.collect.*;

/**
 * Represents the standard amino acids with different categories
 * for cis and trans proline.  Backbone-dependent rotamer data is read
 * from the Dunbrack library.  We use traditional rotamers for amino acids
 * containing all sp3-sp3 bonds in their sidechains.  That is, we use ordered
 * tuples (X<sub>1</sub>, X<sub>2</sub>, ..., X<sub>n</sub>) to represent the
 * backbone torsion angles, where n is the number of sidechain torsions.  (OHs
 * are not counted in the number of torsions.  We use a combination of an ordered
 * tuple and a DiscreteProbabilityDistribution to represent the non-rotameric
 * amino acids.  These amino acids contain an sp<sup>3</sup>-sp<sup>2</sup>
 * torsion at the end (e.g., phenylalanine). That is, we use an ordered tuple
 * (X<sub>1</sub>, X<sub>2</sub>, ..., X<sub>n-1</sub>) to represent the standard
 * rotamer part of the sidechain and then a probability distribution to represent
 * the terminal torsion.  Some amino acids do not contain any rotable bonds (e.g.
 * glycine) and therefore do not have associated library data.
 */
public enum AminoAcid
{
    // enum constants
    ALA("Ala",      "alanine",         RotamerType.HAS_NO_ROTAMERS),
    GLY("Gly",      "glycine",         RotamerType.HAS_NO_ROTAMERS),
    VAL("Val",      "valine",          RotamerType.IS_ROTAMERIC),
    LEU("Leu",      "leucine",         RotamerType.IS_ROTAMERIC),
    ILE("Ile",      "isoleucine",      RotamerType.IS_ROTAMERIC),
    //PRO("Pro",      "proline",         RotamerType.SPECIAL),
    CPR("Cpr",      "cis-proline",     RotamerType.IS_ROTAMERIC),
    TPR("Tpr",      "trans-proline",   RotamerType.IS_ROTAMERIC),
    PHE("Phe",      "phenylalanine",   RotamerType.NON_ROTAMERIC),
    TYR("Tyr",      "tyrosine",        RotamerType.NON_ROTAMERIC),
    TRP("Trp",      "tryptophan",      RotamerType.NON_ROTAMERIC),
    SER("Ser",      "serine",          RotamerType.IS_ROTAMERIC),
    THR("Thr",      "threonine",       RotamerType.IS_ROTAMERIC),
    CYS("Cys",      "cysteine",        RotamerType.IS_ROTAMERIC),
    MET("Met",      "methionine",      RotamerType.IS_ROTAMERIC),
    ASN("Asn",      "aspargine",       RotamerType.NON_ROTAMERIC),
    GLN("Gln",      "glutamine",       RotamerType.NON_ROTAMERIC),
    LYS("Lys",      "lysine",          RotamerType.IS_ROTAMERIC),
    ARG("Arg",      "arginine",        RotamerType.IS_ROTAMERIC),
    HIS("His",      "histidine",       RotamerType.NON_ROTAMERIC),
    ASP("Asp",      "aspartate",       RotamerType.NON_ROTAMERIC),
    GLU("Glu",      "glutamate",       RotamerType.NON_ROTAMERIC),
    ALL("All",      "all amino acids", RotamerType.SPECIAL);

    // fields

    /**
     * An abbreviation like "Ala".
     */
    private String shortName;

    /**
     * A full name like "alanine".
     */
    private String fullName;

    /**
     * Indicates the kinds of sidechain torsions present.
     */
    private RotamerType rotamerType;

    /**
     * The filename containing the data for this residue.
     */
    private String filename;

    /**
     * Contains the rotamer library data for this amino acid.
     */
    private SideChainRotamerLibrary library;

    // enum constructor
    AminoAcid(String shortName, String fullName, RotamerType rotamerType)
    {
        this.fullName = fullName;
        this.shortName = shortName;
        this.rotamerType = rotamerType;

        // determine filename
        if ( rotamerType == RotamerType.IS_ROTAMERIC )
            filename = Settings.ROTAMER_LIBRARY_DIRECTORY + shortName.toLowerCase() + ".bbdep.rotamers.lib";
        else if ( rotamerType == RotamerType.NON_ROTAMERIC )
            filename = Settings.ROTAMER_LIBRARY_DIRECTORY + shortName.toLowerCase() + ".bbdep.densities.lib";
        else if ( rotamerType == RotamerType.HAS_NO_ROTAMERS || rotamerType == RotamerType.SPECIAL )
            filename = "";
        else
            throw new IllegalArgumentException("Unrecognized RotamerType in AminoAcid constructor!");

	    //call constructor of side chain rotamer library
    }

    /**
     * Returns a brief description of this amino acid.
     * @return the textual description of this amino acid
     */
    @Override
    public String toString()
    {
        return shortName;
    }

    /**
     * Retuns the filename associated with an amino acid
     * @return the String containing the filename
     */
    public String getFilename()
    {
	return filename;
    }

    /**
     * Indicates whether the amino acid can be represented by standard rotamers,
     * is non-rotameric, or contains no rotatable bonds at all.  For a full description,
     * see the <a href="http://dunbrack.fccc.edu/bbdep2010/">Dunbrack backbone-dependent
     *   rotamer library</a> page.
     */
    public enum RotamerType
    {
        /**
         * Represents an amino acid that has standard rotameric degrees of freedom.
         * That is, all sidechain torsions involves sp3-sp3 bonds.
         */
        IS_ROTAMERIC,

        /**
         * Represents an amino acid that contains a terminal sp2-sp3 torsion.
         */
        NON_ROTAMERIC,

        /**
         * Represents an amino acid that does not have sidechain torsions.
         */
        HAS_NO_ROTAMERS,

        /**
         * Represents ALL of the amino acids for the Ramachandran library.
         */
        SPECIAL;
    }

    /**
     * Gives a random set of sidechain torsion angles for this amino acid.
     * @param psi the backbone angle
     * @param phi the backbone angle
     * @return the torsion angles X1, X2, ... as an ordered list in degrees
     */
    public List<Double> getRandomRotamer(Double phi, Double psi)
    {
	if (rotamerType == RotamerType.HAS_NO_ROTAMERS)
	    throw new IllegalArgumentExcpetion("Non rotameric amino acid");
	else if (rotamerType == RotamerType.IS_ROTAMERIC) {
	    RotamericLibrary rotLib = new RotamericLibrary(this);
	    DiscreteProbabilityDistribution<List<Double>> dpd = rotLib.get(phi,psi);
	    return dpd.getRandom();
	}
	else if (rotamerType == RotamerType.NON_ROTAMERIC) {
	    NonRotamericLibrary nRotLib = new NonRotamericLibrary(this);
	    DiscreteProbabilityDistribution<NonRotamericLibrary.NonRotamericAngles> dpd1 = nRotLib.get(phi,psi);
	    NonRotamericLibrary.NonRotamericAngles nrA = dpd1.getRandom();
	    Double lastChi = nrA.getDiscreteProbabilityDistribution().getRandom();
	    
	    return new LinkedList<Double>();
	}
					  
    // for testing
    public static void main(String[] args)
    {
        System.out.println("hello");
	    //System.out.println(AminoAcid.getRotamer(AminoAcid.Gln, 120.0, 120.0));
    }
}
