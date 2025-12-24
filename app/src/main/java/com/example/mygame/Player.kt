package com.example.mygame

import android.graphics.BitmapFactory
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.mygame.GlobalParam.GlobalDxKoef

@Composable
fun TestCanvas() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Простой текст без textMeasurer
        // (для теста можно использовать drawIntoCanvas)
    }
}

class Player(
    val name: String,
    var kazna: Int,
    var income: Int,
    val pole: Pole
) {

    val image_mass: MutableList<Int> = mutableListOf(
        R.drawable.skeleton,
        R.drawable.image_return,
        R.drawable.point,
        R.drawable.six
    )

    val dx: Int get() = pole.dx.toInt()
    val poleX: Float get() = pole.poleX
    val poleY: Float get() = pole.poleY
    val mass get() = pole.mass

    // Используем mutableStateListOf для автоматического обновления
    val units = mutableStateListOf<Unit>(
        Unit(koorOnInt(6,10),
             0,
             dx
        )
    )

    val farms = mutableListOf<Farm>(
        Farm(koorOnInt(5,5),
            Terrain.FARM.value,
            20)
    )



    var selectedUnit by mutableStateOf<Int?>(null)

    var touchRender by mutableStateOf(0)

    fun onTouch(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val (massX, massY) = Search_massY_massY(event.x,event.y)

                if (massX!=null && massY!=null) {

                    if (selectedUnit != null) {
                        val possibleMoves = Possible_moves(
                            units[selectedUnit!!].koorOnPole.x,
                            units[selectedUnit!!].koorOnPole.y,
                            2)
                        for (i in possibleMoves.indices) {
                            if (massX==possibleMoves[i].x && massY==possibleMoves[i].y) {
                                units[selectedUnit!!].koorOnPole.x = massX
                                units[selectedUnit!!].koorOnPole.y = massY

                                units[selectedUnit!!].size.value = dx
                                selectedUnit = null
                                break
                            }
                        }
                    }else{
                        if (selectedUnit==null){
                            for (i in units.indices){
                                if (massX==units[i].koorOnPole.x && massY==units[i].koorOnPole.y){
                                    selectedUnit=i
                                    units[i].size.value=dx+dx/2
                                }
                            }
                        }
                    }
                }
                touchRender++
            }
        }
    }

    @Composable
    fun Render() {
        val context = LocalContext.current
        val imageBitmaps = remember {
            image_mass.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }
        // Заранее загружаем bitmap
        val farmBitmap = ImageBitmap.imageResource(id = R.drawable.farm)
        Log.d("","render")

        // Создаем TextMeasurer
        val textMeasurer = rememberTextMeasurer()

        Canvas(modifier = Modifier.fillMaxSize()) {

            RenderPossibleMoves(imageBitmaps[3])
            RenderUnits(imageBitmaps)
            RenderFarms(farmBitmap)

            // Казна
            drawText(
                textMeasurer = textMeasurer,
                text = "$kazna",
                style = TextStyle(
                    color = androidx.compose.ui.graphics.Color.Black,
                    fontSize = 24.sp,        // ← Размер больше (по умолчанию 14.sp)
                    fontWeight = FontWeight.Bold ), // <- жирный текст
                topLeft = Offset(pole.display.x*0.1f, pole.display.y*0.05f)
            )

            // Доход
            drawText(
                textMeasurer = textMeasurer,
                text = "$income",
                style = TextStyle(
                    color = androidx.compose.ui.graphics.Color.Black,
                    fontSize = 24.sp,        // ← Размер больше (по умолчанию 14.sp)
                    fontWeight = FontWeight.Bold ), // <- жирный текст
                topLeft = Offset(pole.display.x*0.47f, pole.display.y*0.05f)
            )

        }
    }

    fun DrawScope.RenderPossibleMoves(possibleBitmap: ImageBitmap)
    {
        if (selectedUnit != null) {
            val selectedX = units[selectedUnit!!].koorOnPole.x
            val selectedY = units[selectedUnit!!].koorOnPole.y
            val possibleMoves = Possible_moves(selectedX, selectedY, 2)

            // Преобразуем возможные ходы в пары Pair
            val possibleMovesSet = possibleMoves.map { Pair(it.x, it.y) }

            for (i in pole.mass.indices) {
                for (j in pole.mass[i].indices) {
                    // Проверяем, что клетка НЕ входит в possibleMoves
                    if (Pair(i, j) !in possibleMovesSet) {
                        this.RenderImageUnit(i,j,dx,dx,possibleBitmap)
                    }
                }
            }
        }
    }

    fun DrawScope.RenderFarms(farmBitmap: ImageBitmap)
    {
        for (i in farms.indices) {
            val farm = farms[i]
            this.RenderImageUnit(farm.koorOnPole.x, farm.koorOnPole.y, dx, dx, farmBitmap)
        }
    }

    fun DrawScope.RenderUnits(imageBitmaps: List<ImageBitmap>)
    {
        for (i in units.indices){
            val number=units[i].strength

            if (number in imageBitmaps.indices) {
                var scale=0
                if (selectedUnit!=null && i==selectedUnit){
                    scale=50
                }
                this.RenderImageUnit(units[i].koorOnPole.x,units[i].koorOnPole.y,dx,dx,imageBitmaps[number],scale)
            }
        }
    }


    fun DrawScope.RenderImageUnit(i : Int, j : Int, lx : Int, ly : Int,  imageBitmap : ImageBitmap, scale :Int = 0 ) {
        var l = 0
        if (i % 2 != 0) {
            l = dx / 2
        }
        val posX = (poleX + i * (dx * GlobalDxKoef)).toInt()
        val posY = (poleY + j * dx + l).toInt()

        drawImage(
            image = imageBitmap,
            dstOffset = IntOffset(posX-scale/2, posY-scale/2),
            dstSize = IntSize(lx+scale, lx+scale)
        )
    }

    fun Possible_moves(X: Int, Y: Int, N: Int): List<koorOnInt> {

        // Массив вообще всех возможных ходов
        val allMoves = mutableSetOf<koorOnInt>() // Set убирает дубликаты
        // Получаем список координат ферм для быстрой проверки
        val farmCoordinates = farms.map { it.koorOnPole.x to it.koorOnPole.y }.toSet()

        // Получаем возможные ходы вокруг
        val directMoves = mutableListOf<koorOnInt>()
        val move =  mutableListOf<koorOnInt>()

        move.add(koorOnInt(X-1, Y))
        move.add(koorOnInt(X, Y-1))
        move.add(koorOnInt(X+1, Y))
        move.add(koorOnInt(X, Y+1))
        if (X % 2 == 0) {
            move.add(koorOnInt(X-1, Y-1))
            move.add(koorOnInt(X+1, Y-1))
        } else{
            move.add(koorOnInt(X-1, Y+1))
            move.add(koorOnInt(X+1, Y+1))
        }

        for (i in move){
            if (isValidMove(i)){
                directMoves.add(i)
            }
        }
        // Добавляем возможные ходы вокруг ко всем
        allMoves.addAll(directMoves)

        if (N > 1) {
            // делаем цикл по возможным ходам вокруг
            for (move in directMoves) {
                // получаем возможные ходы от возможного move хода
                val recursiveMoves = Possible_moves(move.x, move.y, N - 1)
                // добавляем эти возможные ходы ко всем возможным ходам
                allMoves.addAll(recursiveMoves)
            }
        }

        // удаляем фермы из возможных ходов
        allMoves.removeAll { move ->
            move.x to move.y in farmCoordinates
        }

        // возращаем все возможные ходы
        return allMoves.toList()
    }

    // Вспомогательная функция для проверки клетки
    fun isValidMove(koorOnInt: koorOnInt): Boolean {
        // Проверяем границы массива
        if (koorOnInt.x < 0 || koorOnInt.x >= mass.size || koorOnInt.y < 0 || koorOnInt.y >= mass[0].size) {
            return false
        }
        // Проверяем, что не вода
        if (mass[koorOnInt.x][koorOnInt.y] == Terrain.WATER.value) {
            return false
        }

        return true
    }

    fun Search_massY_massY(X: Float, Y: Float): Pair<Int?, Int?> {
        val lenPoleX = poleX + dx * GlobalDxKoef * mass.size + dx * 0.3f
        val lenPoleY = poleY + dx * mass[0].size + dx / 2

        var massX: Int? = null
        var massY: Int? = null

        if (poleX < X && X < lenPoleX && poleY < Y && Y < lenPoleY) {
            massX = ((X - poleX) / (dx * GlobalDxKoef)).toInt()

            if (massX % 2 != 0) {
                massY = ((Y - poleY - dx / 2) / dx).toInt()
            } else {
                massY = ((Y - poleY) / dx).toInt()
            }

            // Ограничиваем координаты
            if (massY > mass[0].size-1) massY = mass[0].size-1
            if (massX > mass.size-1) massX = mass.size-1
            if (massY < 0) massY = 0
            if (massX < 0) massX = 0
        }

        return Pair(massX, massY)
    }
}

