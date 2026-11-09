package com.cellguard.app.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.cellguard.app.core.database.dao.BatterySnapshotDao
import com.cellguard.app.core.database.dao.BatterySnapshotDao_Impl
import com.cellguard.app.core.database.dao.ChargingSessionDao
import com.cellguard.app.core.database.dao.ChargingSessionDao_Impl
import com.cellguard.app.core.database.dao.HealthEstimateDao
import com.cellguard.app.core.database.dao.HealthEstimateDao_Impl
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CellGuardDatabase_Impl : CellGuardDatabase() {
  private val _batterySnapshotDao: Lazy<BatterySnapshotDao> = lazy {
    BatterySnapshotDao_Impl(this)
  }

  private val _chargingSessionDao: Lazy<ChargingSessionDao> = lazy {
    ChargingSessionDao_Impl(this)
  }

  private val _healthEstimateDao: Lazy<HealthEstimateDao> = lazy {
    HealthEstimateDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(1,
        "c2a882e45096b1c8ffd7b30b596de776", "d14790c5c6e78b64df673e5ed729a8ca") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `battery_snapshots` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `percentage` INTEGER NOT NULL, `temperatureCelsius` REAL NOT NULL, `voltageMv` INTEGER NOT NULL, `currentMa` REAL, `isCharging` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `charging_sessions` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `startTime` INTEGER NOT NULL, `endTime` INTEGER NOT NULL, `startPercentage` INTEGER NOT NULL, `endPercentage` INTEGER NOT NULL, `startChargeCounterUah` INTEGER, `endChargeCounterUah` INTEGER, `startTemperatureC` REAL NOT NULL, `maxTemperatureC` REAL NOT NULL, `avgTemperatureC` REAL NOT NULL, `avgVoltageMv` INTEGER NOT NULL, `avgCurrentMa` REAL, `sourceName` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `estimatedCapacityContribution` REAL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `health_estimates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `estimatedFullCapacityMah` REAL, `verifiedDesignCapacityMah` INTEGER, `healthPercentage` REAL, `conditionName` TEXT NOT NULL, `confidenceName` TEXT NOT NULL, `cycleCount` INTEGER, `estimatedLossPct` REAL, `validSessionsCount` INTEGER NOT NULL)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'c2a882e45096b1c8ffd7b30b596de776')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `battery_snapshots`")
        connection.execSQL("DROP TABLE IF EXISTS `charging_sessions`")
        connection.execSQL("DROP TABLE IF EXISTS `health_estimates`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsBatterySnapshots: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsBatterySnapshots.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("percentage", TableInfo.Column("percentage", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("temperatureCelsius", TableInfo.Column("temperatureCelsius",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("voltageMv", TableInfo.Column("voltageMv", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("currentMa", TableInfo.Column("currentMa", "REAL", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsBatterySnapshots.put("isCharging", TableInfo.Column("isCharging", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysBatterySnapshots: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesBatterySnapshots: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoBatterySnapshots: TableInfo = TableInfo("battery_snapshots",
            _columnsBatterySnapshots, _foreignKeysBatterySnapshots, _indicesBatterySnapshots)
        val _existingBatterySnapshots: TableInfo = read(connection, "battery_snapshots")
        if (!_infoBatterySnapshots.equals(_existingBatterySnapshots)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |battery_snapshots(com.cellguard.app.core.database.entity.BatterySnapshotEntity).
              | Expected:
              |""".trimMargin() + _infoBatterySnapshots + """
              |
              | Found:
              |""".trimMargin() + _existingBatterySnapshots)
        }
        val _columnsChargingSessions: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsChargingSessions.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("startTime", TableInfo.Column("startTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("endTime", TableInfo.Column("endTime", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("startPercentage", TableInfo.Column("startPercentage",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("endPercentage", TableInfo.Column("endPercentage", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("startChargeCounterUah",
            TableInfo.Column("startChargeCounterUah", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("endChargeCounterUah", TableInfo.Column("endChargeCounterUah",
            "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("startTemperatureC", TableInfo.Column("startTemperatureC",
            "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("maxTemperatureC", TableInfo.Column("maxTemperatureC", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("avgTemperatureC", TableInfo.Column("avgTemperatureC", "REAL",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("avgVoltageMv", TableInfo.Column("avgVoltageMv", "INTEGER",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("avgCurrentMa", TableInfo.Column("avgCurrentMa", "REAL", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("sourceName", TableInfo.Column("sourceName", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("isCompleted", TableInfo.Column("isCompleted", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsChargingSessions.put("estimatedCapacityContribution",
            TableInfo.Column("estimatedCapacityContribution", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysChargingSessions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesChargingSessions: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoChargingSessions: TableInfo = TableInfo("charging_sessions",
            _columnsChargingSessions, _foreignKeysChargingSessions, _indicesChargingSessions)
        val _existingChargingSessions: TableInfo = read(connection, "charging_sessions")
        if (!_infoChargingSessions.equals(_existingChargingSessions)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |charging_sessions(com.cellguard.app.core.database.entity.ChargingSessionEntity).
              | Expected:
              |""".trimMargin() + _infoChargingSessions + """
              |
              | Found:
              |""".trimMargin() + _existingChargingSessions)
        }
        val _columnsHealthEstimates: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsHealthEstimates.put("id", TableInfo.Column("id", "INTEGER", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("estimatedFullCapacityMah",
            TableInfo.Column("estimatedFullCapacityMah", "REAL", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("verifiedDesignCapacityMah",
            TableInfo.Column("verifiedDesignCapacityMah", "INTEGER", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("healthPercentage", TableInfo.Column("healthPercentage", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("conditionName", TableInfo.Column("conditionName", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("confidenceName", TableInfo.Column("confidenceName", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("cycleCount", TableInfo.Column("cycleCount", "INTEGER", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("estimatedLossPct", TableInfo.Column("estimatedLossPct", "REAL",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsHealthEstimates.put("validSessionsCount", TableInfo.Column("validSessionsCount",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysHealthEstimates: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesHealthEstimates: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoHealthEstimates: TableInfo = TableInfo("health_estimates", _columnsHealthEstimates,
            _foreignKeysHealthEstimates, _indicesHealthEstimates)
        val _existingHealthEstimates: TableInfo = read(connection, "health_estimates")
        if (!_infoHealthEstimates.equals(_existingHealthEstimates)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |health_estimates(com.cellguard.app.core.database.entity.HealthEstimateEntity).
              | Expected:
              |""".trimMargin() + _infoHealthEstimates + """
              |
              | Found:
              |""".trimMargin() + _existingHealthEstimates)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "battery_snapshots",
        "charging_sessions", "health_estimates")
  }

  public override fun clearAllTables() {
    super.performClear(false, "battery_snapshots", "charging_sessions", "health_estimates")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(BatterySnapshotDao::class,
        BatterySnapshotDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(ChargingSessionDao::class,
        ChargingSessionDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(HealthEstimateDao::class, HealthEstimateDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun batterySnapshotDao(): BatterySnapshotDao = _batterySnapshotDao.value

  public override fun chargingSessionDao(): ChargingSessionDao = _chargingSessionDao.value

  public override fun healthEstimateDao(): HealthEstimateDao = _healthEstimateDao.value
}
