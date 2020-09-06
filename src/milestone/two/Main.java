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
		   
		   csvPath= "D:\\Cecilia\\Desktop\\zookeeper\\csv\\ZOOKEEPER_datasetVirgole VERSIONE FINALE.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\zookeeper\\csv\\ZOOKEEPER_datasetWeka.arff";
		   
		   //csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\datasetVirgole VERSIONE FINALE.csv";
		   //arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\datasetWeka.arff";

		   csv2arff(csvPath,arffPath);
		   walkForward3(arffPath);
		   /*
		   parts = walkForward(arffPath);
		   int dim=getDatasetSize(arffPath) ;
		   
		   List<EvaluationData> dbEntryList = Classification.startEvaluation(parts,dim);
		   Writer.write(dbEntryList);
		   */
		   
	   }
	   
	   public static void run() throws Exception {
		   
		   String csvPath;
		   String arffPath;
		   
		   List<DatasetPart> parts;
		  
		   releases=MainControl.getReleases();
		   
		   csvPath= "D:\\Cecilia\\Desktop\\zookeeper\\csv\\ZOOKEEPER_datasetVirgole VERSIONE FINALE.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\zookeeper\\csv\\ZOOKEEPER_datasetWeka.arff";
		   
		   //csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\csv\\datasetVirgole VERSIONE FINALE.csv";
		   //arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1\\csv\\datasetWeka.arff";
				   
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
						
						numTraining++;
					}
				   
			   }
			   
			   //testing
			   else {
				
				if (instance.value(0)==releases.get(i)) {
					
					numTesting++;
				}
			   
			   
			   }
		   }
		   
		   Instances training = new Instances(data, 0, numTraining);
		   Instances testing = new Instances(data, numTraining, numTesting);
		   
		   parts.add(new DatasetPart (run, training, testing));
		   
		   
		   
	   }
	   
	   public static int getDatasetSize(String arffPath) {
		   
		   Instances data = null;
		   
		    DataSource source;
			try {
				source = new DataSource(arffPath);
				data = source.getDataSet();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return data.numInstances();
		    
			
		    
		   
	   }
	   
	
	 
	   
	   public static List <DatasetPart> walkForward(String arffPath) throws Exception {

		    
		    List <DatasetPart> parts = new ArrayList<>();
			DataSource source = new DataSource(arffPath);
			Instances data = source.getDataSet();
		    
			if (data.classIndex() == -1) {
		       data.setClassIndex(data.numAttributes() - 1);
		    }
		    
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
		    	
		    	calculateTraining( nRun, firstRun, numInst,parts,  data ) ;
		    	nRun++;
			    

		    } //end while (runs)
		    
		  parts.remove(0);		 
		  return parts;
		   
	   }
	   
	   public static void calculateTraining(int nRun, int firstRun, int numInst, List <DatasetPart> parts, Instances data ) {
	   

	    	int endTraining = nRun-1;	//l'ultima release su cui faccio training è la nRun-1
		    int testingRelease = nRun;	//la release su cui faccio testing coincide con il nRun
		   
		    int numTraining=0;			//conta quante istanze devo prendere per costruire training set
		    int numTesting=0;			//conta quante istante devo prendere per costruire testing set 
	    	
		    int bugsTraining=0;
		    int bugsTesting=0;
		    
		    List<Integer>  trainingReleases = new ArrayList<>();
		    
	    	//se sto alla run1, passo alla run 2 e inizio a creare training e testing set
		    
		    if (nRun>firstRun) { 
   			    		
	    		//per ogni run, scorro le release
	    		
	    		 for (int i=0; i<testingRelease; i++) {	
	    			 
					 trainingReleases.add(releases.get(i));

	    			 for (int j = 0; j < numInst; j++) {
					   
	    				 Instance instance = data.instance(j);
							    				 
	    				 //training
	    				 if (i<endTraining) {
	    					 
		   
						   if (instance.value(0)==releases.get(i)) {								
								numTraining++;
								   if (instance.stringValue(data.numAttributes()-1).equals("Y")) {
									   bugsTraining++;		   
								   }
								
							}
							
						   
	    				 }
					   
	    				 //testing
	    				 else {				 
							   
	    					 	    					 
	    					 if (instance.value(0)==testingRelease) {
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
		    double bugTrain=0;
		    double bugTest=0;
		    
		    if (numTraining!=0 ) {
		    	 bugTrain=bugsTraining/(float)numTraining;
		    	 bugTest=bugsTesting/(float)(numTesting);
		    }
		    
		    if (numTraining!=0 ) {
		    	 bugTrain=bugsTraining/(float)numTraining;
		    	 bugTest=bugsTesting/(float)(numTesting);
		    }
			
		    part.setTrainingRel(trainingReleases);
		    part.setTestingRel(testingRelease);
		    part.setPercTraining(percTraining);
		    part.setPercBugTraining(bugTrain);
		    part.setPercBugTesting(bugTest);
		    parts.add(part);
	    	
		    
		    
			
	   
	}
	   

	   public static void walkForward3(String arffPath) {
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
		    
		    int dataSize = data.size();
		    
		    Instances training=null;
		    Instances test=null;
		    //List<Integer>  trainingReleases = new ArrayList<>();
		    
		    int numTraining, numTesting;
		    Instances training2=null;
		    Instances testing2=null;
		    
		    DatasetPart part = null;
		    
		    
		    for(int j=2; j <=releases.size(); j++) {
		    	
		    	 numTraining=0;
			     numTesting=0;
		    	
		    	
				   
				    training = new Instances(data,0);
				    test = new Instances(data,0);
				    
				    
				   for(int i=0; i<data.size();i++) {
					   
					   int release = Integer.parseInt(data.get(i).toString(0));
					   
					   if(release<j) {
						   
						   numTraining++;
						   
						   training.add(data.get(i));
						 
						   
					   }else if(release == j) {
						   
						   numTesting++;
						   test.add(data.get(i));
					   }
				   }
				   
				   
				   
				   
				    training2 = new Instances(data, 0, numTraining);
				    testing2 = new Instances(data, numTraining, numTesting);
				    part = new DatasetPart (training2, testing2);
				    parts.add(part);
		    }
		    
		    System.out.println("\n\n=== training2: "+part.getTraining());
		    System.out.println("\n\n=== testing2: "+part.getTesting());
		    System.out.println("\n\n=== parts size: "+parts.size());
		    
		    
		  
		
	   
		    getNumTrainingRelease(parts );
		    calculatenumDefectsInTraining(parts,  data );
		    calculatenumDefectsInTesting(parts,  data );
		    
		    for (int i=0; i<parts.size();i++) {
				   System.out.println("\n\n=== part"+i+"  tr=  "+parts.get(i).getTrainingRel()+"   test= "+parts.get(i).getTestingRel());
				   System.out.println("\n\n=== part"+i+"    percTraining= "+parts.get(i).getPercTraining()+"  bugsTraining=  "+parts.get(i).getPercBugTraining()+"   test= "+parts.get(i).getPercBugTesting());
			   }
		    
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
						   
						   //training.add(data.get(i));
						 
						   
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
