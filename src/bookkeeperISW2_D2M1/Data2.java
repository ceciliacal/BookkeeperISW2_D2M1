package bookkeeperISW2_D2M1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Data2 {	
	
	//cosi scrivo UNA RIGA DEL DATASET
	
	/* la classe Data rappresenta una classe di bookkeeper, cioè un file .java
	 * mi serve:
	 * filename
	 * data in cui filename è stato MODIFY/ADD/DELETE
	 * buggy
	 * metriche
	 */
	
	private List<Integer> buggyReleases;
	private String filename;
	private Boolean buggy;
	
	private int release;
	private String file;
	

	public Data2 (String name, int release) {
		
		this.setFilename(name);
		this.initBuggyReleases();
		buggyReleases.add(release);
	}


	public String getFilename() {
		return filename;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}


	public List<Integer> getBuggyReleases() {
		return buggyReleases;
	}


	public void setBuggyReleases(List<Integer> buggyReleases) {
		this.buggyReleases = buggyReleases;
	}

	public void initBuggyReleases() {
		
		this.buggyReleases= new ArrayList<Integer>();
	}


	public int getRelease() {
		return release;
	}


	public void setRelease(int release) {
		this.release = release;
	}


	
}