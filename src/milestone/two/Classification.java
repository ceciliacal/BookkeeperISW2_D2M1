package milestone.two;
/*
 *  How to use WEKA API in Java 
 *  Copyright (C) 2014 
 *  @author Dr Noureddin M. Sadawi (noureddin.sadawi@gmail.com)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it as you wish ... 
 *  I ask you only, as a professional courtesy, to cite my name, web page 
 *  and my YouTube Channel!
 *  
 */

//import required classes

import weka.core.Instances;

import java.util.List;

import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.GreedyStepwise;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;



public class Classification{
	
	public static void classificate() {
		
	}

	
	public static void evalutationNoFiltered(List <DatasetPart> parts) throws Exception {
		double p =0;
		double r =0;
		double auc=0;
		double kappa=0;
		int tp=0;
		int fp=0;
		int tn=0;
		int fn=0;

		
		for (int i=0;i<parts.size();i++) {
			DatasetPart part = parts.get(i);
			Instances training = part.getTraining();
			Instances testing = part.getTesting();

			
			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			System.out.println("\n\n------------------ run "+part.getRun()+" ------------------------");

			// -------------------- RANDOM FOREST --------------------
			
			RandomForest classifier1 = new RandomForest();
			
			classifier1.buildClassifier(training);
			Evaluation eval1 = new Evaluation(testing);	
			System.out.println("RANDOM FOREST:");
			eval1.evaluateModel(classifier1, testing);
			
			
			p=eval1.precision(1);
			r=eval1.recall(1);
			auc=eval1.areaUnderROC(1);
			kappa =eval1.kappa();
			
			tp=(int)eval1.numTruePositives(1);
			fp=(int)eval1.numFalsePositives(1);
			tn=(int)eval1.numTrueNegatives(1);
			fn=(int)eval1.numFalseNegatives(1);
			
			System.out.println("precision = "+p);
			System.out.println("recall = "+r);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			System.out.println("TP = "+tp);
			System.out.println("FP = "+fp);
			System.out.println("TN = "+tn);
			System.out.println("FN = "+fn);
			
			part.setPrecisionRF(p);
			part.setRecallRF(r);
			part.setAucRF(auc);
			part.setKappaRF(kappa);
			
			part.setTpRF(tp);
			part.setFpRF(fp);
			part.setTnRF(tn);
			part.setFnRF(fn);
			
			// -------------------- NAIVE BAYES --------------------

			NaiveBayes classifier2 = new NaiveBayes();			
			classifier2.buildClassifier(training);
			Evaluation eval2 = new Evaluation(testing);	
			System.out.println("\nNAIVE BAYES:");
			eval2.evaluateModel(classifier2, testing);
			
			p=eval2.precision(1);
			r=eval2.recall(1);
			auc=eval2.areaUnderROC(1);
			kappa =eval2.kappa();
			
			tp=(int)eval2.numTruePositives(1);
			fp=(int)eval2.numFalsePositives(1);
			tn=(int)eval2.numTrueNegatives(1);
			fn=(int)eval2.numFalseNegatives(1);
			
			System.out.println("precision = "+p);
			System.out.println("recall = "+r);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			System.out.println("TP = "+tp);
			System.out.println("FP = "+fp);
			System.out.println("TN = "+tn);
			System.out.println("FN = "+fn);
			
			
			part.setPrecisionNB(p);
			part.setRecallNB(r);
			part.setAucNB(auc);
			part.setKappaNB(kappa);
			
			part.setTpNB(tp);
			part.setFpNB(fp);
			part.setTnNB(tn);
			part.setFnNB(fn);

			// -------------------- IBK --------------------
			
			IBk classifier3 = new IBk();
			classifier3.buildClassifier(training);
			Evaluation eval3 = new Evaluation(testing);	
			System.out.println("\nIBK");
			eval3.evaluateModel(classifier3, testing);
			
			p=eval3.precision(1);
			r=eval3.recall(1);
			auc=eval3.areaUnderROC(1);
			kappa =eval3.kappa();
			
			tp=(int)eval3.numTruePositives(1);
			fp=(int)eval3.numFalsePositives(1);
			tn=(int)eval3.numTrueNegatives(1);
			fn=(int)eval3.numFalseNegatives(1);
			
			System.out.println("precision = "+p);
			System.out.println("recall = "+r);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			System.out.println("TP = "+tp);
			System.out.println("FP = "+fp);
			System.out.println("TN = "+tn);
			System.out.println("FN = "+fn);
			
			
			part.setPrecisionIB(p);
			part.setRecallIB(r);
			part.setAucIB(auc);
			part.setKappaIB(kappa);
			
			part.setTpIB(tp);
			part.setFpIB(fp);
			part.setTnIB(tn);
			part.setFnIB(fn);
			

		
		}
	  
		
	}
	
