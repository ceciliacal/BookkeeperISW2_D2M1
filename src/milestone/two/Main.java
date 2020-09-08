package milestone.two;


import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import project.bookkeeper.MainControl;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class Main {
	
	protected static List<Integer> releases;
	
		
	
	   public static void main(String[] args) throws Exception {
		   
		   String csvPath;
		   String arffPath;
		   
		   List<DatasetPart> parts;
		  
		   releases=MainControl.getReleases();
		 
		   
		   csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\outputFinali\\zookeeper_BuggyDataset_outputD2M1.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\outputFinali\\datasetWekaZook.arff";
		   
		   //csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\outputFinali\\bookkeeper_BuggyDataset_outputD2M1.csv";
		   //arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\outputFinali\\datasetWekaBookk.arff";

		   csv2arff(csvPath,arffPath);
		   
		   parts = walkForward(arffPath);
		  
		   
		   List<EvaluationData> dbEntryList = ClassificationControl.startEvaluation(parts,arffPath);
		   WriterBoundary.write(dbEntryList);
		   
		   
	   }
	   


	   public static void csv2arff(String csvPath, String arffPath) throws IOException {
		  
		   // load CSV
		    CSVLoader loader = new CSVLoader();
		    loader.setSource(new File(csvPath));
		    Instances data = loader.getDataSet();	//get instances object

		    // save ARFF
		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(data);	//set the dataset we want to convert
		    //and save as ARFF
		    saver.setFile(new File(arffPath));
		    saver.writeBatch();
	   }
	 
	
	   
	  
	   
	 
	   

	   public static List <DatasetPart> walkForward(String arffPath) {
		   List <DatasetPart> parts = new ArrayList<>();
		   Instances data=null;
		   
		   	//load dataset
			DataSource source;
			try {
				source = new DataSource(arffPath);
				data = source.getDataSet();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (data.classIndex() == -1) {
			       data.setClassIndex(data.numAttributes() - 1);
			    }
		    
		  
		    
		    
		    int numTraining;
		    int numTesting;
		    Instances training2=null;
		    Instances testing2=null;
		    
		    DatasetPart part = null;
		    
		    
		    for(int j=2; j <=releases.size(); j++) {
	    	
	    	  numTraining=0;
		      numTesting=0;
	    	
		    
			   for(int i=0; i<data.size();i++) {
				   
				   int release = Integer.parseInt(data.get(i).toString(0));
				   
				   if(release<j) {
					   
					   numTraining++;
					   
				
					   
				   }else if(release == j) {
					   
					   numTesting++;
					
				   }
			   }
				   
				   
				   
					   
			    training2 = new Instances(data, 0, numTraining);
			    testing2 = new Instances(data, numTraining, numTesting);
			    part = new DatasetPart (training2, testing2);
			    parts.add(part);
		    
		    }
		    
		
	   
		    getNumTrainingRelease(parts );
		    calculatenumDefectsInTraining(parts,  data );
		    calculatenumDefectsInTesting(parts,  data );
		    
		  
	   
	   return parts;
	   
	   }
	   
	   public static void getNumTrainingRelease(List <DatasetPart> parts ) {
		   
		   List<Integer> trainingRel;
		   int testingRel = 0;
		   
		   for(int j=2; j <=releases.size(); j++) {
			   
			   trainingRel = new ArrayList<>();
		    	
				    
				   for(int i=0; i<releases.size();i++) {
					   
					   int release = releases.get(i); 
					   
					   if(release<j) {
						   
						   trainingRel.add(release);
						   						 
						   
					   }else if(release == j) {
						   
						   testingRel = release;
						   parts.get(j-2).setTestingRel(testingRel);
						   parts.get(j-2).setTrainingRel(trainingRel);
						   break;
					   }
				   }
				   

		    }
		   
		   


		   
	   }
	   
	   public static void calculatenumDefectsInTraining(List <DatasetPart> parts, Instances data ) {
		   
		   int numDefectsInTraining;
		   int numTraining=0;
		   
		   
		  for (int i=0;i<parts.size();i++) {
			  
			  numDefectsInTraining=0;
			  numTraining=parts.get(i).getTraining().size();
			 
			  for (int j=0;j<parts.get(i).getTraining().size();j++) {
				  
				  Instance instance = parts.get(i).getTraining().get(j);
				
				if (instance.stringValue(data.numAttributes()-1).equals("Y")) {
					
					numDefectsInTraining++;
				  }
				  
			  }
			  
			  double percDefects=numDefectsInTraining/(float)parts.get(i).getTraining().size();
			  double percTraining = numTraining/(float)data.size();
			  parts.get(i).setPercBugTraining(percDefects*100);
			  parts.get(i).setPercTraining(percTraining*100);
			  
			  
		  }
		  
		  
	   
	   }
	   
	   public static void calculatenumDefectsInTesting(List <DatasetPart> parts, Instances data ) {
		   
		   int numDefectsInTesting;
		   
		   
		   
		  for (int i=0;i<parts.size();i++) {
			  
			  numDefectsInTesting=0;
			 
			  for (int j=0;j<parts.get(i).getTesting().size();j++) {
				  
				  Instance instance = parts.get(i).getTesting().get(j);
				
				if (instance.stringValue(data.numAttributes()-1).equals("Y")) {
					
					numDefectsInTesting++;
				  }
				  
			  }
			  
			  double percDefects=numDefectsInTesting/(float)parts.get(i).getTesting().size();
			  parts.get(i).setPercBugTesting(percDefects*100);			  
			  
		  }
	   
	   }
	   
	 
	 
}
