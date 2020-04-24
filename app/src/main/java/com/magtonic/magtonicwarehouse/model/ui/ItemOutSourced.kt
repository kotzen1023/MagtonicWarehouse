package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJOutSourced

class ItemOutSourced {
    var rjOutSourced: RJOutSourced? = RJOutSourced()

    companion object {
        const val RESULT_CORRECT = "0"

        fun tranRJOutSourceStrToItemOutSourced(RJItemOutSourcedStr: String): ItemOutSourced? {
            val gson = Gson()
            val itemOutSourced = ItemOutSourced()
            val rjOutSourced: RJOutSourced // = new RJReceipt();
            try {
                rjOutSourced = gson.fromJson<Any>(RJItemOutSourcedStr, RJOutSourced::class.java) as RJOutSourced
                itemOutSourced.rjOutSourced = rjOutSourced
            } catch (ex: Exception) {
                return null
            }

            return itemOutSourced
        }//trans_RJReceiptStr_To_ItemReceipt

    }
}