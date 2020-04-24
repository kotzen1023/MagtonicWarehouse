package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJStorage

class ItemStorage {
    enum class ItemState {
        INITIAL,
        UPLOADED,
        UPLOAD_FAILED,
    }

    var rjStorage: RJStorage? = RJStorage()

    var rvb01 = ""
    var rvb02 = ""//

    //var checkbool: Boolean? = false //是否入庫
    var state = ItemState.INITIAL
    companion object {
        //val RESULT_CORRECT = "1"

        fun transRJStorageStrToItemStorage(RJStorageStr: String): ItemStorage? {
            val gson = Gson()
            val itemStorage = ItemStorage()
            val rjStorage: RJStorage // = new RJReceipt();
            try {
                rjStorage = gson.fromJson<Any>(RJStorageStr, RJStorage::class.java) as RJStorage
                itemStorage.rjStorage = rjStorage
                itemStorage.rvb01 = rjStorage.rvb01
                itemStorage.rvb02 = rjStorage.rvb02


            } catch (ex: Exception) {
                return null
            }

            return itemStorage
        }
    }
}