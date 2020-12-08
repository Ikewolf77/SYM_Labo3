package com.moodboardapp.sym_labo2

import android.content.Intent
import android.os.Bundle
import android.os.RemoteException
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.moodboardapp.sym_labo2.beacon.AdapterBeacon
import com.moodboardapp.sym_labo2.beacon.BeaconModel
import org.altbeacon.beacon.*


class Activity2 : AppCompatActivity(), BeaconConsumer {
    val TAG = "Activity2"
    private lateinit var beaconManager: BeaconManager
    private lateinit var listBeacons: ListView
    private lateinit var btnBackActivities: Button

    /* iBeacon */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)
        beaconManager = BeaconManager.getInstanceForApplication(this)
        beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"))
        beaconManager.bind(this)

        listBeacons = findViewById(R.id.LIST_BEACONS)

        btnBackActivities = findViewById(R.id.BACK_TO_ACTIVITES)

        btnBackActivities.setOnClickListener({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        beaconManager.unbind(this)
    }

    override fun onBeaconServiceConnect() {
        beaconManager.removeAllMonitorNotifiers()

        beaconManager.addRangeNotifier(
            object : RangeNotifier {
                override fun didRangeBeaconsInRegion(
                    beacons: Collection<Beacon>,
                    region: Region?
                ) {
                    if (beacons.size > 0) {

                        val listItems = beacons.map {
                            BeaconModel(it.id1.toString(), it.id3.toInt(), it.id2.toInt(), it.rssi)
                        }

                        val adapter = AdapterBeacon(listItems as ArrayList<BeaconModel>, applicationContext)

                        listBeacons.adapter = adapter
                    } else {
                        listBeacons.adapter = AdapterBeacon(ArrayList(), applicationContext)
                    }
                }
            }
        )

        try {
            beaconManager.startRangingBeaconsInRegion(Region("myRangingUniqueId", null, null, null))
        } catch (e: RemoteException) {
        }
    }

}