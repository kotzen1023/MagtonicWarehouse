package com.magtonic.magtonicwarehouse

import android.content.*


import android.os.Bundle

import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View

import android.widget.AdapterView
import android.widget.ListView


import androidx.appcompat.app.AppCompatActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.confirmFailLogList

import com.magtonic.magtonicwarehouse.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ShowReceiptConfirmFailedActivity : AppCompatActivity() {
    private val mTAG = ShowReceiptConfirmFailedActivity::class.java.name
    private var mContext: Context? = null

    private var mReceiver: BroadcastReceiver? = null
    private var isRegister = false

    private var receiptConfirmFailLogAdapter: ReceiptConfirmFailLogAdapter? = null
    private var receiptConfirmFailLogMoreDetailAdapter: ReceiptConfirmFailLogMoreDetailAdapter? = null
    private var listView: ListView?= null
    private var listViewDetail: ListView?= null

    private var receiptConfirmFailedMoreDetailList = ArrayList<ReceiptConfirmFailLogMoreDetail>()

    private var isReceiptConfirmFailedLogInDetail: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_confirm_failed_log_show)

        mContext = applicationContext
        listView = findViewById(R.id.listViewReceiptConfirmFailedList)
        listViewDetail = findViewById(R.id.listViewReceiptConfirmFailedDetail)

        listView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            Log.d(mTAG, "click $position")

            val moreDetailIntent = Intent()
            moreDetailIntent.action = Constants.ACTION.ACTION_RECEIPT_SHOW_CONFIRM_FAILED_MORE_DETAIL
            moreDetailIntent.putExtra("INDEX", position.toString())
            mContext?.sendBroadcast(moreDetailIntent)
        }

        if (mContext != null) {

            val currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            Log.e(mTAG, "currentDate = $currentDate")

            //val item0 = ReceiptConfirmFailLog("123", "AP22-19030050", "2020-06-01", "10:10:10", "test")
            //confirmFailLogList.add(item0)

            if (confirmFailLogList.size > 0) {
                val todayList: ArrayList<ReceiptConfirmFailLog> = ArrayList()

                //save data for today
                for (item in confirmFailLogList) {
                    if (item.getOccurDate() == currentDate) {
                        todayList.add(item)
                    }
                }

                //refresh current list
                confirmFailLogList.clear()
                for (item in todayList) {
                    confirmFailLogList.add(item)
                }
            } else {
                Log.e(mTAG, "confirmFailLogList = 0")
            }



            receiptConfirmFailLogAdapter = ReceiptConfirmFailLogAdapter(mContext, R.layout.fragment_receipt_fail_log_item, confirmFailLogList)
            listView!!.adapter = receiptConfirmFailLogAdapter

            //more detail
            receiptConfirmFailLogMoreDetailAdapter = ReceiptConfirmFailLogMoreDetailAdapter(mContext, R.layout.fragment_receipt_fail_log_more_detail_item, receiptConfirmFailedMoreDetailList)
            listViewDetail!!.adapter = receiptConfirmFailLogMoreDetailAdapter
        }

        //for action bar
        val actionBar: androidx.appcompat.app.ActionBar? = supportActionBar

        if (actionBar != null) {

            actionBar.setDisplayUseLogoEnabled(true)
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = getString(R.string.receipt_confirm_failed_history)

        }


        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_RECEIPT_SHOW_CONFIRM_FAILED_MORE_DETAIL, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_RECEIPT_SHOW_CONFIRM_FAILED_MORE_DETAIL")

                        val idxString = intent.getStringExtra("INDEX")

                        val idx = idxString?.toInt()

                        receiptConfirmFailedMoreDetailList.clear()

                        if (idx != null) {
                            val item0 = ReceiptConfirmFailLogMoreDetail("代碼", confirmFailLogList[idx].getCode() as String)
                            receiptConfirmFailedMoreDetailList.add(item0)
                            val item1 = ReceiptConfirmFailLogMoreDetail("採購單號", confirmFailLogList[idx].getPmn01() as String)
                            receiptConfirmFailedMoreDetailList.add(item1)
                            val item2 = ReceiptConfirmFailLogMoreDetail("日期", confirmFailLogList[idx].getOccurDate() as String)
                            receiptConfirmFailedMoreDetailList.add(item2)
                            val item3 = ReceiptConfirmFailLogMoreDetail("時間", confirmFailLogList[idx].getOccurTime() as String)
                            receiptConfirmFailedMoreDetailList.add(item3)
                            val item4 = ReceiptConfirmFailLogMoreDetail("原因", confirmFailLogList[idx].getReason() as String)
                            receiptConfirmFailedMoreDetailList.add(item4)

                        }


                        listView!!.visibility = View.GONE
                        listViewDetail!!.visibility = View.VISIBLE

                        isReceiptConfirmFailedLogInDetail = 1

                        if (receiptConfirmFailLogMoreDetailAdapter != null) {
                            receiptConfirmFailLogMoreDetailAdapter?.notifyDataSetChanged()
                        }

                    }

                }
            }
        }

        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_RECEIPT_SHOW_CONFIRM_FAILED_MORE_DETAIL)
            mContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }
    }

    override fun onDestroy() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                mContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroy()
    }

    override fun onResume() {
        Log.i(mTAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.i(mTAG, "onPause")
        super.onPause()

        //disable Scan2Key Setting
        val enableServiceIntent = Intent()
        enableServiceIntent.action = "unitech.scanservice.scan2key_setting"
        enableServiceIntent.putExtra("scan2key", true)
        sendBroadcast(enableServiceIntent)
    }

    override fun onBackPressed() {

        if (isReceiptConfirmFailedLogInDetail == 1) {
            isReceiptConfirmFailedLogInDetail = 0
            listView!!.visibility = View.VISIBLE
            listViewDetail!!.visibility = View.GONE
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menuInflater.inflate(R.menu.sign, menu)



        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            android.R.id.home-> {
                if (isReceiptConfirmFailedLogInDetail == 1) {
                    isReceiptConfirmFailedLogInDetail = 0
                    listView!!.visibility = View.VISIBLE
                    listViewDetail!!.visibility = View.GONE
                } else {
                    finish()
                }
            }
        }


        return true
    }
}