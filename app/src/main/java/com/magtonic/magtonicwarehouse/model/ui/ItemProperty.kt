package com.magtonic.magtonicwarehouse.model.ui

import com.google.gson.Gson
import com.magtonic.magtonicwarehouse.model.receive.RJProperty

class ItemProperty {
    var rjProperty: RJProperty? = RJProperty()

    companion object {
        //val RESULT_CORRECT = "1"

        fun transRJPropertyStrToItemProperty(RJPropertyStr: String): ItemProperty? {
            val gson = Gson()
            val itemProperty = ItemProperty()
            val rjProperty: RJProperty // = new RJProperty();
            try {
                rjProperty = gson.fromJson<Any>(RJPropertyStr, RJProperty::class.java) as RJProperty
                itemProperty.rjProperty = rjProperty
                //itemStorage.rvb01 = rjStorage.rvb01
                //itemStorage.rvb02 = rjStorage.rvb02


            } catch (ex: Exception) {
                return null
            }

            return itemProperty
        }
    }
}