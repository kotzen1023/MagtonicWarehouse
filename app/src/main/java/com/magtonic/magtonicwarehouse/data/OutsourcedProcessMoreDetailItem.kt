package com.magtonic.magtonicwarehouse.data

class OutsourcedProcessMoreDetailItem(header: String, content: String) {

    private var header: String? = header
    private var content: String? = content

    fun getHeader(): String? {
        return header
    }

    fun getContent(): String? {
        return content
    }

    fun setContent(content: String) {
        this.content = content
    }
}