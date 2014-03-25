package com.example.moneydo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DaylyTask {
	
	private List<Timer> mStacks= new ArrayList<Timer>();
		
	public void addTask(Date date,long time,TimerTask task){
		Timer timer = new Timer();
		timer.schedule(task, date,time);
		mStacks.add(timer);
	}
	
	public void shutDown(){
		for (Timer timer : mStacks) {
			timer.cancel();
			timer.purge();
		}
	}

	
}
