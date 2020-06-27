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
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.RandomForest;
import weka.classifiers.bayes.NaiveBayes;
import weka.filters.supervised.instance.Resample;
import weka.core.converters.ConverterUtils.DataSource;
import weka.classifiers.evaluation.*;
import weka.classifiers.lazy.IBk;



public class Classification{
	
	
	public static void classificate(List <DatasetPart> parts, int num) throws Exception {
		
		
		//for (int i=0; i<parts.size(); i++) {
			
			
			double auc=0;
			double kappa=0;
			
			DatasetPart part = parts.get(1);
			Instances training = part.getTraining();
			Instances testing = part.getTesting();

			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			
			//RandomForest classifier = new RandomForest();
			NaiveBayes classifier = new NaiveBayes();

			classifier.buildClassifier(training);

			Evaluation eval = new Evaluation(testing);	

			eval.evaluateModel(classifier, testing); 
			/*
			double p=eval.precision(numAttr-1);
			double r=eval.recall(numAttr-1);
			
			System.out.println("precision = "+p);
			System.out.println("kappa = "+r);
			*/
			System.out.println("AUC = "+eval.areaUnderROC(1));
			System.out.println("kappa = "+eval.kappa());
			/*

			if (num==1) {
				RandomForest classifier = new RandomForest();
				
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				eval.evaluateModel(classifier, testing);
				System.out.println("classifier = "+classifier.toString());
				//p=eval.precision(numAttr-1);
				//r=eval.recall(numAttr-1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			if (num==2) {
				NaiveBayes classifier = new NaiveBayes();			
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				System.out.println("classifier = "+classifier.toString());
				eval.evaluateModel(classifier, testing);
				
				//p=eval.precision(numAttr-1);
				//r=eval.recall(numAttr-1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			if (num==3) {
				//Ibk classifier = new Ibk();
			}
			

			
			//System.out.println("precision = "+p);
			//System.out.println("recall = "+r);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			
			
		
			
			
			
			
		//}
		  */
		
	}
	
	public static void bho(List <DatasetPart> parts, int num) throws Exception {
		
		double auc=0;
		double kappa=0;
		
		for (int i=0;i<parts.size()-1;i++) {
			DatasetPart part = parts.get(1);
			Instances training = part.getTraining();
			Instances testing = part.getTesting();
	
			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			
			if (num==1) {
				RandomForest classifier = new RandomForest();
				
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				eval.evaluateModel(classifier, testing);
				System.out.println("classifier = "+classifier.toString());
				//p=eval.precision(numAttr-1);
				//r=eval.recall(numAttr-1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			else if (num==2) {
				NaiveBayes classifier = new NaiveBayes();			
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				System.out.println("classifier = "+classifier.toString());
				eval.evaluateModel(classifier, testing);
				
				//p=eval.precision(numAttr-1);
				//r=eval.recall(numAttr-1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			else if (num==3) {
				
				IBk classifier = new IBk();
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				System.out.println("classifier = "+classifier.toString());
				eval.evaluateModel(classifier, testing);
				
				//p=eval.precision(numAttr-1);
				//r=eval.recall(numAttr-1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			
			/* 
			 * Precision = TP/(TP+FP)
			 * Recall = TP/(TP+FN)
			 */
			
			//System.out.println("precision = "+p);
			//System.out.println("recall = "+r);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			
			part.setAuc(auc);
			part.setKappa(kappa);
		
			
			
			
		
	}
	  
		
	}
}
