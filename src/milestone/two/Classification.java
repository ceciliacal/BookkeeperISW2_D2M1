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
		
		double precision=0 ;
		double recall=0 ;
		double auc = 0;
		double kappa = 0;
		
		for (int i=0;i<parts.size();i++) {
			DatasetPart part = parts.get(1);
			Instances training = part.getTraining();
			Instances testing = part.getTesting();
	
			int numAttr = training.numAttributes();
			training.setClassIndex(1);
			testing.setClassIndex(1);
			
			
			if (num==1) {
				RandomForest classifier = new RandomForest();
				
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				eval.evaluateModel(classifier, testing);
				//System.out.println("classifier = "+classifier.toString());
				
				auc = eval.areaUnderROC(1);
				kappa = eval.kappa();
				precision = eval.precision(1);
				recall = eval.recall(1);
				/*
				System.out.println("correct = "+eval.correct());
				System.out.println("Incorrect = "+eval.incorrect());
				System.out.println("FP = "+eval.numFalsePositives(0));
				System.out.println("FP = "+eval.numFalsePositives(1));
				System.out.println("FP = "+eval.numFalsePositives(2));
				*/
				
			}
			else if (num==2) {
				NaiveBayes classifier = new NaiveBayes();			
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				//System.out.println("classifier = "+classifier.toString());
				eval.evaluateModel(classifier, testing);
				
				precision = eval.precision(1);
				recall=eval.recall(1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			else if (num==3) {
				
				IBk classifier = new IBk();
				classifier.buildClassifier(training);
				Evaluation eval = new Evaluation(testing);	
				//System.out.println("classifier = "+classifier.toString());
				eval.evaluateModel(classifier, testing);
				
				precision = eval.precision(1);
				recall = eval.recall(1);
				auc=eval.areaUnderROC(1);
				kappa =eval.kappa();
			}
			
			/* 
			 * Precision = TP/(TP+FP)
			 * Recall = TP/(TP+FN)
			 */
			
			System.out.println("\npart "+i+" :");
			System.out.println("precision = "+precision);
			System.out.println("recall = "+recall);
			System.out.println("AUC = "+ auc);
			System.out.println("kappa = "+kappa);
			
		
			
			
			
		
	}
	  
		
	}
	
	
	public static void bho2(List <DatasetPart> parts, int num) throws Exception {
		double p =0;
		double r =0;
		double auc=0;
		double kappa=0;
		int tp=0;
		int fp=0;
		int tn=0;
		int fn=0;
		
		if (num!=1 && num!=2 & num!=3 ) {
			System.out.println("Numero inserito non valido, inserirne uno tra: 1,2,3");
			return;
		}
		
		for (int i=0;i<parts.size();i++) {
			DatasetPart part = parts.get(i);
			Instances training = part.getTraining();
			Instances testing = part.getTesting();

			
			int numAttr = training.numAttributes();
			training.setClassIndex(numAttr - 1);
			testing.setClassIndex(numAttr - 1);
			System.out.println("\n\n------------------ run "+part.getRun()+" ------------------------");
			//if (num==1) {
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
				
			//}
			//else if (num==2) {
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
				
			//}
			//else if (num==3) {
				
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
				
			//}
			
			/* 
			 * Precision = TP/(TP+FP)
			 * Recall = TP/(TP+FN)
			 */
			
			
			
			
			
			
			
		
		}
	  
		
	}
	
}
