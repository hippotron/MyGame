package com.example.mygame

enum class Terrain(val value: Int) {
    WATER(1),
    FARM(10),
    TOWER(11),
    COOLTOWER(12);
    companion object {
        fun fromValue(value: Int): Terrain? {
            return values().find { it.value == value }
        }
    }
}
