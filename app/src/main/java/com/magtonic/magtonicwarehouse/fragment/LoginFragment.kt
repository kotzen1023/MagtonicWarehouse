package com.magtonic.magtonicwarehouse.fragment


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
//import android.support.v4.app.Fragment
import androidx.fragment.app.Fragment

import android.util.Log
import android.view.*
import android.widget.*
import com.magtonic.magtonicwarehouse.R

import android.widget.ProgressBar
import android.widget.RelativeLayout

import com.magtonic.magtonicwarehouse.MainActivity
import com.magtonic.magtonicwarehouse.MainActivity.Companion.isKeyBoardShow


import com.magtonic.magtonicwarehouse.data.Constants


class LoginFragment : Fragment() {
    private val mTAG = LoginFragment::class.java.name
    private var loginContext: Context? = null

    //private String account;
    //private String password;
    private var editTextAccount: EditText? = null
    private var editTextPassword: EditText? = null

    private var btnLogin: Button? = null

    private var progressBar: ProgressBar? = null
    private var relativeLayout: RelativeLayout? = null
    private var linearLayout: LinearLayout? = null

    companion object {
        //private val mTAG = LoginFragment::class.java.name

        private var mReceiver: BroadcastReceiver? = null
        private var isRegister = false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e(mTAG, "onCreate")

        loginContext = context

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d(mTAG, "onCreateView")

        val view = inflater.inflate(R.layout.fragment_login, container, false)


        //TextView textView = view.findViewById(R.id.textLogin);
        relativeLayout = view.findViewById(R.id.login_container)
        progressBar = ProgressBar(loginContext, null, android.R.attr.progressBarStyleLarge)
        val params = RelativeLayout.LayoutParams(MainActivity.screenHeight / 4, MainActivity.screenWidth / 4)
        params.addRule(RelativeLayout.CENTER_IN_PARENT)

        val localRelativeLayout: RelativeLayout? = relativeLayout
        if (localRelativeLayout != null) {
            localRelativeLayout.addView(progressBar, params)
        } else {
            Log.e(mTAG, "localRelativeLayout = null")
        }
        progressBar!!.visibility = View.GONE

        //detect soft keyboard
        linearLayout = view.findViewById(R.id.linearLayoutLogin)
        /*linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener(

            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    linearLayout!!.getWindowVisibleDisplayFrame(r)
                    val screenHeight = linearLayout!!.rootView.height
                    val keypadHeight = screenHeight - r.bottom
                    isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

                }
            }

        )*/
        linearLayout!!.viewTreeObserver.addOnGlobalLayoutListener {

            val r = Rect()
            linearLayout!!.getWindowVisibleDisplayFrame(r)
            val screenHeight = linearLayout!!.rootView.height
            val keypadHeight = screenHeight - r.bottom
            isKeyBoardShow = (keypadHeight > screenHeight * 0.15)

        }



        btnLogin = view.findViewById(R.id.btnLoginConfirm)

        editTextAccount = view.findViewById(R.id.accountInput)
        editTextPassword = view.findViewById(R.id.passwordInput)


        btnLogin!!.setOnClickListener {
            progressBar!!.visibility = View.VISIBLE
            btnLogin!!.isEnabled = false

            val account: EditText? = editTextAccount
            val password: EditText? = editTextPassword
            if (account != null && password != null) {

                if (account.text.isEmpty() || password.text.isEmpty()) {
                    if (account.text.isEmpty()) {
                        progressBar!!.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                        toast(resources.getString(R.string.login_account_empty))
                    } else {
                        progressBar!!.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                        toast(resources.getString(R.string.login_password_empty))
                    }
                } else {
                    Log.d(mTAG, "no other ")
                    val loginIntent = Intent()
                    loginIntent.action = Constants.ACTION.ACTION_LOGIN_ACTION
                    loginIntent.putExtra("account", account.text.toString())
                    loginIntent.putExtra("password", password.text.toString())
                    loginContext!!.sendBroadcast(loginIntent)
                }

                /*if (account.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    btnLogin!!.isEnabled = true
                    toast(resources.getString(R.string.login_account_empty))
                } else if (password.text.isEmpty()) {
                    progressBar!!.visibility = View.GONE
                    btnLogin!!.isEnabled = true
                    toast(resources.getString(R.string.login_password_empty))
                } else {
                    Log.d(mTAG, "no other ")
                    val loginIntent = Intent()
                    loginIntent.action = Constants.ACTION.ACTION_LOGIN_ACTION
                    loginIntent.putExtra("account", account.text.toString())
                    loginIntent.putExtra("password", password.text.toString())
                    loginContext!!.sendBroadcast(loginIntent)
                }*/
            }

            /*val intent = Intent(loginContext, CheckEmpExistService::class.java)
            intent.action = Constants.ACTION.ACTION_CHECK_EMP_EXIST_ACTION
            intent.putExtra("EMP_NO", editTextAccount!!.text.toString())
            //intent.putExtra("PASSWORD", editTextPassword.getText().toString());

            loginContext!!.startService(intent)*/
        }

        val filter: IntentFilter

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action != null) {
                    if (intent.action!!.equals(Constants.ACTION.ACTION_LOGIN_FRAGMENT_LOGIN_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_LOGIN_FRAGMENT_LOGIN_FAILED")

                        progressBar!!.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                    } else if (intent.action!!.equals(Constants.ACTION.ACTION_NETWORK_FAILED, ignoreCase = true)) {
                        Log.d(mTAG, "ACTION_NETWORK_FAILED")

                        progressBar!!.visibility = View.GONE
                        btnLogin!!.isEnabled = true
                    }
                }
            }
        }


        if (!isRegister) {
            filter = IntentFilter()
            filter.addAction(Constants.ACTION.ACTION_LOGIN_FRAGMENT_LOGIN_FAILED)
            filter.addAction(Constants.ACTION.ACTION_NETWORK_FAILED)
            loginContext?.registerReceiver(mReceiver, filter)
            isRegister = true
            Log.d(mTAG, "registerReceiver mReceiver")
        }



        return view
    }

    override fun onDestroyView() {
        Log.i(mTAG, "onDestroy")

        if (isRegister && mReceiver != null) {
            try {
                loginContext!!.unregisterReceiver(mReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            isRegister = false
            mReceiver = null
            Log.d(mTAG, "unregisterReceiver mReceiver")
        }

        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Log.i(mTAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

    }

    private fun toast(message: String) {
        val toast = Toast.makeText(loginContext, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.CENTER_VERTICAL, 0, 0)
        val group = toast.view as ViewGroup
        val textView = group.getChildAt(0) as TextView
        textView.textSize = 30.0f
        toast.show()
    }


}