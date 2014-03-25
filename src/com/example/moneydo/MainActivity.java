package com.example.moneydo;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;



import net.jazzyjohn.ui.circlelayout.CircleList;
import net.jazzyjohn.ui.circlelayout.CircleList.CircleListener;
import net.jazzyjohn.ui.circlelayout.CircleList.CircleNode;

import com.example.moneydo.MainController.StageDetail;
import com.example.moneydo.mathlogic.MathEntry;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.TimePicker;

public class MainActivity extends Activity {

	protected MainController mController = MainController.GetSigleton();
	
	protected NotificationController mNotContoller = NotificationController.GetSigleton();
	
	
	protected static final String INTENT_LAYOUT="INTENT_LAYOUT";
	
	protected enum WindowStage{
		WS_MAIN_WINDOW,
		WS_DIALOG_WINDOW
	}
	protected WindowStage mStage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNotContoller.SetActivity(this);
        mController.SetActivity(this);
        Intent intent = getIntent();
        if(intent.hasExtra(INTENT_LAYOUT)){
        	int lauoyt =intent.getIntExtra(INTENT_LAYOUT, R.layout.activity_main);
        	if(lauoyt==R.layout.activity_main){
        		 mStage=WindowStage.WS_MAIN_WINDOW;
        	}else{
        		 mStage=WindowStage.WS_DIALOG_WINDOW;
        	}
        		
        	setContentView(lauoyt);
        	
        }else{
        	mStage=WindowStage.WS_MAIN_WINDOW;
        	setContentView(R.layout.activity_main);
            ReDraw();
        }

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	  super.onConfigurationChanged(newConfig);
    	  switch(mStage){
    	  	case WS_MAIN_WINDOW:
    	  		ReDraw();
    	  		break;
    	  	case WS_DIALOG_WINDOW:
    	  		break;
    	  }
    }
    private class BtnListener implements CircleListener 
    {
    	@Override
		public void onClick(CircleNode node) {
			
			
		}

		@Override
		public void onUp(CircleNode node) {
			
			node.mCircleRadius/=1.5f;
			node.ForcedSizeChange();
			findViewById(R.id.AVMoneyCircle).invalidate();
		}

		@Override
		public void onDown(CircleNode node) {
			
			node.mCircleRadius*=1.5f;
			node.ForcedSizeChange();
			findViewById(R.id.AVMoneyCircle).invalidate();
		}
    }
    private void ReDraw(){
    	HashMap<MathEntry.EntryType,MathEntry> list =mController.ReturnCurrentMainList();
    	final CircleList MainList = (CircleList) findViewById(R.id.AVMoneyCircle);
    	final MainController.StageType stageType =mController.GetStage();
    	MainList.addRoot(String.valueOf(list.get(MathEntry.EntryType.ET_AVAILABLEMONEY).GetAmount()), list.get(MathEntry.EntryType.ET_AVAILABLEMONEY).GetNormalName(getResources()),new CircleListener() {
			
			@Override
			public void onClick(CircleNode node){
				if(stageType!=MainController.StageType.ST_MAIN_PAGE){
					mController.ChangeStage(MainController.StageType.ST_MAIN_PAGE);
					ReDraw();
				}else{
					setContentView(R.layout.addentry_dialog);
					mStage =WindowStage.WS_DIALOG_WINDOW;
				}
			}

			@Override
			public void onUp(CircleNode node) {
				
				node.mBorderSize -=10;
				MainList.invalidate ();
			}

			@Override
			public void onDown(CircleNode node) {
				
				node.mBorderSize +=10;
				MainList.invalidate ();
			}
		});
    	
	    if(stageType==MainController.StageType.ST_MAIN_PAGE){
	    	MainList.setLvl(0);
	    	MainList.addNodeToRoot(getResources().getString(R.string.spend_money),null,new BtnListener(){
				@Override
				public void onClick(CircleNode node) {
					setContentView(R.layout.spend_dialog);
					mStage =WindowStage.WS_DIALOG_WINDOW;
				}
				
			});
	    	
	    	for (Iterator<MathEntry.EntryType> iterator = list.keySet().iterator(); iterator.hasNext();) {
	    		final MathEntry.EntryType key =  iterator.next();
	    		if(key==MathEntry.EntryType.ET_AVAILABLEMONEY){
	    			continue;
	    		}
	    		if(list.get(key)!=null){
	    			MainList.addNodeToRoot(String.valueOf(list.get(key).GetAmount()),list.get(key).GetNormalName(getResources()),new BtnListener(){
	    				@Override
	    				public void onClick(CircleNode node) {
	    					mController.ChangeStage(mController.DecideStage(key));
	    					ReDraw();
	    				}
	    				
	    			});
	    		}
	    		
			}
	
    	}else{
    		
    		MathEntry rootEntry=null;
    		switch(stageType){
			case ST_NEEDED_MONEY:
				rootEntry=list.get(MathEntry.EntryType.ET_NEEDEDMONEY);
				MainList.setLvl(1);
				break;
			case ST_PLANNED_INCOME:
				rootEntry=list.get(MathEntry.EntryType.ET_PLANEDINCOME);
				MainList.setLvl(1);
				break;
			case ST_PLANNED_OUTCOME:
				rootEntry=list.get(MathEntry.EntryType.ET_PLANEDOUTCOME);
				MainList.setLvl(1);
				break;
			default:
				break;
    			
    		
    		}
    		CircleNode node =MainList.addNodeToRoot(String.valueOf(rootEntry.GetAmount()),rootEntry.GetLabel(getResources()),new BtnListener());
			
    		StageDetail detail  = mController.GetStageDetail();
    		if(node!=null){
	    		for (Iterator<MathEntry> iterator = detail.mList.iterator(); iterator.hasNext();) {
	    			MathEntry entry =  iterator.next();
	    			
	    			if(entry==rootEntry){
	    				continue;
	    			}
	    			MainList.addNewNode(node,String.valueOf(entry.GetAmount()),entry.GetLabel(getResources()),new BtnListener(){
	    				@Override
	    				public void onClick(CircleNode node) {
	    					
	    				}
	    				
	    			});
				}
    		}
    		
    	}
	    MainList.startAnim();
	    MainList.invalidate();
	    updateWidget();
    }
    
    public void updateWidget() {
    	try {
			
	
	    	Context context = getApplicationContext();
	        AppWidgetManager manager = AppWidgetManager.getInstance(context);
	        ComponentName thisWidget = new ComponentName(context, MainWidgetProvider.class);
	    
	        
	        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
	
	        remoteViews.setTextViewText(R.id.widgetTextView, mController.GetMainAmount());
	     // Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(context, MainActivity.class);
			resultIntent.putExtra(MainActivity.INTENT_LAYOUT, R.layout.spend_dialog);
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(MainActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );
	        remoteViews.setOnClickPendingIntent(R.id.widgetTextView,resultPendingIntent);
	
	
	
	        manager.updateAppWidget(thisWidget, remoteViews);
    	} catch (NullPointerException e) {
			
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_exit:
            	finish();
                System.exit(0);
                return true;
            case R.id.action_settings:
            	setContentView(R.layout.settings_dialog);
				mStage =WindowStage.WS_DIALOG_WINDOW;
            	break;
              
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
        	switch(mStage){
    	  	case WS_MAIN_WINDOW:
    	  		if(mController.GetStage()!=MainController.StageType.ST_MAIN_PAGE){
    	  			mController.ChangeStage(MainController.StageType.ST_MAIN_PAGE);
    	  			ReDraw();
    	  			return true;
    	  		}
    	  		break;
    	  	case WS_DIALOG_WINDOW:
    	  		openMainWindow();
    	  		return true;
    	  }
        }
        return super.onKeyDown(keyCode, event);
    }
    public void openMainWindow(){
    	mStage=WindowStage.WS_MAIN_WINDOW;
    	setContentView(R.layout.activity_main);
    	ReDraw();
    }
    public void spendMoney(View view){
    	EditText textField = (EditText) findViewById(R.id.MoneyCount);
    	float amount =0;
    	try {
    		amount  =  Float.parseFloat(textField.getText().toString());
		} catch ( NumberFormatException e) {
			return;
		}
    	mController.spendMoney(amount);
    	InputMethodManager imm = (InputMethodManager)getSystemService(
  		      Context.INPUT_METHOD_SERVICE);
  		imm.hideSoftInputFromWindow(textField.getWindowToken(), 0);
    	openMainWindow();
    }
    public void saveSetting(View view){
    	TimePicker picker = (TimePicker) findViewById(R.id.timeOFNotifyPicker);
    	
    	openMainWindow(); 
    }
    public void cancel(View view){
    	openMainWindow(); 
    }
    public void addEntry(View view){
    	RadioGroup group = (RadioGroup) findViewById(R.id.EntryTypeRadioGroup);
    	DatePicker picker = (DatePicker) findViewById(R.id.DateOfEntry);
    	EditText textField = (EditText) findViewById(R.id.EntryAmount);
    	float amount =0;
    	try {
    		amount  =  Float.parseFloat(textField.getText().toString());
		} catch ( NumberFormatException e) {
			return;
		}
    	
    	switch(group.getCheckedRadioButtonId()){
    		case R.id.ETS_PI:
    			mController.AddPIEntry(amount,getDateFromDatePicket(picker));
    		break;
    		case R.id.ETS_PO:
    			mController.AddPOEntry(amount,getDateFromDatePicket(picker));
    		break;
    		case R.id.ETS_NM:
    			mController.AddNMEntry(amount);
    		break;
    	
    	}
    	InputMethodManager imm = (InputMethodManager)getSystemService(
    		      Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(textField.getWindowToken(), 0);
    	openMainWindow();   	
    }
    /**
     * 
     * @param datePicker
     * @return a Date
     */
    public static Date getDateFromDatePicket(DatePicker datePicker){
	    int day = datePicker.getDayOfMonth();
	    int month = datePicker.getMonth();
	    int year =  datePicker.getYear();
	
	    Calendar calendar = Calendar.getInstance();
	    calendar.set(year, month, day);
	
	    return calendar.getTime();
    }
    
}
