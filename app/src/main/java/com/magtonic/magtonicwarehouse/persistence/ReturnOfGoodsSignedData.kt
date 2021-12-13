package com.magtonic.magtonicwarehouse.persistence

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = ReturnOfGoodsSignedData.TABLE_NAME)
class ReturnOfGoodsSignedData(sendOrderWareHouse: String, sendOrder: String, wareHouse: String, timeStamp: Long) {
    companion object {
        const val TABLE_NAME = "returnOfGoodsSignedList"
    }

    @NonNull
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0

    @ColumnInfo(name = "sendOrderWareHouse") //退貨單號+倉庫代號
    private var sendOrderWareHouse: String? = ""

    @ColumnInfo(name = "sendOrder") //退貨單號
    private var sendOrder: String? = ""

    @ColumnInfo(name = "wareHouse") //倉庫代號
    private var wareHouse: String? = ""

    @ColumnInfo(name = "timeStamp") //日期
    private var timeStamp: Long? = null

    init {
        this.sendOrderWareHouse = sendOrderWareHouse
        this.sendOrder = sendOrder
        this.wareHouse = wareHouse
        this.timeStamp = timeStamp
    }

    fun getId(): Int {
        return id
    }

    fun setId(id : Int) {
        this.id = id
    }

    fun getSendOrderWareHouse(): String {
        return sendOrderWareHouse as String
    }

    fun setSendOrderWareHouse(sendOrderWareHouse: String) {
        this.sendOrderWareHouse = sendOrderWareHouse
    }

    fun getSendOrder(): String {
        return sendOrder as String
    }

    fun setSendOrder(sendOrder: String) {
        this.sendOrder = sendOrder
    }

    fun getWareHouse(): String {
        return wareHouse as String
    }

    fun setWareHouse(wareHouse: String) {
        this.wareHouse = wareHouse
    }

    fun getTimeStamp(): Long? {
        return timeStamp
    }

    fun setTimeStamp(timeStamp: Long) {
        this.timeStamp = timeStamp
    }
}