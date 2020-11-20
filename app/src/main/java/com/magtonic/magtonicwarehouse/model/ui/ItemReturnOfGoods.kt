package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJReturnOfGoods


class ItemReturnOfGoods {
    var rjReturnOfGoods: RJReturnOfGoods? = RJReturnOfGoods()

    companion object {
        const val RESULT_CORRECT = "0"

        fun tranRJReturnOfGoodsStrToItemReturnOfGoods(RJItemReturnOfGoodsStr: String): ItemReturnOfGoods? {
            val gson = Gson()
            val itemReturnOfGoods = ItemReturnOfGoods()
            val rjReturnOfGoods: RJReturnOfGoods // = new RJReceipt();
            try {
                rjReturnOfGoods = gson.fromJson<Any>(RJItemReturnOfGoodsStr, RJReturnOfGoods::class.java) as RJReturnOfGoods
                itemReturnOfGoods.rjReturnOfGoods = rjReturnOfGoods
            } catch (ex: Exception) {
                return null
            }

            return itemReturnOfGoods
        }//trans_RJReceiptStr_To_ItemReceipt

    }
}