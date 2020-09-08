package milestone.two;

import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;

public class DatasetPart {

	private int run;
	private Instances training;
	private Instances testing;
	
	private List<Integer> numTrainingRelease;
	private int testingRel;
	
	private double percTraining;	//data on training / total data
	private double percBugTraining;
	private double percBugTesting;

	
	
	public DatasetPart( Instances train, Instances test) {
		this.training=train;
		this.testing=test;
		this.initTrainingRel();
	}

	public void initTrainingRel() {
		
		this.numTrainingRelease= new ArrayList<>();
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


	public int getTestingRel() {
		return testingRel;
	}


	public void setTestingRel(int testingRel) {
		this.testingRel = testingRel;
	}


	public List<Integer> getTrainingRel() {
		return numTrainingRelease;
	}


	public void setTrainingRel(List<Integer> trainingRel) {
		this.numTrainingRelease = trainingRel;
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

	

}
