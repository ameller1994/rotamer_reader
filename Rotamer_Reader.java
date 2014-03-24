import java.io.*;
import java.util.*;
import java.io.FileReader;

public class Rotamer_Reader 
{
    public static void main(String[] args)
    {
	List<String> list = new ArrayList<String>();
	String filenameString = "rotamer_library/asp.bbdep.rotamers.txt";
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
	/*try{
	    BufferedReader reader = new BufferedReader(new FileReader(file));
	    while (reader.readLine() != null)
		list.add(reader.readLine());
		}*/
	catch(IOException ioe) {
	    ioe.printStackTrace();
	}
	int phi = 120;
	int psi = 120; 
	
	for (String s: list)
	    {
		String[] parts = s.split(" ");
		if (parts[0].equals("ASN") && Integer.getInteger(parts[1]) == phi && Integer.getInteger(parts[2]) == psi)
		    {
			System.out.println(parts[9]);
		    }
	    }

    }
}
