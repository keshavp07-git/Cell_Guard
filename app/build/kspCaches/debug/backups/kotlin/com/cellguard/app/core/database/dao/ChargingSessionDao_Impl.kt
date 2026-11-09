package com.cellguard.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cellguard.app.core.database.entity.ChargingSessionEntity
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
public class ChargingSessionDao_Impl(
  __db: RoomDatabase,
) : ChargingSessionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfChargingSessionEntity: EntityInsertAdapter<ChargingSessionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfChargingSessionEntity = object :
        EntityInsertAdapter<ChargingSessionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `charging_sessions` (`id`,`startTime`,`endTime`,`startPercentage`,`endPercentage`,`startChargeCounterUah`,`endChargeCounterUah`,`startTemperatureC`,`maxTemperatureC`,`avgTemperatureC`,`avgVoltageMv`,`avgCurrentMa`,`sourceName`,`isCompleted`,`estimatedCapacityContribution`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ChargingSessionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.startTime)
        statement.bindLong(3, entity.endTime)
        statement.bindLong(4, entity.startPercentage.toLong())
        statement.bindLong(5, entity.endPercentage.toLong())
        val _tmpStartChargeCounterUah: Long? = entity.startChargeCounterUah
        if (_tmpStartChargeCounterUah == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpStartChargeCounterUah)
        }
        val _tmpEndChargeCounterUah: Long? = entity.endChargeCounterUah
        if (_tmpEndChargeCounterUah == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpEndChargeCounterUah)
        }
        statement.bindDouble(8, entity.startTemperatureC.toDouble())
        statement.bindDouble(9, entity.maxTemperatureC.toDouble())
        statement.bindDouble(10, entity.avgTemperatureC.toDouble())
        statement.bindLong(11, entity.avgVoltageMv.toLong())
        val _tmpAvgCurrentMa: Float? = entity.avgCurrentMa
        if (_tmpAvgCurrentMa == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpAvgCurrentMa.toDouble())
        }
        statement.bindText(13, entity.sourceName)
        val _tmp: Int = if (entity.isCompleted) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpEstimatedCapacityContribution: Float? = entity.estimatedCapacityContribution
        if (_tmpEstimatedCapacityContribution == null) {
          statement.bindNull(15)
        } else {
          statement.bindDouble(15, _tmpEstimatedCapacityContribution.toDouble())
        }
      }
    }
  }

  public override suspend fun insertSession(session: ChargingSessionEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfChargingSessionEntity.insertAndReturnId(_connection,
        session)
    _result
  }

  public override fun getAllSessions(): Flow<List<ChargingSessionEntity>> {
    val _sql: String = "SELECT * FROM charging_sessions ORDER BY startTime DESC"
    return createFlow(__db, false, arrayOf("charging_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfStartPercentage: Int = getColumnIndexOrThrow(_stmt, "startPercentage")
        val _columnIndexOfEndPercentage: Int = getColumnIndexOrThrow(_stmt, "endPercentage")
        val _columnIndexOfStartChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "startChargeCounterUah")
        val _columnIndexOfEndChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "endChargeCounterUah")
        val _columnIndexOfStartTemperatureC: Int = getColumnIndexOrThrow(_stmt, "startTemperatureC")
        val _columnIndexOfMaxTemperatureC: Int = getColumnIndexOrThrow(_stmt, "maxTemperatureC")
        val _columnIndexOfAvgTemperatureC: Int = getColumnIndexOrThrow(_stmt, "avgTemperatureC")
        val _columnIndexOfAvgVoltageMv: Int = getColumnIndexOrThrow(_stmt, "avgVoltageMv")
        val _columnIndexOfAvgCurrentMa: Int = getColumnIndexOrThrow(_stmt, "avgCurrentMa")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "isCompleted")
        val _columnIndexOfEstimatedCapacityContribution: Int = getColumnIndexOrThrow(_stmt,
            "estimatedCapacityContribution")
        val _result: MutableList<ChargingSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargingSessionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpStartPercentage: Int
          _tmpStartPercentage = _stmt.getLong(_columnIndexOfStartPercentage).toInt()
          val _tmpEndPercentage: Int
          _tmpEndPercentage = _stmt.getLong(_columnIndexOfEndPercentage).toInt()
          val _tmpStartChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfStartChargeCounterUah)) {
            _tmpStartChargeCounterUah = null
          } else {
            _tmpStartChargeCounterUah = _stmt.getLong(_columnIndexOfStartChargeCounterUah)
          }
          val _tmpEndChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfEndChargeCounterUah)) {
            _tmpEndChargeCounterUah = null
          } else {
            _tmpEndChargeCounterUah = _stmt.getLong(_columnIndexOfEndChargeCounterUah)
          }
          val _tmpStartTemperatureC: Float
          _tmpStartTemperatureC = _stmt.getDouble(_columnIndexOfStartTemperatureC).toFloat()
          val _tmpMaxTemperatureC: Float
          _tmpMaxTemperatureC = _stmt.getDouble(_columnIndexOfMaxTemperatureC).toFloat()
          val _tmpAvgTemperatureC: Float
          _tmpAvgTemperatureC = _stmt.getDouble(_columnIndexOfAvgTemperatureC).toFloat()
          val _tmpAvgVoltageMv: Int
          _tmpAvgVoltageMv = _stmt.getLong(_columnIndexOfAvgVoltageMv).toInt()
          val _tmpAvgCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfAvgCurrentMa)) {
            _tmpAvgCurrentMa = null
          } else {
            _tmpAvgCurrentMa = _stmt.getDouble(_columnIndexOfAvgCurrentMa).toFloat()
          }
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpIsCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          _tmpIsCompleted = _tmp != 0
          val _tmpEstimatedCapacityContribution: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedCapacityContribution)) {
            _tmpEstimatedCapacityContribution = null
          } else {
            _tmpEstimatedCapacityContribution =
                _stmt.getDouble(_columnIndexOfEstimatedCapacityContribution).toFloat()
          }
          _item =
              ChargingSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpStartPercentage,_tmpEndPercentage,_tmpStartChargeCounterUah,_tmpEndChargeCounterUah,_tmpStartTemperatureC,_tmpMaxTemperatureC,_tmpAvgTemperatureC,_tmpAvgVoltageMv,_tmpAvgCurrentMa,_tmpSourceName,_tmpIsCompleted,_tmpEstimatedCapacityContribution)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getSessionsSince(sinceTimestamp: Long): Flow<List<ChargingSessionEntity>> {
    val _sql: String =
        "SELECT * FROM charging_sessions WHERE startTime >= ? ORDER BY startTime DESC"
    return createFlow(__db, false, arrayOf("charging_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sinceTimestamp)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfStartPercentage: Int = getColumnIndexOrThrow(_stmt, "startPercentage")
        val _columnIndexOfEndPercentage: Int = getColumnIndexOrThrow(_stmt, "endPercentage")
        val _columnIndexOfStartChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "startChargeCounterUah")
        val _columnIndexOfEndChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "endChargeCounterUah")
        val _columnIndexOfStartTemperatureC: Int = getColumnIndexOrThrow(_stmt, "startTemperatureC")
        val _columnIndexOfMaxTemperatureC: Int = getColumnIndexOrThrow(_stmt, "maxTemperatureC")
        val _columnIndexOfAvgTemperatureC: Int = getColumnIndexOrThrow(_stmt, "avgTemperatureC")
        val _columnIndexOfAvgVoltageMv: Int = getColumnIndexOrThrow(_stmt, "avgVoltageMv")
        val _columnIndexOfAvgCurrentMa: Int = getColumnIndexOrThrow(_stmt, "avgCurrentMa")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "isCompleted")
        val _columnIndexOfEstimatedCapacityContribution: Int = getColumnIndexOrThrow(_stmt,
            "estimatedCapacityContribution")
        val _result: MutableList<ChargingSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargingSessionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpStartPercentage: Int
          _tmpStartPercentage = _stmt.getLong(_columnIndexOfStartPercentage).toInt()
          val _tmpEndPercentage: Int
          _tmpEndPercentage = _stmt.getLong(_columnIndexOfEndPercentage).toInt()
          val _tmpStartChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfStartChargeCounterUah)) {
            _tmpStartChargeCounterUah = null
          } else {
            _tmpStartChargeCounterUah = _stmt.getLong(_columnIndexOfStartChargeCounterUah)
          }
          val _tmpEndChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfEndChargeCounterUah)) {
            _tmpEndChargeCounterUah = null
          } else {
            _tmpEndChargeCounterUah = _stmt.getLong(_columnIndexOfEndChargeCounterUah)
          }
          val _tmpStartTemperatureC: Float
          _tmpStartTemperatureC = _stmt.getDouble(_columnIndexOfStartTemperatureC).toFloat()
          val _tmpMaxTemperatureC: Float
          _tmpMaxTemperatureC = _stmt.getDouble(_columnIndexOfMaxTemperatureC).toFloat()
          val _tmpAvgTemperatureC: Float
          _tmpAvgTemperatureC = _stmt.getDouble(_columnIndexOfAvgTemperatureC).toFloat()
          val _tmpAvgVoltageMv: Int
          _tmpAvgVoltageMv = _stmt.getLong(_columnIndexOfAvgVoltageMv).toInt()
          val _tmpAvgCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfAvgCurrentMa)) {
            _tmpAvgCurrentMa = null
          } else {
            _tmpAvgCurrentMa = _stmt.getDouble(_columnIndexOfAvgCurrentMa).toFloat()
          }
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpIsCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          _tmpIsCompleted = _tmp != 0
          val _tmpEstimatedCapacityContribution: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedCapacityContribution)) {
            _tmpEstimatedCapacityContribution = null
          } else {
            _tmpEstimatedCapacityContribution =
                _stmt.getDouble(_columnIndexOfEstimatedCapacityContribution).toFloat()
          }
          _item =
              ChargingSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpStartPercentage,_tmpEndPercentage,_tmpStartChargeCounterUah,_tmpEndChargeCounterUah,_tmpStartTemperatureC,_tmpMaxTemperatureC,_tmpAvgTemperatureC,_tmpAvgVoltageMv,_tmpAvgCurrentMa,_tmpSourceName,_tmpIsCompleted,_tmpEstimatedCapacityContribution)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getValidSessionsForEstimation(): List<ChargingSessionEntity> {
    val _sql: String =
        "SELECT * FROM charging_sessions WHERE isCompleted = 1 AND estimatedCapacityContribution IS NOT NULL ORDER BY startTime DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfStartPercentage: Int = getColumnIndexOrThrow(_stmt, "startPercentage")
        val _columnIndexOfEndPercentage: Int = getColumnIndexOrThrow(_stmt, "endPercentage")
        val _columnIndexOfStartChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "startChargeCounterUah")
        val _columnIndexOfEndChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "endChargeCounterUah")
        val _columnIndexOfStartTemperatureC: Int = getColumnIndexOrThrow(_stmt, "startTemperatureC")
        val _columnIndexOfMaxTemperatureC: Int = getColumnIndexOrThrow(_stmt, "maxTemperatureC")
        val _columnIndexOfAvgTemperatureC: Int = getColumnIndexOrThrow(_stmt, "avgTemperatureC")
        val _columnIndexOfAvgVoltageMv: Int = getColumnIndexOrThrow(_stmt, "avgVoltageMv")
        val _columnIndexOfAvgCurrentMa: Int = getColumnIndexOrThrow(_stmt, "avgCurrentMa")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "isCompleted")
        val _columnIndexOfEstimatedCapacityContribution: Int = getColumnIndexOrThrow(_stmt,
            "estimatedCapacityContribution")
        val _result: MutableList<ChargingSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ChargingSessionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpStartPercentage: Int
          _tmpStartPercentage = _stmt.getLong(_columnIndexOfStartPercentage).toInt()
          val _tmpEndPercentage: Int
          _tmpEndPercentage = _stmt.getLong(_columnIndexOfEndPercentage).toInt()
          val _tmpStartChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfStartChargeCounterUah)) {
            _tmpStartChargeCounterUah = null
          } else {
            _tmpStartChargeCounterUah = _stmt.getLong(_columnIndexOfStartChargeCounterUah)
          }
          val _tmpEndChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfEndChargeCounterUah)) {
            _tmpEndChargeCounterUah = null
          } else {
            _tmpEndChargeCounterUah = _stmt.getLong(_columnIndexOfEndChargeCounterUah)
          }
          val _tmpStartTemperatureC: Float
          _tmpStartTemperatureC = _stmt.getDouble(_columnIndexOfStartTemperatureC).toFloat()
          val _tmpMaxTemperatureC: Float
          _tmpMaxTemperatureC = _stmt.getDouble(_columnIndexOfMaxTemperatureC).toFloat()
          val _tmpAvgTemperatureC: Float
          _tmpAvgTemperatureC = _stmt.getDouble(_columnIndexOfAvgTemperatureC).toFloat()
          val _tmpAvgVoltageMv: Int
          _tmpAvgVoltageMv = _stmt.getLong(_columnIndexOfAvgVoltageMv).toInt()
          val _tmpAvgCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfAvgCurrentMa)) {
            _tmpAvgCurrentMa = null
          } else {
            _tmpAvgCurrentMa = _stmt.getDouble(_columnIndexOfAvgCurrentMa).toFloat()
          }
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpIsCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          _tmpIsCompleted = _tmp != 0
          val _tmpEstimatedCapacityContribution: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedCapacityContribution)) {
            _tmpEstimatedCapacityContribution = null
          } else {
            _tmpEstimatedCapacityContribution =
                _stmt.getDouble(_columnIndexOfEstimatedCapacityContribution).toFloat()
          }
          _item =
              ChargingSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpStartPercentage,_tmpEndPercentage,_tmpStartChargeCounterUah,_tmpEndChargeCounterUah,_tmpStartTemperatureC,_tmpMaxTemperatureC,_tmpAvgTemperatureC,_tmpAvgVoltageMv,_tmpAvgCurrentMa,_tmpSourceName,_tmpIsCompleted,_tmpEstimatedCapacityContribution)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestSession(): ChargingSessionEntity? {
    val _sql: String = "SELECT * FROM charging_sessions ORDER BY startTime DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfStartPercentage: Int = getColumnIndexOrThrow(_stmt, "startPercentage")
        val _columnIndexOfEndPercentage: Int = getColumnIndexOrThrow(_stmt, "endPercentage")
        val _columnIndexOfStartChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "startChargeCounterUah")
        val _columnIndexOfEndChargeCounterUah: Int = getColumnIndexOrThrow(_stmt,
            "endChargeCounterUah")
        val _columnIndexOfStartTemperatureC: Int = getColumnIndexOrThrow(_stmt, "startTemperatureC")
        val _columnIndexOfMaxTemperatureC: Int = getColumnIndexOrThrow(_stmt, "maxTemperatureC")
        val _columnIndexOfAvgTemperatureC: Int = getColumnIndexOrThrow(_stmt, "avgTemperatureC")
        val _columnIndexOfAvgVoltageMv: Int = getColumnIndexOrThrow(_stmt, "avgVoltageMv")
        val _columnIndexOfAvgCurrentMa: Int = getColumnIndexOrThrow(_stmt, "avgCurrentMa")
        val _columnIndexOfSourceName: Int = getColumnIndexOrThrow(_stmt, "sourceName")
        val _columnIndexOfIsCompleted: Int = getColumnIndexOrThrow(_stmt, "isCompleted")
        val _columnIndexOfEstimatedCapacityContribution: Int = getColumnIndexOrThrow(_stmt,
            "estimatedCapacityContribution")
        val _result: ChargingSessionEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpStartPercentage: Int
          _tmpStartPercentage = _stmt.getLong(_columnIndexOfStartPercentage).toInt()
          val _tmpEndPercentage: Int
          _tmpEndPercentage = _stmt.getLong(_columnIndexOfEndPercentage).toInt()
          val _tmpStartChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfStartChargeCounterUah)) {
            _tmpStartChargeCounterUah = null
          } else {
            _tmpStartChargeCounterUah = _stmt.getLong(_columnIndexOfStartChargeCounterUah)
          }
          val _tmpEndChargeCounterUah: Long?
          if (_stmt.isNull(_columnIndexOfEndChargeCounterUah)) {
            _tmpEndChargeCounterUah = null
          } else {
            _tmpEndChargeCounterUah = _stmt.getLong(_columnIndexOfEndChargeCounterUah)
          }
          val _tmpStartTemperatureC: Float
          _tmpStartTemperatureC = _stmt.getDouble(_columnIndexOfStartTemperatureC).toFloat()
          val _tmpMaxTemperatureC: Float
          _tmpMaxTemperatureC = _stmt.getDouble(_columnIndexOfMaxTemperatureC).toFloat()
          val _tmpAvgTemperatureC: Float
          _tmpAvgTemperatureC = _stmt.getDouble(_columnIndexOfAvgTemperatureC).toFloat()
          val _tmpAvgVoltageMv: Int
          _tmpAvgVoltageMv = _stmt.getLong(_columnIndexOfAvgVoltageMv).toInt()
          val _tmpAvgCurrentMa: Float?
          if (_stmt.isNull(_columnIndexOfAvgCurrentMa)) {
            _tmpAvgCurrentMa = null
          } else {
            _tmpAvgCurrentMa = _stmt.getDouble(_columnIndexOfAvgCurrentMa).toFloat()
          }
          val _tmpSourceName: String
          _tmpSourceName = _stmt.getText(_columnIndexOfSourceName)
          val _tmpIsCompleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsCompleted).toInt()
          _tmpIsCompleted = _tmp != 0
          val _tmpEstimatedCapacityContribution: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedCapacityContribution)) {
            _tmpEstimatedCapacityContribution = null
          } else {
            _tmpEstimatedCapacityContribution =
                _stmt.getDouble(_columnIndexOfEstimatedCapacityContribution).toFloat()
          }
          _result =
              ChargingSessionEntity(_tmpId,_tmpStartTime,_tmpEndTime,_tmpStartPercentage,_tmpEndPercentage,_tmpStartChargeCounterUah,_tmpEndChargeCounterUah,_tmpStartTemperatureC,_tmpMaxTemperatureC,_tmpAvgTemperatureC,_tmpAvgVoltageMv,_tmpAvgCurrentMa,_tmpSourceName,_tmpIsCompleted,_tmpEstimatedCapacityContribution)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteSessionsOlderThan(beforeTimestamp: Long) {
    val _sql: String = "DELETE FROM charging_sessions WHERE startTime < ?"
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
    val _sql: String = "DELETE FROM charging_sessions"
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
