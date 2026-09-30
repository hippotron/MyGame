package com.example.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PictureRender {
    @Composable
    fun RenderImage(id: Int, X: Dp, Y: Dp, size: Dp) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = X, y = Y)
                .size(size)
                .clip(RoundedCornerShape(10.dp))
            //.padding(6.dp)
        )
    }
    @Composable
    fun RenderText(text: String, X: Dp, Y: Dp, textSize: Int, bold: Boolean = true, color: Color = Color(0xFF6B4A2A), center: Boolean = false){
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = X, y = Y),
            fontSize = textSize.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            textAlign =  if (center )TextAlign.Center else null
        )
    }

    private fun Modifier.parchmentCard(): Modifier {
        val fill = Color(0xF5F3E6C8)
        val stroke = Color(0xFFC4A06A)
        val shape = RoundedCornerShape(14.dp)
        return this
            .clip(shape)
            .background(fill)
            .border(1.5.dp, stroke, shape)
    }
}
