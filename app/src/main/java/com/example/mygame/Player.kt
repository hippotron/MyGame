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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.mygame.GlobalParam.GlobalDxKoef
import com.example.mygame.GlobalParam.TextRender

class Player(
    val name: String,
    var kazna: Int,
    var income: Int,
    val pole: Pole
) {

    val image_mass_units: MutableList<Int> = mutableListOf(
        R.drawable.skeleton,
        R.drawable.barbarian,
        R.drawable.knight,
        R.drawable.hard_khight
    )

    // Используем mutableStateListOf для автоматического обновления
    val units = mutableStateListOf<Unit>(
        Unit(koorOnInt(6,10),
             strength = 1,
             pole.dx
        )
    )

    val buildings = mutableListOf<Build>(
        Build(koorOnInt(5,5),
            Terrain.FARM.value,
            20)
    )

    var selectedUnit by mutableStateOf<Int?>(null)

    var touchRender by mutableStateOf(0)

    var delivereUnit = false

    var strengthAddUnit = 0
    val unitSettings = ButtonImage(
        (pole.display.x*0.6).toInt(),
        (pole.display.y*0.9).toInt(),
        200,
        200,
        R.drawable.skeleton)


    val clAddUnit0 = ButtonImage(
        (pole.display.x*0.15).toInt(),
        (pole.display.y*0.85).toInt(),
        150,
        150,
        R.drawable.skeleton,
        name2 = R.drawable.dedicated_skeleton,
        type = mutableStateOf(1)
    )
    val clAddUnit1 = ButtonImage(
        (pole.display.x*0.35).toInt(),
        (pole.display.y*0.85).toInt(),
        150,
        150,
        R.drawable.barbarian,
        name2 = R.drawable.dedicated_barbarin,
        type = mutableStateOf(1)
    )
    val clAddUnit2 = ButtonImage(
        (pole.display.x*0.55).toInt(),
        (pole.display.y*0.85).toInt(),
        150,
        150,
        R.drawable.knight,
        name2 = R.drawable.dedicated_knight,
        type = mutableStateOf(1)
    )
    val clAddUnit3 = ButtonImage(
        (pole.display.x*0.75).toInt(),
        (pole.display.y*0.85).toInt(),
        150,
        150,
        R.drawable.hard_khight,
        name2 = R.drawable.dedicated_hard_knight,
        type = mutableStateOf(1)
    )

    fun addUnit(x: Int, y: Int, strength: Int, size: Int){
        units.add(Unit(koorOnInt(x,y),strength,size))
    }

    fun onTouch(event: MotionEvent)
    {
        when (event.actionMasked)
        {
            MotionEvent.ACTION_DOWN -> {
                val (massX, massY) = Search_massY_massY(event.x,event.y)

                if (massX != null && massY != null)
                {
                    if (delivereUnit != true) {
                        if (selectedUnit != null) {
                            val possibleMoves = Possible_moves(
                                units[selectedUnit!!].koorOnPole.x,
                                units[selectedUnit!!].koorOnPole.y,
                                2,
                                units[selectedUnit!!]
                            )
                            for (i in possibleMoves.indices) {
                                if (massX == possibleMoves[i].x && massY == possibleMoves[i].y) {
                                    units[selectedUnit!!].koorOnPole.x = massX
                                    units[selectedUnit!!].koorOnPole.y = massY

                                    units[selectedUnit!!].size.value = pole.dx
                                    selectedUnit = null
                                    break
                                }
                            }
                        } else { // selectedUnit == null
                            for (i in units.indices) {
                                if (massX == units[i].koorOnPole.x && massY == units[i].koorOnPole.y) {
                                    selectedUnit = i
                                    units[i].size.value = pole.dx + pole.dx / 2 }
                            }
                        }
                    } else { //delivereUnit == true

                        addUnit(massX,massY, strengthAddUnit,pole.dx)
                        delivereUnit=false
                    }
                }
                touchRender++
            }

            MotionEvent.ACTION_UP -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (delivereUnit==true){
                    strengthAddUnit = 0
                    clAddUnit0.type.value = 1
                    clAddUnit1.type.value = 1
                    clAddUnit2.type.value = 1
                    clAddUnit3.type.value = 1

                    if (clAddUnit0.click(mx, my)) {
                        strengthAddUnit = 0
                        clAddUnit0.type.value=2
                    }
                    if (clAddUnit1.click(mx, my)) {
                        strengthAddUnit = 1
                        clAddUnit1.type.value=2
                    }
                    if (clAddUnit2.click(mx, my)) {
                        strengthAddUnit = 2
                        clAddUnit2.type.value=2
                    }
                    if (clAddUnit3.click(mx, my)) {
                        strengthAddUnit = 3
                        clAddUnit3.type.value=2
                    }
                }

                if (unitSettings.click(event.x.toInt(),event.y.toInt())) {
                    if (delivereUnit==false) {
                        delivereUnit = true
                    } else {
                        delivereUnit=false
                    }
                }

                touchRender++
            }
        }
    }

    @Composable
    fun Render()
    {
        val updateRender = touchRender
        val context = LocalContext.current
        val imageBitmapsUnits = remember {
            image_mass_units.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }

        val transparentHexagonBitmap = ImageBitmap.imageResource(id = R.drawable.transparent_hexagon)

        // Заранее загружаем bitmap
        val farmBitmap = ImageBitmap.imageResource(id = R.drawable.farm)
        Log.d("","render")

        // Создаем TextMeasurer
        val textMeasurer = rememberTextMeasurer()

        unitSettings.Render()

        if (delivereUnit==true){
            clAddUnit0.Render()
            clAddUnit1.Render()
            clAddUnit2.Render()
            clAddUnit3.Render()
        }

        Canvas(modifier = Modifier.fillMaxSize()) {

            RenderPossibleMoves(transparentHexagonBitmap)
            RenderUnits(imageBitmapsUnits)
            RenderFarms(farmBitmap)
            // Казна
            TextRender(
                textMeasurer,
                kazna,
                (pole.display.x*0.1),
                (pole.display.y*0.05),
                24)
            // Доход
            TextRender(
                textMeasurer,
                income,
                (pole.display.x*0.47),
                (pole.display.y*0.05),
                24)
        }
    }

    fun DrawScope.RenderPossibleMoves(possibleBitmap: ImageBitmap)
    {
        if (selectedUnit != null) {
            val selectedX = units[selectedUnit!!].koorOnPole.x
            val selectedY = units[selectedUnit!!].koorOnPole.y
            val possibleMoves = Possible_moves(selectedX, selectedY, 2, units[selectedUnit!!])

            // Преобразуем возможные ходы в пары Pair
            val possibleMovesSet = possibleMoves.map { Pair(it.x, it.y) }

            for (i in pole.mass.indices) {
                for (j in pole.mass[i].indices) {
                    // Проверяем, что клетка НЕ входит в possibleMoves
                    if (Pair(i, j) !in possibleMovesSet) {
                        // прозначный шестигранник
                        this.RenderImage(i,j,pole.dx,pole.dx,possibleBitmap)
                    }
                }
            }
        }
    }

    fun DrawScope.RenderFarms(farmBitmap: ImageBitmap)
    {
        for (i in buildings.indices) {
            val farm = buildings[i]
            this.RenderImage(farm.koorOnPole.x, farm.koorOnPole.y, pole.dx, pole.dx, farmBitmap)
        }
        //Log.d("player","$currentDx")
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
                this.RenderImage(units[i].koorOnPole.x,units[i].koorOnPole.y,pole.dx,pole.dx,imageBitmaps[number],scale)
            }
        }
    }

    fun DrawScope.RenderImage(i : Int, j : Int, lx : Int, ly : Int, imageBitmap : ImageBitmap, scale :Int = 0, trDX: Int = 0)
    {
        var l = 0
        if (i % 2 != 0) {
            l = ly / 2
        }
        var posX = (pole.poleX + i * (lx * GlobalDxKoef)).toInt()
        val posY = (pole.poleY + j * ly + l).toInt()
        var finallLx = lx
        if ( trDX!= 0){
            finallLx = trDX
            posX = (posX+pole.dx*0.7).toInt()
        }

        drawImage(
            image = imageBitmap,
            dstOffset = IntOffset(posX-scale/2, posY-scale/2),
            dstSize = IntSize(finallLx+scale, ly+scale)
        )
    }

    fun Possible_moves(X: Int, Y: Int, N: Int, currentUnit1: Unit): List<koorOnInt>
    {

        // Массив вообще всех возможных ходов
        val movesAround = mutableSetOf<koorOnInt>() // Set убирает дубликаты

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
        movesAround.addAll(directMoves)

        if (N > 1) {
            // делаем цикл по возможным ходам вокруг
            for (move in directMoves) {
                // получаем возможные ходы от возможного move хода
                val recursiveMoves = Possible_moves(move.x, move.y, N - 1, currentUnit1)
                // добавляем эти возможные ходы ко всем возможным ходам
                movesAround.addAll(recursiveMoves)
            }
        }

        // Получаем список координат ферм для быстрой проверки
        val farmCoordinates = buildings.map { it.koorOnPole.x to it.koorOnPole.y }.toSet()

        // удаляем фермы из возможных ходов
        movesAround.removeAll { move ->
            move.x to move.y in farmCoordinates
        }

        // Создаем mutable список с текущей позицией
        val allMoves = mutableListOf(koorOnInt(X, Y))

        // Добавляем отфильтрованные ходы вокруг
        allMoves.addAll(movesAround.filter { move ->
            // Находим юнита на этой позиции (если есть)
            val unitAtCell = units.find { it.koorOnPole.x == move.x && it.koorOnPole.y == move.y }

            // Если юнита нет - клетка доступна
            if (unitAtCell == null) return@filter true

            // Если есть юнит и он слабее текущего - клетка доступна для атаки
            // Если юнит сильнее или равен по силе - клетка недоступна
            currentUnit1.strength > unitAtCell.strength
        })

        // возращаем все возможные ходы
        return allMoves
    }

    // Вспомогательная функция для проверки клетки
    fun isValidMove(koorOnInt: koorOnInt): Boolean
    {
        // Проверяем границы массива
        if (koorOnInt.x < 0 || koorOnInt.x >= pole.mass.size || koorOnInt.y < 0 || koorOnInt.y >= pole.mass[0].size) {
            return false
        }
        // Проверяем, что не вода
        if (pole.mass[koorOnInt.x][koorOnInt.y] == Terrain.WATER.value) {
            return false
        }

        return true
    }

    fun Search_massY_massY(X: Float, Y: Float): Pair<Int?, Int?>
    {
        val lenPoleX = pole.poleX + pole.dx * GlobalDxKoef * pole.mass.size + pole.dx * 0.3
        val lenPoleY = pole.poleY + pole.dx * pole.mass[0].size + pole.dx / 2

        var massX: Int? = null
        var massY: Int? = null

        if (pole.poleX < X && X < lenPoleX && pole.poleY < Y && Y < lenPoleY) {
            massX = ((X - pole.poleX) / (pole.dx * GlobalDxKoef)).toInt()

            if (massX % 2 != 0) {
                massY = ((Y - pole.poleY - pole.dx / 2) / pole.dx).toInt()
            } else {
                massY = ((Y - pole.poleY) / pole.dx).toInt()
            }

            // Ограничиваем координаты
            if (massY > pole.mass[0].size-1) massY = pole.mass[0].size-1
            if (massX > pole.mass.size-1) massX = pole.mass.size-1
            if (massY < 0) massY = 0
            if (massX < 0) massX = 0
        }

        return Pair(massX, massY)
    }
}

