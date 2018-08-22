package com.tip.itso.z.vision

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_security_screen.*
import android.content.Context.INPUT_METHOD_SERVICE
import android.telephony.SignalStrength


class SecurityScreen : AppCompatActivity() {

    var securityKey:String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_screen)


        if(DataManager.isPrefsInitialized == false)
        {
            DataManager.initializePrefs(this.applicationContext)
            DataManager.updateKeyAndValidityData("1234", "062018")
        }

        sp_0.setOnEditorActionListener() {
            v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                sp_1.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(sp_1, InputMethodManager.SHOW_IMPLICIT)
            }
            else
            {
                false
            }
        }

        sp_1.setOnEditorActionListener() {
            v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                sp_2.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(sp_2, InputMethodManager.SHOW_IMPLICIT)
            }
            else
            {
                false
            }
        }

        sp_2.setOnEditorActionListener() {
            v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE)
            {
                sp_3.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(sp_3, InputMethodManager.SHOW_IMPLICIT)
            }
            else
            {
                false
            }
        }
        vision_logo_solo.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                securityKey = sp_0.text.toString() + sp_1.text.toString() + sp_2.text.toString() + sp_3.text.toString()
                securityKey = securityKey!!.toUpperCase()
                Log.i("KEYS", securityKey + " - " + DataManager.secretKey)
                if(securityKey == DataManager.secretKey)
                {
                    sp_0.text.clear()
                    sp_1.text.clear()
                    sp_2.text.clear()
                    sp_3.text.clear()

                    val intent = Intent(applicationContext, MainScreen::class.java)
                    startActivity(intent)
                }
                else
                {
                    sp_0.text.clear()
                    sp_1.text.clear()
                    sp_2.text.clear()
                    sp_3.text.clear()
                }
            }
        })
    }


}