package bookkeeperISW2_D2M1;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Release {
	
	private int index;
	private String versionID;
	private String versionName;
	private LocalDateTime date;

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public Release(int i,String name, LocalDateTime d) {
		
		this.index=i;
		this.versionName=name;
		this.date=d;
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


/*
	public void LocalDateTime(LocalDateTime date) {
		this.date = date.toLocalDate();
	}
	*/

}
