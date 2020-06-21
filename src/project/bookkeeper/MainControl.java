package project.bookkeeper;

import java.io.File;
import java.io.IOException;
//import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
//import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
import java.util.List;
//import java.util.Map;
//import java.util.TreeMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
//import org.eclipse.jgit.internal.storage.file.PackBitmapIndexRemapper.Entry;
//import org.eclipse.jgit.lib.IndexDiff;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
//import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONException;

public class MainControl {

	public static List <String> classesList;
	public static List<Ticket> ticketlist;
	public static List<RevCommit> myCommitsList;	//lista dei commit relativi ai ticket
	public static List <Release> allReleases;
	public static List <Release> releases;
	public static List<Data> entries;
	public static List<RenamedFile> renames;
	public static String path="D:\\Cecilia\\Desktop\\bookkeeper";
	
	public static int halfRelease;
	public static Repository repository;
	
	public static List<Rename> renameList;
	
	public static void main(String[] args) throws IOException, JSONException, NoHeadException, GitAPIException {
		
		int numDefects;

		List <Ticket> good = new ArrayList<Ticket>();		//tickets con AV regolare che utilizzo per calcolare proportion
		List <Ticket> wrong = new ArrayList<Ticket>();		//tickets senza IV (AV), e quindi di cui calcolo predictedIV
		classesList = new ArrayList<String>();
		entries= new ArrayList<Data>();
		
		Git git= Git.open(new File(path));
    	repository=git.getRepository();
    	
    	releases=GetJiraInfo.getReleaseInfo();
    	halfRelease=setHalfRelease();
    	
    	ticketlist= GetJiraInfo.getTicketInfo( releases);	//ticketList viene inizializzata in getTicketInfo
    	numDefects=ticketlist.size();
    	

    	setOvFvIv();
    	GetGitInfo.getFilesPerRelease(git, entries);
    	//printData(entries);
    	
    	
    	//mi salvo tutti i commits del log di bookkeeper in commitsIDlist e intanto li aggiungo ai relativi ticket
    	myCommitsList=GetGitInfo.getCommitsID(git, ticketlist, path);	//va dopo getTicketInfo perché senno non conosco ticketID
		//System.out.println("commits dim: "+myCommitsList.size());

		renameList=checkRename(entries, git);
    	//GetJiraInfo.printTicketList(ticketlist);
 	
    	//classesList=GetGitInfo.listAllFiles(repository);	//listAllFiles prende tutti i file Java della repo
	    	
    	renames = new ArrayList<RenamedFile>();
    	addJavaFiles (repository);	//questo li mette nei vari ticket (prende quelli toccati dai commit di un ticket)
    	
    	
    	//entries= cartesiano();
    	//GetGitInfo.printList(classesList);
    	
    	
    	ProportionMethod proportionMethod = new ProportionMethod();
    	proportionMethod.checkDates2(good, wrong, halfRelease);
    	proportionMethod.proportion(good, wrong, numDefects);
    	proportionMethod.defineAV(halfRelease);
    	
    	//finalPrintTickets();
    	System.out.println("ticketlist size: "+ticketlist.size());

    	
    

    	bugsPerRelease();
    	
    	
    	//GetJiraInfo.printTicketList(ticketlist);

    	//CsvWriter.write(entries);
    	
    	//ComputeMetrics.NR();
    	//System.out.println("commitList size: "+myCommitsList.size());
    	
    	Metrics.calculate();
    	CsvWriter.write(entries);

	
	}
	
