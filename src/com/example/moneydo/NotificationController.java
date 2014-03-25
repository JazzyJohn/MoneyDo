package com.example.moneydo;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

public class NotificationController {
	
	private Activity mMainActivity;
	
	public void SetActivity(Activity activity){
		mMainActivity = activity;
	}
	private static  NotificationController sigleton;

	

	public static NotificationController GetSigleton(){
		if(sigleton==null){
			sigleton=new NotificationController();
		}
		return sigleton;
	}
	
	public void AddNotification(String notTitle, String notText,int layout){
		if(mMainActivity==null){
			return;
		}
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(mMainActivity)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(notTitle)
		        .setContentText(notText)
		        .setAutoCancel(true);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(mMainActivity, MainActivity.class);
		resultIntent.putExtra(MainActivity.INTENT_LAYOUT, layout);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mMainActivity);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) mMainActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
		
	}
	
	
}
