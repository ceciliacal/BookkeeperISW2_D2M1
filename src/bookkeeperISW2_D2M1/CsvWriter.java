package bookkeeperISW2_D2M1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CsvWriter {
	
	public CsvWriter() {
		
	}
	
	public static void write (List<Data> list) {
		
		try (PrintWriter writer = new PrintWriter(new File("test.csv"))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("Release");
		      sb.append(';');
		      
		      sb.append("Filename");
		      sb.append(';');
		      
		      sb.append("Buggy");
		      sb.append('\n');

		      for (int i=0;i<list.size();i++) {	  
		    	  
		    	  if(list.get(i).getRelease()<8) {
		      
				      sb.append(list.get(i).getRelease());	//release index
				      sb.append(';');
				      sb.append(list.get(i).getFilename());	//filename
				      sb.append(';');
				      sb.append(list.get(i).getBuggy());	//buggyness
				      sb.append('\n');
		    	 
		    	  }
		      }

		      writer.write(sb.toString());

		      System.out.println("done!");

		    } catch (FileNotFoundException e) {
		      System.out.println(e.getMessage());
		    }
		
	}
}