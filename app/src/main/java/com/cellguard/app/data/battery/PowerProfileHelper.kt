package com.cellguard.app.data.battery

import android.content.Context
import android.os.Build

object PowerProfileHelper {

    /**
     * Safely attempts to query Android's internal PowerProfile for rated battery capacity in mAh.
     * Returns null if access is denied, unsupported, or invalid.
     */
    fun getDesignCapacityMah(context: Context): Int? {
        return try {
            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
            val constructor = powerProfileClass.getConstructor(Context::class.java)
            val powerProfile = constructor.newInstance(context)
            val method = powerProfileClass.getMethod("getBatteryCapacity")
            val capacityDouble = method.invoke(powerProfile) as? Double
            val capacityInt = capacityDouble?.toInt()

            // Realistic sanity range: standard Android smartphones have 800mAh to 12000mAh
            if (capacityInt != null && capacityInt in 800..12000) {
                capacityInt
            } else {
                null
            }
        } catch (_: Throwable) {
            null
        }
    }
}
