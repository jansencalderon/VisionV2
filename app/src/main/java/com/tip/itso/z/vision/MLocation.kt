package com.tip.itso.z.vision

import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import io.realm.RealmObject
import java.util.*

object MLocation : RealmObject(){


    var currentlatLng:LatLng = LatLng(0.0,0.0)
    var accurateLat:Double = 0.0
    var accurateLng:Double = 0.0
    var locationHistory:ArrayList<LatLng>? = null
    var accuracy:Float = 0F;
    var isLocationInitiated:Boolean = false;

}