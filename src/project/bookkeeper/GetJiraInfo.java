package project.bookkeeper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class GetJiraInfo {
	
	   public static Map<LocalDateTime, String> releaseNames;
	   public static Map<LocalDateTime, String> releaseID;
	   public static List<LocalDateTime> releases;
	   public static Integer numVersions;
	   

	public static void main(String[] args) throws IOException, JSONException {
		   

		 
	}
	
		//get Release Info: popola liste su release (attributi di questa classe)
		//ex createCsvReleaseInfo
		public static List<Release> getReleaseInfo()  throws IOException, JSONException {
			
			List<Release> myReleases = new ArrayList<>();
			String projName ="BOOKKEEPER ";
			int j, i;
			
			//Fills the arraylist with releases dates and orders them
			//Ignores releases with missing dates
			releases = new ArrayList<>();
	        String url = "https://issues.apache.org/jira/rest/api/2/project/" + projName;
	        JSONObject json = readJsonFromUrl(url);
	        JSONArray versions = json.getJSONArray("versions");
	        releaseNames = new HashMap<>();
	        releaseID = new HashMap<> ();
	        for (i = 0; i < versions.length(); i++ ) {
	            String name = "";
	            String id = "";
	            if(versions.getJSONObject(i).has("releaseDate")) {
	               if (versions.getJSONObject(i).has("name"))
	                  name = versions.getJSONObject(i).get("name").toString();
	               if (versions.getJSONObject(i).has("id"))
	                  id = versions.getJSONObject(i).get("id").toString();
	               addRelease(versions.getJSONObject(i).get("releaseDate").toString(),
	                          name,id);
	            }
	        }
	        // order releases by date
	        Collections.sort(releases, new Comparator<LocalDateTime>(){
	            //@Override
	            public int compare(LocalDateTime o1, LocalDateTime o2) {
	                return o1.compareTo(o2);
	            }
	        });
	        
	        if (releases.size() < 6) {
	            return null;
	        }

	         //System.out.println("myReleases: ");
	          
	         for (j=0;j<releases.size();j++) {
	        	 
	        	 myReleases.add(new Release(j+1,releaseNames.get((releases).get(j)),releases.get(j)));
	        	 //System.out.println(myReleases.get(j).getIndex()+"			"+ myReleases.get(j).getVersionName()+"			"+myReleases.get(j).getDate());
	        	 
	         }
	        
	         
	         return myReleases;
		}

		
		public static void write() {
			String projName ="BOOKKEEPER ";
			int i;
			FileWriter fileWriter = null;
			try {
		            fileWriter = null;
		            String outname = projName + "VersionInfo.csv";
						    //Name of CSV for output
						    fileWriter = new FileWriter(outname);
		            fileWriter.append("Index;Version ID;Version Name;Date");
		            fileWriter.append("\n");
		            numVersions = releases.size();
		            for ( i = 0; i < releases.size(); i++) {
		               Integer index = i + 1;
		               fileWriter.append(index.toString());
		               fileWriter.append(";");
		               fileWriter.append(releaseID.get(releases.get(i)));
		               fileWriter.append(";");
		               fileWriter.append(releaseNames.get(releases.get(i)));
		               fileWriter.append(";");
		               fileWriter.append(releases.get(i).toString());
		               fileWriter.append("\n");
		            }

		         } catch (Exception e) {
		            System.out.println("Error in csv writer");
		            e.printStackTrace();
		         } finally {
		            try {
		               fileWriter.flush();
		               fileWriter.close();
		            } catch (IOException e) {
		               System.out.println("Error while flushing/closing fileWriter !!!");
		               e.printStackTrace();
		            }
		         }
		         return;
			
		}
 
	
	   public static void addRelease(String strDate, String name, String id) {
		      LocalDate date = LocalDate.parse(strDate);
		      LocalDateTime dateTime = date.atStartOfDay();
		      if (!releases.contains(dateTime))
		         releases.add(dateTime);
		      releaseNames.put(dateTime, name);
		      releaseID.put(dateTime, id);
		   }
	   
	   
	   //get Ticket Info (Issue)
	   public static List<Ticket> getTicketInfo(List <Release> rel) throws IOException, JSONException {

		   List<Ticket> ticketList = new ArrayList<>();
		   
	  	   String projName ="BOOKKEEPER";	  	   
		   Integer i=0; //inizio index da cui prendo file json
		   Integer j=0; //fine index del file json
		   Integer total=1; 
		   
		   
	      do {
	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	         
	    	  j = i + 1000;
	        
	         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	  		         + projName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	  		         + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
	  		         + i.toString() + "&maxResults=" + j.toString();
	         
	         //prendo file json
	         JSONObject json = readJsonFromUrl(url);
	         
	         //faccio parse del file json
	         JSONArray issues = json.getJSONArray("issues");	//ordered sequence of values, array di tickets (issues)
	         total = json.getInt("total");						//numero totale degli elementi nel file json considerato
	         
	       //itero per ogni issue (ticket)
	         for (; i < total && i < j; i++) {
	        	 
	            //per ogni issue(bug), prendo key=ticketID, versions=AV, resolutionDate=FV, created=OV 
	            String key = issues.getJSONObject(i%1000).get("key").toString();	//key= ticketID
	            //System.out.println(key);
	            
	            
	            //resolutiondate= data fix del bug
	            LocalDate resolutiondate = LocalDate.parse(((CharSequence) issues.getJSONObject(i%1000).getJSONObject("fields").get("resolutiondate")).subSequence(0,10));
	            //System.out.println(resolutiondate);
	            
	            //versions=AV del bug   
	            JSONArray versions = issues.getJSONObject(i%1000).getJSONObject("fields").getJSONArray("versions");	            
	            List <Integer> versionsList= createAVList(versions, rel);
	            
	            //created= data creazione ticket (OV)  
	            LocalDate created = LocalDate.parse(((CharSequence) issues.getJSONObject(i%1000).getJSONObject("fields").get("created")).subSequence(0,10));
	            //System.out.println(created);
	            
	            

	            
	           ticketList.add(new Ticket(key,versionsList,resolutiondate,created));        	            
	         
	         } 
	         
	      } while (i < total);
	     
	      return ticketList;
	  
	   }
	   
	   

	   
	   public static List<Integer> createAVList(JSONArray ver, List<Release> rel) throws JSONException {
		   List<Integer> av = new ArrayList<>();
		   int len;
		   int i;
		   
		   
		   len=ver.length();
		   
		   if (len==0) {
			   av.add(0);
		   }
		   else {
			   for (i=0;i<len;i++) {
				   
				   av.add( indexConversion(ver.getJSONObject(i).get("name").toString(),  rel ));
				   
				   //AV.add(ver.getJSONObject(i).get("name").toString());
			   }
		   }
		   
		return av;
		   
		   
	   }
	   
	   public static int indexConversion(String name, List<Release> rel ) {
		   int res, i;
		   
		   for (i=0;i<rel.size();i++) {
			   
			   if (name.equals(rel.get(i).getVersionName())) {
				   
				   res=rel.get(i).getIndex();
				   return res;
			   }
		   }
		   
		   res=-1;
		   return res;
	   
	   }
	   

	   
	   public static void printTicketList(List<Ticket> ticketList) {
		   int i, j, k;
		   int len=ticketList.size();
		   
		   for (i=0;i<len;i++) {	//prendo ticket i-esimo
			  
			   System.out.println("ticketID: "+ ticketList.get(i).getTicketID());	
			   System.out.println("resolutionDate: "+ ticketList.get(i).getResolutionDate());
			   System.out.println("created: "+ ticketList.get(i).getCreatedDate());
			   
			   /*
			   for (j=0;j<ticketList.get(i).getAV().size();j++) {				//scorro Lista delle AV del ticket i-esimo
	
				   System.out.println("AV n "+j+": " + ticketList.get(i).getAV().get(j));
				   
			   
			   }
			   */
			   
			   
			   System.out.println("AV: " + ticketList.get(i).getAV());
			   
			   for (k=0;k<ticketList.get(i).getRelatedCommits().size();k++) {	//scorro Lista dei Commits del ticket i-esimo
					
				   System.out.println("commit n "+k+": " + ticketList.get(i).getRelatedCommits().get(k));	   
			   
			   }
			   
			   for (k=0;k<ticketList.get(i).getRelatedJavaFiles().size();k++) {	//scorro Lista delle classi toccate dai commit del ticket i-esimo
					
				   System.out.println("file n "+k+": " + ticketList.get(i).getRelatedJavaFiles().get(k));	   
			   
			   }

			   
			   
			   
			   System.out.println("============================");  
			
		   }
		   
		   
	   }


	   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	      InputStream is = new URL(url).openStream();
	      try {
	         BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	         String jsonText = readAll(rd);

	         return new JSONObject(jsonText);
	       } finally {
	         is.close();
	       }
	   }
	   
	   private static String readAll(Reader rd) throws IOException {
		      StringBuilder sb = new StringBuilder();
		      int cp;
		      while ((cp = rd.read()) != -1) {
		         sb.append((char) cp);
		      }
		      return sb.toString();
		   }

	
}