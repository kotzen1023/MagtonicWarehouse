package com.magtonic.magtonicwarehouse.data

class ReceiptConfirmFailLog(code: String, pmn01: String, occurDate: String, occurTime: String, reason: String) {
    private var code: String ?= code
    private var pmn01: String ?= pmn01
    private var occurDate: String ?= occurDate
    private var occurTime: String ?= occurTime
    private var reason: String ?= reason

    fun getCode(): String? {
        return code
    }

    fun getPmn01(): String? {
        return pmn01
    }

    fun getOccurDate(): String? {
        return occurDate
    }

    fun getOccurTime(): String? {
        return occurTime
    }

    fun getReason(): String? {
        return reason
    }
}