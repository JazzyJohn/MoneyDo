package com.example.moneydo;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class MainWidgetProvider extends AppWidgetProvider {
	 protected MainController mController = MainController.GetSigleton();
		
	  @Override
      public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
           
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
        		   
           appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
      }
}