	public static List<Rename> checkRename(List<Data> dbEntries, Git git) throws IOException, NoHeadException, GitAPIException {
		
		List<Rename> renameList = new ArrayList<Rename>();		
		

    	//get Commits
    	Iterable<RevCommit> log = git.log().call();
    	List<RevCommit> logCommitList = new  ArrayList<RevCommit>();
    	
    	for (RevCommit commit : log) {
            
    		//checkCommitDate=compareDates(commit);
    		//if (checkCommitDate==true) {
                logCommitList.add(commit);
    		//}
            
      
   
        }
    	
    	System.out.println("logCommitslist : "+logCommitList.size());
		RevWalk rw = new RevWalk(repository);
		
		//for (int i=0;i<dbEntries.size();i++) {
			//comList=dbEntries.get(i).getRelease().getCommitsOfRelease();
			//System.out.println("commits dim: "+logCommitList.size());

			//dopo aggiungi file per ogni ticket
			for (int j=0;j<logCommitList.size();j++) {
				
				RevCommit commit = logCommitList.get(j);
				RevCommit parent = null;
				
				if(commit.getParentCount() !=0) {
					parent = (RevCommit)commit.getParent(0);
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
					//System.out.println("sono in diffs ");
						
						String changeType = diffEntry.getChangeType().toString();
						String oldPath = diffEntry.getOldPath();
						String newPath = diffEntry.getNewPath();
						
						if(oldPath.contains(".java"))	{
							if (diffEntry.getChangeType().toString().equals("RENAME") ) {
								//System.out.println("newpath: "+renameList.get(0).getNewpath());
								//System.out.println("oldpaths: "+renameList.get(0).getOldpaths());
								
								if (renameList.size()==0) {
									createRename(renameList,oldPath,newPath);
									
								}
								
								else {
									//System.out.println(" list size >0 ");
									//mi ritorna l'ultimo rename 
									checkRenameFiles(renameList,oldPath,newPath);	
						
									
								}
	
								
					
							
							}
						}
						
						
						
					}
					

			}
			/*
			for (int j=0; j<renameList.size();j++) {
				System.out.println("newpath: "+renameList.get(j).getNewpath());
				System.out.println("oldpaths: "+renameList.get(j).getOldpaths());
				System.out.println(" -------------------- ");


			}
			System.out.println("Lista renames: " + renameList.size());
			*/
			/*
			//aggiorno il path di ogni file in ogni release con l'ultimo rename
			for (int i=0;i<entries.size();i++) {
				
				List<String> filesOfRelease=entries.get(i).getRelease().getFilesOfRelease();
				
				for (int j=0;j<filesOfRelease.size();j++) {
					
					String filename=filesOfRelease.get(j);
					
					for (int k=0;k<renameList.size();k++) {
						
						for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
							
							String renameFile = renameList.get(k).getOldpaths().get(m);
							if (renameFile.equals(filename)) {
								renameList.get(k).setNewpath(renameFile);
								
							}
						}
					}
				}
			}
			*/
			
			//aggiorno il path di ogni file in ogni release con l'ultimo rename
			for (int i=0;i<entries.size();i++) {
				
				List<String> filesOfRelease=entries.get(i).getRelease().getFilesOfRelease();
				
				for (int j=0;j<filesOfRelease.size();j++) {
					
					String filename=filesOfRelease.get(j);
					
					for (int k=0;k<renameList.size();k++) {
						
						for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
							
							String renameFile = renameList.get(k).getOldpaths().get(m);
							
							if (renameFile.equals(filename)) {
								
								filesOfRelease.set(j, renameList.get(k).getNewpath());								
								
							}
						}
					}
				}
			}
			
