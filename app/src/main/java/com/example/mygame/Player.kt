package com.example.mygame

import android.util.Log
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
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.mygame.GlobalParam.GlobalDxKoef
import com.example.mygame.ui.theme.Economic

class Player(
    val name: String,
    val pole: Pole,
    var color: Int,
    val economic: HashMap<Terrain, Economic>,
    val image_mass: HashMap<Terrain, ImageBitmap>
) {


    // Используем mutableStateListOf для автоматического обновления
    val units = mutableStateListOf<Unit>( )

    val buildings = mutableListOf<Build>()

    var selectedUnit by mutableStateOf<Int?>(null)

    var touchRender by mutableStateOf(0)

    var delivereUnit = false
    var delivereBuild = false

    var selectAddUnit: Terrain = Terrain.NONE
    var selectAddBulid: Terrain = Terrain.NONE

    val buildSettings = ButtonImage(
        (pole.display.x*0.29).toInt(),
        (pole.display.y*0.89).toInt(),
        (pole.display.x*0.21).toInt(),
        (pole.display.y*0.1).toInt(),
        R.drawable.farm)

    val unitSettings = ButtonImage(
        (pole.display.x*0.6).toInt(),
        (pole.display.y*0.9).toInt(),
        (pole.display.x*0.21).toInt(),
        (pole.display.y*0.09).toInt(),
        R.drawable.skeleton)

    var unitButtonsMap= HashMap<Terrain, ButtonImage>()
    var buildButtonMap = HashMap<Terrain, ButtonImage>()

    var kazna by mutableStateOf(0)
    var income by mutableStateOf(0)
    var sale by  mutableStateOf(0)


    init {
        initButtonUnit()
        initButtonBuild()
    }

    fun initButtonUnit() {
        val b1 =
            ButtonImage(
                (pole.display.x * 0.15).toInt(),
                (pole.display.y * 0.85).toInt(),
                150,
                150,
                R.drawable.skeleton,
                name2 = R.drawable.dedicated_skeleton,
                type = mutableStateOf(1)
            )
        unitButtonsMap[Terrain.UNIT1]=b1

        val b2 = ButtonImage(
            (pole.display.x * 0.35).toInt(),
            (pole.display.y * 0.85).toInt(),
            150, 150,
            R.drawable.barbarian,
            name2 = R.drawable.dedicated_barbarin,
            type = mutableStateOf(1)
        )
        unitButtonsMap[Terrain.UNIT2]=b2
        val b3 =
            ButtonImage(
                (pole.display.x * 0.55).toInt(),
                (pole.display.y * 0.85).toInt(),
                150, 150,
                R.drawable.knight,
                name2 = R.drawable.dedicated_knight,
                type = mutableStateOf(1)
            )
        unitButtonsMap[Terrain.UNIT3]=b3
        val b4 =
            ButtonImage(
                (pole.display.x * 0.75).toInt(),
                (pole.display.y * 0.85).toInt(),
                150, 150,
                R.drawable.hard_khight,
                name2 = R.drawable.dedicated_hard_knight,
                type = mutableStateOf(1)
            )
        unitButtonsMap[Terrain.UNIT4]=b4
    }

    fun initButtonBuild() {
        val b1 =
            ButtonImage(
                (pole.display.x * 0.25).toInt(),
                (pole.display.y * 0.85).toInt(),
                150, 150,
                R.drawable.farm,
                name2 = R.drawable.dedicated_farm,
                type = mutableStateOf(1)
            )
        buildButtonMap[Terrain.FARM]=b1
        val b2 =
            ButtonImage(
                (pole.display.x * 0.45).toInt(),
                (pole.display.y * 0.85).toInt(),
                150, 150,
                R.drawable.tower,
                name2 = R.drawable.dedicated_tower,
                type = mutableStateOf(1)
            )
        buildButtonMap[Terrain.TOWER]=b2
        val b3 =
            ButtonImage(
                (pole.display.x * 0.65).toInt(),
                (pole.display.y * 0.85).toInt(),
                150, 150,
                R.drawable.tower_hard,
                name2 = R.drawable.tower_hard,
                type = mutableStateOf(1)
            )
        buildButtonMap[Terrain.HARD_TOWER]=b3
    }

    fun getKazna(){
        getIncome()
        getSale()

        kazna=kazna+income-sale
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

    fun onTouch(event: MotionEvent)
    {
        getIncome()
        when (event.actionMasked)
        {
            MotionEvent.ACTION_DOWN -> {

                //strengthAddUnit = if (strengthAddUnit!=0) strengthAddUnit else 0
                //selectAddBulid = if (selectAddBulid!=0) selectAddBulid else 0

                val (massX, massY) = Search_massY_massY(event.x,event.y)

                if (massX != null && massY != null){
                    if (!delivereBuild) {
                        if (!delivereUnit) {
                            if (selectedUnit != null && units[selectedUnit!!].canMove/* ==true */ ) {

                                val possibleMoves = Possible_moves(
                                    units[selectedUnit!!].koorOnPole.x,
                                    units[selectedUnit!!].koorOnPole.y,
                                    2,
                                    units[selectedUnit!!]
                                )
                                for (i in possibleMoves.indices) {
                                    if (massX == possibleMoves[i].x && massY == possibleMoves[i].y) {

                                        if (units[selectedUnit!!].koorOnPole.x==massX &&
                                                units[selectedUnit!!].koorOnPole.y==massY) {
                                            units[selectedUnit!!].canMove = true
                                        } else {
                                            units[selectedUnit!!].canMove = false
                                        }

                                        //pole.mass[units[selectedUnit!!].koorOnPole.x][units[selectedUnit!!].koorOnPole.y].occupied = false
                                        units[selectedUnit!!].koorOnPole.x = massX
                                        units[selectedUnit!!].koorOnPole.y = massY

                                        pole.mass[massX][massY].player = this
                                       // pole.mass[massX][massY].occupied = true

                                        units[selectedUnit!!].size.value = pole.dx

                                        selectedUnit = null
                                        break
                                    }
                                }
                            } else { // selectedUnit == null
                                for (i in units.indices) {
                                    if (massX == units[i].koorOnPole.x && massY == units[i].koorOnPole.y) {
                                        if (units[i].canMove) {
                                            selectedUnit = i
                                            units[i].size.value = pole.dx + pole.dx / 2
                                            break
                                        }
                                    }
                                }
                            }
                        } else { //delivereUnit == true
                            //if (pole.mass[massX][massY].player==this && !pole.mass[massX][massY].occupied) {
                                addUnit(massX, massY, selectAddUnit, pole.dx)
                                pole.mass[massX][massY].player=this
                            //}
                            selectAddUnit = Terrain.UNIT1
                            delivereUnit = false
                        }
                    } else{
                        if (pole.mass[massX][massY].player==this) {
                            addBuild(massX, massY, selectAddBulid)
                        }
                        selectAddBulid = Terrain.NONE
                        delivereBuild = false
                    }
                }
                //touchRender++
            }

            MotionEvent.ACTION_UP -> {
                for (i in unitButtonsMap){
                    i.value.type.value=1
                }
                for (i in buildButtonMap){
                    i.value.type.value=1
                }

                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (delivereUnit){
                    for (i in unitButtonsMap){
                        if (i.value.click(mx,my)){
                            selectAddUnit = i.key
                            i.value.type.value=2
                            break
                        }
                    }
                }

                if (delivereBuild){
                    for (i in buildButtonMap){
                        if (i.value.click(mx,my)){
                            selectAddBulid = i.key
                            i.value.type.value=2
                            break
                        }
                    }
                }

                if (buildSettings.click(mx,my)){
                    delivereBuild = !delivereBuild // if (true) false else true
                    if (delivereBuild){
                        selectAddBulid= Terrain.FARM
                        val a = buildButtonMap.getValue(selectAddBulid)
                        a.type.value=2
                    }
                    delivereUnit = false
                }

                if (unitSettings.click(mx,my)) {
                    delivereUnit = !delivereUnit // if (true) false else true
                    if (delivereUnit){
                        selectAddUnit= Terrain.UNIT1
                        val a = unitButtonsMap.getValue(selectAddUnit)
                        a.type.value=2
                    }
                    delivereBuild = false
                }

                touchRender++
            }
        }
    }

    @Composable
    fun Render()
    {
        val updateRender = touchRender


        /*
        val imageBitmapsUnits = remember {
            image_mass_units.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }

        val imageBitmapsBuilds = remember {
            image_mass_builds.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }*/



        val transparentHexagonBitmap = ImageBitmap.imageResource(id = R.drawable.transparent_hexagon)

        Log.d("","render")

        // Создаем TextMeasurer
        //val textMeasurer = rememberTextMeasurer()

        unitSettings.Render()
        buildSettings.Render()

        if (delivereUnit==true){
            for (i in unitButtonsMap){
                i.value.Render()
            }
        }
        if (delivereBuild==true){
            for (i in buildButtonMap){
                i.value.Render()
            }
        }

       // val images = getImages()
        Canvas(modifier = Modifier.fillMaxSize()) {
            RenderUnits()
            RenderFarms()
            RenderPossibleMoves(transparentHexagonBitmap)
            RenderPossibleCellBuild(transparentHexagonBitmap)
        }
    }

    /*@Composable
    fun getImages(): HashMap<Int, ImageBitmap> {
        val context = LocalContext.current
        var images=HashMap<Int, ImageBitmap>()
        for (i in image_mass){
            if (i.value != -1) {
                images[i.value] =
                    BitmapFactory.decodeResource(context.resources, i.value).asImageBitmap()
            }
        }
        return images
    }*/

    fun DrawScope.RenderPossibleMoves(possibleBitmap: ImageBitmap)
    {
        if (selectedUnit != null) {
            val selectedX = units[selectedUnit!!].koorOnPole.x
            val selectedY = units[selectedUnit!!].koorOnPole.y
            val possibleMoves = Possible_moves(selectedX, selectedY, 2, units[selectedUnit!!])

            // Преобразуем возможные ходы в пары Pair
            val possibleMovesSet = possibleMoves.map { Pair(it.x, it.y) }
            println(possibleMovesSet)
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
    fun DrawScope.RenderPossibleCellBuild(possibleBitmap: ImageBitmap){
        if (delivereBuild || delivereUnit){
            for (i in pole.mass.indices){
                for (j in pole.mass[i].indices){
                    if (pole.mass[i][j].player == this@Player && isValidMove(koorOnInt(i,j))) {
                        continue
                    }
                    this.RenderImage(i,j,pole.dx,pole.dx,possibleBitmap)
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
                    image
                )
            }
        }
    }

    fun DrawScope.RenderUnits()
    {
        for (i in units.indices){
            println(units[i].type)
            if (!image_mass.containsKey(units[i].type)) {
                continue
            }

                val image=image_mass.getValue(units[i].type)

            if (image!=null) {
                var scale=0
                if (selectedUnit!=null && i==selectedUnit){
                    scale=50
                }
                this.RenderImage(units[i].koorOnPole.x,units[i].koorOnPole.y,pole.dx,pole.dx,image,scale)

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

    fun addUnit(x: Int, y: Int, type: Terrain, size: Int){
        if (isValidMove(koorOnInt(x,y))) {
            units.add(Unit(koorOnInt(x, y), type, size, true))
        }
    }

    fun addBuild(x: Int, y: Int, type: Terrain){
        if (isValidMove(koorOnInt(x,y))) {
            buildings.add(Build(koorOnInt(x, y), type))
        }
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
        allMoves.addAll(movesAround)

        // Добавляем отфильтрованные ходы вокруг
        //allMoves.addAll(movesAround.filter { move ->
            // Находим юнита на этой позиции (если есть)
            //val unitAtCell = units.find { it.koorOnPole.x == move.x && it.koorOnPole.y == move.y }

            // Если юнита нет - клетка доступна
            //if (unitAtCell == null) return@filter true

            // Если есть юнит и он слабее текущего - клетка доступна для атаки
            // Если юнит сильнее или равен по силе - клетка недоступна
            //currentUnit1.strength > unitAtCell.strength
        //})

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
        if (pole.mass[koorOnInt.x][koorOnInt.y].land == Terrain.WATER) {
            return false
        }

        val koorPairUnits = units.map { Pair(it.koorOnPole.x, it.koorOnPole.y) }
        println(koorPairUnits)

        if (Pair(koorOnInt.x, koorOnInt.y) in koorPairUnits) {
            return false
        }

        val koorPairBuild = buildings.map { Pair(it.koorOnPole.x, it.koorOnPole.y) }

        if (Pair(koorOnInt.x, koorOnInt.y) in koorPairBuild) {
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

