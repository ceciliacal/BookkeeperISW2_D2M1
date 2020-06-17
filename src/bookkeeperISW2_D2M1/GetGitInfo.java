package bookkeeperISW2_D2M1;


import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	
	public static void main(String[] args) throws IOException, JSONException, NoHeadException, GitAPIException {
		
		 /*
		
        String path= new String();
    	path="D:\\Cecilia\\Desktop\\bookkeeper";
		
    	GetJiraInfo getInfo = new GetJiraInfo();
    	
    	List<Ticket> ticketlist= getInfo.getTicketInfo();
    	
    	getCommitsID(ticketlist,path);
    	getInfo.printTicketList(ticketlist);
    	
    	//populateLists(path);
		 */
    	
    	

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
            
    		checkCommitDate=compareDates(commit);
    		if (checkCommitDate==true) {
                logCommitList.add(commit);
    		}
            
      
   
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
    	
    	LocalDate dateCommit = Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDate();
		LocalDate dateHalfRelease = releases.get(halfRelease-1).getDate();
		
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
