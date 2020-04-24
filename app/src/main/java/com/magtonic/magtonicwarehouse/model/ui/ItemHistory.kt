package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJHistory


class ItemHistory {
    var rjHistory: RJHistory? = RJHistory()

    var pmn01 = ""
    var pmn02 = ""

    companion object {
        const val RESULT_CORRECT = "1"

        fun transRJHistoryStrToItemHistory(RJHistoryStr: String, PONum_spilit: String, poLineInt: String): ItemHistory? {
            val gson = Gson()
            val itemHistory = ItemHistory()
            val rjHistory: RJHistory // = new RJReceipt()

            try {
                rjHistory = gson.fromJson<Any>(RJHistoryStr, RJHistory::class.java) as RJHistory
                itemHistory.rjHistory = rjHistory
                itemHistory.pmn01 = PONum_spilit
                itemHistory.pmn02 = poLineInt



            } catch (ex: Exception) {
                return null
            }

            return itemHistory
        }
    }
}