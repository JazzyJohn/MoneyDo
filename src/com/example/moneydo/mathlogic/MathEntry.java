package com.example.moneydo.mathlogic;


import com.example.moneydo.R;

import android.content.res.Resources;



public class MathEntry {
	
	
	
	//Amount 
	private float amount;
	
	
	public enum EntryType{
			ET_PLANEDINCOME,
			ET_PLANEDOUTCOME,
			ET_NEEDEDMONEY,
			ET_AVAILABLEMONEY
		
	};
	private EntryType type;
	
	protected MathEntry(EntryType inType){
		type = inType;
	}
	
	public EntryType GetType(){
		return type;
	}
	public float GetAmount(){
		return amount;
		
	}
	
	
	public void SetAmount(float inAmount){
		amount 		= inAmount;
		
	}

	public String GetNormalName(Resources resources) {
		
		switch (type) {
			case ET_AVAILABLEMONEY: return resources.getString(R.string.availablemoney);
			case ET_NEEDEDMONEY: return resources.getString(R.string.Needed_Money);	
			case ET_PLANEDINCOME: return resources.getString(R.string.Planned_Income);	
			case ET_PLANEDOUTCOME: return resources.getString(R.string.Planned_Outcome);		
		}
		return "";
	}

	public String GetLabel(Resources resources) {
		
		return GetNormalName(resources);
	}

	public void LowerAmount(float delta) {
				
	}
	

}
