package com.example.mygame

import com.google.gson.annotations.SerializedName

enum class Terrain(val value: Int){
    @SerializedName("NONE")
    NONE(0),
    @SerializedName("WATER")
    WATER(1),
    @SerializedName("LAND")
    LAND(2),
    @SerializedName("FARM")
    FARM(3),
    @SerializedName("TOWER")
    TOWER(4),
    @SerializedName("HARD_TOWER")
    HARD_TOWER(5),
    @SerializedName("UNIT1")
    UNIT1(6),
    @SerializedName("UNIT2")
    UNIT2(7),
    @SerializedName("UNIT3")
    UNIT3(8),
    @SerializedName("UNIT4")
    UNIT4(9),
    @SerializedName("immutableFARM")
    immutableFARM(10)
}