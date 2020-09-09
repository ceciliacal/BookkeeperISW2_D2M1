package milestone.two;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;

public class DatasetPart {
	
	private Instances training;
	private Instances testing;
	
	private List<Integer> trainingRel;
	private int testingRel;
	
	private double percTraining;	//data on training / total data
	private double percBugTraining;
	private double percBugTesting;
	
	private String classifier;
	private String featureSelection;
	private String balancing;
	
	private int tp;
	private int fp;
	private int tn;
	private int fn;
	
	private double precision;
	private double recall;
	private double auc;
	private double kappa;


	public DatasetPart( Instances train, Instances test) {
		this.training=train;
		this.testing=test;
		this.initTrainingRel();
	}



	public DatasetPart() {}



	public void initTrainingRel() {
		
		this.trainingRel= new ArrayList<>();
	}


	public List<Integer> getTrainingRel() {
		return trainingRel;
	}

	public void setTrainingRel(List<Integer> numTrainingRelease) {
		this.trainingRel = numTrainingRelease;
	}

	public int getTestingRel() {
		return testingRel;
	}

	public void setTestingRel(int testingRel) {
		this.testingRel = testingRel;
	}

	public double getPercTraining() {
		return percTraining;
	}

	public void setPercTraining(double percTraining) {
		this.percTraining = percTraining;
	}

	public double getPercBugTraining() {
		return percBugTraining;
	}

	public void setPercBugTraining(double percBugTraining) {
		this.percBugTraining = percBugTraining;
	}

	public double getPercBugTesting() {
		return percBugTesting;
	}

	public void setPercBugTesting(double percBugTesting) {
		this.percBugTesting = percBugTesting;
	}

	public String getFeatureSelection() {
		return featureSelection;
	}

	public void setFeatureSelection(String featureSelection) {
		this.featureSelection = featureSelection;
	}

	public String getBalancing() {
		return balancing;
	}

	public void setBalancing(String balancing) {
		this.balancing = balancing;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getFp() {
		return fp;
	}

	public void setFp(int fp) {
		this.fp = fp;
	}

	public int getTn() {
		return tn;
	}

	public void setTn(int tn) {
		this.tn = tn;
	}

	public int getFn() {
		return fn;
	}

	public void setFn(int fn) {
		this.fn = fn;
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

	public double getAuc() {
		return auc;
	}

	public void setAuc(double auc) {
		this.auc = auc;
	}

	public double getKappa() {
		return kappa;
	}

	public void setKappa(double kappa) {
		this.kappa = kappa;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
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

}
