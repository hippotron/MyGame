package com.example.mygame


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

class Unit(

    var koorOnPole: koorOnInt,
    var strength: Int = 0,
    initialSize: Int = 60,

) {
    var size: MutableState<Int> = mutableStateOf(initialSize)
}