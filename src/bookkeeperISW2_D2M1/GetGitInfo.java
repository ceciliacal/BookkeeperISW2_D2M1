package bookkeeperISW2_D2M1;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.json.JSONException;

public class GetGitInfo {
	
	public static int halfRelease= MainControl.halfRelease;
	public static List <Release> releases= MainControl.releases;
	public static Repository repository= MainControl.repository;
	public static List <String> classesList= MainControl.classesList;
	public static List<Data> entries2;
	
	public static LinkedHashMap <RevCommit, LocalDateTime> sameRelease;
	
	public static LinkedHashMap <RevCommit, Integer> lastCommits;
	
	public static void main(String[] args) throws IOException, JSONException, NoHeadException, GitAPIException {
		


    }
	
	// PER RECUPERARE FILES PER RELEASE !!!!!
	
	//faccio confronto tra date commit e releases - associo commit e release
public static void getFilesPerRelease(Git git, List<Data> dbEntries) throws IOException, NoHeadException, GitAPIException {

    	LinkedHashMap <RevCommit, Integer> map = new LinkedHashMap <RevCommit, Integer>();
    	int i;
    	int release;
    	int count=0;

    	//get Commits
    	Iterable<RevCommit> log = git.log().all().call();
    	
    	for (RevCommit commit : log) {
   
    		release=compareCommitsDate(commit);
    		//releases.get(release-1).getCommitsOfRelease().add(commit);
    		
    		map.put(commit, release);
    		//System.out.println("commit id : "+commit.getId()+"   release: "+release+"    commit date:"+commit.getAuthorIdent().getWhen());

    		count++;
    	}
    	
		//System.out.println("count : "+count+"      map size: "+map.size());
    	
		//LinkedHashMap <RevCommit, Integer> lastCommits = new LinkedHashMap <RevCommit, Integer>();
		lastCommits = new LinkedHashMap <RevCommit, Integer>();

		
		for (i=0;i<releases.size();i++) {
		
			getLastCommit(map, releases.get(i).getIndex(), lastCommits);

		}
				
		retrieveFiles(lastCommits, dbEntries);
		
		System.out.println("\n\n\n");

		for (int j=0;j<dbEntries.size();j++) {
			
			//System.out.println("release: "+dbEntries.get(j).getRelease()+"       file: "+dbEntries.get(j).getFilename());

			
		}
       
	}
    	

	//prendo ultimo commit per release
	public static LinkedHashMap <RevCommit, Integer> getLastCommit(LinkedHashMap <RevCommit, Integer> map, int release, LinkedHashMap <RevCommit, Integer> lastCommits)	{
		
		LocalDateTime dateCommit;
		//LinkedHashMap <RevCommit, LocalDateTime> sameRelease = new LinkedHashMap <RevCommit, LocalDateTime>();
		sameRelease = new LinkedHashMap <RevCommit, LocalDateTime>();

		for (HashMap.Entry<RevCommit, Integer> entry : map.entrySet()) {
			
			//prendo tutti i commit di una stessa release
    		if (entry.getValue()==release) {
    			
    			//System.out.println("commit : "+entry.getKey()+"    date:"+entry.getKey().getAuthorIdent().getWhen()+"      release:"+entry.getValue());
    			
    			dateCommit = Instant.ofEpochSecond(entry.getKey().getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();	
    			
    			//aggiungo commit id e data del suo commit
    			
    			
    			sameRelease.put(entry.getKey(), dateCommit);
    			
    			//releases.get(release-1).getCommitsOfRelease().add(entry.getKey());
    			
    			
    		}	
		}
		
		//chiama LOC TOUCHED
		
		HashMap.Entry<RevCommit, LocalDateTime> maxEntry= null;
		
		
		
		for (HashMap.Entry<RevCommit, LocalDateTime> entry : sameRelease.entrySet()) {

			if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue())>0) {
    			
    			maxEntry= entry;
    		
    		}			
		}
		
		if (sameRelease.size()==0) {
			//copio same Release della release precedente
				return null;

		}
		
