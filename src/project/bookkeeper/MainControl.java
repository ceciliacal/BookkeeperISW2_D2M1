package project.bookkeeper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONException;

public class MainControl {

	protected static List <String> classesList;
	protected static List<Ticket> ticketlist;
	protected static List<RevCommit> myCommitsList;	//lista dei commit relativi ai ticket
	protected static List <Release> releases;
	protected static List<Data> entries;
	protected static List<RenamedFile> renames;
	protected static final String path="D:\\Cecilia\\Desktop\\bookkeeper";
	
	public static int halfRelease;
	public static Repository repository;
	
	public static List<Rename> renameList;
	
	public static void main(String[] args) throws IOException, JSONException, GitAPIException {
		
		int numDefects;

		List <Ticket> good = new ArrayList<>();		//tickets con AV regolare che utilizzo per calcolare proportion
		List <Ticket> wrong = new ArrayList<>();		//tickets senza IV (AV), e quindi di cui calcolo predictedIV
		
		classesList = new ArrayList<>();
		entries= new ArrayList<>();
		
		Git git= Git.open(new File(path));

		
    	repository=git.getRepository();
    	
    	releases=GetJiraInfo.getReleaseInfo();
    	halfRelease=setHalfRelease();
    	
    	ticketlist= GetJiraInfo.getTicketInfo( releases);	//ticketList viene inizializzata in getTicketInfo
    	numDefects=ticketlist.size();
    	

    	setOvFvIv();
    	GetGitInfo.getFilesPerRelease(git, entries);
    	
    	
    	//mi salvo tutti i commits del log di bookkeeper in commitsIDlist e intanto li aggiungo ai relativi ticket
    	myCommitsList=GetGitInfo.getCommitsID(git, ticketlist );	//va dopo getTicketInfo perché senno non conosco ticketID
		//System.out.println("commits dim: "+myCommitsList.size());
    	
    	// ---> addJavaFiles (repository);	//questo li mette nei vari ticket (prende quelli toccati dai commit di un ticket)
    	
		//renameList=checkRename(entries, git);
    	//GetJiraInfo.printTicketList(ticketlist);
 	
    	//classesList=GetGitInfo.listAllFiles(repository);	//listAllFiles prende tutti i file Java della repo
	    	
    	renames = new ArrayList<RenamedFile>();
    	//addJavaFiles (repository);	
    	
    	
    	
    	renameList=checkRename(entries, git);
    	addJavaFiles (repository);

    	
    	//entries= cartesiano();
    	//GetGitInfo.printList(classesList);
    	//renameList=checkRename(entries, git);
    	
    	ProportionMethod proportionMethod = new ProportionMethod();
    	proportionMethod.checkDates(good, wrong);
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
	
	public static List<Rename> checkRename(List<Data> dbEntries, Git git) throws IOException, GitAPIException {
		
		List<Rename> renameList = new ArrayList<>();		

    	//get Commits
    	Iterable<RevCommit> log = git.log().call();
    	List<RevCommit> logCommitList = new  ArrayList<>();
    	
    	for (RevCommit commit : log) {
                logCommitList.add(commit);
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
						
						String oldPath = diffEntry.getOldPath();
						String newPath = diffEntry.getNewPath();
						
					//per ogni diffEntry, vedo se è un Rename di files java
					//if (diffEntry.getChangeType().toString().equals("RENAME")) {
						
						if (diffEntry.getChangeType().toString().equals("RENAME") && (diffEntry.getOldPath().contains(".java") || diffEntry.getNewPath().contains(".java")))	{

								boolean oPCheck = true;
								boolean nPCheck = true;
								
								//scorro la lista di rename (renameList) se è popolata per vedere se oldPath o newPath sono presenti
								for(Rename fileRenamed : renameList) {
									
									if(!fileRenamed.checkAlias(oldPath)) {
										oPCheck = false;
										if(fileRenamed.checkAlias(newPath)) {
											fileRenamed.getOldpaths().add(newPath);
											nPCheck = false;
										}
									}
									if(!fileRenamed.checkAlias(newPath)) {
										nPCheck = false;
										if(fileRenamed.checkAlias(oldPath)) {
											fileRenamed.getOldpaths().add(oldPath);
											oPCheck = false;
										}
									}
								}
								
								//se non sono presenti, creo nuovo renamedFile
								if(oPCheck && nPCheck) {
									
									Rename fileRenamed = new Rename();
									fileRenamed.getOldpaths().add(oldPath);
									fileRenamed.getOldpaths().add(newPath);
									renameList.add(fileRenamed);
								}
								
								//System.out.println("newpath: "+renameList.get(0).getNewpath());
								//System.out.println("oldpaths: "+renameList.get(0).getOldpaths());
						}
					}
											
						
				//}
					
			}
				
			
			System.out.println("Lista renames: " + renameList.size());
			
			for (int x=0; x<renameList.size();x++) {
				System.out.println("newpath: "+renameList.get(x).getNewpath());
				System.out.println("oldpaths: "+renameList.get(x).getOldpaths());
				System.out.println(" -------------------- ");


			}
			
			System.out.println("\n\n\n");
			
			
			// per ogni insieme di alias di ogni file calcolo il nome piu recente ad esso associato 
			// per ogni file in renameList, imposto il newpath di esso come il piu recente ad esso associato (perche sta nelle dbEntries)
			
			for (int i=0;i<dbEntries.size();i++) {
				
				String fileName = dbEntries.get(i).getFilename();
					
				for (int k=0;k<renameList.size();k++) {
						
						for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
							
							String renameFile = renameList.get(k).getOldpaths().get(m);
							
							if (renameFile.equals(fileName) || fileName.contains(renameFile) ) {
								
								//filesOfRelease.set(y, renameList.get(k).getNewpath());	
								renameList.get(k).setNewpath(renameFile);
								
							}
						}
					}
				}
					
