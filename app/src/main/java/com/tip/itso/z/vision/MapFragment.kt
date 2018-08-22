package com.tip.itso.z.vision


import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import kotlinx.android.synthetic.main.fragment_map_tab.*
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.tip.itso.z.vision.R.id.mapView
import java.util.*


class MapFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_map_tab, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(context!!, "pk.eyJ1IjoicmVyb3NhIiwiYSI6ImNqaXZwMDA0NzI0Zm8za21zYno1cTFkOG0ifQ.B812UDhUQ-2bBiXAMcWtFg");

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var location:Location? = null;


        mapView.onCreate(savedInstanceState);
        //mapView.setOfflineRegionDefinition { LatLngBounds.from(20.14751, 127.22361, 4.38698, 116.26953) }
        mapView.getMapAsync(OnMapReadyCallback {
            it.setCameraPosition(CameraPosition.Builder()
                    .target(MLocation.currentlatLng)
                    .zoom(15.0)
                    .build())

            val handler = Handler()
            val runnable = object : Runnable {
                override fun run() {
                    UpdateMap(it)
                    handler.postDelayed(this, 5000)
                }
            }

            handler.postDelayed(runnable, 0)
        })

    }

    var selfMarker:Marker? = null
    public fun UpdateMap(it:MapboxMap)
    {
        if(selfMarker != null)
        {
            it.removeMarker(selfMarker!!)

        }
        else
        {
            selfMarker = it.addMarker(MarkerOptions()
                    .position(MLocation.currentlatLng)
                    .title("MY POSITION")
                    .snippet("This is your position"))
        }


    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        //mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //mapView.onSaveInstanceState(outState)
    }

}
