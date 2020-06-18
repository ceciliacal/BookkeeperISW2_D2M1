package bookkeeperISW2_D2M1;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Release {
	
	private int index;
	private String versionID;
	private String versionName;
	private LocalDateTime date;
	
	private List<RevCommit> commitsOfRelease;
	private List<String> filesOfRelease;
	private RevCommit lastCommit;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Release(int i,String name, LocalDateTime d) {
		
		this.index=i;
		this.versionName=name;
		this.date=d;
		
		this.initCommits();
		this.initFiles();
	}

	public void initCommits() {
		
		this.commitsOfRelease= new ArrayList<RevCommit>();
	}
	
	public void initFiles() {
		
		this.filesOfRelease= new ArrayList<String>();
	}

	public int getIndex() {
		return index;
	}



	public void setIndex(int index) {
		this.index = index;
	}



	public String getVersionID() {
		return versionID;
	}



	public void setVersionID(String versionID) {
		this.versionID = versionID;
	}



	public String getVersionName() {
		return versionName;
	}



	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}



	public LocalDateTime getDate() {
		return date;
	}

	public List<RevCommit> getCommitsOfRelease() {
		return commitsOfRelease;
	}

	public void setCommitsOfRelease(List<RevCommit> commitsOfRelease) {
		this.commitsOfRelease = commitsOfRelease;
	}

	public List<String> getFilesOfRelease() {
		return filesOfRelease;
	}

	public void setFilesOfRelease(List<String> filesOfRelease) {
		this.filesOfRelease = filesOfRelease;
	}

	public RevCommit getLastCommit() {
		return lastCommit;
	}

	public void setLastCommit(RevCommit lastCommit) {
		this.lastCommit = lastCommit;
	}


/*
	public void LocalDateTime(LocalDateTime date) {
		this.date = date.toLocalDate();
	}
	*/

}
