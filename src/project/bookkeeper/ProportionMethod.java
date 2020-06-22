package project.bookkeeper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ProportionMethod {
	
	public static final List<Ticket> ticketlist= MainControl.ticketlist;

	
	public void checkDates2(List <Ticket> goodProp, List <Ticket> noIV) {
		int i;	
		/*
		int ovIs1=0;
		int mettoIn_noIV=0;
		int removed=0;
		int smistoTraIv_e_Good=0;
		int good=0;
		int noIv=0;
		List <Ticket> ovIs1_list = new ArrayList<Ticket>();
		List <Ticket> mettoIn_noIV_list = new ArrayList<Ticket>();
		List <Ticket> removed_list = new ArrayList<Ticket>();
		*/
		

		
		
		for (i=0;i<ticketlist.size();i++){
			
			//se ticket ha data creazione > data fix, allora dati non sono consistenti
			if (ticketlist.get(i).getCreatedDate().compareTo(ticketlist.get(i).getResolutionDate())>0) {
				Log.infoLog("Errore: date inconsistenti");
				return;
			 }
			
			if  (ticketlist.get(i).getOV()==1) {	// IV=1
				
				//ovIs1_list.add(ticketlist.get(i));
				ticketlist.get(i).setIV(1);
				ticketlist.get(i).getAV().remove(0);
				ticketlist.get(i).getAV().add(1);
				//ovIs1++;
				
				
			}
			//questi hanno l'AV	(???)
			else {	
				
				//se ticket ha IV>OV o IV>FV, significa che AV presa da Jira non è affidabile e quindi la ricalcolo usando proportion
				//pertanto imposto IV=0 ed è come se AV non fosse presente (ignoro AV di Jira)
			
				if ((ticketlist.get(i).getIV()>ticketlist.get(i).getOV()) || (ticketlist.get(i).getIV() >ticketlist.get(i).getFV())) {
					
					//mettoIn_noIV_list.add(ticketlist.get(i));
					ticketlist.get(i).setIV(0);
					ticketlist.get(i).getAV().clear();
					//mettoIn_noIV++;

					
				}
				
				
			}
			
			
		}
		
		//questi NON hanno AV 
		for (i=0;i<ticketlist.size();i++){
			

			//se OV==FV e IV=0 (IV=0 se: 1. il ticket non ha AV da Jira 2. se AV presa da Jira è inconsistente (cioè IV>OV || IV>FV)

			//Questi li rimuovo perche OV=FV

			if ((ticketlist.get(i).getIV()==0)&&(ticketlist.get(i).getOV()==ticketlist.get(i).getFV())) {
				
				//removed_list.add(ticketlist.get(i));
				ticketlist.remove(i);
				//removed++;
				i--;

				
			}
		
		}

		int k;
		//System.out.println("dim ticketlist: "+ticketlist.size());
		//System.out.println("HALF: "+halfRelease);

		
		//rimuovo i ticket con IV=0 & OV>7 || FV>7 , perché non posso calcolarne predictedIV
		
		/*
		for (k=0;k<ticketlist.size();k++){
			
			if (ticketlist.get(k).getIV()==0)  {
				if ((ticketlist.get(k).getOV()>halfRelease) || (ticketlist.get(k).getFV()>halfRelease)) {
				
				
			
				
				//removed_list.add(ticketlist.get(i));
				//System.out.println("ticket: "+ticketlist.get(k).getTicketID()+" 		OV: "+ticketlist.get(k).getOV()+" 		FV: "+ticketlist.get(k).getFV()+" 		IV: "+ticketlist.get(k).getIV());

				ticketlist.remove(k);
				removed++;
				k--;

				//}
			}
		
		}
		*/
		
		//System.out.println("dim ticketlist: "+ticketlist.size());	
		
		int persi=0;
		/*
		System.out.println("\n\n");
		System.out.println("ovIs1: "+ovIs1);
		for (i=0;i<ovIs1_list.size();i++){
			
			 System.out.println(ovIs1_list.get(i).getTicketID()+"		IV: "+ ovIs1_list.get(i).getIV()  +"		OV: "+ovIs1_list.get(i).getOV() +"		FV: "+ovIs1_list.get(i).getFV()+"		AV: "+ovIs1_list.get(i).getAV());
		}
		System.out.println("\n\n");
		System.out.println("removed: "+removed);
		for (i=0;i<removed_list.size();i++){
			
			 System.out.println(removed_list.get(i).getTicketID()+"		IV: "+ removed_list.get(i).getIV()  +"		OV: "+removed_list.get(i).getOV() +"		FV: "+removed_list.get(i).getFV()+"		AV: "+removed_list.get(i).getAV());
			
		}
		System.out.println("\n\n");
		System.out.println("mettoIn_noIV: "+mettoIn_noIV);
		for (i=0;i<mettoIn_noIV_list.size();i++){
			
			
			 System.out.println(mettoIn_noIV_list.get(i).getTicketID()+"		IV: "+ mettoIn_noIV_list.get(i).getIV()  +"		OV: "+mettoIn_noIV_list.get(i).getOV() +"		FV: "+mettoIn_noIV_list.get(i).getFV()+"		AV: "+mettoIn_noIV_list.get(i).getAV());
			
		}
		
		*/
		//System.out.println("\n\n");
			
			/*
			 * quindi finora abbiamo settato:
			 * - IV=1 per i ticket aventi OV=1
			 * - IV=0 per i ticket che hanno un AV (di Jira) non affidabile, perche risulta che IV>OV o IV>FV 
			 * 
			 * e abbiamo rimosso:
			 * - i ticket con OV=FV & IV=0 (o perché AV non c'è su Jira, o perché IV>OV || IV>FV)
			 * 
			 */
			
		//AVRO GIA TOLTO TICKET CON IV>7
		//ora prendiamo ticket su cui calcolare Proportion
		
		for(i=0;i<ticketlist.size();i++) {
			
			//smistoTraIv_e_Good++;
			
			//Prendiamo ticket con IV !=0 (cioè che hanno AV)
			if (ticketlist.get(i).getIV()!=0) {

				//FV!=IV && FV!=OV -> sennò P=0
				if ((ticketlist.get(i).getFV()!=ticketlist.get(i).getIV()) && (ticketlist.get(i).getFV()!=ticketlist.get(i).getOV())&&(ticketlist.get(i).getIV()<=ticketlist.get(i).getOV())){
					//if (ticketlist.get(i).getOV()<=halfRelease && ticketlist.get(i).getFV()<=halfRelease ) {
							
						//good++;
	
						//System.out.println("ticket: "+ticketlist.get(i).getTicketID()+"		created: "+ticketlist.get(i).getCreatedDate()+"		resolution: "+ticketlist.get(i).getResolutionDate()+" 		IV: "+ticketlist.get(i).getIV()+" 		OV: "+ticketlist.get(i).getOV()+" 		FV: "+ticketlist.get(i).getFV());
						goodProp.add(ticketlist.get(i));
						
					//}
					
				}
				else {
					
					persi++;
					//System.out.println("=============ticket: "+ticketlist.get(i).getTicketID()+"		created: "+ticketlist.get(i).getCreatedDate()+"		resolution: "+ticketlist.get(i).getResolutionDate()+" 		IV: "+ticketlist.get(i).getIV()+" 		OV: "+ticketlist.get(i).getOV()+" 		FV: "+ticketlist.get(i).getFV());

				}
				
				
			}
			else {

					noIV.add(ticketlist.get(i));
					//noIv++;
					
				}
			
			}

		

		

		
		

		/*
		System.out.println("smistoTraIv_e_Good: "+smistoTraIv_e_Good);
		System.out.println("\n\n");
		System.out.println("good: "+good+"         dim listaGood: "+goodProp.size());
		for (i=0;i<goodProp.size();i++) {
			
			System.out.println("ticket: "+goodProp.get(i).getTicketID()+"		created: "+goodProp.get(i).getCreatedDate()+"		resolution: "+goodProp.get(i).getResolutionDate()+" 		IV: "+goodProp.get(i).getIV()+" 		OV: "+goodProp.get(i).getOV()+" 		FV: "+goodProp.get(i).getFV()+"		AV: "+ovIs1_list.get(i).getAV());
		}
		System.out.println("\n\n");
		System.out.println("noIv: "+noIv+"         dim noIv: "+noIV.size());
		for (i=0;i<noIV.size();i++) {
			
			System.out.println("ticket: "+noIV.get(i).getTicketID()+"		created: "+noIV.get(i).getCreatedDate()+"		resolution: "+noIV.get(i).getResolutionDate()+" 		IV: "+noIV.get(i).getIV()+" 		OV: "+noIV.get(i).getOV()+" 		FV: "+noIV.get(i).getFV()+"		AV: "+ovIs1_list.get(i).getAV());
		}
		
		System.out.println("persi: "+persi);
		
		
		*/
		

	}
	
	
	public void proportion(List<Ticket> mytickets, List <Ticket> noIv, int numDefects) {
		
		int dim;
		double perc;	//percentuale: 1% degli ultimi fixed bugs 
		int i;
		double iv;
		double fv;
		double ov;
		double p;
		double predictedIV;
		int prop;
		String formatted;
		
		LinkedHashMap<Ticket, Integer> pList = new LinkedHashMap<Ticket, Integer>();	//lista di P da cui calcolo la media dell'ultimo 1% dei difetti
		
		//calcolo 1% dei difetti
		perc= numDefects*0.01;		
		String.format("%.3f", perc);	//arrotondo la percentuale
		//System.out.println("perc= "+perc);
		
		dim= (int) Math.round(perc);	//dimensione della moving window: arrotondamento di perc (può essere sia x difetto, sia x eccesso)
		//System.out.println("dim= "+dim);
		
		
		Collections.reverse(mytickets);
		Collections.reverse(noIv);
		
		//ora calcola P per ognuno dei mytickets
		for (i=0;i<mytickets.size();i++) {
			
			iv=mytickets.get(i).getIV();
			ov=mytickets.get(i).getOV();
			fv=mytickets.get(i).getFV();
			
			p= (fv-iv)/(fv-ov);
			String.format("%3f", p);
			
			//System.out.println("ticketID= "+mytickets.get(i).getTicketID()+"  		p= "+p+"  		numTicket= "+mytickets.get(i).getNumTicket());

			prop= (int) Math.round(p);
			
			mytickets.get(i).setP(prop);
			pList.put(mytickets.get(i), prop);			
			
		}
		
		proportionCalculus(pList, noIv, dim);

		//System.out.println("\n\n\n");

		
		List<Integer> window;
		
		int average;
		int lastKey=0;
		int countInsideIf;

		 //System.out.println("==== FORSE predIV");

		for (i=0; i<noIv.size();i++) {
			
			//average=0;
			countInsideIf=0;
			
			//se ticketnoIV-iesimo supera un ticket good, deve prendere la p corrispondete
			//a quel ticket good e le ultime 4 (dim) p a partire da esso.
			

			for (HashMap.Entry<Ticket, Integer> pList_entry : pList.entrySet()) {		//good
				
				
				if (noIv.get(i).getNumTicket()<pList_entry.getKey().getNumTicket()) {		
			    	 

			    	 //prendi ultimi 4 entry della pList (ultimi 4 ticket, di cui prendo il value "p")

					countInsideIf++; 

					//System.out.println("ticketID_noIv= "+noIv.get(i).getTicketID()+"  		goodID_pList= "+pList_entry.getKey().getTicketID());
					//System.out.println("------------------- ");

					
					 window=buildWindow(pList, dim, lastKey ) ;  
					 average= calculateAverage(window);
					 //System.out.println("P average= "+ average);

					 
					//ora calcolo predictedIV come media degli ultimi 4 P
					// --
					 noIv.get(i).setP(average);
					 calculatePredictedIV(noIv.get(i));
					 
					 if ((noIv.get(i).getIV()>noIv.get(i).getOV()) || (noIv.get(i).getIV()>noIv.get(i).getFV()) ) {
						 
						 System.out.println("ERRORE: versioni inconsistenti.");
						 return;

					 }
	
					 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());

			    	 break;

			     }
			
				lastKey=pList_entry.getKey().getNumTicket();
					
			}
			
			if (countInsideIf==0) {		//non sono mai entrato nell'IF perche ticketNoIv > ticketGood => prendo ultimo ticketGood
				
				//System.out.println("        -------------------> lastKey= "+lastKey);
				
				window=buildWindow(pList, dim, lastKey ) ;  
				average= calculateAverage(window);
				
				 noIv.get(i).setP(average);
				 calculatePredictedIV(noIv.get(i));
				 
				 if ((noIv.get(i).getIV()>noIv.get(i).getOV()) || (noIv.get(i).getIV()>noIv.get(i).getFV()) ) {
					 
					 System.out.println("ERRORE: versioni inconsistenti.");
					 return;

				 }

				 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());

				
			}

			 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());
		
		}
	
			
	}
	

		
	public void proportionCalculus(LinkedHashMap<Ticket, Integer> pList, List <Ticket> noIv, int dim) {
		
		List<Integer> window;
		
		int average;
		int lastKey=0;
		int countInsideIf;

		 //System.out.println("==== FORSE predIV");

		for (int i=0; i<noIv.size();i++) {
			
			//average=0;
			countInsideIf=0;
			
			//se ticketnoIV-iesimo supera un ticket good, deve prendere la p corrispondete
			//a quel ticket good e le ultime 4 (dim) p a partire da esso.
			

			for (HashMap.Entry<Ticket, Integer> pList_entry : pList.entrySet()) {		//good
				
				
				if (noIv.get(i).getNumTicket()<pList_entry.getKey().getNumTicket()) {		
			    	 

			    	 //prendi ultimi 4 entry della pList (ultimi 4 ticket, di cui prendo il value "p")

					countInsideIf++; 

					//System.out.println("ticketID_noIv= "+noIv.get(i).getTicketID()+"  		goodID_pList= "+pList_entry.getKey().getTicketID());
					//System.out.println("------------------- ");

					
					 window=buildWindow(pList, dim, lastKey ) ;  
					 average= calculateAverage(window);
					 //System.out.println("P average= "+ average);

					 
					//ora calcolo predictedIV come media degli ultimi 4 P
					 noIv.get(i).setP(average);
					 calculatePredictedIV(noIv.get(i));
					 
					 if ((noIv.get(i).getIV()>noIv.get(i).getOV()) || (noIv.get(i).getIV()>noIv.get(i).getFV()) ) {
						 
						 System.out.println("ERRORE: versioni inconsistenti.");
						 return;

					 }
	
					 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());

			    	 break;

			     }
			
				lastKey=pList_entry.getKey().getNumTicket();
					
			}
			
			if (countInsideIf==0) {		//non sono mai entrato nell'IF perche ticketNoIv > ticketGood => prendo ultimo ticketGood
				
				//System.out.println("        -------------------> lastKey= "+lastKey);
				
				window=buildWindow(pList, dim, lastKey ) ;  
				average= calculateAverage(window);
				
				 noIv.get(i).setP(average);
				 calculatePredictedIV(noIv.get(i));
				 
				 if ((noIv.get(i).getIV()>noIv.get(i).getOV()) || (noIv.get(i).getIV()>noIv.get(i).getFV()) ) {
					 
					 System.out.println("ERRORE: versioni inconsistenti.");
					 return;

				 }

				 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());

				
			}

			 //System.out.println(noIv.get(i).getTicketID()+"            IV: "+ noIv.get(i).getIV()+"           OV : "+noIv.get(i).getOV()+"             FV: "+noIv.get(i).getFV()+"             AV: "+noIv.get(i).getAV());
		
		}
	
		
		
	}
	
	
	
	public List<Integer> buildWindow(LinkedHashMap<Ticket, Integer> pList, int dim, int targetNumTicket ) {
		
		int i;
		//System.out.println("targetNumTicket: "+ targetNumTicket);

		List<Integer> window= new ArrayList<Integer>();
		
		List <Integer> tempWindow = new ArrayList<Integer>();
		
		for (HashMap.Entry<Ticket, Integer> pList_entry : pList.entrySet()) {
			
			//System.out.println("key: "+pList_entry.getKey().getTicketID()+"			p: "+pList_entry.getValue());
			tempWindow.add(pList_entry.getValue());
			
			if (pList_entry.getKey().getNumTicket()==targetNumTicket) {
				
				break;
				
			}
			
			
		}
		
		i=tempWindow.size()-dim;
		//System.out.println("tempWindow: "+tempWindow+"        size: "+i);
		
		int count=0;
		

		
		while (count<dim) {
			
			//System.out.println("tempWindow.get(i): "+tempWindow.get(i));
			window.add(tempWindow.get(i));
			i++;
			count++;

		}
		
		
		//System.out.println("window: "+window);
		return window;
		
		
	}
	
	
	public void calculatePredictedIV(Ticket myTicket) {
		
		double IV;
		double FV;
		double OV;
		double res;
		double P;
		int predIv;

		//Predicted IV = FV -(FV -OV) * P
		
		OV=myTicket.getOV();
		FV=myTicket.getFV();
		P= (double) myTicket.getP();
		
		res=  FV -(FV -OV) * P;
		//String formattedCommand = String.format("%3f", res);
		
		//String.format("%3f", res);
		String.format("%3f", res);

		predIv= (int) Math.round(res);		
		//System.out.println("predictedIV: "+ predIv);	
	
		myTicket.setIV(predIv);
		//System.out.println("IV: "+ myTicket.getIV());	

		
	}
		
		
	public int calculateAverage(List<Integer>  window) {
		
		int i, avg;
		double  sum, tempAvg, len;
		
		sum=0;
		len=(double) window.size();
		
		for (i=0;i<window.size();i++) {
		
		sum=sum+window.get(i);

		}
		
		tempAvg= (sum)/len;
		//System.out.println("tempAvg: "+tempAvg);	

		String.format("%3f", tempAvg);
		//System.out.println("tempAvg: "+tempAvg);	

		avg= (int) Math.round(tempAvg);
		//System.out.println("avg: "+avg);	

		
		return avg;
	
		
	}


	

	