		System.out.println("release: "+release+"    maxCommit id : "+maxEntry.getKey().getId()+"    maxRelease :"+maxEntry.getValue());
		lastCommits.put(maxEntry.getKey(), release);
		return lastCommits;
		
	}
    		


	public static int compareCommitsDate(RevCommit commit) {
		
		int i, res;
		//LocalDate dateCommit, dateRelease;
		LocalDateTime commitDate;
		LocalDateTime d2, dateRelease;
		commitDate = Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
		
		
		res=0;
		
		for (i=0;i<releases.size();i++) {
			
			dateRelease = releases.get(i).getDate();
			if (commitDate.compareTo(dateRelease)<0) {
				
				res=releases.get(i).getIndex();
				break;
				
			}
		}

		
		return res;
		
		
	}

	//prendo TUTTI i file in una release
	public static void retrieveFiles(LinkedHashMap <RevCommit, Integer> lastCommits, List<Data> dbEntries) throws IOException {
		
		int count;
		int fileLoc=0;
		int fileLocTouched=0;
        Ref head = repository.exactRef("HEAD");
        List<String> allFiles = new ArrayList<String>();	//lista in cui metto tutti i file della repository
        List<String> files;
        
        for (HashMap.Entry<RevCommit, Integer> entry : lastCommits.entrySet()) {
            
        	files = new ArrayList<String>();
        	count=0;
        	RevWalk walk = new RevWalk(repository);

            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = entry.getKey().getTree();
            //System.out.println("Having tree: " + tree);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
            	
            	if (treeWalk.getPathString().contains(".java")) {
            		            		
            		classesList.add(treeWalk.getPathString());
            		
            		Data dbEntry=new Data(entry.getValue(),treeWalk.getPathString());
            		fileLoc= Metrics.loc(treeWalk);
            		dbEntry.setLoc(fileLoc);
            		dbEntries.add(dbEntry);
            		
            		//chiama qua LOC TOUCHED e gli passi il commit da lastCommits e la release da value(Integer)
            		//io gli sto passand ogni LastCommit
            		fileLocTouched= Metrics.locTouched(entry.getKey());
            		count++;
            		
            		 //System.out.println("count release "+entry.getValue()+": "+count);
                     System.out.println("release: "+entry.getValue()+"     fileLocTouched: "+fileLocTouched+"       file: "+treeWalk.getPathString());
            		 //System.out.println("count: "+count);
	
            		
            	}
            	

            }
 
            //System.out.println("================================================================");
   		 //System.out.println("count: "+count);

        }
	}
	

	//prendo TUTTE classe in una release
	public static List<Data> retrieveJavaFiles(int release, List<String> files, List<Data> dbEntries) {

        int i;
        int count=0;
        
        for(i=0;i<files.size();i++) {
        	
        	if (files.get(i).contains(".java")) {
        		
        		dbEntries.add(new Data(release, files.get(i)));
        		classesList.add(files.get(i));
        		count++;
        	}
        	
        }
        
        System.out.println("count: "+count);
        return dbEntries;

		

	}
    
    public static List<RevCommit> getCommitsID(Git git, List<Ticket> ticketlist, String pathName) throws IOException, NoHeadException, GitAPIException {
    	
    	//Git git= Git.open(new File(pathName));
    	List<RevCommit> myCommits= new ArrayList <RevCommit>();
    	int i;
    	int count=0;
    	Boolean checkCommitDate;
    	

    	//get Commits
    	Iterable<RevCommit> log = git.log().call();
    	List<RevCommit> logCommitList = new  ArrayList<RevCommit>();
    	
    	for (RevCommit commit : log) {
            
    		//checkCommitDate=compareDates(commit);
    		//if (checkCommitDate==true) {
                logCommitList.add(commit);
    		//}
            
      
   
        }
    	
    	//inserisco in Ticket gli id dei commit che si riferiscono ad esso
    	for (i=0;i<ticketlist.size();i++) {
    		
    		for (RevCommit commit : logCommitList) {
    			
    			if (commit.getFullMessage().contains(ticketlist.get(i).getTicketID()+":")) {
    				
    				ticketlist.get(i).getRelatedCommits().add(commit.getId().toString());

    				myCommits.add(commit);
 
    				count++;   
    				
    			}
    				
    		}
    		
    		
    	}
    	
   
    	return myCommits;
		//System.out.println("numero commit id +: "+count+"   num tot commit logCommitList: "+logCommitList.size()+"    numero tickets id:"+ticketlist.size());
    	
    }
    
   
    public static Boolean compareDates(RevCommit commit) {
    	
    	int i;
    	
    	LocalDateTime dateCommit = Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
		LocalDateTime dateHalfRelease = releases.get(halfRelease-1).getDate();
		
		//System.out.println("date commit: "+dateCommit+"   dateHalfRelease "+dateHalfRelease);

		if (dateCommit.compareTo(dateHalfRelease)<0) {
			//System.out.println("dentro -> date commit: "+dateCommit+"   dateHalfRelease "+dateHalfRelease);
			return true;
		}
		
		return false;
    	
    }

	
    //prendo lista di tutti i file presenti nella repository del progetto
    public static List<String> listAllFiles(Repository repository) throws IOException {
        Ref head = repository.exactRef("HEAD");
        List<String> allFiles = new ArrayList<String>();	//lista in cui metto tutti i file della repository
        List<String> classes;	//lista in cui metto tutti i file .java della repository

        // a RevWalk allows to walk over commits based on some filtering that is defined
        RevWalk walk = new RevWalk(repository);

        RevCommit commit = walk.parseCommit(head.getObjectId());
        RevTree tree = commit.getTree();
        //System.out.println("Having tree: " + tree);

        // now use a TreeWalk to iterate over all files in the Tree recursively
        // you can set Filters to narrow down the results if needed
        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        while (treeWalk.next()) {
        	allFiles.add(treeWalk.getPathString());
            //System.out.println("found: " + treeWalk.getPathString());
        }
        
        classes=listJavaFiles(allFiles);
        return classes;
    }
    

    
    public static void printList(List<String> list) {
    	int i;
    	int len= list.size();
    	
    	for(i=0;i<len;i++) {
            System.out.println("found: " +list.get(i));
    	}
        System.out.println("dim classesList: " +classesList.size());

    }
    	
 
    //prendo tutti i file .java (classi) presenti nel progetto
    public static List<String> listJavaFiles(List<String> fileList) {
    	int i;
    	int len= fileList.size();
        List<String> classes = new ArrayList<String>();	//lista in cui metto tutti i file .java della repository

    	
    	for(i=0;i<len;i++) {          
            if (fileList.get(i).contains(".java")) {
            	classes.add(fileList.get(i));  	
            }
    	}
		return classes;
    	
    }
    	 		


}
