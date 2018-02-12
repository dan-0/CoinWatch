package com.idleoffice.coinwatch.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.idleoffice.coinwatch.data.model.coindata.CoinData
import com.idleoffice.coinwatch.data.model.coindata.CoinDataDao

@Database(entities = [CoinData::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDataDao(): CoinDataDao
}