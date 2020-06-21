package bookkeeperISW2_D2M1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.patch.HunkHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Metrics {

	public static List<RevCommit> commitList= MainControl.myCommitsList;
	public static List<String> classesList= MainControl.classesList;
	public static Repository repository= MainControl.repository;

	public static void main(String[] args) throws NoHeadException, GitAPIException, IOException {
		

	}
	

	
	public static void calculate(List<Release> releases) throws IOException, NoHeadException, GitAPIException {
		
		int nr;
		List <Data> dbEntries = MainControl.entries;
		
		//nr_auth(dbEntries);
		prova2(dbEntries);
		
		/*
		//prendo ogni release
		for (int i=0;i<dbEntries.size();i++) {
			//prendo ogni file di quella release
			nr=NR(dbEntries.get(i).getRelease(),dbEntries.get(i).getFilename());
			dbEntries.get(i).setNr(nr);
			
		}
		*/
	
			
		
		
		
	}
	
	
	//number of revisions of a file (num of commits of a file)
	/* 
	 * prendo un file
	 * prendo tutti i commit
	 * vedo in quanti commit è presente quel file 
	 * 
	 */
	
	public static void nr_auth(List <Data> dbEntries) throws NoHeadException, GitAPIException, IOException {
		
		
		//per ogni release, prendo ogni file ed esamino tutti i commit (TOTALI, commitsList) per vedere quante volte è presente quel file
		//per ogni file, vedo i commit in cui è presente e prendo l'autore del commit. Ogni volta che è diverso, conto ++.
		
		
		int i,j;
		int count=0;
		int auth=0;
		int checkauth=0;
		/*
		//System.out.println("file to find= "+filename);
		
		//List<RevCommit> comList = MainControl.myCommitsList;
		
		List<RevCommit> comList;
		
		
		for (i=0;i<dbEntries.size();i++) {	//prendo file
			
			List<PersonIdent> authors = new ArrayList <PersonIdent>();
			comList=dbEntries.get(i).getRelease().getCommitsOfRelease();
			count=0;
			
			for (j=0;j<comList.size();j++) {
    		
    			
    			ObjectId oldTree = comList.get(j).getTree();
    			RevCommit parent = (RevCommit) comList.get(j).getParent(0).getId();
    			
    	    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	    	diffFormatter.setRepository( repository );
    	    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	    	diffFormatter.setDetectRenames(true);
    	    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	    	//System.out.println("\n\n\n");
    	    	
    	    	//se la lista di autori non contiene l'autore del commit corrente, lo aggiungo
    			if (authors.contains(comList.get(j).getAuthorIdent())==false) {
    				authors.add(comList.get(j).getAuthorIdent());
    			}
    			
    	    	
    			for( DiffEntry entry : entries ) {
 
    	    		if (entry.toString().contains(dbEntries.get(i).getFilename())) {
    	    			
    	    			//se la lista di autori non contiene l'autore del commit corrente, lo aggiungo
    	    			if (authors.contains(comList.get(j).getAuthorIdent())==false) {
    	    				authors.add(comList.get(j).getAuthorIdent());
    	    			}
    	    			
    	    			
    	    			count++;
    	    			//System.out.println("commit: "+commitList.get(j).getId()+"         date: "+commitList.get(j).getAuthorIdent().getWhen()+"         diffEntry: "+entry.toString()+"         count: "+count);
	
    	    		}
    	    	}
    			
    		}
			
			dbEntries.get(i).setNr(count);
			auth=authors.size();
			dbEntries.get(i).setnAuth(auth);
	
		}
		*/
		for (j=0;j<dbEntries.size();j++) {
		//List<RevCommit> comList = dbEntries.get(j).getRelease().getCommitsOfRelease();
		List<RevCommit> comList = MainControl.myCommitsList;

		//System.out.println("comList= "+comList.size());
    	
		for (i=0;i<comList.size();i++) {
    		
    			
    			ObjectId oldTree = comList.get(i).getTree();
    			RevCommit parent = (RevCommit) comList.get(i).getParent(0).getId();
    			
    	    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	    	diffFormatter.setRepository( repository );
    	    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	    	diffFormatter.setDetectRenames(true);
    	    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	    	//System.out.println("\n\n\n");
    	    	
    	    	for( DiffEntry entry : entries ) {
    	    		
    	    		
    	    		if (entry.toString().contains(dbEntries.get(j).getFilename())) {
    	    			
    	    			count++;
    	    			//System.out.println("commit: "+commitList.get(j).getId()+"         date: "+commitList.get(j).getAuthorIdent().getWhen()+"         diffEntry: "+entry.toString()+"         count: "+count);
    	    			
    	    			
    	    		}
    	    	}
    			
    		}
			dbEntries.get(i).setNr(count);
		}
    	
    	
    	//System.out.println("file: "+filename+"   count (n commit del file)= "+count);
    	return;
    	
		
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
	
	


	public static int locTouched(List<RevCommit> releaseCommits) throws IOException {
		int locAdded = 0;
		int locDeleted = 0;
		int locTouched = 0;
		//System.out.println(" CACCAAAAAA ");
        //System.out.println("commits: "+releaseCommits.size());

		Ref head = repository.exactRef("HEAD");
		for (int i=0;i<releaseCommits.size();i++) {
			
			
			ObjectId oldTree = releaseCommits.get(releaseCommits.size()-1-i).getTree();
			RevCommit parent = (RevCommit) releaseCommits.get(releaseCommits.size()-1-i).getParent(0).getId();
			
	    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
	    	diffFormatter.setRepository( repository );
	    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
	    	diffFormatter.setContext(0);
	    	diffFormatter.setDetectRenames(true);
	    	List<DiffEntry> entries = new ArrayList <DiffEntry>();
	    	
	    	entries = diffFormatter.scan( parent.getTree(),oldTree );
	    	//System.out.println("\n\n\n");
	    	
	    	for( DiffEntry entry : entries ) {
	    		
	    		for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
		            locDeleted += edit.getEndA() - edit.getBeginA();
		            locAdded += edit.getEndB() - edit.getBeginB();
		        }
	    		
	    		locTouched= locAdded+locDeleted;
		    }
	    	
	    	return locTouched;
		}
		return locTouched;
	}
	
	//ADDED
	public static void locTouched2(Release release, String filename ) throws IOException {
		int locAdded = 0;
		int locDeleted = 0;
		int locTouched = 0;
	    System.out.println( "release: " +release.getIndex()+"     filename: "+filename);

		List<RevCommit> releaseCommits = release.getCommitsOfRelease();
		for (int i=1;i<releaseCommits.size();i++) {
			
			
			ObjectId oldTree = releaseCommits.get(releaseCommits.size()-i).getTree();
			RevCommit parent = (RevCommit) releaseCommits.get(releaseCommits.size()-i).getParent(0).getId();
			// Obtain tree iterators to traverse the tree of the old/new commit
			ObjectReader reader = repository.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset( reader, oldTree );
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset( reader, parent.getTree() );
		
			// Use a DiffFormatter to compare new and old tree and return a list of changes
			DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
			diffFormatter.setRepository( repository);
			diffFormatter.setContext( 0 );
			diffFormatter.setDetectRenames(true);
			List<DiffEntry> entries = diffFormatter.scan( newTreeIter, oldTreeIter );
		
			// Print the contents of the DiffEntries
			for( DiffEntry entry : entries ) {
				
				//&&entry.getChangeType().toString().equals("ADD")
				if (entry.toString().contains(filename)) {
					System.out.println( "-------------------------------" );
					System.out.println("commit: "+releaseCommits.get(i) );
					System.out.println("entry: "+ entry );
					FileHeader fileHeader = diffFormatter.toFileHeader( entry );
					List<? extends HunkHeader> hunks = fileHeader.getHunks();
					
					for( HunkHeader hunk : hunks ) {
					    System.out.println( hunk );
					    System.out.println( "new lines: "+hunk.getNewLineCount() );
					    System.out.println( "new startLine: "+hunk.getNewStartLine());
				    
				  }
				}
			}
		}
	}
									//Release release, String filename
	public static void locTouched3(List<Data> dbEntries) throws IOException {
		
		int locAdded;
		int locDeleted;
		int locTouched;
		int churn;
		
	    //System.out.println( "release: " +release.getIndex()+"     filename: "+filename);
		List<RevCommit> comList;
		RevWalk rw = new RevWalk(repository);

		for (int i=0;i<dbEntries.size();i++) {	//prendo file
			
			locAdded = 0;
			locDeleted = 0;
			locTouched = 0;
			churn=0;
			
			comList=dbEntries.get(i).getRelease().getCommitsOfRelease();
			
			for (int j=0;j<comList.size();j++) {
				
			
				ObjectId oldTree = comList.get(j).getTree();
				RevCommit parent = (RevCommit) comList.get(j).getParent(0).getId();
				// Obtain tree iterators to traverse the tree of the old/new commit
				ObjectReader reader = repository.newObjectReader();
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset( reader, oldTree );
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset( reader, parent.getTree() );
			
				// Use a DiffFormatter to compare new and old tree and return a list of changes
				DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
				diffFormatter.setRepository( repository);
				diffFormatter.setContext( 0 );
				diffFormatter.setDetectRenames(true);
				List<DiffEntry> entries = diffFormatter.scan( newTreeIter, oldTreeIter );
			
				// Print the contents of the DiffEntries
				for( DiffEntry entry : entries ) {
					
					//&&entry.getChangeType().toString().equals("ADD")
					if (entry.toString().contains(dbEntries.get(i).getFilename())) {
						/*
						System.out.println( "-------------------------------" );
						System.out.println("commit: "+releaseCommits.get(i) );
						System.out.println("entry: "+ entry );
						*/
						FileHeader fileHeader = diffFormatter.toFileHeader( entry );
						for (Edit edit : diffFormatter.toFileHeader(entry).toEditList()) {
							//System.out.println("edit type: "+ edit.getType() );
				            locDeleted += edit.getEndA() - edit.getBeginA();
				            //System.out.println("locDeleted= "+locDeleted);
				            locAdded += edit.getEndB() - edit.getBeginB();
				            //System.out.println("locAdded= "+locAdded);
				        }
			    		
			    		locTouched= locAdded+locDeleted;
			    		//System.out.println("locTouched= "+locTouched);
					}
				}
			}
		}
	}
	
	/*	
public static void prova(ArrayList<Release> releases,Repository repository) throws IOException {
		
		
		
		RevWalk rw = new RevWalk(repository);
		
		

		//mi prendo tutti i commit nella release e mi calcolo le metriche per ogni file delal release
		for(int i = 0;i<releases.size();i++) {
			//per ogni file nella release
			for(int k = 0;k<releases.get(i).getListFile().size();k++) {
				FileJava2 file = releases.get(i).getListFile().get(k);
				String fileName = file.getName();
				System.out.println("fileName = " + fileName);
				int locAdded = 0;
				int locTouched = 0;
				int locDeleted = 0;
				int locModify = 0;
				int churn = 0;
				for(int j = 0;j<releases.get(i).getListCommit().size();j++) {
					RevCommit commit = releases.get(i).getListCommit().get(j);

					RevCommit parent = null;
					
					if(commit.getParentCount() !=0) {
						parent = (RevCommit)commit.getParent(0);
						//System.out.println("DENTRO IF --> il parent è  = " + parent);

					}
						//System.out.println("parent = " + parent);
						DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
						df.setRepository(repository);
						df.setDiffComparator(RawTextComparator.DEFAULT);
						df.setDetectRenames(true);
						List<DiffEntry> diffs;
						if(parent != null) {
								diffs = df.scan(parent.getTree(), commit.getTree());
						}
						else {
							//System.out.println("Il commit è : " + commit.getId().getName() + "\tnon c'è parent");
							ObjectReader reader = rw.getObjectReader();
							 diffs =df.scan(new EmptyTreeIterator(),
							        new CanonicalTreeParser(null, reader, commit.getTree()));
						}
						for (DiffEntry diff : diffs) {     							// For each file changed in the commit
							if(diff.getOldPath().equals(fileName) ||diff.getNewPath().equals(fileName) ){
								//System.out.println("\ncommit = " + commit.getId().getName());
								//System.out.println("tipo di diff: " + diff.getChangeType());
								//System.out.println("il parent è  = " + commit.getParent(0));

								for(Edit edit : df.toFileHeader(diff).toEditList()) {
									//System.out.println("edit.getType() = " + edit.getType());
									//if (edit.getType() == Edit.Type.INSERT) {
										locAdded += edit.getEndB() - edit.getBeginB();
										//locDeleted += edit.getEndA() - edit.getBeginA();

										//System.out.println("locAdded = " + locAdded + ",\tlocDeleted = " + locDeleted +"\n");
										//System.out.println(edit.getLengthB() - edit.getLengthA());
										/*
										System.out.println("edit.getEndB() = " + edit.getEndB());
										System.out.println("edit.getBeginB() = " + edit.getBeginB());
										
										
										System.out.println("edit.getEndA() = " + edit.getEndA());
										System.out.println("edit.getBeginA() = " + edit.getBeginA());
										 */
										
										//locTouched += edit.getEndB() - edit.getBeginB();
									//} else if (edit.getType() == Edit.Type.DELETE) {
						//				locDeleted += edit.getEndA() - edit.getBeginA();
										//System.out.println("locAdded = " + locAdded + ",\tlocDeleted = " + locDeleted +"\n");
										

										//locTouched += edit.getEndA() - edit.getBeginA();
								//	} else if (edit.getType() == Edit.Type.REPLACE) {
										//locModify += edit.getEndA() - edit.getBeginA();
									//	locModify += edit.getEndB() - edit.getBeginB();
										//locModify += edit.getEndA() - edit.getBeginA();
										//locAdded += edit.getEndB() - edit.getBeginB();
									//	locDeleted += edit.getEndA() - edit.getBeginA();

										//System.out.println("locAdded = " + locAdded + ",\tlocDeleted = " + locDeleted +"\n");


										//locTouched += edit.getEndA() - edit.getBeginA();
	
	/*								}
								}
								
								//System.out.println("locAdded = " + locAdded + ",\tlocDeleted = " + locDeleted +",\tlocModify = " +locModify +"\n\n\n\n\n\n\n");
								//System.out.println("locDeleted = " + locDeleted);

							}
						//}
					//}
						//System.out.println("----------------\n\n");
				}
			
				locTouched = locAdded+locDeleted;
				//cambio info file
				file.setLOC_added(locAdded);
				file.setLOC_touched(locTouched);
				file.setChurn(churn);
			}
			
		}
	}

	*/
	public static void prova2(List<Data> dbEntries) throws IOException {
		
		int nr, locAdded, locTouched, locDeleted, churn, chgGetSize;	
		int locAddedOnce, churnOnce, chgGetSizeOnce;	
		Integer max;
		int avg;
		
		ProportionMethod computeAvg = new ProportionMethod();
		
		List<RevCommit> comList;
		List<Integer> churnList=  new ArrayList <Integer>();
		List<Integer> locAddedList = new ArrayList <Integer>();
		List<String> numFiles = new ArrayList <String>();
		List<Integer> chgSetSizeList = new ArrayList <Integer>();
		List<PersonIdent> authors = new ArrayList <PersonIdent>();
		
		RevWalk rw = new RevWalk(repository);
		
		//mi prendo tutti i commit nella release e mi calcolo le metriche per ogni file delal release
		for(int i = 0;i<dbEntries.size();i++) {
			
			nr = 0;
			locAdded = 0;
			locTouched = 0;
			locDeleted = 0;
			churn = 0;
			chgGetSize = 0;
			
			comList=dbEntries.get(i).getRelease().getCommitsOfRelease();
			
			
			//List<Integer> locAddedList = new ArrayList <Integer>();
			
			//per ogni file nella release
			for(int j = 0;j<comList.size();j++) {
									
					RevCommit commit = comList.get(j);
	
					RevCommit parent = null;
					
					if(commit.getParentCount() !=0) {
						parent = (RevCommit)commit.getParent(0);
						//System.out.println("DENTRO IF --> il parent è  = " + parent);
	
					}
						//System.out.println("parent = " + parent);
						
					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
					df.setRepository(repository);
					df.setDiffComparator(RawTextComparator.DEFAULT);
					df.setDetectRenames(true);
					List<DiffEntry> entries;
					
						if(parent != null) {
							entries = df.scan(parent.getTree(), commit.getTree());
						}
						else {
							
							//System.out.println("Il commit è : " + commit.getId().getName() + "\tnon c'è parent");
							ObjectReader reader = rw.getObjectReader();
							entries =df.scan(new EmptyTreeIterator(),
							        new CanonicalTreeParser(null, reader, commit.getTree()));
						}
						
						
						//differenze tra il commit e il parent
						for (DiffEntry diffEntry : entries) { 
							
							// For each file changed in the commit 
							//(per ogni differenza/cambiamento presente nel commit) -> vedo se un commit contiene file
							String diffFileName;
							
							if (diffEntry.toString().contains(".java")) {
							
								
								if (diffEntry.getChangeType().toString().equals("RENAME") || (diffEntry.getChangeType().toString().equals("DELETE"))){
									diffFileName = diffEntry.getOldPath();	
								}
								else {
									diffFileName = diffEntry.getNewPath();
								}
								
								String rename = MainControl.verifyRename(diffFileName);
								String fileToUse = null;
								
								if (rename!=null) {
									fileToUse=rename;
								}
								else {
									fileToUse=diffFileName;
								}
									
								//if(diffEntry.getOldPath().equals(dbEntries.get(i).getFilename()) ||diffEntry.getNewPath().equals(dbEntries.get(i).getFilename()) ){
								if (fileToUse.equals(dbEntries.get(i).getFilename())) {
										
									nr++;
									//aggiusta autori + file = dbENtry.getfilename ?
						   			//se la lista di autori non contiene l'autore del commit corrente, lo aggiungo
			    	    			if (authors.contains(comList.get(j).getAuthorIdent())==false) {
			    	    				authors.add(comList.get(j).getAuthorIdent());
			    	    			}
			    	    			
			    	    			
									//per ogni modifica (edit) presente nel file
									for(Edit edit : df.toFileHeader(diffEntry).toEditList()) {
			
											locAddedOnce = edit.getEndB() - edit.getBeginB();
											locAdded += edit.getEndB() - edit.getBeginB();
											//locAddedList.add(locAdded);
											locAddedList.add(locAddedOnce);
				
											locDeleted += edit.getEndA() - edit.getBeginA();	//endA=BeginB
		
											//churn = locAdded- locDeleted;
											//churnList.add(churn);
											
											churnOnce = locAdded- locDeleted;
											churnList.add(churnOnce);
											
									}
									
									//prendo i path tutti i file toccati dal commit 
									//cosi se contengono il file che sto esaminando, vedo quanti ne ho committati con lui
									if (diffEntry.getChangeType().toString().equals("DELETE")) {
										numFiles.add(diffEntry.getOldPath());
									}
									else {	//se ho MODIFY, ADD o RENAME, aggiungo newPath del file della diffEntry
										numFiles.add(diffEntry.getNewPath());
									}
								
								}
								
							
							}
						}

						
						if (numFiles.contains(dbEntries.get(i).getFilename())) {
							
							chgGetSize = chgGetSize+numFiles.size()-1;	//numero dei file commitati insieme al file "dbEntry.get(i).getFilename()"
							
							chgGetSizeOnce = numFiles.size()-1;
							chgSetSizeList.add(chgGetSizeOnce);
						}
	
				} //end comList
			
				dbEntries.get(i).setNr(nr);
				dbEntries.get(i).setnAuth(authors.size());
				
				//prendo loc touched per un file (dbEntry.get(i).getFilename) in un commit di una release, e vado agli altri 
				//commit della stessa release per vedere le modifiche apportate sempre a quello stesso file
	
				//dopo che ho scorso tutti i commit di una release che contengono un certo file, calcolo :

				// ============= LOC TOUCHED , LOC ADDED , MAX&AVG
				max = maxElement(locAddedList);			//devono essere quelle volta per volta (non somma)
				avg= computeAvg.calculateAverage(locAddedList);
				
				locTouched = locAdded+locDeleted;
				
				dbEntries.get(i).setMax_locAdded(max);
				dbEntries.get(i).setAvg_locAdded(avg);
				dbEntries.get(i).setLocAdded(locAdded);
				dbEntries.get(i).setLocTouched(locTouched);
				
				// ============= CHURN, MAX&AVG
				churn = locAdded- locDeleted;
				
				max = maxElement(churnList);
				avg= computeAvg.calculateAverage(locAddedList);
				
				dbEntries.get(i).setChurn(churn);
				dbEntries.get(i).setMax_churn(max);
				dbEntries.get(i).setAvg_churn(avg);

				
				// ============= chgSetSize, MAX&AVG
				max = maxElement(chgSetSizeList);
				avg= computeAvg.calculateAverage(chgSetSizeList);
				
				dbEntries.get(i).setChgSetSize(chgGetSize);
				dbEntries.get(i).setMax_chgSetSize(max);
				dbEntries.get(i).setAvg_chgSetSize(avg);
				
				// ============= CLEAR LISTS =============
				
				locAddedList.clear();
				churnList.clear();
				chgSetSizeList.clear();
				numFiles.clear();
				authors.clear();
				
				
			} //end release -> cambio file
					
		}
	
	

	public static void nFix(List<Data> dbEntries) {
		//numero di bug fixati in quel file
		//devo vedere se nei file della release il commit.getFulMessage contiene "%fix%"
		
		
		
	}
	
	public static int maxElement(List<Integer> list) {
		
		if (list.size()>0) {
			int max=list.get(0);
			for (int i=0;i<list.size();i++) {
				
				if (list.get(i)>max) {
					max=list.get(i);
				}
				
			}
			return max;
		}
		else {
			return 0;
		}

		
	}
	
	



}
	
	

