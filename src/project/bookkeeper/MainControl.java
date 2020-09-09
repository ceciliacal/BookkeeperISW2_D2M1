package project.bookkeeper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
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
	protected static List<Data> entries;			//lista di output
	protected static List<Rename> renameList;
	
	public static final String PROJECTNAME="bookkeeper";
	protected static final String RENAME="RENAME";
	protected static int lastRelease;
	protected static int halfRelease;
	
	
	
	
	public static void main(String[] args) throws Exception {
		
		int numDefects;
		Repository repository;
		String path ="D:\\Cecilia\\Desktop\\"+PROJECTNAME;

		List <Ticket> ticketsWithAV = new ArrayList<>();			//tickets con AV regolare che utilizzo per calcolare proportion
		List <Ticket> ticketsNoAV = new ArrayList<>();		//tickets senza IV (AV), e quindi di cui calcolo predictedIV
		
		classesList = new ArrayList<>();
		entries= new ArrayList<>();
		
		Log.setupLogger();
		
		Git git= Git.open(new File(path));
    	repository=git.getRepository();
    	
    	//recupero release
    	releases=GetJiraInfoBoundary.getReleaseInfo(); 	
    	lastRelease=releases.get(releases.size()-1).getIndex();
    	  	
    	//recupero tickets
    	ticketlist= GetJiraInfoBoundary.getTicketInfo( releases);	//ticketList viene inizializzata in getTicketInfo
    	numDefects=ticketlist.size();
    	setOvFvIv();
    	
    	//prendo tutti i file presenti in ogni release
    	GetGitInfoBoundary.getFilesPerRelease(git, entries, repository);
    	
    	
    	//mi salvo tutti i commits del log di bookkeeper in commitsIDlist e intanto li aggiungo ai relativi ticket
    	myCommitsList=GetGitInfoBoundary.getCommitsID(git, ticketlist );	//va dopo getTicketInfo perché senno non conosco ticketID
 	
    	renameList=checkRename(entries, git, repository);
    	addJavaFiles (repository);

    	halfRelease=releases.size()/2;
   
    	//calcolo Proportion
    	ProportionControl proportionMethod = new ProportionControl();
    	proportionMethod.checkDates(ticketsWithAV, ticketsNoAV); 	
    	proportionMethod.proportion(ticketsWithAV, ticketsNoAV, numDefects);
    	proportionMethod.defineAV(halfRelease);

    	//calcolo buggyness
    	bugsPerRelease();
 
    	//calcolo metriche
    	MetricsControl.calculate(repository);
    	
    	CsvWriterBoundary.write(entries);
    	
    	

	
	}
	
	public static String uppercaseProjName() {		
		
		return PROJECTNAME.toUpperCase();
		
	}
	
	
	public static List<RevCommit> getCommitList (Git git) throws GitAPIException {
		
    	Iterable<RevCommit> log = git.log().call();
    	List<RevCommit> logCommitList = new ArrayList<>();
    	
    	for (RevCommit commit : log) {
                logCommitList.add(commit);
        }
		
    	return logCommitList;
		
	}
	
	
	public static List<DiffEntry> getEntryList (RevWalk rw, DiffFormatter df, RevCommit commit) throws IOException {

		List<DiffEntry> diffEntries;
		RevCommit parent = null;
		
		if(commit.getParentCount() !=0) {
			parent =commit.getParent(0);
		}
			
		
		
		if(parent != null) {
			
			diffEntries = df.scan(parent.getTree(), commit.getTree());
			
		}
		else {
			
			ObjectReader reader = rw.getObjectReader();
			diffEntries =df.scan(new EmptyTreeIterator(),
			        new CanonicalTreeParser(null, reader, commit.getTree()));
		}
		
		return diffEntries;

	}
	
	public static List<Rename> checkRename(List<Data> dbEntries, Git git, Repository repository) throws IOException, GitAPIException {
		
		List<Rename> renameList = new ArrayList<>();		
		List<RevCommit> logCommitList = getCommitList(git);
    	
		RevWalk rw = new RevWalk(repository);

		for (int j=0;j<logCommitList.size();j++) {
			
			
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			
			List<DiffEntry> entries=getEntryList(rw, df, logCommitList.get(j));

			
			//differenze tra il commit e il parent
			for (DiffEntry diffEntry : entries) { 
					
				String oldPath = diffEntry.getOldPath();
				String newPath = diffEntry.getNewPath();
					
				//per ogni diffEntry, vedo se è un Rename di files java
				if (diffEntry.getChangeType().toString().equals(RENAME) && (diffEntry.toString().contains(".java")))	{

					populateRenameList(oldPath, newPath, renameList);

				}
			}				
				
		}
			
			updateAfterRenames(dbEntries, renameList);
				
	
			return renameList;
	}
	
	
	public static void populateRenameList(String oldPath, String newPath, List<Rename> renameList) {
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
	}
		

	
	public static void updateAfterRenames(List<Data> dbEntries, List<Rename> renameList) {
			
		updateRenameList(dbEntries, renameList);
		updateDbEntries(dbEntries, renameList);
		
	}
	
	public static void updateRenameList(List<Data> dbEntries, List<Rename> renameList) {
		// per ogni insieme di alias di ogni file calcolo il nome piu recente ad esso associato 
		// per ogni file in renameList, imposto il newpath di esso come il piu recente ad esso associato (perche sta nelle dbEntries)
				
		for (int i=0;i<dbEntries.size();i++) {
			
			String fileName = dbEntries.get(i).getFilename();
				
			for (int k=0;k<renameList.size();k++) {
					
					for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
						
						String renameFile = renameList.get(k).getOldpaths().get(m);
						
						if (renameFile.equals(fileName) || fileName.contains(renameFile) ) {
							renameList.get(k).setNewpath(renameFile);
							
						}
					}
				}
			}
	}
	

	public static void updateDbEntries(List<Data> dbEntries, List<Rename> renameList) {
		//aggiorno il path di ogni file in ogni release con l'ultimo rename
		for (int i=0;i<dbEntries.size();i++) {
			
			String fileName = dbEntries.get(i).getFilename();
				
			for (int k=0;k<renameList.size();k++) {
					
				for( int m=0;m<renameList.get(k).getOldpaths().size();m++) {
						
						String renameFile = renameList.get(k).getOldpaths().get(m);
						
						if (renameFile.equals(fileName) || fileName.contains(renameFile) ) {
														
							dbEntries.get(i).setFilename(renameList.get(k).getNewpath());
						}
					}
				}
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
	

	
	public static void printData() {
		
		int i;
    	Log.infoLog("DataList");
    	
    	Log.infoLog("entries size: "+entries.size());

		for(i=0;i<entries.size();i++) {
	    	Log.infoLog("release:"+entries.get(i).getRelease()+"       file: "+entries.get(i).getFilename());

		}
		
	}


    public static List<Integer> getReleases() throws IOException, JSONException {

    	List<Release> releases= GetJiraInfoBoundary.getReleaseInfo();
    	List<Integer> myReleases = new ArrayList<>();
    	
    	for (int i=0;i<releases.size()/2;i++) {
    		myReleases.add(releases.get(i).getIndex());
    	}
    	
    	return myReleases;
    	
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
    		
    		if (diffEntry.toString().contains(".java")) {
    		    			
    			String diffFileName;
    			
    			
    			
				if (diffEntry.getChangeType().toString().equals(RENAME) || (diffEntry.getChangeType().toString().equals("DELETE"))){
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
				
    		}
    	
    	}
    	
    	return javaFilesPerTicket;
		
    	
    }


	public static void addJavaFiles(Repository repo) throws IOException {
		//aggiungo a ogni ticket la lista dei file toccati dai commit contenenti l'id del ticket 
		
		int i;
		int j;
		
    	for (i=0;i<ticketlist.size();i++) {
    		
    		for (j=0;j<myCommitsList.size();j++) {
    			
    			if (myCommitsList.get(j).getFullMessage().contains(ticketlist.get(i).getTicketID()+":")){
    				//prendi file modificati da questo commit e aggiungili al ticket
    				
    				ticketlist.get(i).setRelatedJavaFiles(getJavaFiles(repo, myCommitsList.get(j)));


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
				//perche quella buggy è dalla successiva in cui l'ho notato in poi
				
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
						
			//IV -> prendi un AV che sia minore di OV e che sia la prima possibile (escluso -1)
			//per quanto riguarda ordinamento, dovrei ordinare VersionInfo per DATE e mettere
			//gli interi in base a quello
			ticketlist.get(i).setIV(minElement(ticketlist.get(i).getAV()));	
			//ho FV=0 e OV=0 per ticket di GIUGNO 2020 !!!! della release non ancora uscita
			
			checkFvOvBecauseOfLastRelease(ticketlist.get(i));

		}
		
		checkFvOvNotZero();

	}
	

	public static void checkFvOvBecauseOfLastRelease(Ticket myTicket) {
		
		if (myTicket.getOV()==0) {
			myTicket.setOV(lastRelease);
		}
		
		if (myTicket.getFV()==0) {
			myTicket.setFV(lastRelease);
		}
		
	}
	
	public static void checkFvOvNotZero() {
			
		for (int i=0;i<ticketlist.size();i++) {
			
			Ticket myTicket = ticketlist.get(i);
			
			if (myTicket.getOV()==0 || myTicket.getFV()==0) {
				 Log.infoLog("IV o FV sono nulli !");
				 return;		
			}
			

		}
			
			
		}

	public static int minElement(List<Integer> list) {
		
		if (!list.isEmpty()) {
			int min=list.get(0);
			
			for (int i=0;i<list.size();i++) {
				
				if (list.get(i)<min) {
					min=list.get(i);
				}
				
			}
			return min;
		}
		else {
			return 0;
		}

		
	}
	

	public static void finalPrintTickets(List<Ticket> list) {
		
		int i;
		
		for (i=0;i<list.size();i++) {
			Log.infoLog(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV()+"             AV: "+ticketlist.get(i).getAV());
			
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
			
			if (entries.get(i).getRelease().getIndex()==myRelease && entries.get(i).getFilename().contains(myFileName) ) { 
				
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

		for (i=0;i<ticketlist.size();i++) {	//prendo un ticket

			if (ticketlist.get(i).getAV().size()>0) {	//se quel ticket ha AV
				
				for (j=0;j<ticketlist.get(i).getAV().size();j++) {	//scorro la lista delle AV di quel ticket
					
					if (ticketlist.get(i).getAV().get(j)==releaseIndex) {	//se tra le AV trovo la release x
					//allora vuol dire che quella release ha come classi buggy quelle riportate come attributo di quel ticket
					//prendo relatedJavaFiles del ticket i e lo segno a buggy in quella release
						
						for (k=0;k<ticketlist.get(i).getRelatedJavaFiles().size();k++) {	//quindi prendo le classi di quel ticket
							
							computeBuggyness(releaseIndex, ticketlist.get(i).getRelatedJavaFiles().get(k));
							
						}											
						
					}
					
				}
			
			}
			
		}

	}	
	


}
	
	
