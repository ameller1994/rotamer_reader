import java.util.*;
import java.io.*;
import org.apache.commons.math3.geometry.euclidean.threed.*;
import org.jgrapht.graph.*;

/**
   A class used that lets us build larger peptides. It contains information about geometry, force field parameters, and connections for the 20 standard amino acids. Each amino acid has been processed in Macromodel with a version corresponding to the N terminal, the C terminal, and a version corresponding to being part of a polypeptide chain. 
*/

public class ProtoAminoAcid {
    
    /** A nested enum describing a ProtoAminoAcid as either a C-terminal, N-terminal, or central amino acid */
    private enum Position {
	
	/** Represents N-terminal postion */
	N_TERMINAL,
	
	/** Represents C-terminal position */
	C_TERMINAL,

	/** Represents chain Amino Acid (not on terminal ends) */
	CHAIN;

	public String toString() {
	    switch(this) 
		{
		case N_TERMINAL : return "nterm";
		case C_TERMINAL : return "cterm";
		default : return "middle";
		}
	    
	}
		    
			
    }

    /** A simple weighted graph which represents bonds between atoms */
    SimpleWeightedGraph<Atom, DefaultWeightedEdges> bonds;
    
    /**
       A list of the atoms that are not sticky in the prototypical amino acid
    */
    List<Atom> nonStickyAtoms;

    /**
       An atom used to indicate which atom is the C alpha atom
    */
    Atom cAlpha;

    
    public ProtoAminoAcid(AminoAcid aminoAcid, Position position) {

	Scanner thisFile = null;
	try
	    {
		thisFile = new Scanner(new FileReader("aa_library/" + aminoAcid.toString().toLowerCase()+"_"+position.toString()+"-out"));

		int delimeterCount = 0;
		int totalAtoms = 0;

		while (thisFile.hasNextLine())
		    {
			//ignore lines before the data we are interested in 
			String currentLine = thisFile.nextLine();

			if (currentLine.indexOf("m_atom[") >= 0)
			    {
				String[] parts = currentLine.split("\\s+");
				System.out.println(parts[1].substring(7,parts[1].length()-1));
				totalAtoms = Integer.parseInt(parts[1].substring(7,parts[1].length()-1));
			    }
		       
			if (currentLine.indexOf(":::") < 0 && delimeterCount < 5) 
			    {
				continue;
			    }
			else if (currentLine.indexOf(":::") >= 0)
			    {
				delimeterCount++;
				continue;
			    }
		      

	
			for(int i=0; i< totalAtoms; i++) {
			    
			    // parse currentLine to access data fields
			    String processedLine = currentLine.replaceAll("\"","");
			    String[] parts = processedLine.split("\\s+");
			    
			    String atomName = parts[14].substring(0,1);
			    Atom atom = new Atom(atomName, new Vector3D(Double.parseDouble(parts[3]),Double.parseDouble(parts[4]),Double.parseDouble(parts[5])));
			    
			    System.out.println(parts[3]);
			    System.out.println(parts[14]);
			    
			    currentLine = thisFile.nextLine();
			}
		    }
	    }
	catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        finally
            {
                if (thisFile != null)
                    thisFile.close();
            }

    }

    public static void main(String[] args) {
	ProtoAminoAcid test = new ProtoAminoAcid(AminoAcid.ALA, Position.N_TERMINAL);
	
    }
}
