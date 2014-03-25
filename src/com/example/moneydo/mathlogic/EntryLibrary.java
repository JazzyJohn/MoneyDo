package com.example.moneydo.mathlogic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.example.moneydo.mathlogic.MathEntry.EntryType;

public class EntryLibrary {
	protected List<MathEntry> entryList;
	
	public EntryLibrary(){
		entryList =  new ArrayList<MathEntry>();
		MathEntry entry =  new CountingEntry(MathEntry.EntryType.ET_AVAILABLEMONEY);
		entry.SetAmount(0);
		entryList.add(entry);
		SortByDate();
	}
	
	public void  AddEntry(EntryType type, float amount){
		MathEntry entry =  new MathEntry(type);
		entry.SetAmount(amount);
		entryList.add(entry);
		SortByDate();
		
	}
	public void AddEntry(EntryType type, float amount, Date Date ){
		DatedEntry entry   = new DatedEntry(type);
		entry.SetAmount(amount);
		entry.SetDate(Date);
		entryList.add(entry);
		SortByDate();
	}
	
	private void SortByDate(){
		Collections.sort(entryList, new Comparator<MathEntry>(){
				
					public int compare(MathEntry o1, MathEntry o2) {
						DatedEntry entry1,entry2;
						try{
							entry1=(DatedEntry)o1;
							
						}catch(ClassCastException e){
							return 1;
						}
						try{
							entry2=(DatedEntry)o2;
							
						}catch(ClassCastException e){
							return -1;
						} 
						return entry1.GetDate().compareTo(entry2.GetDate());
			    	}
		});
		
	}
	public void DoGlobalCount(Date time){
		float allmoney=0,neededmoney=0,planedIncome=0,planedOutcome=0;
		MathEntry aventry = null;
		
		int day_next =-1;
		for (Iterator<MathEntry> entryIterator = entryList.iterator(); entryIterator.hasNext();){
			 MathEntry entry  = entryIterator.next();
			 switch(entry.GetType()){
			 	case ET_NEEDEDMONEY:
					
					neededmoney+=entry.GetAmount();
			 		break;
			 	case ET_PLANEDINCOME:
			 		try{
						DatedEntry dEntry = (DatedEntry) entry;
						if(time.after(dEntry.GetDate())){
							planedIncome+=dEntry.GetAmount();
						}else{
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(dEntry.GetDate());
							if(day_next==-1){
								day_next = calendar.get(Calendar.DAY_OF_MONTH);
							}
						}
					}catch (ClassCastException e) {
						continue;
					}
			 		break;
			 	case ET_PLANEDOUTCOME:
			 		try{
						DatedEntry dEntry = (DatedEntry) entry;
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(dEntry.GetDate());
						if(!(day_next!=-1&&calendar.get(Calendar.DAY_OF_MONTH)>day_next)){
							planedOutcome+=dEntry.GetAmount();			
						}
									
					}catch (ClassCastException e) {
						continue;
					}
			 		break;
			 	case ET_AVAILABLEMONEY:
			 		aventry = entry;
			 		break;
		 		default:
		 			break;
			 }
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		if(day_next!=-1){
			allmoney-=neededmoney*day_next;
		}else{
			allmoney-=neededmoney*calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		
		allmoney+=planedIncome;
		allmoney-=planedOutcome;
		if(aventry!=null){
			aventry.SetAmount(allmoney);
		}
		
	}
	
	public MathEntry GetClosesEntry(EntryType type,Date date){
		MathEntry anywayEntry=null;
		for (Iterator<MathEntry> entryIterator = entryList.iterator(); entryIterator.hasNext();){
			try{
				DatedEntry  entry  = (DatedEntry)entryIterator.next();
				 if(entry!=null && entry.GetType()==type){
					 if(entry.GetDate().after(date)){
						 return  entry;
					 }else{
						 anywayEntry = entry;
					 }
				 }	
			}catch(ClassCastException e){
				
			} 
		 }
		 return anywayEntry;
	}
	public MathEntry GetClosesEntry(EntryType type){
		 for (Iterator<MathEntry> entryIterator = entryList.iterator(); entryIterator.hasNext();){
			 MathEntry entry  = entryIterator.next();
			 if(entry.GetType()==type){
				return  entry;
			 }			 
		 }
		 return null;
	}

	public List<MathEntry> GetAllByType(EntryType type) {
		List<MathEntry> list = new ArrayList<MathEntry>();
		for (Iterator<MathEntry> entryIterator = entryList.iterator(); entryIterator.hasNext();){
			 MathEntry entry  = entryIterator.next();
			 if(entry.GetType()==type){
				 list.add(entry);
			 }			 
		 }
		return list;
	}
}
