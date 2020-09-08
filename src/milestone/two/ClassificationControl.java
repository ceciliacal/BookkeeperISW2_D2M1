package milestone.two;

import java.util.ArrayList;
import java.util.List;

import project.bookkeeper.Log;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;

public class ClassificationControl {
	
	public static final String RF="Random Forest";
	public static final String NB="Naive Bayes";
	public static final String IB="IBk";
	
	public static final String FILTERED_EVAL="Best First";
	public static final String UNFILTERED_EVAL="No";
	
	public static final String NO_SAMPLING="No";
	public static final String OVER_SAMPLING="Over Sampling";
	public static final String UNDER_SAMPLING="Under Sampling";
	public static final String SMOTE="SMOTE";
	
	protected static List<EvaluationData> dbEntryList;
	protected static int dim;
	
	private ClassificationControl() {	
	}
	
	//metodo per classificazione di ogni parte del dataset (parti definite da walk forward)
	public static List<EvaluationData> startEvaluation(List <DatasetPart> parts, String arffPath) throws Exception {
		
		dbEntryList = new ArrayList<>();
		
		Instances data = null;
		   
	    DataSource source;
		
	    try {
			
			source = new DataSource(arffPath);
			data = source.getDataSet();
			dim= data.numInstances();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		List<String> classifierNames = new ArrayList<>();
		classifierNames.add(RF);		//RANDOM FOREST
		classifierNames.add(NB);		//NAIVE BAYES
		classifierNames.add(IB);		//IBK
		
		for (int i=0;i<parts.size();i++) {
			
			DatasetPart part = parts.get(i);
			
			
			for (int j=0;j<classifierNames.size();j++) {
				
				setEvalOptions(part, classifierNames.get(j), dbEntryList);
				
			}
		}
		return dbEntryList;
	}
	
	public static void setEvalOptions(DatasetPart part, String classifierName, List<EvaluationData> dbEntryList) throws Exception {
		

		List<String> featureSelOptions = new ArrayList<>();
		featureSelOptions.add(UNFILTERED_EVAL);		//NO fs
		featureSelOptions.add(FILTERED_EVAL);		//Best First
		
		List<String> balancingOptions = new ArrayList<>();
		balancingOptions.add(NO_SAMPLING);
		balancingOptions.add(UNDER_SAMPLING);		
		balancingOptions.add(OVER_SAMPLING);		
		balancingOptions.add(SMOTE);	
		
		
		for (int i=0;i<featureSelOptions.size();i++) {
			
			for (int j=0;j<balancingOptions.size();j++) {
								
				evaluation(part, featureSelOptions.get(i), balancingOptions.get(j), classifierName, dbEntryList);
				
			}
			
		}
		
	}
		
		public static void evaluation(DatasetPart part, String featureSelection, String balancingMode, String classifierName, List<EvaluationData> dbEntryList) throws Exception {
			
			Classifier classifier = null;
			Evaluation eval = null;
			
			Instances training = part.getTraining();
			Instances testing = part.getTesting();
			
			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			
			
			// evaluation SENZA feature selection
			if (featureSelection.equals(UNFILTERED_EVAL)) {		
				
				//no balancing
				if (balancingMode.equals(NO_SAMPLING)) {
					
					classifier = chooseClassifier(classifierName);
					
					classifier.buildClassifier(training);
					eval = new Evaluation(testing);	
					eval.evaluateModel(classifier, testing);
					
				}
				//con balancing
				else {
					
					FilteredClassifier fc = evaluationBalancing(part, training, classifierName, balancingMode);
					fc.buildClassifier(training);
					eval = new Evaluation(testing);	
					eval.evaluateModel(fc, testing);
					

					
				}
				
				
			}
			
			// evaluation CON feature selection
			else if (featureSelection.equals(FILTERED_EVAL)) {
				
				eval = featureSelection(part, training, testing, classifierName, balancingMode);		
				
			}
			
			else {	
				
				Log.errorLog("Errore nella scelta della Feature Selection");
				System.exit(-1);
			}
			
			
			//setto in part (DatasetPart) i valori dell' evaluation 
			try {
				setValues(part, eval, classifierName, featureSelection, balancingMode, dbEntryList);
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
					
 		}
		
		
		public static Evaluation featureSelection (DatasetPart part, Instances training, Instances testing, String classifierName, String balancingMode)  {
			
			AttributeSelection filter = new AttributeSelection();
			
			CfsSubsetEval subsetEval = new CfsSubsetEval();		//evaluator
			GreedyStepwise search = new GreedyStepwise();		//search algorithm
			search.setSearchBackwards(true);
			
			Evaluation eval = null;
			
			try {


				filter.setEvaluator(subsetEval);
				filter.setSearch(search);
				filter.setInputFormat(training);
				

				Instances filteredTraining = Filter.useFilter(training, filter);
				filteredTraining.setClassIndex(filteredTraining.numAttributes() - 1);
				
				Instances filteredTesting = Filter.useFilter(testing, filter);	
				filteredTesting.setClassIndex(filteredTesting.numAttributes() - 1);
				
							
				
				if (balancingMode.isEmpty()) {			
					Log.errorLog("Errore nella Feature Selection");
					System.exit(-1);		
				}
				
				
				
				//senza balancing
				else if (balancingMode.equals(NO_SAMPLING)){
					
					Classifier classifier = chooseClassifier(classifierName);
								
					classifier.buildClassifier(filteredTraining);
					eval = new Evaluation(testing);
					eval.evaluateModel(classifier, filteredTesting);
					
				}
				
				//con balancing
				else {
					
					FilteredClassifier fc = evaluationBalancing(part,filteredTraining, classifierName, balancingMode);

					fc.buildClassifier(filteredTraining);
					eval = new Evaluation(testing);
					eval.evaluateModel(fc, filteredTesting);
					
					
					
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}

			return eval;
		
			
			
			


			
		}
		
		
		public static FilteredClassifier evaluationBalancing(DatasetPart part, Instances training, String classifierName, String balancingMode)  {
			

			FilteredClassifier fc = new FilteredClassifier();
			
			if (classifierName.equals(RF)) {
				
				RandomForest randomForest = new RandomForest();
				fc.setClassifier(randomForest);
				
			}
			
			else if (classifierName.equals(NB)) {
				
				NaiveBayes naiveBayes = new NaiveBayes();
				fc.setClassifier(naiveBayes);
				
			}
			
			else if (classifierName.equals(IB)) {
				
				IBk ibk = new IBk();
				fc.setClassifier(ibk);

			}
			
			else {	
				
				Log.errorLog("Errore nel nome del classificatore");
				System.exit(-1);
			}

			try {
				
				//---- over sampling ----
				if (balancingMode.equals(OVER_SAMPLING)){
					
					Resample resample = new Resample();
					
					
						resample.setInputFormat(training);
						
					
					
					resample.setNoReplacement(false);
					resample.setBiasToUniformClass(0.1);
					
					double sizePerc = 2 * ( getSampleSizePerc(part,dim) );

					resample.setSampleSizePercent(sizePerc); //y/2 = %data appartenente a majority class
					//majority class : numero d'istanze 
					
					String[] overOpts = new String[]{ "-B", "1.0", "-Z", "130.3"};
					
						
						resample.setOptions(overOpts);
						
					
					fc.setFilter(resample);
				}
				
				//---- under sampling ----
				else if (balancingMode.equals(UNDER_SAMPLING)){
								
					SpreadSubsample  spreadSubsample = new SpreadSubsample();
					String[] opts = new String[]{ "-M", "1.0"};
					
						
						spreadSubsample.setOptions(opts);
					
					
					fc.setFilter(spreadSubsample);
					
				}
				
				//---- SMOTE ----
				else if (balancingMode.equals(SMOTE)){
					
					weka.filters.supervised.instance.SMOTE smote = new weka.filters.supervised.instance.SMOTE();
					
						
						smote.setInputFormat(training);
						
					
					fc.setFilter(smote);

				}
				//param input è errato
				else {
					
					Log.errorLog("Errore nella tecnica di balancing");
					System.exit(-1);
					
				}
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			
			
			
			return fc;

		
			
		}
		
		public static double getSampleSizePerc(DatasetPart part, int dim) {
			
			double res;
			
			double numBuggyClasses = part.getPercBugTraining();
			int numBuggyClassesTemp = (int) (part.getPercBugTraining())*100;
			
			if (numBuggyClassesTemp > dim - numBuggyClassesTemp) {
				
				res=numBuggyClasses;	//majority
			}
			
			else {
				res = ((dim - numBuggyClasses)*100)/ (double) dim;
					
			}
		

			return res;	
		}
		
		
		
		public static Classifier chooseClassifier(String classifierName) {
			
			Classifier classifier = null;
			
			if (classifierName.equals(RF)) {
				
				classifier = new RandomForest();
				
			}
			
			else if (classifierName.equals(NB)) {
				
				classifier = new NaiveBayes();
				
			}
			
			else if (classifierName.equals(IB)) {
				
				classifier = new IBk();	

			}
			
			else {	
				
				Log.errorLog("Errore nel nome del classificatore");
				System.exit(-1);
			}
			
			return classifier;

		}
		
		public static void setValues( DatasetPart part, Evaluation eval, String classifierName, String featureSelection, String balancingMode, List<EvaluationData> dbEntryList) {
			
			double p =0;
			double r =0;
			double auc=0;
			double kappa=0;
			int tp=0;
			int fp=0;
			int tn=0;
			int fn=0;
			
			p=eval.precision(1);
			r=eval.recall(1);
			auc=eval.areaUnderROC(1);
			kappa =eval.kappa();
			
			tp=(int)eval.numTruePositives(1);
			fp=(int)eval.numFalsePositives(1);
			tn=(int)eval.numTrueNegatives(1);
			fn=(int)eval.numFalseNegatives(1);
			
			EvaluationData dbEntry = new EvaluationData();
			
			dbEntry.setTrainingRel(part.getTrainingRel());
			dbEntry.setTestingRel(part.getTestingRel());
			
			dbEntry.setPercTraining(part.getPercTraining());
			dbEntry.setPercBugTraining(part.getPercBugTraining());
			dbEntry.setPercBugTesting(part.getPercBugTesting());
			
			dbEntry.setClassifier(classifierName);

				
			if (featureSelection.equals(FILTERED_EVAL)) {
				
				dbEntry.setFeatureSelection(FILTERED_EVAL);
				//da settare quelli con feat. selection
			}
			else {
				dbEntry.setFeatureSelection(UNFILTERED_EVAL);
				
			}
			
			if (balancingMode.equals(NO_SAMPLING)) {
				dbEntry.setBalancing(NO_SAMPLING);
			}
			if (balancingMode.equals(OVER_SAMPLING)) {
				dbEntry.setBalancing(OVER_SAMPLING);
			}
			if (balancingMode.equals(UNDER_SAMPLING)) {
				dbEntry.setBalancing(UNDER_SAMPLING);
			}
			if (balancingMode.equals(SMOTE)) {
				dbEntry.setBalancing(SMOTE);
			}
			
			
			dbEntry.setPrecision(p);
			dbEntry.setPrecision(p);
			dbEntry.setRecall(r);
			dbEntry.setAuc(auc);
			dbEntry.setKappa(kappa);
			
			dbEntry.setTp(tp);
			dbEntry.setFp(fp);
			dbEntry.setTn(tn);
			dbEntry.setFn(fn);
			
			
		
			dbEntryList.add(dbEntry);


		}
		
		
		
		
		
		
		
	}
