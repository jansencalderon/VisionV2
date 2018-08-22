package com.tip.itso.z.vision

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object DataManager {

    var phoneNumber: String? = null
    //var destinationNumber: String = "+639157036733"


    var destinationUID: String = "yxna4FUjE2hfkLoRDcmOlLzN6xm1"
    var lastSentSms:Long = 0;
    var lastSentLocation: String = "NA"
    var isSessionInProgress: Boolean = false
    var sessionID: String = "NA"

    var destinationNumber: String?
        get() = prefs?.getString("destinationNumber", "+639157036733")
        set(value)
        {
            setDestinationNumberValue(value)
        }

    fun setDestinationNumberValue(value:String?)
    {
        val prefsEditor = prefs?.edit()

        if (prefsEditor != null) {
            prefsEditor.putString("destinationNumber", value!!)
            prefsEditor.apply()
        }
    }

    var sesionValidity:String?
        get() = prefs?.getString("sessionValidity", "NA")
        set(value)
        {
            setSessionValidityValue(value)
        }

    fun setSessionValidityValue(value:String?)
    {
        val prefsEditor = prefs?.edit()

        if (prefsEditor != null) {
            prefsEditor.putString("sessionValidity", value!!)
            prefsEditor.apply()
        }
    }

    var currentSessionNumber:Int?
        get() = prefs?.getInt("currentSessionNumber", 0)
        set(value)
        {
            setStoredSessionNumber(value)
        }

    fun setStoredSessionNumber(value: Int?)
    {
        val prefsEditor = prefs?.edit()

        if (prefsEditor != null) {
            prefsEditor.putInt("currentSessionNumber", value!!)
            prefsEditor.apply()
        }
    }

    var isSMSLocationSendingOn:Boolean?
        get() = prefs?.getBoolean("smsLocationSending", false)
        set(value)
        {
            setSmsLocationSendingValue(value)
        }

    fun setSmsLocationSendingValue(value:Boolean?)
    {
        val prefsEditor = prefs?.edit()

        if (prefsEditor != null) {
            prefsEditor.putBoolean("smsLocationSending", value!!)
            prefsEditor.apply()
        }
    }

    var isUSSDAlertsOn:Boolean? = false

    var isCommandsOn:Boolean? = false

    var secretKey:String?
        get() = prefs?.getString(secretKeyDataString, "1234");
        set(value)
        {
            val prefsEditor = prefs?.edit()

            if (prefsEditor != null) {
                prefsEditor.putString(secretKeyDataString, value!!)
                prefsEditor.apply()
            }
        }

    var keyValidity:String? = null;
    var isKeyReady:Boolean = false;
    var isKeyValid:Boolean = false;

    var prefs: SharedPreferences? = null;
    var isPrefsInitialized:Boolean = false;

    private const val secretKeyDataString:String = "yeKterces"
    private const val keyValidityDataString:String = "ytidilaVyek"

    public fun initializePrefs(context: Context)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        isPrefsInitialized = true;
    }

    public fun authenticateSecretKeyAndValidity()
    {
        if(isPrefsInitialized && isKeyReady)
        {

        }
    }

    public fun updateKeyAndValidityData(secretKey: String, keyValidity: String)
    {
        val prefsEditor = prefs?.edit()

        if (prefsEditor != null) {
            prefsEditor.putString(secretKeyDataString, secretKey)
            prefsEditor.putString(keyValidityDataString, keyValidity)
            prefsEditor.apply()
        }
    }
}