package milestone.two;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import project.bookkeeper.MainControl;

public class Writer {

	
	private Writer() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static String formatter(double val) {
		
		DecimalFormat df = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));
		return df.format(val);
				
	}


	
	public static void write (List<EvaluationData> list) throws FileNotFoundException {
		
		//try (PrintWriter writer = new PrintWriter(new File("outputFinali\\bookkeeper_WekaDataset_m2d2output.csv"))) {
		try (PrintWriter writer = new PrintWriter(new File("outputFinali\\zookeeper_WekaDataset_m2d2output.csv"))) {
			
			StringBuilder sb = new StringBuilder();
			sb.append("Dataset");
			sb.append(',');
			
			sb.append("#TrainingRelease");
			sb.append(',');
			
			sb.append("%Training");
			sb.append(',');
			
			sb.append("%DefectiveTraining");
			sb.append(',');
			
			sb.append("%DefectiveTesting");
			sb.append(',');
			
			sb.append("Classifier");
			sb.append(',');
			
			sb.append("Balancing");
			sb.append(',');			
			
			sb.append("FeatureSelection");
			sb.append(',');
			
			sb.append("TP");
			sb.append(',');
			
			sb.append("FP");
			sb.append(',');
			
			sb.append("TN");
			sb.append(',');
			
			sb.append("FN");
			sb.append(',');
			
			sb.append("Precision");
			sb.append(',');

			sb.append("Recall");
			sb.append(',');
			
			sb.append("AUC");
			sb.append(',');
			
			sb.append("Kappa");
			sb.append('\n');
		
			for (int i=0;i<list.size();i++) {
				
				
				sb.append(MainControl.PROJECTNAME);
				sb.append(',');
				sb.append(list.get(i).getTrainingRel().size());
				sb.append(',');
				
				sb.append(formatter(list.get(i).getPercTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTesting()));
				sb.append(',');
				sb.append(list.get(i).getClassifier());
				sb.append(',');
				sb.append(list.get(i).getBalancing());
				sb.append(',');
				sb.append(list.get(i).getFeatureSelection());
				sb.append(',');
				sb.append(formatter(list.get(i).getTp()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFp()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTn()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFn()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPrecision()));
				sb.append(',');
				sb.append(formatter(list.get(i).getRecall()));
				sb.append(',');
				sb.append(formatter(list.get(i).getAuc()));
				sb.append(',');
				sb.append(formatter(list.get(i).getKappa()));
				sb.append('\n');
				
			}
		
			
			
			writer.write(sb.toString());
		
		
		}
	}
		
}
