package com.tip.itso.z.vision;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class USSDCodesReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String num = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if(num.equals("**11##")) //Hide App
        {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        else if(num.equals("**22##")) //Show App
        {
            PackageManager packageManager = context.getPackageManager();
            ComponentName componentName = new ComponentName(context, MainActivity.class);
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        }
        else if(num.equals("**33##")) //Start New Track Session
        {

        }
    }
}
