package com.example.moneydo.mathlogic;

import java.text.DateFormat;
import java.util.Date;

import android.content.res.Resources;





public class DatedEntry extends MathEntry{

	private Date dateOfEntry;

	protected DatedEntry(EntryType inType){
		super(inType);
	}
	
	public Date GetDate(){
		return dateOfEntry;		
	}
	public void SetDate(Date inDate){
		dateOfEntry = inDate;
	}
	public String GetLabel(Resources resources) {
		
		return DateFormat.getDateInstance().format(dateOfEntry);
	}
	
}
