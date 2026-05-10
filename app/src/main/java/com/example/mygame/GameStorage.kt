package com.example.mygame


import android.content.Context
//import com.example.mygame.Scene.Person
import com.example.mygame.data.GameMap_data
import com.example.mygame.data.profile_data
import com.google.gson.Gson
import java.io.File
import java.io.IOException
import com.google.gson.GsonBuilder

class GameStorage(private val context: Context) {

    private val gson = Gson()

    private val prettyGson = GsonBuilder()
        .setPrettyPrinting()
        .create()
    val filenameProfile = "Profile.json"
    // Сохранение в JSON
    fun saveToJsonGameMap(fileName: String, game_map: GameMap_data): Boolean {
        return try {
            val file = File(context.filesDir, fileName)
            val jsonString = prettyGson.toJson(game_map)
            file.writeText(jsonString)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    // Чтение из JSON
    fun loadFromJsonGameMap(fileName: String): GameMap_data? {
        return try {
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonString = file.readText()
                //println(jsonString)
                gson.fromJson(jsonString, GameMap_data::class.java)
            } else {
                null
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun saveToJsonProfile(): Boolean {
        return try {
            val file = File(context.filesDir, filenameProfile)
            val jsonString = prettyGson.toJson(profile_data.DEFAULT_PROFILE)
            file.writeText(jsonString)
            //println("save")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun loadFromJsonProfile2(): Boolean {
        return try {
            val file = File(context.filesDir, filenameProfile)
            if (file.exists()) {
                val jsonString = file.readText()
                //println(jsonString)
                gson.fromJson(jsonString, profile_data.DEFAULT_PROFILE::class.java)
                true
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    fun loadFromJsonProfile(): Boolean {
        return try {
            val file = File(context.filesDir, filenameProfile)
            if (file.exists()) {
                val jsonString = file.readText()
                //println("Loading profile: $jsonString")

                // Загружаем профиль из JSON
                val loadedProfile = gson.fromJson(jsonString, profile_data::class.java)

                // Обновляем DEFAULT_PROFILE
                profile_data.DEFAULT_PROFILE = loadedProfile

                //println("Loaded profile: ${loadedProfile.playerName}, ${loadedProfile.playerId}")
                true
            } else {
                //println("Profile file not found")
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun deleteFile(fileName: String): Boolean {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }
}