package bookkeeperISW2_D2M1;

import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Data {
	
	private int release;	//release dopo rename
	private String filename;
	private String buggy;
	private List<RevCommit> relatedCommits;
	
	private String path;
	
	public Data(int rel, String name) {
		
		this.release=rel;
		this.filename=name;
		this.setBuggy("N");
	}
	
	public int getRelease() {
		return release;
	}
	
	public void setRelease(int release) {
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
	

}