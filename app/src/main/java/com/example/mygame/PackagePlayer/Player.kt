package com.example.mygame.PackagePlayer

import android.content.Context
import android.graphics.BitmapFactory
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.imageResource
import com.example.mygame.PackagePlayer.OtherForPlayer.Build
import com.example.mygame.Economic
import com.example.mygame.GlobalParam.GlobalDxKoef
import com.example.mygame.GlobalParam.RenderImage
import com.example.mygame.PackageForKoor.koorOnInt
import com.example.mygame.Pole.Pole
import com.example.mygame.R
import com.example.mygame.Scene.GameScene
import com.example.mygame.Terrain
import com.example.mygame.PackagePlayer.OtherForPlayer.Unit

class Player(
    val name: String,
    var pole: Pole,
    var color: Int,
    val economic: HashMap<Terrain, Economic>,
    val image_mass: HashMap<Terrain, ImageBitmap>,
    val gameScene: GameScene,
    val context: Context
) {


    // Используем mutableStateListOf для автоматического обновления
    var units = mutableStateListOf<Unit>()
    var buildings = mutableStateListOf<Build>()
    var selectedUnit by mutableStateOf<Int?>(null)
    var touchRender by mutableStateOf(0)

    var kazna by mutableStateOf(0)
    var income by mutableStateOf(0)
    var sale by mutableStateOf(0)

    fun getKazna(){
        getIncome()
        getSale()

        kazna=kazna+income-sale
        if (kazna<0) kazna=0
    }

    fun getSale(){
        var sumFarm=0
        for (i in buildings){
            if (i.type != Terrain.FARM){
                val c = economic[i.type]?.sale ?: 0
                sumFarm=sumFarm+c
            }
        }

        var sumUnits=0
        for (i in units){
            val c = economic[i.type]?.sale ?: 0
            sumUnits=sumUnits+c
        }
        sale=sumUnits+sumFarm
    }

    fun getIncome(){
        var sumCells=0
        for (i in pole.mass){
            for (cell in i){
                if (cell.player == this){
                    sumCells++
                }
            }
        }

        var sumFarm=0
        for (i in buildings){
            if (i.type == Terrain.FARM){
                sumFarm++
            }
        }

        val cell = economic[Terrain.LAND]?.income ?: 0
        val farm = economic[Terrain.FARM]?.income ?: 0

        income = sumCells * cell + sumFarm*farm
    }

    fun onTouch(event: MotionEvent) {
        getIncome()
        getSale()
        when (event.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                if (pole.ogranichenie.min.x<event.x && event.x < pole.ogranichenie.max.x &&
                        pole.ogranichenie.min.y<event.y && event.y < pole.ogranichenie.max.y)
                {

                    val (massX, massY) = Search_massY_massY(event.x, event.y)

                    if (massX != null && massY != null) {
                        // Проверяем глобальные состояния из GameScene
                        if (!gameScene.delivereBuild) {
                            if (!gameScene.delivereUnit) {
                                // Обработка выбора и перемещения юнитов
                                if (selectedUnit != null && units[selectedUnit!!].Movement != 0) {
                                    val possibleMoves = Possible_moves(
                                        units[selectedUnit!!].koorOnPole.x,
                                        units[selectedUnit!!].koorOnPole.y,
                                        1,
                                        units[selectedUnit!!]
                                    )
                                    for (i in possibleMoves.indices) {
                                        if (massX == possibleMoves[i].x && massY == possibleMoves[i].y) {
                                            if (massX == units[selectedUnit!!].koorOnPole.x &&
                                                massY == units[selectedUnit!!].koorOnPole.y
                                            ) {
                                                units[selectedUnit!!].size.value = pole.dx
                                                selectedUnit = null
                                                break
                                            }
                                            deleteUnit(massX, massY)
                                            deleteBuild(massX, massY)

                                            units[selectedUnit!!].koorOnPole.x = massX
                                            units[selectedUnit!!].koorOnPole.y = massY
                                            units[selectedUnit!!].Movement =
                                                units[selectedUnit!!].Movement - 1
                                            pole.mass[massX][massY].player = this

                                            units[selectedUnit!!].size.value = pole.dx
                                            selectedUnit = null
                                            break
                                        }
                                    }
                                } else { // selectedUnit == null
                                    //println("else")
                                    for (i in units.indices) {
                                        //println("unit "+i)
                                        if (massX == units[i].koorOnPole.x && massY == units[i].koorOnPole.y) {
                                            //println("massX==x")
                                            if (units[i].Movement != 0) {
                                                //println("movement!=0")
                                                selectedUnit = i
                                                units[i].size.value = pole.dx + pole.dx / 2
                                                break
                                            }
                                        }
                                    }
                                }
                            } else { // delivereUnit == true (из GameScene)
                                if (pole.mass[massX][massY].player == this && gameScene.selectAddUnit != Terrain.NONE) {
                                    // Используем selectAddUnit из GameScene
                                    addUnit(massX, massY, gameScene.selectAddUnit, pole.dx)
                                }
                            }
                        } else { // delivereBuild == true (из GameScene)
                            if (pole.mass[massX][massY].player == this && gameScene.selectAddBulid != Terrain.NONE) {
                                // Используем selectAddBulid из GameScene
                                addBuild(massX, massY, gameScene.selectAddBulid)
                            }
                        }
                    }
                }
                touchRender++
            }
        }
    }

    @Composable
    fun Render(renderPlayerIndicator: Boolean)
    {
        val updateRender = touchRender
        val transparentHexagonBitmap = ImageBitmap.imageResource(id = R.drawable.transparent_hexagon)

        Canvas(modifier = Modifier.fillMaxSize()) {
            RenderUnits(renderPlayerIndicator)
            RenderFarms()
            RenderPossibleMoves(transparentHexagonBitmap)
        }
    }

    fun DrawScope.RenderPossibleMoves(possibleBitmap: ImageBitmap)
    {
        if (selectedUnit != null && !gameScene.delivereUnit && !gameScene.delivereBuild ) {
            //println("1")
            val selectedX = units[selectedUnit!!].koorOnPole.x
            val selectedY = units[selectedUnit!!].koorOnPole.y
            val possibleMoves = Possible_moves(selectedX, selectedY, 1, units[selectedUnit!!])

            // Преобразуем возможные ходы в пары Pair
            val possibleMovesSet = possibleMoves.map { Pair(it.x, it.y) }
            for (i in pole.mass.indices) {
                for (j in pole.mass[i].indices) {
                    // Проверяем, что клетка НЕ входит в possibleMoves
                    if (Pair(i, j) !in possibleMovesSet) {
                        // прозначный шестигранник
                        this.RenderImage(i,j,pole.dx,pole.dx,possibleBitmap,pole = pole)
                    }
                }
            }
        }
    }

    fun DrawScope.RenderFarms()
    {
        for (i in buildings.indices){
            val image=image_mass.getValue(buildings[i].type)

            if (image!=null) {
                this.RenderImage(
                    buildings[i].koorOnPole.x,
                    buildings[i].koorOnPole.y,
                    pole.dx,
                    pole.dx,
                    image,
                    pole = pole
                )
            }
        }
    }

    fun DrawScope.RenderUnits(renderPlayerIndicator: Boolean)
    {
        for (i in units.indices){
            if (!image_mass.containsKey(units[i].type)) {
                continue
            }

                val image=image_mass.getValue(units[i].type)

            if (image!=null) {
                var scale=0
                if (selectedUnit!=null && i==selectedUnit){
                    scale=(pole.dx/2).toInt()
                }

                this.RenderImage(units[i].koorOnPole.x,units[i].koorOnPole.y,pole.dx,pole.dx,image,scale, pole = pole)

                if (renderPlayerIndicator) {
                    var imageIndicator = BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.movement_indicator_full
                    ).asImageBitmap()

                    if (units[i].Movement == 1) {
                        imageIndicator = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.movement_indicator_half
                        ).asImageBitmap()
                    } else if (units[i].Movement <= 0) {
                        imageIndicator = BitmapFactory.decodeResource(
                            context.resources,
                            R.drawable.movement_indicators_zero
                        ).asImageBitmap()
                    }
                    this.RenderImage(
                        units[i].koorOnPole.x,
                        units[i].koorOnPole.y,
                        pole.dx,
                        (pole.dx * 0.1).toInt(),
                        imageIndicator,
                        0,
                        0,
                        pole.dx,
                        pole = pole
                    )
                }
            }
        }
    }

    fun deleteUnit(massX: Int, massY: Int){
        if (isValidMoveForUnit(massX,massY, selectedUnit!!)){
            for (player in gameScene.listPlayers){
                if (player!=this){
                    for (vragUnit in player.units.indices){
                        if (player.units[vragUnit].koorOnPole.x==massX && player.units[vragUnit].koorOnPole.y==massY){
                            player.units.removeAt(vragUnit)
                            //println("юнит убит")
                            break
                        }
                    }
                }
            }
        }
    }

    fun deleteBuild(massX: Int,massY: Int){
        if (isValidForBuild(massX,massY,selectedUnit!!)){
            for (player in gameScene.listPlayers){
                if (player!=this){
                    for (vragBuild in player.buildings.indices){
                        if (player.buildings[vragBuild].koorOnPole.x == massX && player.buildings[vragBuild].koorOnPole.y == massY){
                            player.buildings.removeAt(vragBuild)
                            //println("здание уничтожено")
                            break
                        }
                    }
                }
            }
        }
    }

    fun isValidForBuild(x: Int,y: Int,selectUnit: Int?): Boolean{
        for (player in gameScene.listPlayers){
            if (player!=this){
                for (vragBuild in player.buildings){
                    if (vragBuild.koorOnPole.x == x && vragBuild.koorOnPole.y == y){
                        if (vragBuild.type>units[selectUnit!!].type){
                            return false
                        }
                    }
                }
            }
        }

        return true
    }

    fun addUnit(x: Int, y: Int, type: Terrain, size: Int)
    {
        val unitType = economic.getValue(type)
        if (kazna - unitType.price >= 0 && isValidMove(koorOnInt(x, y))) {
            units.add(Unit(koorOnInt(x, y), type, size, 2))

            kazna -= unitType.price
            gameScene.delivereUnit=false
        }
    }

    fun addBuild(x: Int, y: Int, type: Terrain)
    {

        val bulidType = economic.getValue(type)
        if (kazna - bulidType.price >= 0 && isValidMove(koorOnInt(x, y))) {
            buildings.add(Build(koorOnInt(x, y), type))
            kazna -= bulidType.price
            gameScene.delivereBuild=false
        }
    }

    fun Possible_moves(X: Int, Y: Int, N: Int, currentUnit1: Unit?): List<koorOnInt>
    {
        if ((currentUnit1?.Movement ?: 1) <= 0){
            return mutableListOf()
        }
        // Массив вообще всех возможных ходов
        val movesAround = mutableSetOf<koorOnInt>() // Set убирает дубликаты around - вокруг

        // Получаем возможные ходы вокруг
        val verifiedMoves = mutableListOf<koorOnInt>() // verified - проверенные
        val move =  mutableListOf<koorOnInt>()

        move.add(koorOnInt(X - 1, Y))
        move.add(koorOnInt(X, Y - 1))
        move.add(koorOnInt(X + 1, Y))
        move.add(koorOnInt(X, Y + 1))
        if (X % 2 == 0) {
            move.add(koorOnInt(X - 1, Y - 1))
            move.add(koorOnInt(X + 1, Y - 1))
        } else{
            move.add(koorOnInt(X - 1, Y + 1))
            move.add(koorOnInt(X + 1, Y + 1))
        }

        for (i in move){

            if (isValidMove(i,selectedUnit!!)){
                verifiedMoves.add(i)
            }
        }
        // Добавляем возможные ходы вокруг ко всем
        movesAround.addAll(verifiedMoves)

        if (N > 1) {
            // делаем цикл по возможным ходам вокруг
            for (move in verifiedMoves) {
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
        allMoves.addAll(movesAround)

        // возращаем все возможные ходы
        return allMoves
    }

    // Функция длч проверки клетки #isValidMove - действительный ход
    fun isValidMove(koorOnInt: koorOnInt, selectUnit: Int?=null): Boolean
    {
        // Проверяем границы массива
        if (koorOnInt.x < 0 || koorOnInt.x >= pole.mass.size || koorOnInt.y < 0 || koorOnInt.y >= pole.mass[0].size) {
            return false
        }
        // Проверяем, что не вода
        if (pole.mass[koorOnInt.x][koorOnInt.y].land == Terrain.WATER) {
            return false
        }

        val koorPairUnits = units.map { Pair(it.koorOnPole.x, it.koorOnPole.y) }
        if (Pair(koorOnInt.x, koorOnInt.y) in koorPairUnits) {
            return false
        }

        val koorPairBuild = buildings.map { Pair(it.koorOnPole.x, it.koorOnPole.y) }
        if (Pair(koorOnInt.x, koorOnInt.y) in koorPairBuild) {
            return false
        }

        if (selectUnit!=null) {
            val unitTypeInt = economic.getValue(units[selectUnit].type).protection
            val unit4 = economic.getValue(Terrain.UNIT4).protection

            // Если клетка принадлежит другому игроку
            if (pole.mass[koorOnInt.x][koorOnInt.y].player != this) {
                // Получаем защиту клетки
                val cellProtection = pole.mass[koorOnInt.x][koorOnInt.y].protection

                // Проверяем условия:
                // 1. Если юнит НЕ является UNIT4
                // 2. И защита юнита слабее или такая же как у защиты клетки
                if (unitTypeInt != unit4 && cellProtection >= unitTypeInt) {
                    //println("11")
                    return false
                }
            }

            for (player in gameScene.listPlayers) {
                if (player != this) {
                    for (vragUnit in player.units) {
                        if (vragUnit.koorOnPole.x == koorOnInt.x && vragUnit.koorOnPole.y == koorOnInt.y) {
                            if (vragUnit.type > units[selectUnit].type) {
                                return false
                            }
                        }
                    }
                }
            }
        }

        return true
    }

    fun isValidMoveForUnit(x: Int, y: Int, selectUnit: Int?=null): Boolean
    {
        if (selectUnit!=null) {
            for (player in gameScene.listPlayers) {
                if (player != this) {
                    for (vragUnit in player.units) {
                        if (vragUnit.koorOnPole.x == x && vragUnit.koorOnPole.y == y) {
                            if (vragUnit.type > units[selectUnit].type) {
                                return false
                            }
                        }
                    }
                }
            }
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