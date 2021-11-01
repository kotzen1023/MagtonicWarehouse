package com.magtonic.magtonicwarehouse.persistence

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OutsourcedSignedData::class], version = 2, exportSchema = true)

abstract class OutsourcedSignedDataDB : RoomDatabase(){
    companion object {
        const val DATABASE_NAME = "outsourcedSign.db"
    }

    abstract fun outsourcedSignedDataDao(): OutsourcedSignedDataDao
}