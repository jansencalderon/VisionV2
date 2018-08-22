package com.tip.itso.z.vision;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.mapbox.mapboxsdk.Mapbox;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class Vision extends MultiDexApplication {
    private static Vision sInstance;

    @Override
    public void onCreate()
    {
        super.onCreate();
        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        if(!FirebaseApp.getApps(this).isEmpty())
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }

        sInstance = this;
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
