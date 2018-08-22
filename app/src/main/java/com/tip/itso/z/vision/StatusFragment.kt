package com.tip.itso.z.vision


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_status_tab.*
import org.jetbrains.anko.Android
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.customView
import org.jetbrains.anko.editText
import org.jetbrains.anko.support.v4.alert
import java.sql.Timestamp
import java.util.*

class StatusFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_status_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_hide_app.setOnClickListener(View.OnClickListener { v: View? ->
            //activity?.finishAffinity()
            HideAppActivity.exitApplication(context);
        })

        btn_set_listener.setOnClickListener(View.OnClickListener { v: View? ->
            setNewReceiver()
        })

        toggle_location_sending_mode.setChecked(DataManager.isSMSLocationSendingOn!!)

        toggle_location_sending_mode.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                DataManager.isSMSLocationSendingOn = false;
            } else {
                DataManager.isSMSLocationSendingOn = true;
            }
        }

        session_toggle.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                startNewSession()
                //session_toggle.setBackgroundColor(R.color.red)
            } else {
                stopCurrentSession()
                // session_toggle.setBackgroundColor(R.color.army_brown)
            }
        }

        startLocationServices()
    }

    fun startNewSession() {
        DataManager.isSessionInProgress = true;
        Log.d(StatusFragment::class.simpleName, "Data Manager Start New Session: " + DataManager.isSessionInProgress.toString())
        if (getSessionValidity().equals(DataManager.sesionValidity)) {
            var newSessionNumber = DataManager.currentSessionNumber!! + 1
            DataManager.currentSessionNumber = newSessionNumber
            DataManager.sessionID = newSessionNumber.toString().padStart(10, '0')
        } else {
            var newSessionNumber = 1
            DataManager.currentSessionNumber = newSessionNumber
            DataManager.sessionID = newSessionNumber.toString().padStart(10, '0')
            DataManager.sesionValidity = getSessionValidity()
        }
    }

    fun stopCurrentSession() {
        DataManager.isSessionInProgress = false;
        Log.d(StatusFragment::class.simpleName, "Data Manager Stop Current Session: " + DataManager.isSessionInProgress.toString())
    }

    fun getSessionValidity(): String {
        val millis = System.currentTimeMillis()

        val stamp = Timestamp(millis)
        val date = java.util.Date(stamp.time)

        var cal: Calendar = Calendar.getInstance()
        cal.time = date

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)

        var validity = year.toString() + month.toString() + day.toString()

        return validity
    }

    fun startLocationServices() {
        context!!.startService(Intent(context, LocationTrackingService::class.java))
    }

    fun stopLocationServices() {
        context!!.stopService(Intent(context, LocationTrackingService::class.java))
    }

    fun setNewReceiver() {
        alert {
            var editText: EditText? = null
            title = "Set Listener"

            customView {
                editText = editText {
                    hint = "+639123456789"
                }
            }

            positiveButton("Set number as listener.") {
                DataManager.destinationNumber = "${editText!!.text}"
                Log.d("mysms", DataManager.destinationNumber)
            }
        }.show()
    }
}
