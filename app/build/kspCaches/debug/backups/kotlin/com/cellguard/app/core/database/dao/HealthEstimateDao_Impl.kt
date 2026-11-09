package com.cellguard.app.core.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.cellguard.app.core.database.entity.HealthEstimateEntity
import javax.`annotation`.processing.Generated
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
public class HealthEstimateDao_Impl(
  __db: RoomDatabase,
) : HealthEstimateDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHealthEstimateEntity: EntityInsertAdapter<HealthEstimateEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHealthEstimateEntity = object :
        EntityInsertAdapter<HealthEstimateEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `health_estimates` (`id`,`timestamp`,`estimatedFullCapacityMah`,`verifiedDesignCapacityMah`,`healthPercentage`,`conditionName`,`confidenceName`,`cycleCount`,`estimatedLossPct`,`validSessionsCount`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HealthEstimateEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.timestamp)
        val _tmpEstimatedFullCapacityMah: Float? = entity.estimatedFullCapacityMah
        if (_tmpEstimatedFullCapacityMah == null) {
          statement.bindNull(3)
        } else {
          statement.bindDouble(3, _tmpEstimatedFullCapacityMah.toDouble())
        }
        val _tmpVerifiedDesignCapacityMah: Int? = entity.verifiedDesignCapacityMah
        if (_tmpVerifiedDesignCapacityMah == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpVerifiedDesignCapacityMah.toLong())
        }
        val _tmpHealthPercentage: Float? = entity.healthPercentage
        if (_tmpHealthPercentage == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpHealthPercentage.toDouble())
        }
        statement.bindText(6, entity.conditionName)
        statement.bindText(7, entity.confidenceName)
        val _tmpCycleCount: Int? = entity.cycleCount
        if (_tmpCycleCount == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpCycleCount.toLong())
        }
        val _tmpEstimatedLossPct: Float? = entity.estimatedLossPct
        if (_tmpEstimatedLossPct == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpEstimatedLossPct.toDouble())
        }
        statement.bindLong(10, entity.validSessionsCount.toLong())
      }
    }
  }

  public override suspend fun insertEstimate(estimate: HealthEstimateEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfHealthEstimateEntity.insertAndReturnId(_connection,
        estimate)
    _result
  }

  public override fun getLatestEstimate(): Flow<HealthEstimateEntity?> {
    val _sql: String = "SELECT * FROM health_estimates ORDER BY timestamp DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("health_estimates")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfEstimatedFullCapacityMah: Int = getColumnIndexOrThrow(_stmt,
            "estimatedFullCapacityMah")
        val _columnIndexOfVerifiedDesignCapacityMah: Int = getColumnIndexOrThrow(_stmt,
            "verifiedDesignCapacityMah")
        val _columnIndexOfHealthPercentage: Int = getColumnIndexOrThrow(_stmt, "healthPercentage")
        val _columnIndexOfConditionName: Int = getColumnIndexOrThrow(_stmt, "conditionName")
        val _columnIndexOfConfidenceName: Int = getColumnIndexOrThrow(_stmt, "confidenceName")
        val _columnIndexOfCycleCount: Int = getColumnIndexOrThrow(_stmt, "cycleCount")
        val _columnIndexOfEstimatedLossPct: Int = getColumnIndexOrThrow(_stmt, "estimatedLossPct")
        val _columnIndexOfValidSessionsCount: Int = getColumnIndexOrThrow(_stmt,
            "validSessionsCount")
        val _result: HealthEstimateEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpEstimatedFullCapacityMah: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedFullCapacityMah)) {
            _tmpEstimatedFullCapacityMah = null
          } else {
            _tmpEstimatedFullCapacityMah =
                _stmt.getDouble(_columnIndexOfEstimatedFullCapacityMah).toFloat()
          }
          val _tmpVerifiedDesignCapacityMah: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedDesignCapacityMah)) {
            _tmpVerifiedDesignCapacityMah = null
          } else {
            _tmpVerifiedDesignCapacityMah =
                _stmt.getLong(_columnIndexOfVerifiedDesignCapacityMah).toInt()
          }
          val _tmpHealthPercentage: Float?
          if (_stmt.isNull(_columnIndexOfHealthPercentage)) {
            _tmpHealthPercentage = null
          } else {
            _tmpHealthPercentage = _stmt.getDouble(_columnIndexOfHealthPercentage).toFloat()
          }
          val _tmpConditionName: String
          _tmpConditionName = _stmt.getText(_columnIndexOfConditionName)
          val _tmpConfidenceName: String
          _tmpConfidenceName = _stmt.getText(_columnIndexOfConfidenceName)
          val _tmpCycleCount: Int?
          if (_stmt.isNull(_columnIndexOfCycleCount)) {
            _tmpCycleCount = null
          } else {
            _tmpCycleCount = _stmt.getLong(_columnIndexOfCycleCount).toInt()
          }
          val _tmpEstimatedLossPct: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedLossPct)) {
            _tmpEstimatedLossPct = null
          } else {
            _tmpEstimatedLossPct = _stmt.getDouble(_columnIndexOfEstimatedLossPct).toFloat()
          }
          val _tmpValidSessionsCount: Int
          _tmpValidSessionsCount = _stmt.getLong(_columnIndexOfValidSessionsCount).toInt()
          _result =
              HealthEstimateEntity(_tmpId,_tmpTimestamp,_tmpEstimatedFullCapacityMah,_tmpVerifiedDesignCapacityMah,_tmpHealthPercentage,_tmpConditionName,_tmpConfidenceName,_tmpCycleCount,_tmpEstimatedLossPct,_tmpValidSessionsCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllEstimates(): Flow<List<HealthEstimateEntity>> {
    val _sql: String = "SELECT * FROM health_estimates ORDER BY timestamp ASC"
    return createFlow(__db, false, arrayOf("health_estimates")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfEstimatedFullCapacityMah: Int = getColumnIndexOrThrow(_stmt,
            "estimatedFullCapacityMah")
        val _columnIndexOfVerifiedDesignCapacityMah: Int = getColumnIndexOrThrow(_stmt,
            "verifiedDesignCapacityMah")
        val _columnIndexOfHealthPercentage: Int = getColumnIndexOrThrow(_stmt, "healthPercentage")
        val _columnIndexOfConditionName: Int = getColumnIndexOrThrow(_stmt, "conditionName")
        val _columnIndexOfConfidenceName: Int = getColumnIndexOrThrow(_stmt, "confidenceName")
        val _columnIndexOfCycleCount: Int = getColumnIndexOrThrow(_stmt, "cycleCount")
        val _columnIndexOfEstimatedLossPct: Int = getColumnIndexOrThrow(_stmt, "estimatedLossPct")
        val _columnIndexOfValidSessionsCount: Int = getColumnIndexOrThrow(_stmt,
            "validSessionsCount")
        val _result: MutableList<HealthEstimateEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HealthEstimateEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpEstimatedFullCapacityMah: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedFullCapacityMah)) {
            _tmpEstimatedFullCapacityMah = null
          } else {
            _tmpEstimatedFullCapacityMah =
                _stmt.getDouble(_columnIndexOfEstimatedFullCapacityMah).toFloat()
          }
          val _tmpVerifiedDesignCapacityMah: Int?
          if (_stmt.isNull(_columnIndexOfVerifiedDesignCapacityMah)) {
            _tmpVerifiedDesignCapacityMah = null
          } else {
            _tmpVerifiedDesignCapacityMah =
                _stmt.getLong(_columnIndexOfVerifiedDesignCapacityMah).toInt()
          }
          val _tmpHealthPercentage: Float?
          if (_stmt.isNull(_columnIndexOfHealthPercentage)) {
            _tmpHealthPercentage = null
          } else {
            _tmpHealthPercentage = _stmt.getDouble(_columnIndexOfHealthPercentage).toFloat()
          }
          val _tmpConditionName: String
          _tmpConditionName = _stmt.getText(_columnIndexOfConditionName)
          val _tmpConfidenceName: String
          _tmpConfidenceName = _stmt.getText(_columnIndexOfConfidenceName)
          val _tmpCycleCount: Int?
          if (_stmt.isNull(_columnIndexOfCycleCount)) {
            _tmpCycleCount = null
          } else {
            _tmpCycleCount = _stmt.getLong(_columnIndexOfCycleCount).toInt()
          }
          val _tmpEstimatedLossPct: Float?
          if (_stmt.isNull(_columnIndexOfEstimatedLossPct)) {
            _tmpEstimatedLossPct = null
          } else {
            _tmpEstimatedLossPct = _stmt.getDouble(_columnIndexOfEstimatedLossPct).toFloat()
          }
          val _tmpValidSessionsCount: Int
          _tmpValidSessionsCount = _stmt.getLong(_columnIndexOfValidSessionsCount).toInt()
          _item =
              HealthEstimateEntity(_tmpId,_tmpTimestamp,_tmpEstimatedFullCapacityMah,_tmpVerifiedDesignCapacityMah,_tmpHealthPercentage,_tmpConditionName,_tmpConfidenceName,_tmpCycleCount,_tmpEstimatedLossPct,_tmpValidSessionsCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearAll() {
    val _sql: String = "DELETE FROM health_estimates"
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
