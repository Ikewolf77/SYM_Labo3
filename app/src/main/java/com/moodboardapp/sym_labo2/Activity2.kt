package com.moodboardapp.sym_labo2

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.altbeacon.beacon.*


class Activity2 : AppCompatActivity(), BeaconConsumer {
    val TAG = "Activity2"
    private val PERMISSION_REQUEST_FINE_LOCATION = 1
    private val PERMISSION_REQUEST_BACKGROUND_LOCATION = 2
    private lateinit var beaconManager: BeaconManager
    private lateinit var somethingHappens: TextView

    /* iBeacon */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.bind(this)
        somethingHappens = findViewById(R.id.somethingHappens)

    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers()
        beaconManager.addMonitorNotifier(
            object : MonitorNotifier {
                override fun didDetermineStateForRegion(state: Int, p1: Region?) {
                    val text = "I have just switched from seeing/not seeing beacons: " + state
                    Log.i(TAG, text)
                    somethingHappens.text = text
                }

                override fun didEnterRegion(region: Region?) {
                    val text = "I just saw an beacon for the first time !"
                    Log.i(TAG, text)
                    somethingHappens.text = text
                }

                override fun didExitRegion(region: Region?) {
                    val text = "I no longer see an beacon"
                    Log.i(TAG, text)
                    somethingHappens.text = text
                }
            }
        )
    }

}