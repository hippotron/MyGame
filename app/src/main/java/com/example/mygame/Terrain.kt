package com.example.mygame

enum class Terrain(val value: Int) {
    WATER(-1),
    FARM(0),
    TOWER(1),
    HARD_TOWER(2);
    companion object {
        fun fromValue(value: Int): Terrain? {
            return values().find { it.value == value }
        }
    }
}
