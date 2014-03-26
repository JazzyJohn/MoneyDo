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
		float neededmoney=0,planedIncomeSum=0;
		MathEntry aventry = null;

		List<SmallEntry> planedOutcome =   new ArrayList<SmallEntry>() ,planedIncome = new ArrayList<SmallEntry>();

		for (Iterator<MathEntry> entryIterator = entryList.iterator(); entryIterator.hasNext();){
			MathEntry entry  = entryIterator.next();
			switch(entry.GetType()){
			case ET_NEEDEDMONEY:

				neededmoney+=entry.GetAmount();
				break;
			case ET_PLANEDINCOME:
				try{
					DatedEntry dentry = (DatedEntry)entry;
					Calendar lcalendar = Calendar.getInstance();
					lcalendar.setTime( dentry.GetDate());
					planedIncome.add(0,new SmallEntry(dentry.GetAmount(),lcalendar.get(Calendar.DAY_OF_MONTH)));
				}catch(ClassCastException e){
					continue;
				} 

				break;
			case ET_PLANEDOUTCOME:
				try{
					DatedEntry dentry = (DatedEntry)entry;
					Calendar lcalendar = Calendar.getInstance();
					lcalendar.setTime( dentry.GetDate());
					planedOutcome.add(0,new SmallEntry(dentry.GetAmount(),lcalendar.get(Calendar.DAY_OF_MONTH)));
				}catch(ClassCastException e){
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
		int maxDay =calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		for (int i =0; i<planedIncome.size();i++) {
			SmallEntry smallEntry = planedIncome.get(i);
			float moneyAmount=neededmoney*maxDay;
			if(i+1<planedIncome.size()){
				int CurDays =maxDay -smallEntry.day;
				maxDay = smallEntry.day;
				moneyAmount=neededmoney* CurDays;
			}
			if(smallEntry.money>moneyAmount){
				smallEntry.money-=moneyAmount;
			}else{
				if(i+1<planedIncome.size()){
					moneyAmount=moneyAmount-smallEntry.money;
					smallEntry.money=0;
					planedIncome.get(i+1).money-=moneyAmount;
				}else{
					smallEntry.money-=moneyAmount;
				}

			}
		}
		if(planedIncome.size()==0){
			for (int j =0; j<planedOutcome.size();j++) {
				planedIncomeSum-= planedOutcome.get(j).money;
			}
			planedIncomeSum-=neededmoney*maxDay;
		}else{
			for (int i =0; i<planedIncome.size();i++) {
				SmallEntry incomeEntry = planedIncome.get(i);
				for (int j =0; j<planedOutcome.size();j++) {
					SmallEntry outcomeEntry = planedOutcome.get(j);
					if(incomeEntry.day>outcomeEntry.day&&i+1<planedIncome.size()){
						break;
					}
					if(outcomeEntry.money==0){
						continue;
					}

					if(incomeEntry.money>outcomeEntry.money){
						incomeEntry.money-=outcomeEntry.money;
						outcomeEntry.money=0;
					}else{
						if(!(i+1<planedIncome.size())){
							incomeEntry.money-=outcomeEntry.money;
							outcomeEntry.money=0;
						}
					}
				}
			}

			for (int i =0; i<planedIncome.size();i++) {
				SmallEntry incomeEntry = planedIncome.get(i);
				if(calendar.get(Calendar.DAY_OF_MONTH)>incomeEntry.day){
					planedIncomeSum+=planedIncome.get(i).money;
				}

			}
		}
		if(aventry!=null){
			aventry.SetAmount(planedIncomeSum);
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
	private class SmallEntry{
		float money;
		int day;
		public SmallEntry(float initMoney,int initDay){
			money= initMoney;
			day = initDay;
		}
	}
}
