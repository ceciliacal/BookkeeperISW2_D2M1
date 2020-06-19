package bookkeeperISW2_D2M1;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Data {
	
	private Release release;	//release dopo rename
	private String filename;
	private String buggy;
	
	private int loc;
	private int nr;
	private int nFix;
	private int nAuth;
	private int locTouched;
	private int locAdded;
	private int max_locAdded;
	private double avg_locAdded;
	private int churn;
	private int max_churn;
	private double avg_churn;
	private int chgSetSize;	//n files committed together with C
	private int max_chgSetSize;
	private double avg_chgSetSize;
	
	private List<RevCommit> relatedCommits;
	
	private String path;
	
	public Data(Release rel, String name) {
		
		this.release=rel;
		this.filename=name;
		this.setBuggy("N");
	}
	
	public Release getRelease() {
		return release;
	}
	
	public void setRelease(Release release) {
		this.release = release;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	
	public String getPath() {
		return path;
	}
	
	public void setPath(String path) {
		this.path = path;
	}

	public String getBuggy() {
		return buggy;
	}

	public void setBuggy(String buggy) {
		this.buggy = buggy;
	}

	public List<RevCommit> getRelatedCommits() {
		return relatedCommits;
	}

	public void setRelatedCommits(List<RevCommit> relatedCommits) {
		this.relatedCommits = relatedCommits;
	}

	public int getLoc() {
		return loc;
	}

	public void setLoc(int loc) {
		this.loc = loc;
	}

	public int getNr() {
		return nr;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public int getnAuth() {
		return nAuth;
	}

	public void setnAuth(int nAuth) {
		this.nAuth = nAuth;
	}

	public int getLocTouched() {
		return locTouched;
	}

	public void setLocTouched(int locTouched) {
		this.locTouched = locTouched;
	}

	public int getLocAdded() {
		return locAdded;
	}

	public void setLocAdded(int locAdded) {
		this.locAdded = locAdded;
	}

	public int getChurn() {
		return churn;
	}

	public void setChurn(int churn) {
		this.churn = churn;
	}

	public int getMax_locAdded() {
		return max_locAdded;
	}

	public void setMax_locAdded(int max_locAdded) {
		this.max_locAdded = max_locAdded;
	}

	public double getAvg_locAdded() {
		return avg_locAdded;
	}

	public void setAvg_locAdded(double avg_locAdded) {
		this.avg_locAdded = avg_locAdded;
	}

	public int getMax_churn() {
		return max_churn;
	}

	public void setMax_churn(int max_churn) {
		this.max_churn = max_churn;
	}

	public double getAvg_churn() {
		return avg_churn;
	}

	public void setAvg_churn(double avg_churn) {
		this.avg_churn = avg_churn;
	}

	public int getChgSetSize() {
		return chgSetSize;
	}

	public void setChgSetSize(int chgSetSize) {
		this.chgSetSize = chgSetSize;
	}

	public int getMax_chgSetSize() {
		return max_chgSetSize;
	}

	public void setMax_chgSetSize(int max_chgSetSize) {
		this.max_chgSetSize = max_chgSetSize;
	}

	public double getAvg_chgSetSize() {
		return avg_chgSetSize;
	}

	public void setAvg_chgSetSize(double avg_chgSetSize) {
		this.avg_chgSetSize = avg_chgSetSize;
	}

	public int getnFix() {
		return nFix;
	}

	public void setnFix(int nFix) {
		this.nFix = nFix;
	}
	

}