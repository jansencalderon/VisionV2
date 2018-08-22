package com.tip.itso.z.vision

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.telephony.TelephonyManager
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var PERMISSION_REQUEST:Int = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeDataManager()

        var read_sms_permission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        var send_sms_permission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        var receive_sms_permission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        var fine_location_permission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var boot_permission:Int = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED)
        val ussd_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS)
        val read_phone_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)

        if(read_sms_permission == PackageManager.PERMISSION_GRANTED &&
                send_sms_permission == PackageManager.PERMISSION_GRANTED &&
                receive_sms_permission == PackageManager.PERMISSION_GRANTED &&
                fine_location_permission == PackageManager.PERMISSION_GRANTED &&
                boot_permission == PackageManager.PERMISSION_GRANTED &&
                ussd_permission == PackageManager.PERMISSION_GRANTED &&
                read_phone_permission == PackageManager.PERMISSION_GRANTED)
        {
            /*val handler = Handler();
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_1),500, handler)
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_2),750, handler)
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_3),1000, handler)

            handler.postDelayed({*/
                DataManager.phoneNumber = phoneNumber()
                val intent = Intent(this, SecurityScreen::class.java)
                startActivity(intent)
            //}, 1250)
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_SMS,
                            Manifest.permission.SEND_SMS,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.RECEIVE_BOOT_COMPLETED,
                            Manifest.permission.PROCESS_OUTGOING_CALLS,
                            Manifest.permission.READ_PHONE_STATE),
                    PERMISSION_REQUEST)
        }
    }

    private fun initiateLoad(img:Drawable?, delay:Long, handler: Handler)
    {
        handler.postDelayed({
            loading_img.setImageDrawable(
                    img
            )
        }, delay)
    }

    private fun initializeDataManager()
    {
        DataManager.initializePrefs(this.applicationContext)
        DataManager.updateKeyAndValidityData("1234", "062018")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST && grantResults.size == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            val handler = Handler();
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_1),500, handler)
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_2),750, handler)
            initiateLoad(ContextCompat.getDrawable(applicationContext, R.drawable.loading_3),1000, handler)

            handler.postDelayed({
                val intent = Intent(this, SecurityScreen::class.java)
                startActivity(intent)
            }, 1250)
        } else {
            //finish()
        }
    }

    fun phoneNumber():String? {
        val tm = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        var phone_read_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
        Log.d("Phone", "getting number")
        if (phone_read_permission == PackageManager.PERMISSION_GRANTED)
        {
            val pNumber = tm.line1Number
            if(pNumber != null)
            {
                Log.d("Phone", tm.line1Number.toString() + " written")
                return pNumber.toString()
            }
        }
        return null
    }

}
