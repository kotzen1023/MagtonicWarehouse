package com.magtonic.magtonicwarehouse.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isSecurityGuard
import com.magtonic.magtonicwarehouse.R
import com.magtonic.magtonicwarehouse.data.Constants

class HomeFragment : Fragment() {
    private val mTAG = HomeFragment::class.java.name

    private var homeContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(mTAG, "onCreate")

        homeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val linearLayoutLine1 = view.findViewById<LinearLayout>(R.id.line1)
        val linearLayoutLine2 = view.findViewById<LinearLayout>(R.id.line2)
        val linearLayoutLine3 = view.findViewById<LinearLayout>(R.id.line3)

        val imageViewReceipt = view.findViewById<ImageView>(R.id.imageViewReceipt)
        val textViewReceipt = view.findViewById<TextView>(R.id.textViewReceipt)
        val imageViewStorage = view.findViewById<ImageView>(R.id.imageViewStorage)
        val textViewStorage = view.findViewById<TextView>(R.id.textViewStorage)
        val imageViewMaterial = view.findViewById<ImageView>(R.id.imageViewMaterial)
        val textViewMaterial = view.findViewById<TextView>(R.id.textViewMaterial)
        val imageViewProperty = view.findViewById<ImageView>(R.id.imageViewProperty)
        val textViewProperty = view.findViewById<TextView>(R.id.textViewProperty)
        val imageViewTagPrinter = view.findViewById<ImageView>(R.id.imageViewTagPrinter)
        val textViewTagPrinter = view.findViewById<TextView>(R.id.textViewTagPrinter)
        val imageViewLogout = view.findViewById<ImageView>(R.id.imageViewLogout)
        val textViewLogout = view.findViewById<TextView>(R.id.textViewLogout)
        val imageViewAbout = view.findViewById<ImageView>(R.id.imageViewAbout)
        val textViewAbout = view.findViewById<TextView>(R.id.textViewAbout)
        val imageViewSetting = view.findViewById<ImageView>(R.id.imageViewSetting)
        val textViewSetting = view.findViewById<TextView>(R.id.textViewSetting)
        val imageViewGuest = view.findViewById<ImageView>(R.id.imageViewGuest)
        val textViewGuest = view.findViewById<TextView>(R.id.textViewGuest)

        if (isSecurityGuard) {
            linearLayoutLine1.visibility = View.GONE
            linearLayoutLine2.visibility = View.GONE
            linearLayoutLine3.visibility = View.VISIBLE
        }

        imageViewReceipt.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_RECEIPT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewReceipt.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_RECEIPT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewStorage.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_STORAGE_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewStorage.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_STORAGE_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewMaterial.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_MATERIAL_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewMaterial.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_MATERIAL_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewProperty.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_PROPERTY_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewProperty.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_PROPERTY_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewTagPrinter.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_TAG_PRINTER_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewTagPrinter.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_TAG_PRINTER_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewLogout.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_LOGOUT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewLogout.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_LOGOUT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewAbout.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_ABOUT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewAbout.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_ABOUT_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewSetting.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_USER_SETTING_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewSetting.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_USER_SETTING_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        imageViewGuest.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_GUEST_ACTION
            homeContext!!.sendBroadcast(showIntent)
        }

        textViewGuest.setOnClickListener {
            val showIntent = Intent()
            showIntent.action = Constants.ACTION.ACTION_HOME_GO_TO_GUEST_ACTION
            homeContext!!.sendBroadcast(showIntent)
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