package com.example.mygame

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

class ButtonImage(
    var x: Int,
    var y: Int,
    val dx: Int,
    var dy: Int,
    val name: Int,
    val name2: Int = name,
    var type: MutableState<Boolean> = mutableStateOf(false),  // Используем State
) {

    @Composable
    fun Render(alpha: Float=1f) {
        val image = if (type.value == false)
            ImageBitmap.imageResource(id = name)
        else
            ImageBitmap.imageResource(id = name2)

        // Canvas создает холст(область для рисования) на весь экран
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {

            drawImage(
                image = image,
                dstOffset = IntOffset(x, y),
                dstSize = IntSize(dx, dy),
                alpha = alpha,

                )
        }
    }

    fun click(mx: Int, my: Int): Boolean{
        if (x<mx && mx<x+dx && y<my && my<y+dy){
            return true
        } else{
            return false
        }
    }
}