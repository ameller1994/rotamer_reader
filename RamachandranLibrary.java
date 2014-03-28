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
    }

    public static void main(String[] args)
    {
        GZIPInputStream gzip = null;
        BufferedReader br = null;
        try
            {
                gzip = new GZIPInputStream(new FileInputStream("ramachandran/NDRD_TCB.txt.gz"));
                br = new BufferedReader(new InputStreamReader(gzip));
                for (int i=0; i < 500; i++)
                    {
                        System.out.println(br.readLine());
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
}
