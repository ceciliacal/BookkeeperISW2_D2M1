package project.bookkeeper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

public class Metrics {
	
	//protected
	public static final List<RevCommit> commitList= MainControl.myCommitsList;
	public static final List<String> classesList= MainControl.classesList;
	//public static final Repository repository= MainControl.repository;
	
	  private Metrics() {
		    throw new IllegalStateException("Utility class");
		  }
	
	public static void calculate(Repository repository) throws IOException {
		
		List <Data> dbEntries = MainControl.entries;

		prova2(dbEntries, repository);

	}
	
	

	
	public static int loc(TreeWalk treewalk, Repository repository) throws IOException {
		
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
	
	

	public static void prova2(List<Data> dbEntries, Repository repository) throws IOException {
		
		int nr;
		int locAdded;
		int locTouched;
		int locDeleted; 
		int churn;
		int chgGetSize;	
		int locAddedOnce;
		int churnOnce;
		int chgGetSizeOnce;	
		Integer max;
		int avg;
		
		ProportionMethod computeAvg = new ProportionMethod();
		
		List<RevCommit> comList;
		List<Integer> churnList=  new ArrayList <>();
		List<Integer> locAddedList = new ArrayList <>();
		List<String> numFiles = new ArrayList <>();
		List<Integer> chgSetSizeList = new ArrayList <>();
		List<PersonIdent> authors = new ArrayList <>();
		
		RevWalk rw = new RevWalk(repository);
		
		//mi prendo tutti i commit nella release e mi calcolo le metriche per ogni file delal release
		for(int i = 0;i<dbEntries.size();i++) {
			
			nr = 0;
			locAdded = 0;
			locTouched= 0 ;
			locDeleted = 0;
			churn= 0 ;
			chgGetSize = 0;
			
			comList=dbEntries.get(i).getRelease().getCommitsOfRelease();
			
			
			
			//per ogni file nella release
			for(int j = 0;j<comList.size();j++) {
				
				//List<DiffEntry> entries = getDiffEntryList(rw, comList.get(j));
					
							
					RevCommit commit = comList.get(j);
	
					RevCommit parent = null;
					
					if(commit.getParentCount() !=0) {
						parent = (RevCommit) commit.getParent(0);
	
					}

						
					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
					df.setRepository(repository);
					df.setDiffComparator(RawTextComparator.DEFAULT);
					df.setDetectRenames(true);
					List<DiffEntry> entries;
					
						if(parent != null) {
							entries = df.scan(parent.getTree(), commit.getTree());
						}
						else {
							
							ObjectReader reader = rw.getObjectReader();
							entries =df.scan(new EmptyTreeIterator(),
							        new CanonicalTreeParser(null, reader, commit.getTree()));
						}
						
						
						//differenze tra il commit e il parent
						for (DiffEntry diffEntry : entries) { 
							
							// For each file changed in the commit 
							//(per ogni differenza/cambiamento presente nel commit) -> vedo se un commit contiene file
							
							String diffFileName;
							//String fileToUse;
							
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
						} //end entries

						
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
				
				dbEntries.get(i).setMaxLocAdded(max);
				dbEntries.get(i).setAvgLocAdded(avg);
				dbEntries.get(i).setLocAdded(locAdded);
				dbEntries.get(i).setLocTouched(locTouched);
				
				// ============= CHURN, MAX&AVG
				churn = locAdded- locDeleted;
				
				max = maxElement(churnList);
				avg= computeAvg.calculateAverage(locAddedList);
				
				dbEntries.get(i).setChurn(churn);
				dbEntries.get(i).setMaxChurn(max);
				dbEntries.get(i).setAvgChurn(avg);

				
				// ============= chgSetSize, MAX&AVG
				max = maxElement(chgSetSizeList);
				avg= computeAvg.calculateAverage(chgSetSizeList);
				
				dbEntries.get(i).setChgSetSize(chgGetSize);
				dbEntries.get(i).setMaxChgSetSize(max);
				dbEntries.get(i).setAvgChgSetSize(avg);
				
				// ============= CLEAR LISTS =============
				
				locAddedList.clear();
				churnList.clear();
				chgSetSizeList.clear();
				numFiles.clear();
				authors.clear();
				
				
			} //end release -> cambio file
					
		}
	
	public static String getFileToUse(DiffEntry diffEntry){
	//(per ogni differenza/cambiamento presente nel commit) -> vedo se un commit contiene file
		
		String diffFileName;	
		
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
		
		return fileToUse;

	}
	
	
	public static int maxElement(List<Integer> list) {
		
		if (!list.isEmpty()) {
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
	
	

