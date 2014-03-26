package com.example.moneydo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.example.moneydo.mathlogic.DatedEntry;
import com.example.moneydo.mathlogic.EntryLibrary;
import com.example.moneydo.mathlogic.MathEntry;
import com.example.moneydo.mathlogic.MathEntry.EntryType;

public class MainController {
	
	private EntryLibrary mEntryLibrary = new EntryLibrary();
	
	private MathEntry mAvailableMoney;
	
	private MathEntry mNeededMoney;
	
	private DatedEntry mPlanedIncome;
	
	private DatedEntry mPlanedOutcome;
	
	public enum StageType{
		ST_MAIN_PAGE,
		ST_PLANNED_INCOME,
		ST_PLANNED_OUTCOME,
		ST_NEEDED_MONEY,
	}
	private StageType mStage=StageType.ST_MAIN_PAGE;
	
	private StageDetail mStageDetail=new StageDetail();
	
	protected NotificationController mNotContoller = NotificationController.GetSigleton();
	
	private DaylyTask mDaylyTask = new DaylyTask();
	
	private Activity mActivity;
		
	public static int mPageAmount = 6;
	
	private static  MainController sigleton;
	
	
	

	private MainController(){
		GetNeededEntry();
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 3);
		mDaylyTask.addTask(calendar.getTime(),TimeUnit.SECONDS.toMillis(5), new TimerTask() {
			
			@Override
			public void run() {
				MainController.this.AddDalyQwestion();
				
			}

		
		});
	}
	
	
	protected void AddDalyQwestion() {
		if(mActivity==null){
			return;
		}
		Resources  res = mActivity.getResources();
		mNotContoller.AddNotification(res.getString(R.string.day_end),
				res.getString(R.string.you_have) + String.valueOf(mAvailableMoney.GetAmount()),
				R.layout.spend_dialog
				);
		
	}


	public static MainController GetSigleton(){
		if(sigleton==null){
			sigleton=new MainController();
		}
		return sigleton;
	}
	public void SetActivity(Activity activity){
		mActivity = activity;
	}
	
	private void GetNeededEntry(){
		
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		try{
			SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(mActivity);
			day = Integer.parseInt(	mySharedPreferences.getString("currentdate", ""));
		}catch ( NumberFormatException e) {
			
		}catch ( NullPointerException e) {
			
		}
		calendar.set(Calendar.DAY_OF_MONTH,day);
		Date mWorkingDate =calendar.getTime();
		mEntryLibrary.DoGlobalCount(mWorkingDate);
		mAvailableMoney =  mEntryLibrary.GetClosesEntry(MathEntry.EntryType.ET_AVAILABLEMONEY);
		mPlanedIncome =   (DatedEntry)mEntryLibrary.GetClosesEntry(MathEntry.EntryType.ET_PLANEDINCOME,mWorkingDate);
		
		mNeededMoney =   mEntryLibrary.GetClosesEntry(MathEntry.EntryType.ET_NEEDEDMONEY);
		mPlanedOutcome = (DatedEntry) mEntryLibrary.GetClosesEntry(MathEntry.EntryType.ET_PLANEDOUTCOME,mWorkingDate);
		GenerateStageList();
	}
	public void ChangeStage(StageType stage){
		mStage =stage;
		GenerateStageList();
		mStageDetail.mPage = 0;
		
	}
	private void GenerateStageList(){
		
		switch(mStage){
		case ST_NEEDED_MONEY:
			mStageDetail.mList=	mEntryLibrary.GetAllByType(MathEntry.EntryType.ET_NEEDEDMONEY);
			break;
		case ST_PLANNED_INCOME:
			mStageDetail.mList=	mEntryLibrary.GetAllByType(MathEntry.EntryType.ET_PLANEDINCOME);
			break;
		case ST_PLANNED_OUTCOME:
			mStageDetail.mList=	mEntryLibrary.GetAllByType(MathEntry.EntryType.ET_PLANEDOUTCOME);
			break;
		case ST_MAIN_PAGE:
			mStageDetail.mList=	new ArrayList<MathEntry>();
			break;
		default:
			mStageDetail.mList=	new ArrayList<MathEntry>();
			break;
	}
	}
	public StageType GetStage(){
		return mStage;	
	}
	public StageDetail GetStageDetail(){
		return mStageDetail;
	}
	
	public HashMap<MathEntry.EntryType,MathEntry> ReturnCurrentMainList(){
		HashMap<MathEntry.EntryType,MathEntry> list =  new HashMap<MathEntry.EntryType,MathEntry> ();
		list.put(MathEntry.EntryType.ET_AVAILABLEMONEY,mAvailableMoney);
		list.put(MathEntry.EntryType.ET_PLANEDINCOME,mPlanedIncome);			
		list.put(MathEntry.EntryType.ET_NEEDEDMONEY,mNeededMoney);			
		list.put(MathEntry.EntryType.ET_PLANEDOUTCOME,mPlanedOutcome);			
		return list;
	
	}
	public void AddPIEntry(float amount,Date date){
		mEntryLibrary.AddEntry(MathEntry.EntryType.ET_PLANEDINCOME, amount,date);
		GetNeededEntry();
	};
	public void AddPOEntry(float amount,Date date){
		mEntryLibrary.AddEntry(MathEntry.EntryType.ET_PLANEDOUTCOME, amount,date);
		GetNeededEntry();
	};
	public void AddNMEntry(float amount){	
		mEntryLibrary.AddEntry(MathEntry.EntryType.ET_NEEDEDMONEY, amount);
		GetNeededEntry();
	};
	
	public class StageDetail{
		public List<MathEntry> mList;
		
		public int mPage;
		
		public int mPerPage =mPageAmount;
		
	}
	
	public StageType DecideStage(EntryType type) {
			switch (type) {
				case ET_AVAILABLEMONEY: return StageType.ST_MAIN_PAGE;
				case ET_NEEDEDMONEY: return  StageType.ST_NEEDED_MONEY;	
				case ET_PLANEDINCOME: return  StageType.ST_PLANNED_INCOME;	
				case ET_PLANEDOUTCOME: return  StageType.ST_PLANNED_OUTCOME;		
			}
			return StageType.ST_MAIN_PAGE;
		
		
	}


	public void spendMoney(float amount) {
		if(amount<0){
			amount=-amount;
		}
		mAvailableMoney.LowerAmount(amount);
		
	}


	public CharSequence GetMainAmount() {
		if(mAvailableMoney!=null){
			return String.valueOf(mAvailableMoney.GetAmount());
		}
		return "";
	}
}
