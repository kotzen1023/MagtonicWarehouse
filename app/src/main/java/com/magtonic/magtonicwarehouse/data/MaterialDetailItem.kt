package com.magtonic.magtonicwarehouse.data

import android.widget.*

class MaterialDetailItem (header: String, content: String) {
    private var header: String? = header
    private var content: String? = content
    private var textView: TextView? = null
    private var linearLayout: LinearLayout? = null
    private var editText: EditText? = null
    private var btnOk: Button? = null
    private var changed: Boolean = false
    private var icon: ImageView? = null

    fun getHeader(): String? {
        return header
    }

    fun setHeader(header: String) {
        this.header = header
    }

    fun getContent(): String? {
        return content
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun getTextView(): TextView? {
        return textView
    }

    fun setTextView(textView: TextView) {
        this.textView = textView
    }

    fun getLinearLayout(): LinearLayout? {
        return linearLayout
    }

    fun setLinearLayout(linearLayout: LinearLayout) {
        this.linearLayout = linearLayout
    }

    fun getEditText(): EditText? {
        return editText
    }

    fun setEditText(editText: EditText) {
        this.editText = editText
    }

    fun getBtnOk(): Button? {
        return btnOk
    }

    fun setBtnOk(btnOk: Button) {
        this.btnOk = btnOk
    }

    fun getChange(): Boolean {
        return changed
    }

    fun setChange(changed: Boolean) {
        this.changed = changed
    }

    fun getIcon(): ImageView? {
        return icon
    }

    fun setIcon(icon: ImageView) {
        this.icon = icon
    }
}