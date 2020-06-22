package project.bookkeeper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Rename {
	
	private String newpath;
	private List<String> oldpaths;
	
	public Rename(String n, List<String> o ) {
		this.newpath=n;
		this.oldpaths=o;
	}
	
	public Rename() {
		this.initOldpaths();
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
		
		this.oldpaths= new ArrayList<>();
	}
	
	public boolean checkAlias(String fileName) {
		
		for(String a : oldpaths) {
			
			if(a.equals(fileName)) {
				return false;
			}
		}
		
		return true;
	}

	
}