		public static void evalutationFiltered(List <DatasetPart> parts) throws Exception {

			double p =0;
			double r =0;
			double auc=0;
			double kappa=0;
			int tp=0;
			int fp=0;
			int tn=0;
			int fn=0;
			
			for (int i=0;i<parts.size();i++) {
				
				DatasetPart part = parts.get(i);
				Instances training = part.getTraining();
				Instances testing = part.getTesting();
				
				//create AttributeSelection object
				AttributeSelection filter = new AttributeSelection();
				
				//create evaluator and search algorithm objects
				CfsSubsetEval eval = new CfsSubsetEval();
				GreedyStepwise search = new GreedyStepwise();
				
				//set the algorithm to search backward
				search.setSearchBackwards(true);
				
				//set the filter to use the evaluator and search algorithm
				filter.setEvaluator(eval);
				filter.setSearch(search);
				
				//specify the dataset
				filter.setInputFormat(training);
				
				//apply
				Instances filteredTraining = Filter.useFilter(training, filter);
				
				int numAttrNoFilter = training.numAttributes();
				training.setClassIndex(numAttrNoFilter - 1);
				testing.setClassIndex(numAttrNoFilter - 1);
				
				int numAttrFiltered = filteredTraining.numAttributes();
		
				
				System.out.println("No filter attr: "+ numAttrNoFilter);
				System.out.println("Filtered attr: "+ numAttrFiltered);
			
				// ------------------ RANDOM FOREST --------------------
				
				System.out.println("\n -------------------- FILTERED RANDOM FOREST:");
				RandomForest classifier = new RandomForest();
		
				Evaluation eval1 = new Evaluation(testing);
				
				//evaluation with filtered
				filteredTraining.setClassIndex(numAttrFiltered - 1);
				Instances testingFiltered1 = Filter.useFilter(testing, filter);
				testingFiltered1.setClassIndex(numAttrFiltered - 1);
				classifier.buildClassifier(filteredTraining);
				eval1.evaluateModel(classifier, testingFiltered1);
				
				p=eval1.precision(1);
				r=eval1.recall(1);
				auc=eval1.areaUnderROC(1);
				kappa =eval1.kappa();
				
				tp=(int)eval1.numTruePositives(1);
				fp=(int)eval1.numFalsePositives(1);
				tn=(int)eval1.numTrueNegatives(1);
				fn=(int)eval1.numFalseNegatives(1);
				
				System.out.println("precision = "+p);
				System.out.println("recall = "+r);
				System.out.println("AUC = "+ auc);
				System.out.println("kappa = "+kappa);
				System.out.println("TP = "+tp);
				System.out.println("FP = "+fp);
				System.out.println("TN = "+tn);
				System.out.println("FN = "+fn);
				
				//set part
				
				part.setPrecisionRFfiltered(p);
				part.setRecallRFfiltered(r);
				part.setAucRFfiltered(auc);
				part.setKappaRFfiltered(kappa);
				
				part.setTpRFfiltered(tp);
				part.setFpRFfiltered(fp);
				part.setTnRFfiltered(tn);
				part.setFnRFfiltered(fn);
				
				
				// -------------------- NAIVE BAYES --------------------

				System.out.println("\n -------------------- FILTERED NAIVE BAYES:");
				NaiveBayes classifier2 = new NaiveBayes();	
				Evaluation eval2 = new Evaluation(testing);
				
				//evaluation with filtered
				filteredTraining.setClassIndex(numAttrFiltered - 1);
				Instances testingFiltered2 = Filter.useFilter(testing, filter);
				testingFiltered2.setClassIndex(numAttrFiltered - 1);
				classifier2.buildClassifier(filteredTraining);
				eval2.evaluateModel(classifier2, testingFiltered2);
				
				p=eval2.precision(1);
				r=eval2.recall(1);
				auc=eval2.areaUnderROC(1);
				kappa =eval2.kappa();
				
				tp=(int)eval2.numTruePositives(1);
				fp=(int)eval2.numFalsePositives(1);
				tn=(int)eval2.numTrueNegatives(1);
				fn=(int)eval2.numFalseNegatives(1);
				
				System.out.println("precision = "+p);
				System.out.println("recall = "+r);
				System.out.println("AUC = "+ auc);
				System.out.println("kappa = "+kappa);
				System.out.println("TP = "+tp);
				System.out.println("FP = "+fp);
				System.out.println("TN = "+tn);
				System.out.println("FN = "+fn);
				
				//set part
				
				part.setPrecisionNBfiltered(p);
				part.setRecallNBfiltered(r);
				part.setAucNBfiltered(auc);
				part.setKappaNBfiltered(kappa);
				
				part.setTpNBfiltered(tp);
				part.setFpNBfiltered(fp);
				part.setTnNBfiltered(tn);
				part.setFnNBfiltered(fn);
		
				
				// -------------------- IBK --------------------
				
				System.out.println("\n -------------------- FILTERED IBK");
				IBk classifier3 = new IBk();
		
				Evaluation eval3 = new Evaluation(testing);
				
				//evaluation with filtered
				filteredTraining.setClassIndex(numAttrFiltered - 1);
				Instances testingFiltered3 = Filter.useFilter(testing, filter);
				testingFiltered3.setClassIndex(numAttrFiltered - 1);
				classifier3.buildClassifier(filteredTraining);
				eval3.evaluateModel(classifier3, testingFiltered3);
				
				p=eval3.precision(1);
				r=eval3.recall(1);
				auc=eval3.areaUnderROC(1);
				kappa =eval3.kappa();
				
				tp=(int)eval3.numTruePositives(1);
				fp=(int)eval3.numFalsePositives(1);
				tn=(int)eval3.numTrueNegatives(1);
				fn=(int)eval3.numFalseNegatives(1);
				
				System.out.println("precision = "+p);
				System.out.println("recall = "+r);
				System.out.println("AUC = "+ auc);
				System.out.println("kappa = "+kappa);
				System.out.println("TP = "+tp);
				System.out.println("FP = "+fp);
				System.out.println("TN = "+tn);
				System.out.println("FN = "+fn);
				
				//set part
				
				part.setPrecisionIBfiltered(p);
				part.setRecallIBfiltered(r);
				part.setAucIBfiltered(auc);
				part.setKappaIBfiltered(kappa);
				
				part.setTpIBfiltered(tp);
				part.setFpIBfiltered(fp);
				part.setTnIBfiltered(tn);
				part.setFnIBfiltered(fn);
		
		
				
			}
			
	
	
	}
			

	
}
