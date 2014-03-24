import java.util.*;
import java.io.*;
import java.lang.*;

public enum AminoAcid{
    Ala, Gly, Val, Leu, Ile, Pro, Phe, Tyr, Trp, Ser, Thr, Cys, Met, Asn, Gln, Lys, Arg, His, Asp, Glu;

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
    public String getName(){
	switch (this)
	    {
	    case Ala : return "Ala";
	    case Gly : return "Gly";
	    case Leu : return "Leu";
	    case Val : return "Val";
	    case Ile : return "Ile";
	    case Pro : return "Pro";
	    case Phe : return "Phe";
	    case Tyr : return "Tyr";
	    case Trp : return "Trp";
	    case Ser : return "Ser";
	    case Thr : return "Thr";
	    case Cys : return "Cys";
	    case Met : return "Met";
	    case Asn : return "Asn";
	    case Gln : return "Gln";
	    case Lys : return "Lys";
	    case Arg : return "Arg";
	    case His : return "His";
	    case Asp : return "Asp";
	    case Glu : return "Glu";
	    default : return "ERROR";
	    }
    }
    public static void main(String[] args) {
	System.out.println(AminoAcid.getRotamer(AminoAcid.Gln, 120.0, 120.0));
    }

			   
			   
}
