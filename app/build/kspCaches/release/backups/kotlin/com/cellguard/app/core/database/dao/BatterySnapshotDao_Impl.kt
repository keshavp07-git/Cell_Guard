package com.cellguard.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cellguard.app.core.database.entity.BatterySnapshotEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Float
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BatterySnapshotDao_Impl(
  __db: RoomDatabase,
) : BatterySnapshotDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBatterySnapshotEntity: EntityInsertAdapter<BatterySnapshotEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBatterySnapshotEntity = object :
        EntityInsertAdapter<BatterySnapshotEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `battery_snapshots` (`id`,`timestamp`,`percentage`,`temperatureCelsius`,`voltageMv`,`currentMa`,`isCharging`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BatterySnapshotEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.timestamp)
        statement.bindLong(3, entity.percentage.toLong())
        statement.bindDouble(4, entity.temperatureCelsius.toDouble())
        statement.bindLong(5, entity.voltageMv.toLong())
        val _tmpCurrentMa: Float? = entity.currentMa
        if (_tmpCurrentMa == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpCurrentMa.toDouble())
        }
        val _tmp: Int = if (entity.isCharging) 1 else 0
        statement.bindLong(7, _tmp.toLong())
      }
    }
  }

  public override suspend fun insertSnapshot(snapshot: BatterySnapshotEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfBatterySnapshotEntity.insertAndReturnId(_connection,
        snapshot)
    _result
  }

  public override fun getSnapshotsSince(sinceTimestamp: Long): Flow<List<BatterySnapshotEntity>> {
    val _sql: String = "SELECT * FROM battery_snapshots WHERE timestamp >= ? ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("battery_snapshots")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceTimestamp)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPercentage: Int = getColumnIndexOrThrow(_stmt, "percentage")
        val _columnIndexOfTemperatureCelsius: Int = getColumnIndexOrThrow(_stmt,
            "temperatureCelsius")
        val _columnIndexOfVoltageMv: Int = getColumnIndexOrThrow(_stmt, "voltageMv")
        val _columnIndexOfCurrentMa: Int = getColumnIndexOrThrow(_stmt, "currentMa")
        val _columnIndexOfIsCharging: Int = getColumnIndexOrThrow(_stmt, "isCharging")
        val _result: MutableList<BatterySnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatterySnapshotEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPercentage: Int
          _tmpPercentage = _stmt.getLong(_columnIndexOfPercentage).toInt()
          val _tmpTemperatureCelsius: Float
          _tmpTemperatureCelsius = _stmt.getDouble(_columnIndexOfTemperatureCelsius).toFloat()
          val _tmpVoltageMv: Int
          _tmpVoltageMv = _stmt.getLong(_columnIndexOfVoltageMv).toInt()
          val _tmpCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfCurrentMa)) {
            _tmpCurrentMa = null
          } else {
            _tmpCurrentMa = _stmt.getDouble(_columnIndexOfCurrentMa).toFloat()
          }
          val _tmpIsCharging: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCharging).toInt()
          _tmpIsCharging = _tmp != 0
          _item =
              BatterySnapshotEntity(_tmpId,_tmpTimestamp,_tmpPercentage,_tmpTemperatureCelsius,_tmpVoltageMv,_tmpCurrentMa,_tmpIsCharging)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentSnapshots(limit: Int): List<BatterySnapshotEntity> {
    val _sql: String = "SELECT * FROM battery_snapshots ORDER BY timestamp DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfPercentage: Int = getColumnIndexOrThrow(_stmt, "percentage")
        val _columnIndexOfTemperatureCelsius: Int = getColumnIndexOrThrow(_stmt,
            "temperatureCelsius")
        val _columnIndexOfVoltageMv: Int = getColumnIndexOrThrow(_stmt, "voltageMv")
        val _columnIndexOfCurrentMa: Int = getColumnIndexOrThrow(_stmt, "currentMa")
        val _columnIndexOfIsCharging: Int = getColumnIndexOrThrow(_stmt, "isCharging")
        val _result: MutableList<BatterySnapshotEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatterySnapshotEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpPercentage: Int
          _tmpPercentage = _stmt.getLong(_columnIndexOfPercentage).toInt()
          val _tmpTemperatureCelsius: Float
          _tmpTemperatureCelsius = _stmt.getDouble(_columnIndexOfTemperatureCelsius).toFloat()
          val _tmpVoltageMv: Int
          _tmpVoltageMv = _stmt.getLong(_columnIndexOfVoltageMv).toInt()
          val _tmpCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfCurrentMa)) {
            _tmpCurrentMa = null
          } else {
            _tmpCurrentMa = _stmt.getDouble(_columnIndexOfCurrentMa).toFloat()
          }
          val _tmpIsCharging: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCharging).toInt()
          _tmpIsCharging = _tmp != 0
          _item =
              BatterySnapshotEntity(_tmpId,_tmpTimestamp,_tmpPercentage,_tmpTemperatureCelsius,_tmpVoltageMv,_tmpCurrentMa,_tmpIsCharging)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSnapshotsOlderThan(beforeTimestamp: Long) {
    val _sql: String = "DELETE FROM battery_snapshots WHERE timestamp < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, beforeTimestamp)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM battery_snapshots"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
