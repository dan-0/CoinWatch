package com.idleoffice.coinwatch.data.model.coindata

import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE


@Dao
interface CoinDataDao {
    @Query("SELECT * FROM coin_data")
    fun getAllCoinData(): List<CoinData>

    @Query("SELECT * FROM coin_data WHERE id = :p0")
    fun findCoinDataById(id: Long): CoinData

    @Insert(onConflict = REPLACE)
    fun insertCoinData(coinData: CoinData)

    @Update(onConflict = REPLACE)
    fun updateCoinData(coinData: CoinData)

    @Delete
    fun deleteCoinData(coinData: CoinData)
}