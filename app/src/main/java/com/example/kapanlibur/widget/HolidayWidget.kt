package com.example.kapanlibur.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.kapanlibur.MainActivity
import com.example.kapanlibur.R
import com.example.kapanlibur.data.model.Holiday
import com.example.kapanlibur.di.ApiProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HolidayWidget : AppWidgetProvider() {
    
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }
    
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_holiday)
          // Set click intent to open the app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        
        // Set refresh button click intent
        val refreshIntent = Intent(context, HolidayWidget::class.java)
        refreshIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
        
        // Load upcoming holidays
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiProvider.holidayApiService.getAllHolidays()
                if (response.isSuccessful) {
                    val holidays = response.body() ?: emptyList()
                    val upcomingHolidays = getUpcomingHolidays(holidays, 2)
                    
                    withContext(Dispatchers.Main) {
                        updateWidgetWithHolidays(context, appWidgetManager, appWidgetId, upcomingHolidays)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showErrorInWidget(context, appWidgetManager, appWidgetId)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showErrorInWidget(context, appWidgetManager, appWidgetId)
                }            }
        }
        
        // Show loading state initially
        views.setTextViewText(R.id.holiday1_name, "Loading...")
        views.setTextViewText(R.id.holiday1_date, "")
        views.setTextViewText(R.id.holiday1_day, "")
        views.setTextViewText(R.id.holiday1_month, "")
        views.setTextViewText(R.id.holiday2_name, "")
        views.setTextViewText(R.id.holiday2_date, "")
        views.setTextViewText(R.id.holiday2_day, "")
        views.setTextViewText(R.id.holiday2_month, "")
          appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun updateWidgetWithHolidays(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        holidays: List<Holiday>
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_holiday)
        
        // Set click intent to open the app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        
        // Set refresh button click intent
        val refreshIntent = Intent(context, HolidayWidget::class.java)
        refreshIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent)
        
        // Update holiday information
        if (holidays.isNotEmpty()) {
            views.setTextViewText(R.id.holiday1_name, holidays[0].getShortName())
            views.setTextViewText(R.id.holiday1_date, holidays[0].getFormattedDate())
            val date1 = holidays[0].getLocalDate()
            views.setTextViewText(R.id.holiday1_day, String.format("%02d", date1.dayOfMonth))
            views.setTextViewText(R.id.holiday1_month, date1.month.name.substring(0, 3))
        } else {
            views.setTextViewText(R.id.holiday1_name, "No upcoming holidays")
            views.setTextViewText(R.id.holiday1_date, "")
            views.setTextViewText(R.id.holiday1_day, "--")
            views.setTextViewText(R.id.holiday1_month, "")
        }
        
        if (holidays.size > 1) {
            views.setTextViewText(R.id.holiday2_name, holidays[1].getShortName())
            views.setTextViewText(R.id.holiday2_date, holidays[1].getFormattedDate())
            val date2 = holidays[1].getLocalDate()
            views.setTextViewText(R.id.holiday2_day, String.format("%02d", date2.dayOfMonth))
            views.setTextViewText(R.id.holiday2_month, date2.month.name.substring(0, 3))
        } else {            views.setTextViewText(R.id.holiday2_name, "")
            views.setTextViewText(R.id.holiday2_date, "")
            views.setTextViewText(R.id.holiday2_day, "")
            views.setTextViewText(R.id.holiday2_month, "")
        }
        
        appWidgetManager.updateAppWidget(appWidgetId, views)    }
    
    private fun showErrorInWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_holiday)
        
        views.setTextViewText(R.id.holiday1_name, "Error loading holidays")
        views.setTextViewText(R.id.holiday1_date, "Tap to refresh")
        views.setTextViewText(R.id.holiday1_day, "!")
        views.setTextViewText(R.id.holiday1_month, "")
        views.setTextViewText(R.id.holiday2_name, "")
        views.setTextViewText(R.id.holiday2_date, "")
        views.setTextViewText(R.id.holiday2_day, "")
        views.setTextViewText(R.id.holiday2_month, "")
        
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    private fun getUpcomingHolidays(holidays: List<Holiday>, limit: Int): List<Holiday> {
        val today = LocalDate.now()
        return holidays
            .filter { 
                val holidayDate = it.getLocalDate()
                holidayDate.isAfter(today) || holidayDate.isEqual(today)
            }
            .sortedBy { it.date }
            .take(limit)
    }
}
