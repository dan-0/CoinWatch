package com.idleoffice.coinwatch.data.db

import android.arch.persistence.room.RoomDatabase
import com.idleoffice.coinwatch.data.model.coindata.CoinDataDao


abstract class AppDatabase : RoomDatabase() {
    abstract fun coinDataDao(): CoinDataDao
}