package com.magtonic.magtonicwarehouse.data

import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView

class OutsourcedProcessLowerPartItem(header: String, contentStatic: String, contentDynamic: String) {
    private var header: String? = header
    private var contentStatic: String? = contentStatic
    private var contentDynamic: String? = contentDynamic

    private var textViewStatic: TextView? = null
    private var textViewDynamic: TextView? = null
    private var linearLayout: LinearLayout? = null
    private var editText: EditText? = null
    private var btnOk: Button? = null
    private var changed: Boolean = false
    private var checked: Boolean = false

    fun getHeader(): String? {
        return header
    }

    fun setHeader(header: String) {
        this.header = header
    }

    fun getContentStatic(): String? {
        return contentStatic
    }

    fun setContentStatic(contentStatic: String) {
        this.contentStatic = contentStatic
    }

    fun getContentDynamic(): String? {
        return contentDynamic
    }

    fun setContentDynamic(contentDynamic: String) {
        this.contentDynamic = contentDynamic
    }

    fun getTextViewStatic(): TextView? {
        return textViewStatic
    }

    fun setTextViewStatic(textViewStatic: TextView) {
        this.textViewStatic = textViewStatic
    }

    fun getTextViewDynamic(): TextView? {
        return textViewStatic
    }

    fun setTextViewDynamic(textViewDynamic: TextView) {
        this.textViewDynamic = textViewDynamic
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

    fun getChecked(): Boolean {
        return checked
    }

    fun setChecked(checked: Boolean) {
        this.checked = checked
    }
}