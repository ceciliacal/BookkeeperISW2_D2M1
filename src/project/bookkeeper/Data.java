package project.bookkeeper;

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
	private int maxLocAdded;
	private int avgLocAdded;
	private int churn;
	private int maxChurn;
	private int avgChurn;
	private int chgSetSize;	//n files committed together with C
	private int maxChgSetSize;
	private int avgChgSetSize;
	
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

	public int getMaxLocAdded() {
		return maxLocAdded;
	}

	public void setMaxLocAdded(int maxlocAdded) {
		this.maxLocAdded = maxlocAdded;
	}

	public double getAvgLocAdded() {
		return avgLocAdded;
	}

	public void setAvgLocAdded(int avglocAdded) {
		this.avgLocAdded = avglocAdded;
	}

	public int getMaxChurn() {
		return maxChurn;
	}

	public void setMaxChurn(int maxChurn) {
		this.maxChurn = maxChurn;
	}

	public double getAvgChurn() {
		return avgChurn;
	}

	public void setAvgChurn(int avgChurn) {
		this.avgChurn = avgChurn;
	}

	public int getChgSetSize() {
		return chgSetSize;
	}

	public void setChgSetSize(int chgSetSize) {
		this.chgSetSize = chgSetSize;
	}

	public int getMaxChgSetSize() {
		return maxChgSetSize;
	}

	public void setMaxChgSetSize(int maxChgSetSize) {
		this.maxChgSetSize = maxChgSetSize;
	}

	public double getAvgChgSetSize() {
		return avgChgSetSize;
	}

	public void setAvgChgSetSize(int avgChgSetSize) {
		this.avgChgSetSize = avgChgSetSize;
	}

	public int getnFix() {
		return nFix;
	}

	public void setnFix(int nFix) {
		this.nFix = nFix;
	}
	

}