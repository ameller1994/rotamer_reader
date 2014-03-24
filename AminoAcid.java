// This class represents an amino acid.

import java.util.*;
import java.io.*;
import com.google.common.collect.*;

public enum AminoAcid
{
    // define all of the possible amino acids
    ALA("Ala", "alanine",       true),
    GLY("Gly", "glycine",       true),
    VAL("Val", "valine",        true),
    LEU("Leu", "leucine"        true),
    ILE("Ile", "isoleucine"     true),
    PRO("Pro", "proline",       true),
    PHE("Phe", "phenylalanine", false),
    TYR("Tyr", "tyrosine",      true),
    TRP("Trp", "tryptophan",    false),
    SER("Ser", "serine",        true),
    THR("Thr", "threonine",     true),
    CYS("Cys", "cysteine",      true),
    MET("Met", "methionine",    true),
    ASN("Asn", "aspargine",     false),
    GLN("Gln", "glutamine",     false),
    LYS("Lys", "lysine",        true),
    ARG("Arg", "arginine",      false),
    HIS("His", "histidine",     false),
    ASP("Asp", "aspartate",     false),
    GLU("Glu", "glutamate",     false);

    // fields for each amino acid
    private String shortName;                        // e.g. Ala
    private String fullName;                         // e.g. alanine
    private boolean rotameric;                       // false if the last sidechain torsion involves an sp2 atom

    // enum constructor
    AminoAcid(String shortName, String fullName, boolean rotameric)
    {
        this.fullName = fullName;
        this.shortName = shortName;
        this.rotameric = rotameric;
    }

    public String toString()
    {
        return shortName;
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
