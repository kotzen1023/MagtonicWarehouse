package com.magtonic.magtonicwarehouse.persistence

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = SupplierData.TABLE_NAME)
class SupplierData (key: String, name: String, number: String){
    companion object {
        const val TABLE_NAME = "supplierList"
    }

    @NonNull
    @PrimaryKey(autoGenerate = false)
    private var key: String

    @ColumnInfo(name = "name")
    private var name: String? = ""

    @ColumnInfo(name = "number")
    private var number: String? = ""

    init {
        this.key = key
        this.name = name
        this.number = number
    }

    fun getKey(): String {
        return key as String
    }

    fun setKey(key : String) {
        this.key = key
    }

    fun getName(): String {
        return name as String
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getNumber(): String {
        return number as String
    }

    fun setNumber(number: String) {
        this.number = number
    }
}