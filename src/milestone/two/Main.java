package milestone.two;

import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.core.converters.Saver;
import weka.experiment.Stats;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.SubsetByExpression;

import project.bookkeeper.MainControl;
import project.bookkeeper.Release;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
	
	public static List<Integer> releases;
	

	   public static void main(String[] args) throws Exception {
		   
		   int numReleases;
		   String csvPath;
		   String arffPath;
		   
		   List<DatasetPart> parts;
		  
		   

		   //prendi training e testing da walkforward
		   //passali a test weka easy per darli al classificatore
		   
		   releases=MainControl.getReleases();
		   numReleases=releases.size();
		   System.out.println("numReleases: "+numReleases);

		   csvPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetVirgole.csv";
		   arffPath= "D:\\Cecilia\\Desktop\\bookkeeperISW2_D2M1\\bookkeeperISW2_D2M1.git\\trunk\\datasetCORRETTO.arff";

		   //csv2arff(csvPath,arffPath);
		   parts = walkForward(arffPath);
		   
		   //Classification.bho(parts, 3);
	   }


	   public static void csv2arff(String csvPath, String arffPath) throws IOException {
		  
		   // load CSV
		    CSVLoader loader = new CSVLoader();
		    loader.setSource(new File(csvPath));
		    Instances data = loader.getDataSet();//get instances object

		    // save ARFF
		    ArffSaver saver = new ArffSaver();
		    saver.setInstances(data);//set the dataset we want to convert
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
		    	
		    	//se sto alla run1, passo alla run 2 e inizio a creare training e testing set
			    System.out.println("\n\n=======================================================");
			    System.out.println("nRun: "+ nRun+"        endTraining: "+endTraining+"       testingRelease: "+testingRelease);
		    	if (nRun>firstRun) { 
		    		
		    		
		    		//per ogni run, scorro le release
		    		
		    		 for (int i=0; i<testingRelease; i++) {	
		    		
		    			 for (int j = 0; j < numInst; j++) {
						   
		    				 Instance instance = data.instance(j);
							
		    				 //getTraining e getTEsting -> aggiungo elemento DatasetPart a lista, che contiene nRun, training e testing set
		    				 //getInstances(instance, parts, data, nRun);
		   
						   
		    				 //training
		    				 if (i<endTraining) {	
							   
							   if (instance.value(0)==releases.get(i)) {
									//System.out.println("La release dell'istanza "+j+" è 1");	
									System.out.println("TRAINING ----> The "+j+" instance is: "+ instance.toString());
									numTraining++;
								}
							   
		    				 }
						   
		    				 //testing
		    				 else {
		    					 if (instance.value(0)==testingRelease) {
		    						 //System.out.println("La release dell'istanza "+j+" è 1");	
		    						 System.out.println("TESTING ----> The "+j+" instance is: "+ instance.toString());
		    						 numTesting++;
		    					 }
						   
						   
		    				 }
						   
		    			 }
			
					}

		    	}
		    	
		    	Instances training = new Instances(data, 0, numTraining);
			    Instances testing = new Instances(data, numTraining, numTesting);
			   
			    parts.add(new DatasetPart (nRun, training, testing));
		    	nRun++;

		    }
		    
		  parts.remove(0);
		  System.out.println("\n\nparts dim: "+ parts.size());
		  for (int y=0;y<parts.size();y++) {
			  System.out.println("run: "+ parts.get(y).getRun());
			  System.out.println("training size: "+ parts.get(y).getTraining().size());
			  
		  }
		  return parts;
		   
	   }	  

}
