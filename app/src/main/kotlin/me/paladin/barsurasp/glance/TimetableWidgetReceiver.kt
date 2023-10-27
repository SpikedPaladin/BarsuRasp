package me.paladin.barsurasp.glance

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class TimetableWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TimetableWidget()

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        Log.i("TAG", "onReceive: ${intent.action}")
    }
}