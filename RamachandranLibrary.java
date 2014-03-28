import java.util.*;
import java.util.zip.GZIPInputStream;
import java.io.*;

public class RamachandranLibrary
{
    public static final RamachandranLibrary INSTANCE = new RamachandranLibrary();

    private RamachandranLibrary()
    {
        if (INSTANCE != null)
            throw new IllegalStateException("this should be a singleton!");
        GZIPInputStream gzip = null;
        BufferedReader br = null;
        try
            {
                gzip = new GZIPInputStream(new FileInputStream("ramachandran/NDRD_TCB.txt.gz"));
                br = new BufferedReader(new InputStreamReader(gzip));
                PrintWriter pw = new PrintWriter("NDRD_extract.txt");
                //for (int i=0; i < 250000; i++)
                int count = 0;
                while (true)
                    {
                        String currentLine = br.readLine();
                        if ( currentLine == null )
                            break;
                        String[] fields = currentLine.split("\\s+");
                        count++;
                        if ( fields.length > 1 && ! fields[1].equals("left") )
                        //pw.println(br.readLine());
                            System.out.println(currentLine);
                        else
                            System.out.print(count + "\r");
                    }
                br.close();
                gzip.close();
            }
        catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
    }

    public String toString()
    {
        return "";
    }

    public static void main(String[] args)
    {
        System.out.println(INSTANCE);
    }
}
