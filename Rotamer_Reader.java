import java.io.*;
import java.util.*;
import java.io.FileReader;

public class Rotamer_Reader 
{
    public static void main(String[] args)
    {
	List<String> list = new ArrayList<String>();
	File file = new File("/home/ameller/rotamer/asp.bbdep.rotamers.txt");
	BufferedReader reader = new BufferedReader(new FileReader(file));
	while (reader.readLine() != null)
	    list.add(reader.readLine());

    }
}