			//aggiorno il path di ogni file in ogni release con l'ultimo rename
			for (int i=0;i<dbEntries.size();i++) {
				
				String fileName = dbEntries.get(i).getFilename();
					
				for (int k=0;k<renameList.size();k++) {
						
					for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
							
							String renameFile = renameList.get(k).getOldpaths().get(m);
							
							if (renameFile.equals(fileName) || fileName.contains(renameFile) ) {
															
								//System.out.println("renameFile: "+renameFile);
								//System.out.println("prima dbEntries.set : "+dbEntries.get(i).getFilename());
								dbEntries.get(i).setFilename(renameList.get(k).getNewpath());
								//System.out.println("dopo dbEntries.set : "+dbEntries.get(i).getFilename());
							}
						}
					}
			}
	
			return renameList;
	}
		

	
	
	
	
	public static String verifyRename(String path) {
		
		for (int i=0;i<renameList.size();i++) {
			if (renameList.get(i).getOldpaths().contains(path)) {
				return renameList.get(i).getNewpath();
			}
		}
		return null;
		
		
	}
	

	
	public static void printData() {
		
		int i;
    	Log.infoLog("DataList");
    	
    	Log.infoLog("entries size: "+entries.size());

		for(i=0;i<entries.size();i++) {
	    	Log.infoLog("release:"+entries.get(i).getRelease()+"       file: "+entries.get(i).getFilename());

		}
		
	}


    public static int setHalfRelease() {

    	int dim;
    	int res;
    	
    	dim=releases.size();
		res=dim/2;
		return res;
    	
    }

	
	
	public static List<String> getJavaFiles(Repository repository, RevCommit commit) throws IOException {
		//recupero i file toccati da un certo commit (passato come parametro)

		List<String> javaFilesPerTicket= new ArrayList<>();
		
		ObjectId oldTree = commit.getTree();
		RevCommit parent = (RevCommit) commit.getParent(0).getId();
		
    	DiffFormatter diffFormatter = new DiffFormatter( DisabledOutputStream.INSTANCE );
    	diffFormatter.setRepository( repository );
    	diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
    	diffFormatter.setDetectRenames(true);
    	List<DiffEntry> entries = diffFormatter.scan( parent.getTree(),oldTree );
    	
    	
    	
    	for( DiffEntry diffEntry : entries ) {
    		
    		if (diffEntry.getOldPath().contains(".java") || diffEntry.getNewPath().contains(".java")) {
    		    			
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
				
				if (diffEntry.getChangeType().toString().equals("RENAME")) {
					
					//System.out.println(" oldPath: "+diffEntry.getOldPath()+"       newPath: "+diffEntry.getNewPath()+"       fileToUse (preso da renameList): "+fileToUse);
					
				}
				
				javaFilesPerTicket.add(fileToUse);
				
    		}
    	
    	}
    	
    	return javaFilesPerTicket;
		
    	
    }


	public static void addJavaFiles(Repository repo) throws IOException {
		//aggiungo a ogni ticket la lista dei file toccati dai commit contenenti l'id del ticket 
		
		int i;
		int j;
		
		//System.out.println("\n\n\n in getJavaFiles ");
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
    

	
	public static void setOvFvIv() {
		
		/* OV è quella la cui data è la prima data delle release successiva a created
		 * FV è quella la cui data è la prima data delle release successiva a resolutioDate
		 *  faccio confronto con date, ma poi in OV, FV, AV ci metto INDEX
		 */
		
		int i;
		int j;
		
		
		
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
			
			Log.infoLog(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV()+"             AV: "+ticketlist.get(i).getAV());
			//System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());

			
		}
	}

	
	
	public static void bugsPerRelease() {
		
		int i;

		for (i=0;i<releases.size();i++) {
	
			checkBuggyness(releases.get(i).getIndex());
			
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
	

	
	public static void checkBuggyness(int releaseIndex) {
		//passo una release
		
		/*scorro tutti i ticket e controllo le AV
		 * se releaseIndex è presente nell'AV di quel ticket, allora:
		 * i relatedJavaFiles di quel ticket sono buggy in quella release (Data.Buggy=Yes)
		 */
		
		int i;
		int j;
		int k;
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
							

							//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+"      release: "+releaseIndex+"       nome file: "+ticketlist.get(i).getRelatedJavaFiles().get(k));

							computeBuggyness(releaseIndex, ticketlist.get(i).getRelatedJavaFiles().get(k));

						
							
						}
						
						
						
					}
					
				}
			}

			
		}

	}	
	


}
	
	
