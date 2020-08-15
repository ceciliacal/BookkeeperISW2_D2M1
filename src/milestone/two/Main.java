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
		   
		   csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetVirgole.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetWeka.arff";

		   csv2arff(csvPath,arffPath);
		   
		   parts = walkForward(arffPath);
		   int dim=getDatasetSize(arffPath) ;
		   
		   List<EvaluationData> dbEntryList = Classification.startEvaluation(parts,dim);
		   Writer.write(dbEntryList);
		   
	   }
	   
	   public static void run() throws Exception {
		   
		   String csvPath;
		   String arffPath;
		   
		   List<DatasetPart> parts;
		  
		   releases=MainControl.getReleases();
		   
		   csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetVirgole.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetWeka.arff";

		   csv2arff(csvPath,arffPath);
		   
		   parts = walkForward(arffPath);
		   int dim=getDatasetSize(arffPath) ;
		   
		   List<EvaluationData> dbEntryList = Classification.startEvaluation(parts,dim);
		   Writer.write(dbEntryList);
		   
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
	 
	
	   
	   public static void getInstances(Instance instance, List<DatasetPart> parts, Instances data, int run) {
		   
		   int endTraining = run-1;		//l'ultima release su cui faccio training è la nRun-1
		   int testingRelease = run;	//la release su cui faccio testing coincide con il nRun
		   
		   int numTraining=0;			//conta quante istanze devo prendere per costruire training set
		   int numTesting=0;			//conta quante istante devo prendere per costruire testing set 
		   
		   for (int i=0; i<testingRelease; i++) {	//scorro le release
			  
			   //training
			   if (i<=endTraining) {	
				   
				   if (instance.value(0)==releases.get(i)) {
						//System.out.println("La release dell'istanza "+j+" è 1");	
						//System.out.println("----> The "+j+" instance is: "+ instance.toString());
						numTraining++;
					}
				   
			   }
			   
			   //testing
			   else {
				
				if (instance.value(0)==releases.get(i)) {
					//System.out.println("La release dell'istanza "+j+" è 1");	
					//System.out.println("----> The "+j+" instance is: "+ instance.toString());
					numTesting++;
				}
			   
			   
			   }
		   }
		   
		   Instances training = new Instances(data, 0, numTraining);
		   Instances testing = new Instances(data, numTraining, numTesting);
		   
		   parts.add(new DatasetPart (run, training, testing));
		   
		   
		   
	   }
	   
	   public static int getDatasetSize(String arffPath) throws Exception {
		   
		    DataSource source = new DataSource(arffPath);
			Instances data = source.getDataSet();
		    int numInst = data.numInstances();
		    
		    System.out.println("dataset size : "+ numInst);
			
		    return numInst;
		   
	   }
	   
	   public static List <DatasetPart> walkForward(String arffPath) throws Exception {

		    
		    List <DatasetPart> parts = new ArrayList<>();
		   
		   	//load dataset
			DataSource source = new DataSource(arffPath);
			//get instances object 
			Instances data = source.getDataSet();
			//set class index .. as the last attribute
		    if (data.classIndex() == -1) {
		       data.setClassIndex(data.numAttributes() - 1);
		    }
		    
		    //get number of instances
		    int numInst = data.numInstances();


		    int firstRun = releases.get(0);					//1
		    int lastRun = releases.get(releases.size()-1);	//7
		    
		    int nRun = firstRun;
		    
		   //run 1 -> non la uso
		   //run 2 -> rel 1 training, rel 2 testing
		   //run 3 -> rel 1,2 training, rel 3 testing
		   //run 4 -> rel 1,2,3 training, rel 4 testing
		   //run 5 -> rel 1,2,3,4 training, rel 5 testing
		   //run 6 -> rel 1,2,3,4,5 training, rel 6 testing
		   //run 7 -> rel 1,2,3,4,5,6 training, rel 7 testing
		    
		    while (nRun<=lastRun) {
		    	
		    	
		    	int endTraining = nRun-1;	//l'ultima release su cui faccio training è la nRun-1
			    int testingRelease = nRun;	//la release su cui faccio testing coincide con il nRun
			   
			    int numTraining=0;			//conta quante istanze devo prendere per costruire training set
			    int numTesting=0;			//conta quante istante devo prendere per costruire testing set 
		    	
			    int bugsTraining=0;
			    int bugsTesting=0;
			    
			    List<Integer>  trainingReleases = new ArrayList<>();
			    
		    	//se sto alla run1, passo alla run 2 e inizio a creare training e testing set
			    //System.out.println("\n\n=======================================================");
			    //System.out.println("nRun: "+ nRun+"        endTraining: "+endTraining+"       testingRelease: "+testingRelease);
    
			    if (nRun>firstRun) { 
	    			    		
		    		//per ogni run, scorro le release
		    		
		    		 for (int i=0; i<testingRelease; i++) {	
		    			 
		    			 //System.out.println("i= "+i+"      trainingReleases dim= "+trainingReleases.size());	
    					 trainingReleases.add(releases.get(i));

		    			 for (int j = 0; j < numInst; j++) {
						   
		    				 Instance instance = data.instance(j);
							
		    				 //training
		    				 if (i<endTraining) {
	
							   
							   if (instance.value(0)==releases.get(i)) {								
									//System.out.println("TRAINING ----> The "+j+" instance is: "+ instance.toString());
									numTraining++;
									   if (instance.stringValue(data.numAttributes()-1).equals("Y")) {
										   bugsTraining++;		   
									   }
									
								}
							   
		    				 }
						   
		    				 //testing
		    				 else {				 
								   
		    					 if (instance.value(0)==testingRelease) {
		    						 //System.out.println("TESTING ----> The "+j+" instance is: "+ instance.toString());
		    						 numTesting++;
		    						 if (instance.stringValue(data.numAttributes()-1).contentEquals("Y")) {
										 bugsTesting++;		   
									 }
		    					 }					   
						   
		    				 }
						   
		    			 }
			
					}
		    		 
		    		 	
		    		 trainingReleases.remove(trainingReleases.size()-1);

		    	} //end if (run >1)
		    	
		    	Instances training = new Instances(data, 0, numTraining);
			    Instances testing = new Instances(data, numTraining, numTesting);
			    
			    
			    DatasetPart part = new DatasetPart (nRun, training, testing);
				
			    double percTraining=numTraining/(float)numInst;
			    double bugTrain=bugsTraining/(float)numTraining;
			    double bugTest=bugsTesting/(float)(numTesting);
				
			    part.setTrainingRel(trainingReleases);
			    part.setTestingRel(testingRelease);
			    part.setPercTraining(percTraining);
			    part.setPercBugTraining(bugTrain);
			    part.setPercBugTesting(bugTest);
			    parts.add(part);
		    	
			    nRun++;
		    	

		    } //end while (runs)
		    
		  parts.remove(0);
		  
		  /*

		  
		  System.out.println("\n\nparts dim: "+ parts.size());
		  for (int y=0;y<parts.size();y++) {
			  System.out.println("\n\nrun: "+ parts.get(y).getRun());
			  System.out.println("training size: "+ parts.get(y).getTraining().size());
			  for (int i=0;i<parts.get(y).getTrainingRel().size();i++) {
				  System.out.println("training: "+parts.get(y).getTrainingRel().get(i));
			  }
			  System.out.println("testing size: "+ parts.get(y).getTesting().size());
			  System.out.println("testing: "+ (int) parts.get(y).getTesting().get(0).value(0));
			  
			  System.out.println("%training: "+ parts.get(y).getPercTraining());
			  System.out.println("%defectiveInTraining: "+ parts.get(y).getPercBugTraining());
			  System.out.println("%defectiveInTesting: "+ parts.get(y).getPercBugTesting());
			
		 }
		
		*/
		  
		 
		  return parts;
		   
	   }	
	   
	 
}
