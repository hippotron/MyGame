package com.example.mygame.GlobalParam

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.example.mygame.Pole.Pole

val GlobalDxKoef = 0.74

//val textMeasurer = rememberTextMeasurer()


fun DrawScope.TextRender(
    textMeasurer: TextMeasurer,
    name: String,
    x: Int,
    y: Int,
    size: Int = 25,
    bold: Boolean = false,
    center: Boolean = false,
    color: Color = Color.Black)
{
    var textWidth = 0
    //var textHeight = 0

    if (center) {
        // Рассчитываем размер текста
        val textLayoutResult = textMeasurer.measure(
            text = AnnotatedString(name),
            style = TextStyle(
                fontSize = size.sp,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
            )
        )
        // Смещаем точку рисования так, чтобы центр текста был в (x, y)
        textWidth = textLayoutResult.size.width
        //textHeight = textLayoutResult.size.height
    }

    drawText(
        textMeasurer = textMeasurer,
        text = name,
        style = TextStyle(
            color = color,
            fontSize = size.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        ),
        topLeft = Offset(
            x = (x - textWidth / 2).toFloat(),  // Смещаем на половину ширины влево
            y = (y).toFloat()  // Смещаем на половину высоты вверх
        )
    )
}

fun DrawScope.RenderImage(i: Int, j: Int, lx: Int, ly: Int, imageBitmap: ImageBitmap, scale:Int=0, plusX: Int=0, plusY: Int=0, pole: Pole)
{
    var l = 0
    if (i % 2 != 0) {
        l = pole.dx / 2
    }
    val posX = (pole.poleX + i * (lx * GlobalDxKoef)).toInt()
    val posY = (pole.poleY + j * pole.dx + l).toInt()

    drawImage(
        image = imageBitmap,
        dstOffset = IntOffset(posX-scale/2+plusX, posY-scale/2+plusY),
        dstSize = IntSize(lx+scale, ly+scale)
    )
}

