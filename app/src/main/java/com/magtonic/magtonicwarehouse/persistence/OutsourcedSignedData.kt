package com.magtonic.magtonicwarehouse.persistence

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = OutsourcedSignedData.TABLE_NAME)
class OutsourcedSignedData(sendOrder: String, wareHouse: String, timeStamp: Long) {
    companion object {
        const val TABLE_NAME = "outsourcedSignedList"
    }

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private var id: Int = 0

    @ColumnInfo(name = "sendOrder") //發料單號
    private var sendOrder: String? = ""

    @ColumnInfo(name = "wareHouse") //倉庫代號
    private var wareHouse: String? = ""

    @ColumnInfo(name = "timeStamp") //日期
    private var timeStamp: Long? = null

    init {
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