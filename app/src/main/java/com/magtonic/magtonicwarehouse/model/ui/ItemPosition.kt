package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJPosition

class ItemPosition {
    var rjPosition: RJPosition? = RJPosition()

    companion object {
        const val RESULT_CORRECT = "0"

        fun transRJPositionStrToItemPosition(RJPositionStr: String): ItemPosition? {
            val gson = Gson()
            val itemPosition = ItemPosition()
            val rjPosition: RJPosition
            try {
                rjPosition = gson.fromJson<Any>(RJPositionStr, RJPosition::class.java) as RJPosition
                itemPosition.rjPosition = rjPosition
            } catch (ex: Exception) {
                return null
            }

            return itemPosition
        }//trans_RJReceiptStr_To_ItemReceipt

    }
}