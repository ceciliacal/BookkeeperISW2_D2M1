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
	
	//altre cose da mettere ...
	
	private int tpRF;
	private int fpRF;
	private int tnRF;
	private int fnRF;
	
	private int tpNB;
	private int fpNB;
	private int tnNB;
	private int fnNB;
	
	private int tpIB;
	private int fpIB;
	private int tnIB;
	private int fnIB;
	
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
	

	
	
	
	
	
	public DatasetPart(int run, Instances train, Instances test) {
		this.run=run;
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

	public int getTpRF() {
		return tpRF;
	}

	public void setTpRF(int tpRF) {
		this.tpRF = tpRF;
	}

	public int getFpRF() {
		return fpRF;
	}

	public void setFpRF(int fpRF) {
		this.fpRF = fpRF;
	}

	public int getFnRF() {
		return fnRF;
	}

	public void setFnRF(int fnRF) {
		this.fnRF = fnRF;
	}

	public int getTnRF() {
		return tnRF;
	}

	public void setTnRF(int tnRF) {
		this.tnRF = tnRF;
	}

	public int getFpNB() {
		return fpNB;
	}

	public void setFpNB(int fpNB) {
		this.fpNB = fpNB;
	}

	public int getTnNB() {
		return tnNB;
	}

	public void setTnNB(int tnNB) {
		this.tnNB = tnNB;
	}

	public int getTpNB() {
		return tpNB;
	}

	public void setTpNB(int tpNB) {
		this.tpNB = tpNB;
	}

	public int getFnNB() {
		return fnNB;
	}

	public void setFnNB(int fnNB) {
		this.fnNB = fnNB;
	}

	public int getTpIB() {
		return tpIB;
	}

	public void setTpIB(int tpIB) {
		this.tpIB = tpIB;
	}

	public int getFpIB() {
		return fpIB;
	}

	public void setFpIB(int fpIB) {
		this.fpIB = fpIB;
	}

	public int getTnIB() {
		return tnIB;
	}

	public void setTnIB(int tnIB) {
		this.tnIB = tnIB;
	}

	public int getFnIB() {
		return fnIB;
	}

	public void setFnIB(int fnIB) {
		this.fnIB = fnIB;
	}

	
	

}
