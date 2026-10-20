package com.example.mygame.PackageForKoor

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Переводит координаты, подобранные на эталонном экране, в ту же долю
 * текущего экрана. Эталон: 1080×2424 px, 420 dpi (411.43 × 923.43 dp).
 * На этом экране x(22.dp) остаётся 22.dp. На другом 22.dp по ширине
 * становится той же долей ширины, по высоте — той же долей высоты.
 */
class ScreenRel(context: Context) {
    private val metrics = context.resources.displayMetrics
    private val screenXFl = metrics.widthPixels.toFloat()
    private val screenYFl = metrics.heightPixels.toFloat()
    private val density = metrics.density

    private val screenXDp = screenXFl / density
    private val screenYDp = screenYFl / density

    fun x(percent: Float): Dp = (screenXDp * percent.toFloat() / 100f).dp
    fun y(percent: Float): Dp = (screenYDp * percent.toFloat() / 100f).dp
}
