package com.example.mygame.ClientForServer.Models

import kotlinx.serialization.Serializable


@Serializable
data class GameListItem(val gameId: Int, val date: Long)

@Serializable
data class GetGamesResponse(val games: List<GameListItem>)

@Serializable
data class MyclassData(val a1: Int, val b1: Int)

@Serializable
data class Result(val sum: Int)

@Serializable
data class AddMassValue(val value: String)

@Serializable
data class AddMassResponse(val mass: List<String>)