			for (int i=0;i<ticketlist.size();i++) {
				
				List<String> relatedJavaFiles=ticketlist.get(i).getRelatedJavaFiles();
				
				for (int j=0;j<relatedJavaFiles.size();j++) {
					
					String filename=relatedJavaFiles.get(j);
					
					for (int k=0;k<renameList.size();k++) {
						
						for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
							
							String renameFile = renameList.get(k).getOldpaths().get(m);
							
							if (renameFile.equals(filename)) {
								
								relatedJavaFiles.set(j, renameList.get(k).getNewpath());								
								
							}
						}
					}
				}
			}
			
			
			
			
			return renameList;
			
		}
		
		
	
	
	public static void createRename(List<Rename> renameList, String oldP, String newP) {
		
		//System.out.println(" sono in create");
		List<String> oldpaths=new ArrayList<String>();
		oldpaths.add(oldP);
		oldpaths.add(newP);
		

		Rename rename= new Rename(newP,oldpaths);
		renameList.add(rename);
		/*
		Rename file = new Rename();
		file.getOldpaths().add(oldP);
		file.getOldpaths().add(newP);
		file.setNewpath(file.getOldpaths().get(file.getOldpaths().size()-1));
		*/
		//System.out.println("newpath: "+file.getNewpath()+"   oldpaths: "+file.getOldpaths());

		return ;
		
		
	}
	
	public static void checkRenameFiles(List<Rename> renameList, String myOldpath, String myNewpath ) {
		//se myOldpath sta già in una renameFile, mi torna l'ultimo rename (newpath)
		int i;
		int isThere=0;
		Rename newRelease;
		
		String newPath= null;
		List <String> oldpathsList;
		
		//devo vedere se oldpath sta dentro la lista degli oldPathsList
		//scorro renameList
		//vedo se nel rename[i] ci sta oldpath
		//
		
		
		//se myOldpath è contenuto già tra gli oldpaths di un altro renamedFile,
		//aggiungo myNewpath alla lista di olpaths e lo setto come newpath di quel renamedFile
		for (i=0;i<renameList.size();i++) {
			oldpathsList=renameList.get(i).getOldpaths();
			
			if (oldpathsList.contains(myOldpath)) {
				isThere=1;
				//System.out.println(" isThere");
				oldpathsList.add(myNewpath);
				renameList.get(i).setNewpath(myNewpath);
				
				break;
				
			}
			
			
		}
		
		if (isThere==0) {
			createRename(renameList,myOldpath, myNewpath);
			
		}

	}
	
	public static String verifyRename(String path) {
		
		for (int i=0;i<renameList.size();i++) {
			if (renameList.get(i).getOldpaths().contains(path)) {
				return renameList.get(i).getNewpath();
			}
		}
		return null;
		
		
	}
	

	
	public static void printData(List<Data> dbEntry) {
		
		int i;
    	System.out.println("DataList");
    	
    	System.out.println("entries size: "+entries.size());

		for(i=0;i<entries.size();i++) {
	    	System.out.println("release: "+entries.get(i).getRelease()+"       file: "+entries.get(i).getFilename());

		}
		
	}


    public static int setHalfRelease() {

    	int dim, res;
    	
    	dim=releases.size();
		res=dim/2;
		return res;
    	
    }

	
	
	public static List<String> getJavaFiles(Repository repository, RevCommit commit) throws NoHeadException, GitAPIException, MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		//recupero i file toccati da un certo commit (passato come parametro)
	
		int i, j;
		

		List<String> javaFilesPerTicket= new ArrayList<String>();
		
		ObjectId oldTree = commit.getTree();
		RevCommit parent = (RevCommit) commit.getParent(0).getId();
		
    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	diffFormatter.setRepository( repository );
    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	diffFormatter.setDetectRenames(true);
    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	//System.out.println("\n\n\n");
    	
    	for( DiffEntry diffEntry : entries ) {
    		
    		if (diffEntry.toString().contains(".java")) {
    		
    			//javaFilesPerTicket.add(entry.toString());
    			
    			String diffFileName;
    			
				if (diffEntry.getChangeType().toString().equals("RENAME") || (diffEntry.getChangeType().toString().equals("DELETE"))){
					diffFileName = diffEntry.getOldPath();	
				}
				else {
					diffFileName = diffEntry.getNewPath();
				}
				
				String rename = verifyRename(diffFileName);
				String fileToUse = null;
				
				if (rename!=null) {
					fileToUse=rename;
				}
				else {
					fileToUse=diffFileName;
				}
    			
				javaFilesPerTicket.add(fileToUse);
				
				/*
    			if (entry.getChangeType().toString().equals("MODIFY")){
    				javaFilesPerTicket.add(entry.getNewPath());
    			}
    			
    			if (entry.getChangeType().toString().equals("ADD")){
    				javaFilesPerTicket.add(entry.getNewPath());
    			}
    			
    			
    			if (entry.getChangeType().toString().equals("DELETE")) {
    				javaFilesPerTicket.add(entry.getOldPath());
        			checkDeletes(commit,entry);

    			}
    			
    			if (entry.getChangeType().toString().equals("RENAME")) {

    				javaFilesPerTicket.add(entry.getOldPath()+"->"+entry.getNewPath());
        			checkRenames(commit,entry);

    			}
				*/
    		}
    	
    	}
    	
    	return javaFilesPerTicket;
		
    	
    }


	public static void checkRenames(RevCommit commit, DiffEntry entry) {
		
		LocalDateTime commitDate;
		String oldpath, newpath;
		int i, j, release;		
		
    	//System.out.println("--------- in checkRenames: ");
    	
    	commitDate= commit.getAuthorIdent().getWhen().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		oldpath=entry.getOldPath();
		newpath=entry.getNewPath();
		
		/* esempio del CICLO FOR:
		 * Abbiamo le seguenti rinominazioni:
		 * 			O1->N1 
		 * 			  +		 =>   O1->N2 
		 * .......  N1->N2
		 * 
		 * NB: O1->N1 hanno c=1     ;      N1->N2 hanno c=1   ===> ma   O1->N2 ha c=2 perché è il secondo rename per O1 (indiretto) !!!
		 * 
		 * 
		 */
		
		for (i=0;i<renames.size();i++) {
			
			if (oldpath.equals(renames.get(i).getNewPath())){
				String oldTemp=renames.get(i).getOldPath();
				RenamedFile file= new RenamedFile(oldTemp);
				file.setNewPath(newpath);
				file.setCount(renames.get(i).getCount()+1);
			
			}
		}
		
		
		RenamedFile file= new RenamedFile(oldpath);
		file.setNewPath(newpath);
		file.setCount(1);
		
		for (j=0;j<releases.size();j++) {
			
			//se commitDate < releaseDate (viene prima), determino la release in cui file è ASSENTE
			if (commitDate.compareTo(releases.get(j).getDate())<0) {
				
				release=releases.get(j).getIndex();
				file.setRelease(release);
				
		    	//System.out.println("release dopo rename : "+ release);
		    	
				break;
			}
		}
		
		renames.add(file);
		
    	//System.out.println("old path: "+ file.getOldPath());
    	//System.out.println("new path: "+ file.getNewPath());
    	//System.out.println("count: "+ file.getCount());

		
	}
	
	public static void checkDeletes(RevCommit commit, DiffEntry entry) {
		
		int i;
		String oldpath;
		
    	//System.out.println("-------------------------------------------");

		oldpath= entry.getOldPath();

		//controllo che file eliminato non sia presente in classesList
		for (i=0;i<classesList.size();i++) {
			if (oldpath.equals(classesList.get(i))) {
				
		    	//System.out.println("================ Il file "+ entry.getOldPath()+" è presente in classesList (NON è STATO ELIMINATO)");
		    	return;
			}
		}
		//se non lo è, lo aggiungo a classesList
		classesList.add(oldpath);

	}

	public static void addJavaFiles(Repository repo) throws NoHeadException, MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, GitAPIException, IOException {
		//aggiungo a ogni ticket la lista dei file toccati dai commit contenenti l'id del ticket 
		
		int i, j;
		

    	for (i=0;i<ticketlist.size();i++) {
    		
    		for (j=0;j<myCommitsList.size();j++) {
    			
    			if (myCommitsList.get(j).getFullMessage().contains(ticketlist.get(i).getTicketID()+":")){
    				//prendi file modificati da questo commit e aggiungili al ticket
    				
    				ticketlist.get(i).setRelatedJavaFiles(getJavaFiles(repo, myCommitsList.get(j)));
    				
    				/*
					System.out.println("ticketID: "+ticketlist.get(i).getTicketID());
					System.out.println("commit: "+myCommitsList.get(j).getId());
					System.out.println("related files: "+ ticketlist.get(i).getRelatedJavaFiles());
					*/

    			}
    				
    		}
    		
    		
    	}
	
		
	}
    
    
	
	public static void sortListByDate(List<RevCommit> list) {
		//voglio una lista delle date dei commits che sia ordinata

	
		Collections.sort(list, new Comparator<RevCommit>() {
			  public int compare(RevCommit o1, RevCommit o2) {
			      return o1.getAuthorIdent().getWhen().compareTo(o2.getAuthorIdent().getWhen());
			  }
		});

        /*
		for (RevCommit commit : list) {
			 System.out.println(commit.getAuthorIdent().getWhen());
		}
		*/
		
		
	}
	
	public static void setOvFvIv() {
		
		/* OV è quella la cui data è la prima data delle release successiva a created
		 * FV è quella la cui data è la prima data delle release successiva a resolutioDate
		 *  faccio confronto con date, ma poi in OV, FV, AV ci metto INDEX
		 */
		
		int i, j;
		
		
		
		for (i=0;i<ticketlist.size();i++) {
			
			
			
			//OV
			for (j=0;j<releases.size();j++) {
				
				//se created < releaseDate (viene prima), assegno OV = releaseDate
				if (ticketlist.get(i).getCreatedDate().compareTo(releases.get(j).getDate().toLocalDate())<0) {
					
					ticketlist.get(i).setOV(releases.get(j).getIndex());
					
					break;
				}
				
				
			}
			
			//FV
			for (j=0;j<releases.size();j++) {
				
				//se resolution < releaseDate (viene prima), assegno FV = releaseDate
				if (ticketlist.get(i).getResolutionDate().compareTo(releases.get(j).getDate().toLocalDate())<0) {
					
					ticketlist.get(i).setFV(releases.get(j).getIndex());
					
					break;
				}
				
				
			}
						
			//IV
			ticketlist.get(i).setIV(ticketlist.get(i).getAV().get(0));	
			
			

			//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+" 		OV: "+ticketlist.get(i).getOV()+" 		FV: "+ticketlist.get(i).getFV()+" 		IV: "+ticketlist.get(i).getIV());

		//System.out.println("ticket: "+ticket.get(i).getTicketID()+"		created: "+ticket.get(i).getCreatedDate()+"		resolution: "+ticket.get(i).getResolutionDate()+" 		OV: "+ticket.get(i).getOV()+" 		FV: "+ticket.get(i).getFV()+" 		IV: "+ticket.get(i).getIV());

			//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+"		created: "+ticketlist.get(i).getCreatedDate()+"		resolution: "+ticketlist.get(i).getResolutionDate()+" 		OV: "+ticketlist.get(i).getOV()+" 		FV: "+ticketlist.get(i).getFV()+" 		IV: "+ticketlist.get(i).getIV());


			
		}
		/*
		System.out.println("\nRIMUOVO:\n");

		for (i=0;i<ticketlist.size();i++) {
			
			if (ticketlist.get(i).getIV()>halfRelease) {
				//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+" 		OV: "+ticketlist.get(i).getOV()+" 		FV: "+ticketlist.get(i).getFV()+" 		IV: "+ticketlist.get(i).getIV());
				ticketlist.remove(i);
				i--;
			}
			
		}
		
		System.out.println("ticket totali dopo rimozione IV>7: "+ticketlist.size());
		System.out.println("\n\n");
		*/
		
	}
	

	public static void finalPrintTickets() {
		
		int i;
		
		for (i=0;i<ticketlist.size();i++) {
			
			
			System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV()+"             AV: "+ticketlist.get(i).getAV());
			//System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());

			
		}
	}

	
	public static List<Data> cartesiano() {
		//voglio creare prime due colonne del data set; releaseIndex e filename.
		//poi, con un altra funzione valuto se quel file è buggy o no in quella release
		
		int i,j,k;
		List<Data> dbEntry = new ArrayList<Data>();
		
		for (i=0;i<halfRelease;i++) {
			
			for (j=0;j<classesList.size();j++) {
				
				for (k=0;k<renames.size();k++) {
					
					if (releases.get(i).getIndex()<renames.get(k).getRelease() && classesList.get(j).equals(renames.get(k).getOldPath())) {
						
						dbEntry.add(new Data(releases.get(i), renames.get(k).getNewPath()));
						
					}
				
					
				}
				
				dbEntry.add(new Data(releases.get(i), classesList.get(j)));
				
			}

		}
		
		return dbEntry;
			
	}
	

	
	
	
	public static void bugsPerRelease() {
		
		int i;
		
		//for (i=0;i<halfRelease;i++) {
		for (i=0;i<releases.size();i++) {
	
			isBuggyOrNot2(releases.get(i).getIndex());
			
		}
		
			
	}
	
	public static void computeBuggyness(int myRelease, String myFileName) {
		
		int i;
		for(i=0;i<entries.size();i++) {
			
			if (entries.get(i).getRelease().getIndex()==myRelease && entries.get(i).getFilename().contains(myFileName) ) { //ci dovrei mettere contais per RENAME
				
				entries.get(i).setBuggy("Y");
				
			}
			
			
		}

		
	}
	

	
	public static void isBuggyOrNot2(int releaseIndex) {
		//passo una release
		
		/*scorro tutti i ticket e controllo le AV
		 * se releaseIndex è presente nell'AV di quel ticket, allora:
		 * i relatedJavaFiles di quel ticket sono buggy in quella release (Data.Buggy=Yes)
		 */
		
		int i, j, k;
		//System.out.println("releaseIndex: "+releaseIndex);

		for (i=0;i<ticketlist.size();i++) {	//prendo un ticket
			//System.out.println("--------------");
			//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+"      AV size: "+ticketlist.get(i).getAV().size());

			if (ticketlist.get(i).getAV().size()>0) {	//se quel ticket ha AV
				for (j=0;j<ticketlist.get(i).getAV().size();j++) {	//scorro la lista delle AV di quel ticket
					
					if (ticketlist.get(i).getAV().get(j)==releaseIndex) {	//se tra le AV trovo la release x
						//allora vuol dire che quella release ha come classi buggy quelle riportate come attributo di quel ticket
						
						//System.out.println("file: "+ticketlist.get(i).getRelatedJavaFiles());
						//prendo relatedJavaFiles del ticket i e lo segno a buggy in quella release
						
						for (k=0;k<ticketlist.get(i).getRelatedJavaFiles().size();k++) {	//quindi prendo le classi di quel ticket
							
							//set buggyness (release, filename)
							//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+"      release: "+releaseIndex+"       nome file: "+ticketlist.get(i).getRelatedJavaFiles().get(k));

							computeBuggyness(releaseIndex, ticketlist.get(i).getRelatedJavaFiles().get(k));

						
							
						}
						
						
						
					}
					
				}
			}

			
		}

	}	
	
	
	public static void finalCount() {
		

		int i, count=0;
		for(i=0;i<ticketlist.size();i++) {
			
			if ( ticketlist.get(i).getAV().size()!=0){
				count++;
				
			}
		}
	}


}
	
	
