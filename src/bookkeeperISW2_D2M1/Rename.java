package bookkeeperISW2_D2M1;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Rename {
	
	private String newpath;
	private List<String> oldpaths;
	
	public Rename(String n, List<String> o ) {
		this.newpath=n;
		this.oldpaths=o;
		//this.oldpaths = new ArrayList<>();
		//this.initOldpaths();
		//oldpaths = new ArrayList<>();
	}
	
	public String getNewpath() {
		return newpath;
	}
	public void setNewpath(String newpath) {
		this.newpath = newpath;
	}
	public List<String> getOldpaths() {
		return oldpaths;
	}
	public void setOldpaths(List<String> oldpaths) {
		this.oldpaths = oldpaths;
	}
	public void initOldpaths() {
		
		this.oldpaths= new ArrayList<String>();
	}

	
}
