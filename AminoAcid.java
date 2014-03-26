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
    ALA("Ala",      "alanine",       RotamerType.HAS_NO_ROTAMERS),
    GLY("Gly",      "glycine",       RotamerType.HAS_NO_ROTAMERS),
    VAL("Val",      "valine",        RotamerType.IS_ROTAMERIC),
    LEU("Leu",      "leucine",       RotamerType.IS_ROTAMERIC),
    ILE("Ile",      "isoleucine",    RotamerType.IS_ROTAMERIC),
    PRO("Cpr",      "cis-proline",   RotamerType.IS_ROTAMERIC),
    PRO("Tpr",      "trans-proline", RotamerType.IS_ROTAMERIC),
    PHE("Phe",      "phenylalanine", RotamerType.IS_ROTAMERIC),
    TYR("Tyr",      "tyrosine",      RotamerType.IS_ROTAMERIC),
    TRP("Trp",      "tryptophan",    RotamerType.NON_ROTAMERIC),
    SER("Ser",      "serine",        RotamerType.IS_ROTAMERIC),
    THR("Thr",      "threonine",     RotamerType.IS_ROTAMERIC),
    CYS("Cys",      "cysteine",      RotamerType.IS_ROTAMERIC),
    MET("Met",      "methionine",    RotamerType.IS_ROTAMERIC),
    ASN("Asn",      "aspargine",     RotamerType.NON_ROTAMERIC),
    GLN("Gln",      "glutamine",     RotamerType.NON_ROTAMERIC),
    LYS("Lys",      "lysine",        RotamerType.IS_ROTAMER),
    ARG("Arg",      "arginine",      RotamerType.NON_ROTAMERIC),
    HIS("His",      "histidine",     RotamerType.NON_ROTAMERIC),
    ASP("Asp",      "aspartate",     RotamerType.NON_ROTAMERIC),
    GLU("Glu",      "glutamate",     RotamerType.NON_ROTAMERIC);

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
    AminoAcid(String shortName, String fullName, String filename, RotamerType rotamerType)
    {
        this.fullName = fullName;
        this.shortName = shortName;
        this.rotamerType = rotamerType;

        // determine filename
        if ( rotamerType == IS_ROTAMERIC )
            filename = Settings.ROTAMER_LIBRARY_DIRECTORY + shortName.toLowerCase() + ".bbdep.rotamers.lib";
        else if ( rotamerType == NON_ROTAMERIC )
            filename = Settings.ROTAMER_LIBRARY_DIRECTORY + shortName.toLowerCase() + ".bbdep.densities.lib";
        else if ( rotamerType == HAS_NO_ROTAMERS )
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
        HAS_NO_ROTAMERS;
    }

    /**
     * Gives a random set of sidechain torsion angles for this amino acid.
     * @return the torsion angles X1, X2, ... as an ordered list in degrees
     */
    public List<Double> getRandomRotamer()
    {
	    return new LinkedList<Double>();
	}
/*
    public static List<Double> getRotamer(AminoAcid aminoAcid, double phi, double psi) {
		
	List<String> list = new ArrayList<String>();
        String filenameString = "rotamer_library/" + aminoAcid.getName().toLowerCase() + ".bbdep.rotamers.lib";
        //System.out.println(System.getProperty("user.dir"));

	//Read in entire file
        Scanner thisFile = null;
        try {
            thisFile = new Scanner(new FileReader(filenameString));
            while (thisFile.hasNextLine())
                {
                    String currentLine = thisFile.nextLine();
                    list.add(currentLine);
                }
	}
        catch(IOException ioe) {
            ioe.printStackTrace();
        }

	List<List<Double>> candidateRotamers = new ArrayList<List<Double>>();
	//List<Double> probabilities = new ArrayList<Double>();
	
	//Create a rolling sum of the probabilities so that each rotamer has a probability of being returned corresponding to its probability
	List<Double> probSums = new ArrayList<Double>();
	
	double sum = 0.0;

        for (String s: list)
            {
		
		List<String> parts = new ArrayList<String>();
		
		
		StringTokenizer st = new StringTokenizer(s, " ", false);
		while(st.hasMoreTokens())
		    parts.add(st.nextToken());
		    


		if(parts.get(0).equals(aminoAcid.getName().toUpperCase()) && Double.parseDouble(parts.get(1)) == phi && Double.parseDouble(parts.get(2)) == psi)
                    {
			List<Double> rotamer = new ArrayList<Double>();

                        //System.out.println(Double.parseDouble(parts.get(9)));
			rotamer.add(Double.parseDouble(parts.get(9)));
			rotamer.add(Double.parseDouble(parts.get(10)));
			rotamer.add(Double.parseDouble(parts.get(11)));
			rotamer.add(Double.parseDouble(parts.get(12)));
			
			candidateRotamers.add(rotamer);
			sum = sum + Double.parseDouble(parts.get(8));
			probSums.add(sum);

			//probabilities.add(Double.parseDouble(parts.get(8)));
                    }
            }
	
	//System.out.println(sum);

	double rand = Math.random();
	System.out.println(rand);
	
	double curr = probSums.get(0);
	int counter = 0; 

	//Loop through probSums until you fall within range with rand
	while (rand > curr)
	    {
		counter++;
		curr = probSums.get(counter);
		
	    }
	System.out.println(probSums.get(counter));
	System.out.println(probSums.get(counter+1));

	//based on random chance return set of angles describing rotamer
	return candidateRotamers.get(counter);
    }
*/
					  
    // for testing
    public static void main(String[] args)
    {
        System.out.println("hello");
	    //System.out.println(AminoAcid.getRotamer(AminoAcid.Gln, 120.0, 120.0));
    }
}
