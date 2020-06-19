package bookkeeperISW2_D2M1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CsvWriter {
	
	public CsvWriter() {
		
	}
	
	public static void write (List<Data> list) {
		
		try (PrintWriter writer = new PrintWriter(new File("testProva2.csv"))) {

		      StringBuilder sb = new StringBuilder();
		      sb.append("Release");
		      sb.append(';');
		      
		      sb.append("Filename");
		      sb.append(';');
		      
		      sb.append("LOC");
		      sb.append(';');
		      
		      sb.append("NR");
		      sb.append(';');
		      
		      //sb.append("nFix");
		      //sb.append(';');
		      
		      sb.append("nAuth");
		      sb.append(';');
		      
		      sb.append("locTouched");
		      sb.append(';');
		      
		      sb.append("locAdded");
		      sb.append(';');
		      
		      sb.append("max_locAdded");
		      sb.append(';');
		      
		      sb.append("avg_locAdded");
		      sb.append(';');
		      
		      sb.append("churn");
		      sb.append(';');
		      
		      sb.append("max_churn");
		      sb.append(';');
		      
		      sb.append("avg_churn");
		      sb.append(';');
		      
		      sb.append("chgSetSize");
		      sb.append(';');
		      
		      sb.append("max_chgSetSize");
		      sb.append(';');
		      
		      sb.append("avg_chgSetSize");
		      sb.append(';');
		      
		      sb.append("Buggy");
		      sb.append('\n');

		      for (int i=0;i<list.size();i++) {	  
		    	  
		    	  if(list.get(i).getRelease().getIndex()<8) {
		      
				      sb.append(list.get(i).getRelease().getIndex());	//release index
				      sb.append(';');
				      sb.append(list.get(i).getFilename());	//filename
				      sb.append(';');
				      sb.append(list.get(i).getLoc());	
				      sb.append(';');
				      sb.append(list.get(i).getNr());	
				      sb.append(';');
				    //sb.append(list.get(i).getNr());	
				    //sb.append(';');
				      sb.append(list.get(i).getnAuth());	
				      sb.append(';');
				      sb.append(list.get(i).getLocTouched());	
				      sb.append(';');
				      sb.append(list.get(i).getLocAdded());	
				      sb.append(';');
				      sb.append(list.get(i).getMax_locAdded());	
				      sb.append(';');
				      sb.append(list.get(i).getAvg_locAdded());	
				      sb.append(';');
				      sb.append(list.get(i).getChurn());	
				      sb.append(';');
				      sb.append(list.get(i).getMax_churn());	
				      sb.append(';');
				      sb.append(list.get(i).getAvg_churn());	
				      sb.append(';');
				      sb.append(list.get(i).getChgSetSize());	
				      sb.append(';');
				      sb.append(list.get(i).getMax_chgSetSize());	
				      sb.append(';');
				      sb.append(list.get(i).getAvg_chgSetSize());	
				      sb.append(';');
				      sb.append(list.get(i).getBuggy());	
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