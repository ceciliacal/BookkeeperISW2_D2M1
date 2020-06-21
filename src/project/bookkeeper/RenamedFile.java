package project.bookkeeper;

import java.util.List;

public class RenamedFile {
	
	private String oldPath;
	private String newPath;
	private int count;		//indica il numero di volte che oldpath è stato rinominato (indirettamente anche)
	private int release;	//release da cui avviene rinomina
	
	public RenamedFile(String old) {
		this.oldPath=old;
	}
	
	

	public String getOldPath() {
		return oldPath;
	}
	public void setOldPath(String oldPath) {
		this.oldPath = oldPath;
	}
	


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public String getNewPath() {
		return newPath;
	}


	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}



	public int getRelease() {
		return release;
	}



	public void setRelease(int release) {
		this.release = release;
	}
	
	

}