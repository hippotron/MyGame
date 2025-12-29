package com.example.mygame.GlobalParam

import androidx.compose.animation.core.Spring
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val GlobalDxKoef = 0.74

fun DrawScope.TextRender(textMeasurer: TextMeasurer, name: Int, x: Double , y: Double, size: Int)
{
    // Казна
    drawText(
        textMeasurer = textMeasurer,
        text = "$name",
        style = TextStyle(
            color = androidx.compose.ui.graphics.Color.Black,
            fontSize = size.sp,        // ← Размер больше (по умолчанию 14.sp)
            fontWeight = FontWeight.Bold ), // <- жирный текст
        topLeft = Offset(x.toFloat(), y.toFloat())
    )
}