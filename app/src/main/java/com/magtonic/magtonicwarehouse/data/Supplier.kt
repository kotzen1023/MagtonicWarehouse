package com.magtonic.magtonicwarehouse.data

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
class Supplier (name: String, uniNumber: String){

    var name: String? = name
    var uniNumber: String? = uniNumber
    var key: String? =""

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "uniNumber" to uniNumber
        )
    }


}