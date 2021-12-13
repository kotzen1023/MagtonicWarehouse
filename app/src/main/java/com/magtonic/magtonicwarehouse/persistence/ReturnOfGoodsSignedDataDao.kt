package com.magtonic.magtonicwarehouse.persistence
import androidx.room.*

@Dao
interface ReturnOfGoodsSignedDataDao {
    @Query("SELECT * FROM " + ReturnOfGoodsSignedData.TABLE_NAME)
    fun getAll(): List<ReturnOfGoodsSignedData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(returnOfGoodsSignedData: ReturnOfGoodsSignedData)

    @Query("SELECT * FROM " + ReturnOfGoodsSignedData.TABLE_NAME + " WHERE sendOrderWareHouse LIKE :sendOrderWareHouse")
    fun getReturnOfGoodsSignedBySendOrderWareHouse(sendOrderWareHouse: String): ReturnOfGoodsSignedData

    @Query("SELECT * FROM " + ReturnOfGoodsSignedData.TABLE_NAME + " WHERE sendOrder LIKE :sendOrder")
    fun getReturnOfGoodsSignedBySendOrder(sendOrder: String): List<ReturnOfGoodsSignedData>

    @Update
    fun update(returnOfGoodsSignedData: ReturnOfGoodsSignedData)

    @Query("DELETE FROM " + ReturnOfGoodsSignedData.TABLE_NAME + " WHERE 1")
    fun clearTable()

    @Query("DELETE FROM " + ReturnOfGoodsSignedData.TABLE_NAME + " WHERE timeStamp < :timeStampOlderThan7day")
    fun clearOlderThan7days(timeStampOlderThan7day: Long): Int
}