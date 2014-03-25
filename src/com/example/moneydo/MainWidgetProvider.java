package com.example.moneydo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class MainWidgetProvider extends AppWidgetProvider {
	
	  @Override
      public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
           
           RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

             
           appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
      }
}
