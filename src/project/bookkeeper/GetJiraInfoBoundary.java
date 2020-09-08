package project.bookkeeper;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


public class GetJiraInfoBoundary {

		private static final String FIELDS = "fields";
		private static final String projName = "ZOOKEEPER";
		//private static final String projName = "BOOKKEEPER";
		
		
	
		private GetJiraInfoBoundary() {
	     throw new IllegalStateException("Utility class");
		}

	
		//get Release Info: popola liste su release 

		public static List<Release> getReleaseInfo()  throws IOException, JSONException {
			
			Map<LocalDateTime, String> releaseNames;
			Map<LocalDateTime, String> releaseID;
			List<LocalDateTime> releases;
			
			List<Release> myReleases = new ArrayList<>();
			int j;
			int i;
			
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
	               addRelease(releases, releaseID, releaseNames, versions.getJSONObject(i).get("releaseDate").toString(),
	                          name,id);
	            }
	        }
	        
	        // order releases by date 
	        
	        Collections.sort(releases, (o1, o2) -> o1.compareTo(o2));
	        
	        
	        if (releases.size() < 6) {
	            return Collections.emptyList();
	        }
	          
	         for (j=0;j<releases.size();j++) {
	        	 
	        	 myReleases.add(new Release(j+1,releaseNames.get((releases).get(j)),releases.get(j)));
	        	 
	         }
	        
	         
	         return myReleases;
		}
		
		

		
	
	   public static void addRelease(List<LocalDateTime> releases, Map<LocalDateTime, String> releaseID, Map<LocalDateTime, String> releaseNames, String strDate, String name, String id) {
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
	            
	            //resolutiondate= data fix del bug
	            LocalDate resolutiondate = LocalDate.parse(((CharSequence) issues.getJSONObject(i%1000).getJSONObject(FIELDS).get("resolutiondate")).subSequence(0,10));
	            
	            //versions=AV del bug   
	            JSONArray versions = issues.getJSONObject(i%1000).getJSONObject(FIELDS).getJSONArray("versions");	            
	            List <Integer> versionsList= createAVList(versions, rel);
	            
	            //created= data creazione ticket (OV)  
	            LocalDate created = LocalDate.parse(((CharSequence) issues.getJSONObject(i%1000).getJSONObject(FIELDS).get("created")).subSequence(0,10));
 
	           ticketList.add(new Ticket(key,versionsList,resolutiondate,created));        	            
	         
	         } 
	         
	      } while (i < total);
	     
	      return ticketList;
	  
	   }
	   
	   

	   
	   public static List<Integer> createAVList(JSONArray ver, List<Release> rel) throws JSONException {
		   List<Integer> av = new ArrayList<>();
		   int len;
		   int i;
		   
		   int index;
		   		   
		   len=ver.length();
		   
		   if (len==0) {
			   av.add(0);
		   }
		   else {
			   for (i=0;i<len;i++) {
				   
				   index=indexConversion(ver.getJSONObject(i).get("name").toString(), rel );
				   if (index>-1) {
					   av.add(index);
				   }
			   }
		   }
		   
		 //se AV[0] > OV, butta AV
		   
		   if (av.isEmpty()) {
			   av.add(0);
		   }
		 
		   
		return av;
		   
		   
	   }
	   

	   
	   
	   public static int indexConversion(String name, List<Release> rel ) {
		   int res;
		   int i;
		   
		   for (i=0;i<rel.size();i++) {
			   
			   if (name.equals(rel.get(i).getVersionName())) {
				   
				   res=rel.get(i).getIndex();
				   return res;
			   }
		   }
		   
		   res=-1;		//in questo caso, AV di Jira NON è VALIDA! controllo lo faccio in metodo setIvOvFv
		   return res;
	   
	   }
	   

	   
	   public static void printTicketList(List<Ticket> ticketList) {
		   int i;
		   int k;
		   int len=ticketList.size();
		   
		   for (i=0;i<len;i++) {	//prendo ticket i-esimo
			  
			   Log.infoLog("ticketID: "+ ticketList.get(i).getTicketID());
			   Log.infoLog("resolutionDate: "+ ticketList.get(i).getResolutionDate());
			   Log.infoLog("created: "+ ticketList.get(i).getCreatedDate());
			   
			   
			   Log.infoLog("AV: " + ticketList.get(i).getAV());
			   
			   for (k=0;k<ticketList.get(i).getRelatedCommits().size();k++) {	//scorro Lista dei Commits del ticket i-esimo
					
				   Log.infoLog("commit n "+k+": " + ticketList.get(i).getRelatedCommits().get(k));
			   }
			   
			   for (k=0;k<ticketList.get(i).getRelatedJavaFiles().size();k++) {	//scorro Lista delle classi toccate dai commit del ticket i-esimo
					

				   Log.infoLog("file n "+k+": " + ticketList.get(i).getRelatedJavaFiles().get(k));
			   }

			   
			   
			   Log.infoLog("============================");
			
		   }
		   
		   
	   }


	   public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		      InputStream is = new URL(url).openStream();
		      try (BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8.name()))){
		         StandardCharsets.UTF_8.name();
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