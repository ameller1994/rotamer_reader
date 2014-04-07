
/**
   A class used that lets us build larger peptides. It contains information about geometry, force field parameters, and connections for the 20 standard amino acids. Each amino acid has been processed in Macromodel with a version corresponding to the N terminal, the C terminal, and a version corresponding to being part of a polypeptide chain. 
*/

public class ProtoAminoAcid {
    
    /**
       A list of the atoms that are not sticky in the prototypical amino acid
    */
    List<Atom> nonStickyAtoms;

    /**
       An atom used to indicate which atom is the C alpha atom
    */
    Atom cAlpha;

    
