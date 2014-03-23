
public enum AminoAcid{
    Ala, Gly, Val, Leu, Ile, Pro, Phe, Tyr, Trp, Ser, Thr, Cys, Met, Asn, Gln, Lys, Arg, His, Asp, Glu;

    public static List<Double> getRotamer(AminoAcid aminoAcid, double phi, double psi) {
	List<Double> rotamerAngles = new ArrayList<Double>();
	
	List<String> list = new ArrayList<String>();
        String filenameString = aminoAcid.getName().toLowerCase() + ".bbdep.rotamers.txt";
        System.out.println(System.getProperty("user.dir"));

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

	List<List<Double>> candidateAngles = new ArrayList<List<Double>>();
	List<Double> probabilites = new ArrayList<Double>();

        for (String s: list)
            {
                String[] parts = s.split(" ");
                if(parts[0].compareTo(aminoAcid.getName().toUpperCase()) && Double.getDouble(parts[1]) == phi && Double.getDouble(parts[2]) == psi)
                    {
                        System.out.println(parts[9]);
			rotamerAngles.add(parts[9]);
			
                    }
            }


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
	    case Met ; return "Met";
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
			   
}
