package bookkeeperISW2_D2M1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Metrics {

	public static List<RevCommit> commitList= MainControl.myCommitsList;
	public static List<String> classesList= MainControl.classesList;
	public static Repository repository= MainControl.repository;

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
    	    	diffFormatter.setRepository( repository );
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
	
	
	public static int loc(TreeWalk treewalk) throws MissingObjectException, IOException {
		
		ObjectLoader loader = repository.open(treewalk.getObjectId(0));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		loader.copyTo(output);
		
		String filecontent = output.toString();
		StringTokenizer token= new StringTokenizer(filecontent,"\n");
		
		int count=0;
		while(token.hasMoreTokens()) {
			
			count++;
			token.nextToken();
			
		}
		
		return count;
	}
	
	
	public static int locTouched(RevCommit commit) throws IOException {
		
		int locAdded = 0;
		int locDeleted = 0;
		int locTouched = 0;
		
		ObjectId oldTree = commit.getTree();
		RevCommit parent = (RevCommit) commit.getParent(0).getId();
		
    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	diffFormatter.setRepository( repository );
    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	diffFormatter.setDetectRenames(true);
    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	//System.out.println("\n\n\n");
    	
    	for( DiffEntry entry : entries ) {
    		
    		for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
	            locDeleted += edit.getEndA() - edit.getBeginA();
	            locAdded += edit.getEndB() - edit.getBeginB();
	        }
    		
    		locTouched= locAdded+locDeleted;
	    }
    	
    	return locTouched;

		
		/*
		try {
		    repo = new FileRepository(new File("repo/.git"));
		    RevWalk rw = new RevWalk(repo);
		    RevCommit commit = rw.parseCommit(repo.resolve("486817d67b")); // Any ref will work here (HEAD, a sha1, tag, branch)
		    RevCommit parent = rw.parseCommit(commit.getParent(0).getId());
		    DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
		    df.setRepository(repo);
		    df.setDiffComparator(RawTextComparator.DEFAULT);
		    df.setDetectRenames(true);
		    List<DiffEntry> diffs;
		    diffs = df.scan(parent.getTree(), commit.getTree());
		    filesChanged = diffs.size();
		    for (DiffEntry diff : diffs) {
		        for (Edit edit : df.toFileHeader(diff).toEditList()) {
		            linesDeleted += edit.getEndA() - edit.getBeginA();
		            linesAdded += edit.getEndB() - edit.getBeginB();
		        }
		    }
		} catch (IOException e1) {
		    throw new RuntimeException(e1);
		}
		*/
	}

}