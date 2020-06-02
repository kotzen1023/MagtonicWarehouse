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

    fun setCode(code: String) {
        this.code = code
    }

    fun getPmn01(): String? {
        return pmn01
    }

    fun setPmn01(pmn01: String) {
        this.pmn01 = pmn01
    }

    fun getOccurDate(): String? {
        return occurDate
    }

    fun setOccurDate(occurDate: String) {
        this.occurDate = occurDate
    }

    fun getOccurTime(): String? {
        return occurTime
    }

    fun setOccurTime(occurTime: String) {
        this.occurTime = occurTime
    }

    fun getReason(): String? {
        return reason
    }

    fun setReason(reason: String) {
        this.reason = reason
    }
}