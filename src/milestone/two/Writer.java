package milestone.two;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import project.bookkeeper.MainControl;

public class Writer {

	
	private Writer() {
	    throw new IllegalStateException("Utility class");
	  }
	
	public static String formatter(double val) {
		
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(val);
				
	}


	
	public static void write (List<DatasetPart> list) throws FileNotFoundException {
		
		try (PrintWriter writer = new PrintWriter(new File("m2d2output.csv"))) {
			
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
			
			//mancano due cose
			
			sb.append("Classifier");
			sb.append(',');
			
			//mancano balancing e feature selection
			
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
				List <Integer> sublist = list.get(i).getTrainingRel().subList(0, i+1);
				
				
				sb.append(MainControl.PROJECTNAME);
				sb.append(',');
				sb.append(sublist);
				sb.append(',');
				sb.append("RandomForest");
				sb.append(',');
				sb.append(formatter(list.get(i).getPercTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTesting()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTpRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFpRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTnRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFnRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPrecisionRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getRecallRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getAucRF()));
				sb.append(',');
				sb.append(formatter(list.get(i).getKappaRF()));
				sb.append('\n');
				
				sb.append(MainControl.PROJECTNAME);
				sb.append(',');
				sb.append(sublist);
				sb.append(',');
				sb.append("NaiveBayes");
				sb.append(',');
				sb.append(formatter(list.get(i).getPercTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTesting()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTpNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFpNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTnNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFnNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPrecisionNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getRecallNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getAucNB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getKappaNB()));
				sb.append('\n');
				
				sb.append(MainControl.PROJECTNAME);
				sb.append(',');
				sb.append(sublist);
				sb.append(',');
				sb.append("Ibk");
				sb.append(',');
				sb.append(formatter(list.get(i).getPercTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTraining()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPercBugTesting()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTpIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFpIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getTnIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getFnIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getPrecisionIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getRecallIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getAucIB()));
				sb.append(',');
				sb.append(formatter(list.get(i).getKappaIB()));
				sb.append('\n');
				
				
				
		
				
			}
			
			writer.write(sb.toString());
		
		
		}
	}
		
}
