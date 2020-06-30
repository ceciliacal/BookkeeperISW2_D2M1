package milestone.two;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;

public class DatasetPart {

	private int run;
	private Instances training;
	private Instances testing;
	
	private double precisionRF;
	private double recallRF;
	private double aucRF;
	private double kappaRF;
	
	private double precisionNB;
	private double recallNB;
	private double aucNB;
	private double kappaNB;
	
	private double precisionIB;
	private double recallIB;
	private double aucIB;
	private double kappaIB;
	
	private List<Integer> trainingRel;
	private int testingRel;
	
	
	
	public DatasetPart(int run, Instances train, Instances test) {
		this.run=run;
		this.training=train;
		this.testing=test;
		this.initTrainingRel();
	}

	public void initTrainingRel() {
		
		this.trainingRel= new ArrayList<>();
	}

	public int getRun() {
		return run;
	}



	public void setRun(int run) {
		this.run = run;
	}



	public Instances getTraining() {
		return training;
	}



	public void setTraining(Instances training) {
		this.training = training;
	}



	public Instances getTesting() {
		return testing;
	}



	public void setTesting(Instances testing) {
		this.testing = testing;
	}


	public double getKappaRF() {
		return kappaRF;
	}


	public void setKappaRF(double kappa) {
		this.kappaRF = kappa;
	}


	public double getAucRF() {
		return aucRF;
	}


	public void setAucRF(double auc) {
		this.aucRF = auc;
	}



	public double getPrecisionRF() {
		return precisionRF;
	}


	public void setPrecisionRF(double precision) {
		this.precisionRF = precision;
	}


	public double getRecallRF() {
		return recallRF;
	}


	public void setRecallRF(double recall) {
		this.recallRF = recall;
	}


	public double getPrecisionNB() {
		return precisionNB;
	}


	public void setPrecisionNB(double precisionNB) {
		this.precisionNB = precisionNB;
	}


	public double getRecallNB() {
		return recallNB;
	}


	public void setRecallNB(double recallNB) {
		this.recallNB = recallNB;
	}


	public double getKappaNB() {
		return kappaNB;
	}


	public void setKappaNB(double kappaNB) {
		this.kappaNB = kappaNB;
	}


	public double getAucNB() {
		return aucNB;
	}


	public void setAucNB(double aucNB) {
		this.aucNB = aucNB;
	}


	public double getPrecisionIB() {
		return precisionIB;
	}


	public void setPrecisionIB(double precisionIB) {
		this.precisionIB = precisionIB;
	}


	public double getRecallIB() {
		return recallIB;
	}


	public void setRecallIB(double recallIB) {
		this.recallIB = recallIB;
	}


	public double getAucIB() {
		return aucIB;
	}


	public void setAucIB(double aucIB) {
		this.aucIB = aucIB;
	}


	public double getKappaIB() {
		return kappaIB;
	}


	public void setKappaIB(double kappaIB) {
		this.kappaIB = kappaIB;
	}


	public int getTestingRel() {
		return testingRel;
	}


	public void setTestingRel(int testingRel) {
		this.testingRel = testingRel;
	}


	public List<Integer> getTrainingRel() {
		return trainingRel;
	}


	public void setTrainingRel(List<Integer> trainingRel) {
		this.trainingRel = trainingRel;
	}

}
