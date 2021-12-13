package com.magtonic.magtonicwarehouse.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ReturnOfGoodsSignedData::class], version = 2, exportSchema = true)

abstract class ReturnOfGoodsSignedDataDB : RoomDatabase() {
    companion object {
        const val DATABASE_NAME = "returnSign.db"
    }

    abstract fun returnOfGoodsSignedDataDao(): ReturnOfGoodsSignedDataDao
}