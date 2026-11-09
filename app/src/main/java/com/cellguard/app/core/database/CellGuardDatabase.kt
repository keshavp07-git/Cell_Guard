package com.cellguard.app.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cellguard.app.core.database.dao.BatterySnapshotDao
import com.cellguard.app.core.database.dao.ChargingSessionDao
import com.cellguard.app.core.database.dao.HealthEstimateDao
import com.cellguard.app.core.database.entity.BatterySnapshotEntity
import com.cellguard.app.core.database.entity.ChargingSessionEntity
import com.cellguard.app.core.database.entity.HealthEstimateEntity

@Database(
    entities = [
        BatterySnapshotEntity::class,
        ChargingSessionEntity::class,
        HealthEstimateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CellGuardDatabase : RoomDatabase() {
    abstract fun batterySnapshotDao(): BatterySnapshotDao
    abstract fun chargingSessionDao(): ChargingSessionDao
    abstract fun healthEstimateDao(): HealthEstimateDao

    companion object {
        @Volatile
        private var INSTANCE: CellGuardDatabase? = null

        fun getInstance(context: Context): CellGuardDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CellGuardDatabase::class.java,
                    "cellguard_database.db"
                ).fallbackToDestructiveMigration(dropAllTables = true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
