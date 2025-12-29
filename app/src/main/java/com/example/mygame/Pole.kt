package com.example.mygame

// Добавьте эти импорты:

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import kotlin.math.sqrt
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.mygame.GlobalParam.GlobalDxKoef


class Pole(
    private val context: Context
) {
    // Создаём изменяемый двумерный массив
    var mass: MutableList<MutableList<Int>> = mutableListOf( )

    // Создаём изменяемый двумерный массив
    val image_mass: MutableList<Int> = mutableListOf(
        R.drawable.one,     //0
        R.drawable.two,     //1
        R.drawable.three,   //2
        R.drawable.four,    //3
        R.drawable.five,    //4
        R.drawable.six,     //5
        R.drawable.seven,   //6
        R.drawable.eight,   //7
        R.drawable.nine,    //8
    )
    val display = koorOnInt(context.resources.displayMetrics.widthPixels,
                            context.resources.displayMetrics.heightPixels)
    val ogranichenie = limitation(
            koorOnInt(0, (display.y*0.1).toInt()),
            koorOnInt((display.x*0.8).toInt(),(display.y-display.y*0.1).toInt()))

    // Делаем координаты наблюдаемыми состояниями
    var poleX by mutableStateOf(0)
    var poleY by mutableStateOf(ogranichenie.min.y)
    var dx by mutableStateOf(0)

    var minDx = 0
    var maxDx = display.y*0.1 //300
    private var previousTouchX = 0
    private var previousTouchY = 0
    private var isDragging = false

    private var initialX2 = 0
    private var initialY2 = 0
    private var touchState by mutableStateOf(TouchState())

    init {
        // Функция, вызываемая в конструкторе
        initializePole()
    }

    private fun initializePole() {
        mass = createMatrix(11,17,0)
        calculateHexagonWidth( )
    }

    fun createMatrix(x: Int, y: Int, defaultValue: Int = 0): MutableList<MutableList<Int>> {
        return MutableList(x) { MutableList(y) { defaultValue } }
    }
    fun calculateHexagonWidth() {
        val screenX =ogranichenie.max.x - ogranichenie.min.x
        dx = screenX/mass.size
        for (i in 1..500){
            val d =dx +i
            val k = ((mass.size-1) * (d * GlobalDxKoef))+d
            if (k >=ogranichenie.max.x){
                dx = d-1
                break
            }
        }
        minDx=dx
        ogranichenie.min.y=poleY
        ogranichenie.max.y=ogranichenie.min.y+(mass[0].size)*minDx+minDx/2
        ogranichenie.min.x=((display.x-screenX)/2)
        ogranichenie.max.x=ogranichenie.min.x+screenX+2
        poleX=ogranichenie.min.x
        poleY=ogranichenie.min.y
    }

    @Composable
    fun Render() {
        Log.d("pole","$dx")
        drawHexagonPole() // рисует поле из шестиугольников

        //drawRectPole()// рисует поле из квадратов
        /*
        rect(modifier = Modifier, ogranichenie.min.x, ogranichenie.min.y,
            (ogranichenie.max.x - ogranichenie.min.x), (ogranichenie.max.y - ogranichenie.min.y), 5f, Color.Blue)
        */
    }

    @Composable
    fun rect(modifier: Modifier = Modifier, x: Float, y: Float, dx: Float, dy: Float, l: Float, color: Color) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                start = Offset(x, y),
                end = Offset(x, y + dy),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x, y + dy),
                end = Offset(x + dx, y + dy),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x + dx, y + dy),
                end = Offset(x + dx, y),
                color = color,
                strokeWidth = l
            )
            drawLine(
                start = Offset(x + dx, y),
                end = Offset(x, y),
                color = color,
                strokeWidth = l
            )
        }
    }

    fun onTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // начало касания
                previousTouchX = event.x.toInt()
                previousTouchY = event.y.toInt()
                isDragging = true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                // Второе и последующие касания
                val pointerIndex = event.actionIndex
                if (pointerIndex == 1) {
                    initialX2 = event.getX(1).toInt()
                    initialY2 = event.getY(1).toInt()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    val currentX1 = event.getX(0).toInt()
                    val currentY1 = event.getY(0).toInt()
                    val currentX2 = event.getX(1).toInt()
                    val currentY2 = event.getY(1).toInt()

                    var distance0 = ""

                    val dist = distanceBetweenPoints(currentX1, currentY1, currentX2, currentY2)

                    val k = 2
                    var newX = poleX
                    var newY = poleY
                    var newdx: Int = touchState.newdX

                    // Уменьшение
                    if ((dist - touchState.dist < 0 && dx - k >= minDx)) {
                        newdx = dx - k
                        val old_x = mass.size*(dx*GlobalDxKoef)
                        val new_x = mass.size*(newdx*GlobalDxKoef)
                        var delta = (old_x-new_x).toInt()
                        newX = (poleX+delta/2)

                        val old_y = (mass[0].size*dx+dx*0.5).toInt()
                        val new_y = (mass[0].size*newdx+newdx*0.5).toInt()
                        delta=old_y-new_y
                        newY = (poleY+delta/2)
                    //Увеличение
                    }else if ((dist - touchState.dist > 0 && dx+k<=maxDx)) {
                        newdx = dx + k
                        val old_x=mass.size*(dx*GlobalDxKoef)
                        val new_x=mass.size*(newdx*GlobalDxKoef)
                        var delta = new_x-old_x
                        newX = (poleX-delta/2).toInt()

                        val old_y = mass[0].size*dx+dx*0.5
                        val new_y = mass[0].size*newdx+newdx*0.5
                        delta=new_y-old_y
                        newY = (poleY-delta/2).toInt()

                    }else {
                        touchState = TouchState(
                            do_deltaX1 = currentX1,
                            do_deltaY1 = currentY1,
                            do_deltaX2 = currentX2,
                            do_deltaY2 = currentY2,
                            scale = distance0,
                            dist = dist,
                            )
                        return
                    }

                    dx = newdx
                    val end_pole_x = newX+((mass.size-1) * (dx * GlobalDxKoef))+dx*GlobalDxKoef
                    val end_pole_y = newY+((mass[0].size) *dx + (dx /2))

                    if (newX>=ogranichenie.min.x){
                        poleX=ogranichenie.min.x
                    } else if (end_pole_x<ogranichenie.max.x) {
                        val delta=ogranichenie.max.x-end_pole_x
                        poleX= (newX+delta).toInt()
                    } else {
                        poleX=newX
                    }

                    if (newY>=ogranichenie.min.y){
                        poleY=ogranichenie.min.y
                    } else if (end_pole_y<ogranichenie.max.y) {
                        val delta=ogranichenie.max.y-end_pole_y
                        poleY=newY+delta
                    } else {
                        poleY=newY
                    }

                    touchState = TouchState(
                        do_deltaX1 = currentX1,
                        do_deltaY1 = currentY1,
                        do_deltaX2 = currentX2,
                        do_deltaY2 = currentY2,
                        scale = distance0,
                        dist = dist,
                        )
                }

                if (isDragging && event.pointerCount == 1) {
                    val currentX = event.x.toInt()
                    val currentY = event.y.toInt()

                    // Вычисляем смещение
                    val deltaX = currentX - previousTouchX
                    val deltaY = currentY - previousTouchY

                    // Обновляем позицию поля
                    val newX = poleX + deltaX.toInt()
                    val newDxX =  poleX+mass.size*(dx*GlobalDxKoef)+deltaX+dx*0.3

                    val newY = poleY + deltaY.toInt()
                    val newDyY = poleY + mass[0].size*dx+dx/2+deltaY

                    // Ограничиваем движение в пределах границ
                    if (newX < ogranichenie.min.x && newDxX > ogranichenie.max.x) {
                        poleX = newX
                    }
                    if (newY < ogranichenie.min.y && newDyY > ogranichenie.max.y) {
                        poleY = newY
                    }

                    // Сохраняем текущие координаты для следующего движения
                    previousTouchX = currentX
                    previousTouchY = currentY
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                isDragging = false
                // Сброс координат при отпускании
                if (event.pointerCount <= 1) {
                    initialX2 = 0
                    initialY2 = 0
                    previousTouchX=0
                    previousTouchY=0
                }
                touchState = TouchState()
            }
        }
    }

    fun distanceBetweenPoints(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        val deltaX = x2 - x1
        val deltaY = y2 - y1
        return sqrt((deltaX * deltaX + deltaY * deltaY).toDouble()).toInt()
    }

    @Composable
    fun drawHexagonPole(){
        val context = LocalContext.current
        val imageBitmaps = remember {
            image_mass.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            for (i in mass.indices) {
                for (j in mass[i].indices) {
                    if (mass[i][j] != -1) {
                        var l = 0
                        if (i % 2 != 0) {
                            l = (dx/2)
                        }
                        val posX= (poleX + i * (dx * GlobalDxKoef)).toInt() // переделать на 0,73
                        val posY= (poleY + j * (dx) + l).toInt()

                        if (posX + dx > 0 && posX < display.x &&
                            posY + dx > 0 && posY < display.y
                        ) {
                            // Получаем индекс изображения из массива mass
                            val imageIndex = mass[i][j]
                            if (imageIndex in imageBitmaps.indices) {
                                drawImage(
                                    image = imageBitmaps[imageIndex],
                                    dstOffset = IntOffset(posX, posY),
                                    dstSize = IntSize(dx.toInt(), dx.toInt())
                                )
                            }

                            // Рисуем текст с индексами i и j
                            drawIntoCanvas { canvas ->
                                val textPaint = android.graphics.Paint().apply {
                                    color = android.graphics.Color.BLUE
                                    textSize = dx * 0.4f // Размер текста адаптивный
                                    textAlign = android.graphics.Paint.Align.CENTER
                                }

                                val textX = posX + dx / 2f
                                val textY = posY + dx / 1.7f

                                canvas.nativeCanvas.drawText("$i,$j", textX, textY, textPaint)
                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun drawRectPole(){
        for (i in mass.indices) {
            for (j in mass[i].indices) {
                if (mass[i][j] != -1) {
                    var l = 0f
                    if (i % 2 != 0) {
                        l = (dx * 0.5).toFloat()
                    }
                    val posX: Float = (poleX + i * (dx * 0.7f))
                    val posY: Float = (poleY + j * dx + l)

                    if (posX + dx > 0 && posX < display.x &&
                        posY + dx > 0 && posY < display.y
                    ) {

                        rect(
                            modifier = Modifier,posX,posY,
                            dx.toFloat(),dx.toFloat(),5f, Color.Red
                        )
                    }
                }
            }
        }
    }
}

data class TouchState(
    val do_deltaX1: Int = 0,
    val do_deltaY1: Int = 0,
    val do_deltaX2: Int = 0,
    val do_deltaY2: Int = 0,
    val scale: String = "",
    val dist: Int = 0,
    val newdX: Int = 0
)