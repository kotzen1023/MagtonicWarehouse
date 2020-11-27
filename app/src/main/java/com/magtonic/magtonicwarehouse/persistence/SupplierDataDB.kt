package com.magtonic.magtonicwarehouse.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SupplierData::class], version = 2, exportSchema = true)

abstract class SupplierDataDB : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "supplier.db"
    }

    abstract fun supplierDataDao(): SupplierDataDao
}