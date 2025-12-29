package com.example.mygame


import android.graphics.BitmapFactory
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

class ButtonImage(
    val x: Int,
    val y: Int,
    val dx: Int,
    val dy: Int,
    val name: Int,
    val name2: Int = name,
    var type: MutableState<Int> = mutableStateOf(1)  // Используем State
) {
    @Composable
    fun Render() {
        // эта строка получает доступ к контексту, он необходим для доступа к картинкам
        // val context = LocalContext.current

        val image = if (type.value == 1)
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
                dstSize = IntSize(dx, dy)
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