package com.idleoffice.coinwatch.data.model.coindata

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "coin_data")
data class CoinData(
        @ColumnInfo(name = "currency") var currency: String,
        @ColumnInfo(name = "coin_type") var coinType: String,
        @ColumnInfo(name = "amount") var amount: Long,
        @ColumnInfo(name = "date") @PrimaryKey var date: Long) {
}
