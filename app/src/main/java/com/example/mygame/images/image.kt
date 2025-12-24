package com.example.mygame.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

class Image (private val imageBitmap: ImageBitmap,
             private val x: Int,
             private val y: Int,
             private val dx: Int,
             private val dy: Int
){
    @Composable
    fun DrawScope.Render()
    {
        drawImage(
            image = imageBitmap,
            dstOffset = IntOffset(x,y),
            dstSize = IntSize(dx, dy)
        )
    }
}