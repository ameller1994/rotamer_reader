import java.util.*;

/**
 * Represents backbone-dependent rotamer data for an amino acid.
 */
public abstract class SideChainRotamerLibrary {
    
    public SideChainRotamerLibrary(String fileName)
    {
	    List<String> list = new ArrayList<String>();
        String filenameString = "rotamer_library/" + fileName;
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
}
