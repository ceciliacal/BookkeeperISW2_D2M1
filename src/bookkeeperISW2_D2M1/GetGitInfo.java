package bookkeeperISW2_D2M1;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	public static LinkedHashMap<RevCommit, Integer> lastCommitRelease;
	
	public static void main(String[] args) throws IOException, JSONException, NoHeadException, GitAPIException {
		


    }
	
	// PER RECUPERARE FILES PER RELEASE !!!!!
	public static void getFilesPerRelease(Git git, List<Data> dbEntries) throws IOException, NoHeadException, GitAPIException {

    	
		
    	//get Commits
    	Iterable<RevCommit> log = git.log().all().call();
    	List<RevCommit> logCommitList = new  ArrayList<RevCommit>();
    	
    	for (RevCommit commit : log) {

                logCommitList.add(commit);

        }
    	
    	//=== CONFRONTO DATE COMMIT E DATE RELEASE PER ASSOCIARE A UNA RELEASE TUTTI I SUOI COMMIT
    	
    	for (RevCommit commit : logCommitList) {
    		
    		//prendo un commit e gli associo la release (con confronto date)
    		//poi aggiungo quel commit alla lista di quella release
    		
    		int res_release=associateCommit(commit);
    		
    	}
    	
    
    	
    	//=== ADESSO PRENDO L'ULTIMO COMMIT PER OGNI RELEASE
		
    	lastCommitRelease = new LinkedHashMap<RevCommit, Integer> ();
       	populateLastCommitRelease();
    	
    	
		//System.out.println("lastCommitRelease : "+lastCommitRelease.size());
    	
    	// === PRENDO TUTTI I FILE JAVA A PARTIRE DALL'ULTIMO COMMIT DI UNA RELEASE

    	retrieveJavaFiles(dbEntries);

	}	
		
		
		
		
	
	public static int associateCommit(RevCommit commit) {
		
		int i;
		LocalDateTime commitDate;
		LocalDateTime releaseDate;
		
		int res_release=0;
		
		commitDate= Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
		
		for (i=0;i<releases.size();i++) {
			
			releaseDate=releases.get(i).getDate();
			
			if (commitDate.compareTo(releaseDate)<0) {
				
				//il commit viene prima della data della release res, quindi è dopo la release che ha superato e me lo ritrovo in quella successiva
				
				res_release=releases.get(i).getIndex();
				releases.get(i).getCommitsOfRelease().add(commit);
				//System.out.println("release: "+res_release+"      releaseDate: "+releaseDate+"        commitDate: "+commitDate);

				break;
			}
			
		}
		
		return res_release;
		
	}
	
	public static void populateLastCommitRelease() {
		
		List<LocalDateTime> timeList= new ArrayList<LocalDateTime>();
		List<RevCommit> commitList;
		RevCommit lastCommit;
		
		//ordina lista commit per data e prendi l'ultima
		
		for (int i=0;i<releases.size();i++) {
			
			commitList=releases.get(i).getCommitsOfRelease();	//lista commit della release
			
			//prendi la lista di commit di quella release

			if (commitList.size()==0 && i!=0){
				
				//se questa release non ha commit, gli devo mettere come ultimo commit quello della release prima
				releases.get(i).setLastCommit(releases.get(i-1).getLastCommit());
			}
			else {
				for (int j=0;j<commitList.size();j++){
					
					//ordina lista di commit per data
			        Collections.sort(commitList, new Comparator<RevCommit>(){
			            //@Override
			            public int compare(RevCommit c1, RevCommit c2) {
			            	
			            	LocalDateTime d1= Instant.ofEpochSecond(c1.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
			            	LocalDateTime d2= Instant.ofEpochSecond(c2.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
			                return d1.compareTo(d2);
			            }
			        });
			        
			        //System.out.println("release: "+releases.get(i).getIndex()+"     dateCommit: "+commitList.get(j).getAuthorIdent().getWhen());
			        
			        //ora prendo l'ultima data
			        lastCommit=commitList.get(commitList.size()-1);
					releases.get(i).setLastCommit(lastCommit);
					
			        lastCommitRelease.put(lastCommit, releases.get(i).getIndex());
			        
				
				}
			}
	
		}
		
	}

	//prendo TUTTI i file in una release
	public static void retrieveJavaFiles(List<Data> dbEntries) throws IOException {
		
		int count;
		int fileLoc=0;
		int fileLocTouched;
       
		Ref head = repository.exactRef("HEAD");

        for (int i=0;i<releases.size();i++) {
        	
        	RevCommit lastCommit= releases.get(i).getLastCommit();
        
        	//for (HashMap.Entry<RevCommit, Integer> entry : lastCommitRelease.entrySet()) {
            count=0;
        	RevWalk walk = new RevWalk(repository);

            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = lastCommit.getTree();
            //System.out.println("Having tree: " + tree);

            TreeWalk treeWalk = new TreeWalk(repository);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            while (treeWalk.next()) {
            	
            	if (treeWalk.getPathString().contains(".java")) {
            		            		
            		classesList.add(treeWalk.getPathString());
            		releases.get(i).getFilesOfRelease().add(treeWalk.getPathString());
            		
            		count++;
            		
            		//c'è stato rename?
            		Data dbEntry=new Data(releases.get(i), treeWalk.getPathString());
            		fileLoc= Metrics.loc(treeWalk);
            		dbEntry.setLoc(fileLoc);
            		dbEntries.add(dbEntry);
            		
            		//Metrics.locTouched2(Release release, releases.get(i).getCommitsOfRelease());
            		
            		//chiama qua LOC TOUCHED e gli passi il commit da lastCommits e la release da value(Integer)
            		//io gli sto passand ogni LastCommit
           		 	//System.out.println("release: "+releases.get(i).getIndex()+"     commits: "+releases.get(i).getCommitsOfRelease().size());

            		//fileLocTouched= Metrics.locTouched(releases.get(i).getCommitsOfRelease());
            		
            		
            		 //System.out.println("count release "+entry.getValue()+": "+count);
                     //System.out.println("release: "+releases.get(i).getIndex()+"     fileLocTouched: "+fileLocTouched+"       file: "+treeWalk.getPathString());
            		 //System.out.println("count: "+count);
	
            		
            	}
            	

            }
            
           
 
         System.out.println("================================================================");
         //System.out.println("release "+entry.getValue()+"    nFiles: "+releases.get(entry.getValue()-1).getFilesOfRelease().size());
         System.out.println("count release "+releases.get(i).getIndex()+": "+count);
   		 //System.out.println("count: "+count);

        }
        //System.out.println("count release "+entry.getValue()+": "+count);
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
    	
		//System.out.println("numero commit id +: "+count+"   num tot commit logCommitList: "+logCommitList.size()+"    numero tickets id:"+ticketlist.size());

    	return myCommits;
    	
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
