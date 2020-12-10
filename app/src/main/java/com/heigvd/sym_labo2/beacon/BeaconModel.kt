package com.moodboardapp.sym_labo2.beacon

class BeaconModel {
    var UUID : String
    var MIN: Int
    var MAJ: Int
    var RSSI: Int

    constructor(uuid: String, MIN: Int, MAJ: Int, RSSI: Int) {
        this.UUID = uuid
        this.MIN = MIN
        this.MAJ = MAJ
        this.RSSI = RSSI
    }
}