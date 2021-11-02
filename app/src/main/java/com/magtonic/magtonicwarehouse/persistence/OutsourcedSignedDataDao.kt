package com.magtonic.magtonicwarehouse.persistence
import androidx.room.*

@Dao
interface OutsourcedSignedDataDao {
    @Query("SELECT * FROM " + OutsourcedSignedData.TABLE_NAME)

    fun getAll(): List<OutsourcedSignedData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(outsourcedSignedData: OutsourcedSignedData)

    @Query("SELECT * FROM " + OutsourcedSignedData.TABLE_NAME + " WHERE sendOrderWareHouse LIKE :sendOrderWareHouse")
    fun getOutsourcedSignedBySendOrderWareHouse(sendOrderWareHouse: String): OutsourcedSignedData

    @Query("SELECT * FROM " + OutsourcedSignedData.TABLE_NAME + " WHERE sendOrder LIKE :sendOrder")
    fun getOutsourcedSignedBySendOrder(sendOrder: String): List<OutsourcedSignedData>

    @Update
    fun update(sutsourcedSignedData: OutsourcedSignedData)

    @Query("DELETE FROM " + OutsourcedSignedData.TABLE_NAME + " WHERE 1")
    fun clearTable()
}