public void defineAV (int halfRelease) {

		
	System.out.println("\n\n");	

		int i;
		int count;
		int IV;
		int OV;
		int FV;
		
		for (i=0;i<ticketlist.size();i++) {
		
			IV= ticketlist.get(i).getIV();
			OV= ticketlist.get(i).getOV();
			FV=ticketlist.get(i).getFV();
			
			if (IV==0) {
				
				System.out.println("\n\n-----------------ERRORE------------------  ===> IV=0");	
				System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());
				
			}
			
			//giusto: IV<=OV, OV<=FV, IV<=FV
			if ((IV>OV) || (IV>FV) || (OV>FV)) {
				
				System.out.println("\n\n-----------------ERRORE------------------  ===> (IV>OV) || (IV>FV) || (OV>FV)");	
				System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());

				
			}
			
			ticketlist.get(i).getAV().clear();
			
			count= IV;
					
			
			
			while (count!=FV) {	
				ticketlist.get(i).getAV().add(count);
				
				if (count==halfRelease) {
					break;
				}
				count++;
				
			}
			
			count=0;
			//System.out.println(ticketlist.get(i).getTicketID()+"            IV: "+ ticketlist.get(i).getIV()+"           OV : "+ticketlist.get(i).getOV()+"             FV: "+ticketlist.get(i).getFV());


		}
			
	}
	

}
