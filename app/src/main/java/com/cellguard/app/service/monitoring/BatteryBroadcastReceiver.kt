package com.cellguard.app.service.monitoring

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cellguard.app.CellGuardApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BatteryBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val app = context.applicationContext as? CellGuardApplication ?: return
        val batteryRepo = app.appContainer.batteryRepository
        val historyRepo = app.appContainer.historyRepository

        CoroutineScope(Dispatchers.IO).launch {
            val currentMetrics = batteryRepo.getCurrentMetrics()
            batteryRepo.recordMetricsUpdate(currentMetrics)
        }
    }
}
