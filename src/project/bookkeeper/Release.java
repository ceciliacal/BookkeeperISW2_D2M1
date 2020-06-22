package project.bookkeeper;

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
	
	
	public Release(int i,String name, LocalDateTime d) {
		
		this.index=i;
		this.versionName=name;
		this.date=d;
		
		this.initCommits();
		this.initFiles();
	}

	public void initCommits() {
		
		this.commitsOfRelease= new ArrayList<>();
	}
	
	public void initFiles() {
		
		this.filesOfRelease= new ArrayList<>();
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




}
