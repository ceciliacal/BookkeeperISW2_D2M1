package milestone.two;

import weka.core.Instances;

public class DatasetPart {

	private int run;
	private Instances training;
	private Instances testing;
	
	private double precision;
	private double recall;
	private double auc;
	private double kappa;
	private String classifier;
	
	
	
	public DatasetPart(int run, Instances train, Instances test) {
		this.run=run;
		this.training=train;
		this.testing=test;
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


	public double getKappa() {
		return kappa;
	}


	public void setKappa(double kappa) {
		this.kappa = kappa;
	}


	public double getAuc() {
		return auc;
	}


	public void setAuc(double auc) {
		this.auc = auc;
	}


	public String getClassifier() {
		return classifier;
	}


	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}


	public double getPrecision() {
		return precision;
	}


	public void setPrecision(double precision) {
		this.precision = precision;
	}


	public double getRecall() {
		return recall;
	}


	public void setRecall(double recall) {
		this.recall = recall;
	}

}
