package com.magtonic.magtonicwarehouse.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isReceiptUploadAutoConfirm
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.Constants

class UserSettingFragment : Fragment() {
    private val mTAG = UserSettingFragment::class.java.name
    private var userSettingContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSettingContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")



        val view = inflater.inflate(R.layout.fragment_user_setting, container, false)
        val receiptAutoConfirmUploaded = view.findViewById<CheckBox>(R.id.checkBoxReceiptAutoConfirmUploaded)

        receiptAutoConfirmUploaded.isChecked = isReceiptUploadAutoConfirm

        receiptAutoConfirmUploaded.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_ON
                userSettingContext!!.sendBroadcast(successIntent)
            } else {
                val successIntent = Intent()
                successIntent.action = Constants.ACTION.ACTION_SETTING_RECEIPT_AUTO_CONFIRM_UPLOADED_OFF
                userSettingContext!!.sendBroadcast(successIntent)
            }

        }


        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")



        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }
}