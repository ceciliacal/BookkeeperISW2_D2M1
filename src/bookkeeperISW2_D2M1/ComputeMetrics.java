package bookkeeperISW2_D2M1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class ComputeMetrics {

	public static List<RevCommit> commitList= MainControl.myCommitsList;
	public static List<String> classesList= MainControl.classesList;
	public static Repository repo= MainControl.repository;

	public static void main(String[] args) throws NoHeadException, GitAPIException, IOException {
		

	}
	
	//number of revisions of a file (num of commits of a file)
	/* 
	 * prendo un file
	 * prendo tutti i commit
	 * vedo in quanti commit è presente quel file 
	 * 
	 */
	public static void NR() throws NoHeadException, GitAPIException, IOException {
		
		int i,j;
		int count=0;
		String filename = "bookkeeper-server/src/main/java/org/apache/bookkeeper/bookie/Bookie.java";
		System.out.println("file to find= "+filename);
    	
    	//for (i=0;i<classesList.size();i++) {	//tutti i miei file del dataset
    		
    		for (j=0;j<commitList.size();j++) {	//tutti i commit dei miei ticket
    			
    			ObjectId oldTree = commitList.get(j).getTree();
    			RevCommit parent = (RevCommit) commitList.get(j).getParent(0).getId();
    			
    	    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	    	diffFormatter.setRepository( repo );
    	    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	    	diffFormatter.setDetectRenames(true);
    	    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	    	//System.out.println("\n\n\n");
    	    	
    	    	for( DiffEntry entry : entries ) {
    	    		
    	    		//if (entry.toString().contains(classesList.get(i))) {
    	    		if (entry.toString().contains(filename)) {
    	    			
    	    			count++;
    	    			System.out.println("commit: "+commitList.get(j).getId()+"         date: "+commitList.get(j).getAuthorIdent().getWhen()+"         diffEntry: "+entry.toString()+"         count: "+count);
    	    			
    	    			
    	    		}
    	    	}
    			
    		}
    	//}
    	
    	System.out.println("count (n commit del file)= "+count);
		
	}

}
