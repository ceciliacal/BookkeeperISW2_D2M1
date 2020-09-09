package project.bookkeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ProportionControl {
	
	public static final List<Ticket> ticketlist= MainControl.ticketlist;
	private static final String INCONSISTENT_VERSIONS = "ERRORE: versioni inconsistenti.";

	
	public void checkDates(List <Ticket> goodProp, List <Ticket> noIV) {
		
		int i;	
		
		for (i=0;i<ticketlist.size();i++){
			
			//se ticket ha data creazione > data fix, allora dati non sono consistenti
			if (ticketlist.get(i).getCreatedDate().compareTo(ticketlist.get(i).getResolutionDate())>0) {
				
				Log.infoLog("-------------Errore: date inconsistenti");
				//return
				i++;
			 }
			
			if  (ticketlist.get(i).getOV()==1) {	// IV=1
				
				ticketlist.get(i).setIV(1);
				ticketlist.get(i).getAV().remove(0);
				ticketlist.get(i).getAV().add(1);
					
			}
			
			//questi hanno l'AV	
			else {	
				
				//se ticket ha IV>OV o IV>FV, significa che AV presa da Jira non e affidabile e quindi la ricalcolo usando proportion
				//pertanto imposto IV=0 ed e come se AV non fosse presente (ignoro AV di Jira)
			
				if ((ticketlist.get(i).getIV()>ticketlist.get(i).getOV()) || (ticketlist.get(i).getIV() >ticketlist.get(i).getFV())) {
					
					ticketlist.get(i).setIV(0);
					ticketlist.get(i).getAV().clear();

					
				}
				
				
			}
			
			
		}
		
		//questi NON hanno AV 
		for (i=0;i<ticketlist.size();i++){
			

			//se OV==FV e IV=0 (IV=0 se: 1. il ticket non ha AV da Jira 2. se AV presa da Jira e inconsistente (cioe IV>OV || IV>FV)

			//Questi li rimuovo perche OV=FV

			if ((ticketlist.get(i).getIV()==0)&&(ticketlist.get(i).getOV()==ticketlist.get(i).getFV())) {
				
				ticketlist.remove(i);
				i--;

				
			}
		
		}

		//ora prendiamo ticket su cui calcolare Proportion
		
		getProportionTickets( goodProp, noIV);
		
		
	}

		

		

	public static void getProportionTickets(List<Ticket> goodProp, List<Ticket> noIV) {
		
		for(int i=0;i<ticketlist.size();i++) {
			
			if (ticketlist.get(i).getIV()!=0) {

				//FV!=IV && FV!=OV -> altrimenti P=0
				if ((ticketlist.get(i).getFV()!=ticketlist.get(i).getIV()) && (ticketlist.get(i).getFV()!=ticketlist.get(i).getOV())&&(ticketlist.get(i).getIV()<=ticketlist.get(i).getOV())){
					
						goodProp.add(ticketlist.get(i));
					
				}			
			}
			
			else {

					noIV.add(ticketlist.get(i));
					
				}
			
			}

	}
	
	
	public void proportion(List<Ticket> mytickets, List <Ticket> noIv, int numDefects) {
		
		int dim;
		double perc;	//percentuale: 1% degli ultimi fixed bugs 
		int i;
		double iv;
		double fv;
		double ov;
		double p;
		int prop;
		
		LinkedHashMap<Ticket, Integer> pList = new LinkedHashMap<>();	//lista di P da cui calcolo la media dell'ultimo 1% dei difetti
		
		//calcolo 1% dei difetti
		perc= numDefects*0.01;		

		dim= (int) Math.round(perc);	//dimensione della moving window: arrotondamento di perc (puo essere sia x difetto, sia x eccesso)
		
		Collections.reverse(mytickets);
		Collections.reverse(noIv);
		
		//ora calcola P per ognuno dei mytickets
		for (i=0;i<mytickets.size();i++) {
			
			iv=mytickets.get(i).getIV();
			ov=mytickets.get(i).getOV();
			fv=mytickets.get(i).getFV();
			
			p= (fv-iv)/(fv-ov);

			prop= (int) Math.round(p);
			
			mytickets.get(i).setP(prop);
			pList.put(mytickets.get(i), prop);			
			
		}
		
		
		
		proportionCalculus(pList, noIv, dim);

	
			
	}
	

		
	public void proportionCalculus(Map<Ticket, Integer> pList, List <Ticket> noIv, int dim) {
		
		List<Integer> window;
		
		int average;
		int lastKey=0;
		int countInsideIf;


		for (int i=0; i<noIv.size();i++) {
			
			countInsideIf=0;
			
			
			//se ticketnoIV-iesimo supera un ticket good, deve prendere la p corrispondete
			//a quel ticket good e le ultime 4 (dim) p a partire da esso.
			

			for (Entry<Ticket, Integer> pList_entry : pList.entrySet()) {		//good
				
				
				if (noIv.get(i).getNumTicket()<pList_entry.getKey().getNumTicket()) {		
			    	 

			    	 //prendi ultimi 4 entry della pList (ultimi 4 ticket, di cui prendo il value "p")

					 countInsideIf++;

					 window=buildWindow(pList, dim, lastKey ) ;  
					 average= calculateAverage(window);

					 
					//ora calcolo predictedIV come media degli ultimi 4 P
					 noIv.get(i).setP(average);
					 calculatePredictedIV(noIv.get(i));
					 
					 if(!checkVersions(noIv.get(i).getIV(), noIv.get(i).getOV(), noIv.get(i).getFV())){
						 Log.errorLog("--------ticket versioni incons: "+noIv.get(i).getTicketID()+"  IV= "+noIv.get(i).getIV() +"    OV="+noIv.get(i).getOV() +"   FV="+noIv.get(i).getFV()+"   p= "+noIv.get(i).getP());

						 
					 }
					 				 
			    	 break;

			     }
			
				lastKey=pList_entry.getKey().getNumTicket();
					
			}
			
			if (countInsideIf==0) {		//non sono mai entrato nell'IF perche ticketNoIv > ticketGood => prendo ultimo ticketGood
								
				window=buildWindow(pList, dim, lastKey ) ;  
				average= calculateAverage(window);
				
				 noIv.get(i).setP(average);
				 calculatePredictedIV(noIv.get(i));
				 
				 if(!checkVersions(noIv.get(i).getIV(), noIv.get(i).getOV(), noIv.get(i).getFV())){
					 Log.errorLog("--------ticket versioni incons: "+noIv.get(i).getTicketID()+"  IV= "+noIv.get(i).getIV() +"    OV="+noIv.get(i).getOV() +"   FV="+noIv.get(i).getFV()+"   p= "+noIv.get(i).getP());

					 
					 
				 }
				
				
			}

		
		}
	
		
		
	}
	
	public static boolean checkVersions(int iv, int ov, int fv) {
		
		if ( (iv>ov) || (iv>fv) ) {
			 
			 Log.infoLog(INCONSISTENT_VERSIONS);
			 return false;

		 }
		
		return true;
	
	}

	
	
	public List<Integer> buildWindow(Map<Ticket, Integer> pList, int dim, int targetNumTicket ) {
		
		int i;

		List<Integer> window= new ArrayList<>();
		
		List <Integer> tempWindow = new ArrayList<>();
		
		for (Entry<Ticket, Integer> pList_entry : pList.entrySet()) {
			
			tempWindow.add(pList_entry.getValue());
			
			if (pList_entry.getKey().getNumTicket()==targetNumTicket) {
				
				break;
				
			}
			
			
		}
		
		i=tempWindow.size()-dim;
		
		int count=0;
		

		
		while (count<dim) {
			
			window.add(tempWindow.get(i));
			i++;
			count++;

		}
		
		
		return window;
		
		
	}
	
	
	public void calculatePredictedIV(Ticket myTicket) {
		
		double fv;
		double ov;
		double res;
		double p;
		int predIv;

		//Predicted IV = FV -(FV -OV) * P
		
		ov=myTicket.getOV();
		fv=myTicket.getFV();
		p= (double) myTicket.getP();
		
		res=  fv -(fv -ov) * p;
		predIv= (int) Math.round(res);			
		myTicket.setIV(predIv);

		
	}
		
		
	public int calculateAverage(List<Integer>  window) {
		
		int i;
		int avg;
		double  sum;
		double tempAvg;
		double len;
		
		sum=0;
		len=(double) window.size();
		
		for (i=0;i<window.size();i++) {
		
		sum=sum+window.get(i);

		}
		
		tempAvg= (sum)/len;
		avg= (int) Math.round(tempAvg);
		
		return avg;
	
		
	}


	

	
	public void defineAV (int halfRelease) {
		
		int i;
		int count;
		int iv;
		int ov;
		int fv;
		
		for (i=0;i<ticketlist.size();i++) {
		
			iv= ticketlist.get(i).getIV();
			ov= ticketlist.get(i).getOV();
			fv=ticketlist.get(i).getFV();
			
			if (iv==0) {
				
				Log.infoLog("\n\n-----------------ERRORE------------------  ===> IV=0");
				Log.infoLog(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());
				
			}
			
			//giusto: IV<=OV, OV<=FV, IV<=FV
			if ((iv>ov) || (iv>fv) || (ov>fv)) {
				
				Log.infoLog("\n\n-----------------ERRORE------------------  ===> (IV>OV) || (IV>FV) || (OV>FV)");	
				Log.infoLog(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());

				
			}
			
			ticketlist.get(i).getAV().clear();
			
			count= iv;
					
			
			
			while (count!=fv) {	
				ticketlist.get(i).getAV().add(count);
				
				if (count==halfRelease) {
					break;
				}
				count++;
				
			}
			

		}
			
	}
	

}
