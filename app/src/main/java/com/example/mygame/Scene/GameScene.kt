package com.example.mygame.Scene

import android.content.Context
import android.graphics.BitmapFactory
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.mygame.ButtonImage
import com.example.mygame.Economic
import com.example.mygame.GameStorage
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.GlobalParam.RenderImage
import com.example.mygame.GlobalParam.TextRender
import com.example.mygame.PackageForKoor.koorOnInt
import com.example.mygame.PackagePlayer.OtherForPlayer.Build
import com.example.mygame.PackagePlayer.Player
import com.example.mygame.Pole.Pole
import com.example.mygame.R
import com.example.mygame.Terrain
import com.example.mygame.PackagePlayer.OtherForPlayer.Unit
import com.example.mygame.PackagePlayer.OtherForPlayer.Unit_data
import com.example.mygame.Pole.Data_cell
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.data.GameMap_data
import com.example.mygame.data.Pole_data
import com.example.mygame.data.player_data
import com.example.mygame.data.profile_data
import java.io.File
import kotlin.collections.iterator

class GameScene(override var game: GameEngine, val context: Context) : Scene {
    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    var pole = Pole(context = context)

    val button_point_back = ButtonImage(
        (screenX * 0.9).toInt(), (screenY * 0.1).toInt(),
        (screenX * 0.05).toInt(), (screenY * 0.1).toInt(), R.drawable.point
    )
    val button_next_hod = ButtonImage(
        (screenX * 0.8).toInt(), (screenY * 0.9).toInt(),
        (screenX * 0.14).toInt(), (screenY * 0.07).toInt(), R.drawable.arrow_next_hod
    )

    val mini_fon_for_win = ButtonImage(
        (screenX * 0.1).toInt(), (screenY * 0.1).toInt(),
        (screenX * 0.8).toInt(), (screenY * 0.8).toInt(), R.drawable.mini_fon
    )

    val mini_fon_for_up_game = ButtonImage(
        -10, 0,
        screenX + 20, (screenY * 0.1).toInt(), R.drawable.mini_fon
    )

    var mini_fon_for_down_game = ButtonImage(
        -10, (screenY - screenY * 0.1).toInt(),
        screenX + 20, (screenY * 0.1).toInt(), R.drawable.mini_fon
    )

    var mini_fon_for_pause_menu = ButtonImage(
        (screenX * 0.13).toInt(), (screenY * 0.37).toInt(),
        (screenX * 0.735).toInt(), (screenY * 0.5).toInt(), R.drawable.mini_fon
    )

    val exit_win = ButtonImage(
        (screenX * 0.65).toInt(), (screenY * 0.77).toInt(),
        (screenX * 0.2).toInt(), (screenY * 0.1).toInt(), R.drawable.image_return_mirror
    )

    val leo_koin = ButtonImage(
        (screenX * 0.05).toInt(), (screenY * 0.055).toInt(),
        (screenX * 0.08).toInt(), (screenY * 0.04).toInt(), R.drawable.leo_koin
    )

    val check_mark = ButtonImage(
        (pole.display.x * 0.35).toInt(),
        (pole.display.y * 0.83).toInt(),
        (screenX * 0.08).toInt(),
        (screenY * 0.04).toInt(),
        R.drawable.check_mark
    )

    val cross = ButtonImage(
        (pole.display.x * 0.65).toInt(),
        (pole.display.y * 0.83).toInt(),
        (screenX * 0.08).toInt(),
        (screenY * 0.04).toInt(),
        R.drawable.cross
    )

    val button_return_in_game = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.41).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.return_in_game
    )

    val button_setting = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.515).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.settings
    )

    val button_is_look_rupes = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.62).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.is_look_rules
    )

    val button_return_in_menu = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.725).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.return_in_menu
    )

    val backGround_for_pause_menu = ButtonImage(
        0, 0,
        screenX, screenY, R.drawable.background_black
    )

    var hod_player by mutableStateOf(0)

    var listPlayers: ArrayList<Player> = ArrayList<Player>()
    var economic = HashMap<Terrain, Economic>()

    var image_mass= HashMap<Terrain, ImageBitmap>()

    // Добавляем State для контроля перерисовки
    private var renderTrigger by mutableStateOf(0)

    var delivereUnit by mutableStateOf(false)
    var delivereBuild by mutableStateOf(false)
    var delivereNextHod by mutableStateOf(false)

    var isPauseMenu by mutableStateOf(false)

    var selectAddUnit: Terrain = Terrain.NONE
    var selectAddBulid: Terrain = Terrain.NONE

    val buildSettings = ButtonImage(
        (pole.display.x * 0.29).toInt(),
        (pole.display.y * 0.89).toInt(),
        (pole.display.x * 0.21).toInt(),
        (pole.display.y * 0.1).toInt(),
        R.drawable.farm
    )

    val unitSettings = ButtonImage(
        (pole.display.x * 0.6).toInt(),
        (pole.display.y * 0.9).toInt(),
        (pole.display.x * 0.21).toInt(),
        (pole.display.y * 0.09).toInt(),
        R.drawable.skeleton
    )

    val buttonDownload = ButtonImage(
        (screenX * 0.5).toInt(), (screenY * 0.3).toInt(),
        (screenX * 0.2).toInt(), (screenY * 0.1).toInt(), R.drawable.download
    )

    val buttonSave = ButtonImage(
        (screenX * 0.3).toInt(), (screenY * 0.3).toInt(),
        (screenX * 0.2).toInt(), (screenY * 0.1).toInt(), R.drawable.save
    )

    var unitButtonsMap = HashMap<Terrain, ButtonImage>()
    var buildButtonMap = HashMap<Terrain, ButtonImage>()

    var isSaved: Boolean? by mutableStateOf(null)
    var isDownload: Boolean? by mutableStateOf(null)

    init {
        createPLayers()
        initImage()
        initEconomic2()
        initButtonUnit()  // Инициализируем здесь
        initButtonBuild() // Инициализируем здесь
    }

    // Переносим методы инициализации кнопок
    fun initButtonUnit() {
        val b1 = ButtonImage(
            (pole.display.x * 0.25).toInt(),
            (pole.display.y * 0.77).toInt(),
            150,
            150,
            R.drawable.skeleton,
            name2 = R.drawable.dedicated_skeleton,
            type = mutableStateOf(false)
        )
        unitButtonsMap[Terrain.UNIT1]=b1

        val b2 = ButtonImage(
            (pole.display.x * 0.42).toInt(),
            (pole.display.y * 0.77).toInt(),
            150, 150,
            R.drawable.barbarian,
            name2 = R.drawable.dedicated_barbarin,
            type = mutableStateOf(false)
        )
        unitButtonsMap[Terrain.UNIT2]=b2
        val b3 = ButtonImage(
            (pole.display.x * 0.58).toInt(),
            (pole.display.y * 0.77).toInt(),
            150, 150,
            R.drawable.knight,
            name2 = R.drawable.dedicated_knight,
            type = mutableStateOf(false)
        )
        unitButtonsMap[Terrain.UNIT3]=b3
        val b4 = ButtonImage(
            (pole.display.x * 0.75).toInt(),
            (pole.display.y * 0.77).toInt(),
            150, 150,
            R.drawable.hard_khight,
            name2 = R.drawable.dedicated_hard_knight,
            type = mutableStateOf(false)
        )
        unitButtonsMap[Terrain.UNIT4]=b4
    }

    fun initButtonBuild() {
        val b1 = ButtonImage(
            (pole.display.x * 0.25).toInt(),
            (pole.display.y * 0.76).toInt(),
            150, 150,
            R.drawable.farm,
            name2 = R.drawable.dedicated_farm,
            type = mutableStateOf(false)
        )
        buildButtonMap[Terrain.FARM]=b1
        val b2 = ButtonImage(
            (pole.display.x * 0.45).toInt(),
            (pole.display.y * 0.76).toInt(),
            150, 150,
            R.drawable.tower,
            name2 = R.drawable.dedicated_tower,
            type = mutableStateOf(false)
        )
        buildButtonMap[Terrain.TOWER]=b2
        val b3 = ButtonImage(
            (pole.display.x * 0.65).toInt(),
            (pole.display.y * 0.76).toInt(),
            150, 150,
            R.drawable.tower_hard,
            name2 = R.drawable.dedicated_hard_tower,
            type = mutableStateOf(false)
        )
        buildButtonMap[Terrain.HARD_TOWER]=b3
    }

    fun initImage(){

        //image_mass[Terrain.LAND]=-1
        image_mass[Terrain.FARM]=
            BitmapFactory.decodeResource(context.resources, R.drawable.farm).asImageBitmap()
        image_mass[Terrain.TOWER]=
            BitmapFactory.decodeResource(context.resources, R.drawable.tower).asImageBitmap()
        image_mass[Terrain.HARD_TOWER]=
            BitmapFactory.decodeResource(context.resources, R.drawable.tower_hard).asImageBitmap()

        image_mass[Terrain.UNIT1]=
            BitmapFactory.decodeResource(context.resources, R.drawable.skeleton).asImageBitmap()
        image_mass[Terrain.UNIT2]=
            BitmapFactory.decodeResource(context.resources, R.drawable.barbarian).asImageBitmap()
        image_mass[Terrain.UNIT3]=
            BitmapFactory.decodeResource(context.resources, R.drawable.knight).asImageBitmap()
        image_mass[Terrain.UNIT4]=
            BitmapFactory.decodeResource(context.resources, R.drawable.hard_khight).asImageBitmap()

    }

    fun initEconomic(){
        val farm = Economic(price = 120, protection = 0, income = 50, sale = 0, attack = 0)
        economic[Terrain.FARM]=farm

        val tower = Economic(price = 150, protection = 3, income = 0, sale = 75, attack = 0)
        economic[Terrain.TOWER]=tower

        val hard_tower = Economic(price = 300, protection = 4, income = 0, sale = 150, attack = 0)
        economic[Terrain.HARD_TOWER]=hard_tower

        val unit1 = Economic(price = 100, protection = 1, income = 0, sale = 30, attack = 1)
        val unit2 = Economic(price = 200, protection = 2, income = 0, sale = 50, attack = 2)
        val unit3 = Economic(price = 300, protection = 3, income = 0, sale = 140, attack = 3)
        val unit4 = Economic(price = 400, protection = 4, income = 0, sale = 200, attack = 4)

        economic[Terrain.UNIT1] = unit1
        economic[Terrain.UNIT2] = unit2
        economic[Terrain.UNIT3] = unit3
        economic[Terrain.UNIT4] = unit4

        val cell = Economic(price = 0, protection = 0, income = 10, sale = 0, attack = 0)
        economic[Terrain.LAND]=cell
        //var kk=economic[Terrain.FARM]
        //println(kk?.price)
    }
    fun initEconomic2(){
        val farm = Economic(price = 200, protection = 0, income = 30, sale = 0, attack = 0)
        economic[Terrain.FARM]=farm
        economic[Terrain.immutableFARM]=farm

        val tower = Economic(price = 120, protection = 2, income = 0, sale = 50, attack = 0)
        economic[Terrain.TOWER]=tower


        val hard_tower = Economic(price = 350, protection = 3, income = 0, sale = 200, attack = 0)
        economic[Terrain.HARD_TOWER]=hard_tower

        val unit1 = Economic(price = 80, protection = 1, income = 0, sale = 20, attack = 1)
        val unit2 = Economic(price = 220, protection = 2, income = 0, sale = 80, attack = 2)
        val unit3 = Economic(price = 600, protection = 3, income = 0, sale = 300, attack = 3)
        val unit4 = Economic(price = 1500, protection = 4, income = 0, sale = 800, attack = 4)

        economic[Terrain.UNIT1] = unit1
        economic[Terrain.UNIT2] = unit2
        economic[Terrain.UNIT3] = unit3
        economic[Terrain.UNIT4] = unit4

        val cell = Economic(price = 0, protection = 0, income = 10, sale = 0, attack = 0)
        economic[Terrain.LAND]=cell
        //var kk=economic[Terrain.FARM]
        //println(kk?.price)
    }

    fun createPLayers(){
        addPlayers()
        //listPlayers[hod_player].getKazna()
        for (player in listPlayers){
            player.kazna = 30
            player.income = 70
            player.sale = 0
        }

        for (player in listPlayers.indices){
            //val (x,y)=randomXY()

            if (player==0){
                pole.mass[pole.mass.size-2][1].player=listPlayers[player]
                //pole.mass[pole.mass.size-1][0].player=listPlayers[player]
                cellPlayerInit(pole.mass.size-2,1,listPlayers[player])
                listPlayers[player].units.add(
                    Unit(
                        koorOnInt(pole.mass.size - 2, 1),
                        Terrain.UNIT1,
                        pole.dx,
                        2
                    )
                )
                //listPlayers[player].addUnit(pole.mass.size-2,0, Terrain.UNIT1,pole.dx)
            } else if (player==1){
                pole.mass[1][pole.mass[0].size-2].player=listPlayers[player]
                cellPlayerInit(1,pole.mass[0].size-2,listPlayers[player])
                listPlayers[player].units.add(
                    Unit(
                        koorOnInt(1, pole.mass[0].size - 2),
                        Terrain.UNIT1,
                        pole.dx,
                        2
                    )
                )
            } else if (player==2){
                pole.mass[1][1].player=listPlayers[player]
                cellPlayerInit(1,1,listPlayers[player])
                listPlayers[player].units.add(
                    Unit(
                        koorOnInt(1, 1),
                        Terrain.UNIT1,
                        pole.dx,
                        2
                    )
                )
            }

            //pole.mass[x][y].player=player
            //cellPlayerInit(x,y,player)

            /*
            pole.mass[1][1].player=player
            cellPlayerInit(1,1,pole.mass[1][1].player!!)
            */
        }
    }

    fun cellPlayerInit(X: Int, Y: Int, player: Player) {
        val cells = cells_around(X, Y)
        println(cells.toString())
        for (i in cells){
            pole.mass[i.x][i.y].player=player
        }
    }

    fun addPlayers() {
        val player1 = Player(profile_data.DEFAULT_PROFILE.playerName, pole, 1, economic, image_mass, this, context)
        val player2 = Player("Игрок 2", pole, 3, economic, image_mass, this, context)
        //val player3 = Player("Игрок 3", pole, 4, economic, image_mass, this, context)
        listPlayers = arrayListOf(player1, player2)
    }

    fun forceRender() {
        renderTrigger++
    }

    fun cells_around(X: Int, Y: Int): MutableList<koorOnInt> {
        val mass = arrayListOf<koorOnInt>()
        val cells = mutableListOf<koorOnInt>()

        mass.add(koorOnInt(X, Y))
        mass.add(koorOnInt(X - 1, Y))
        mass.add(koorOnInt(X, Y - 1))
        mass.add(koorOnInt(X + 1, Y))
        mass.add(koorOnInt(X, Y + 1))
        if (X % 2 == 0) {
            mass.add(koorOnInt(X - 1, Y - 1))
            mass.add(koorOnInt(X + 1, Y - 1))
        } else{
            mass.add(koorOnInt(X - 1, Y + 1))
            mass.add(koorOnInt(X + 1, Y + 1))
        }

        for (i in mass){
            if (isValidMove(i)){
                cells.add(i)
            }
        }

        return cells
    }

    fun updateProtectionPole(){
        for (i in pole.mass){
            for (cell in i){
                cell.protection=0
            }
        }
        for (player in listPlayers) {

            val buildList = player.buildings.sortedBy { it.type }
            val unitsList = player.units.sortedBy { it.type }

            for (building in buildList) {
                val aroundCells = cells_around(building.koorOnPole.x, building.koorOnPole.y)

                for (cell in aroundCells) {
                    val buildType = economic[building.type]!!.protection
                    if (pole.mass[cell.x][cell.y].protection<buildType
                        && pole.mass[cell.x][cell.y].player == player) {
                        pole.mass[cell.x][cell.y].protection = buildType
                    }
                }
            }

            for (unit in unitsList){
                val aroundCells = cells_around(unit.koorOnPole.x,unit.koorOnPole.y)

                for (cell in aroundCells){
                    val unitType = economic[unit.type]!!.protection

                    if (pole.mass[cell.x][cell.y].protection<unitType
                        && pole.mass[cell.x][cell.y].player == player){
                        pole.mass[cell.x][cell.y].protection=unitType
                    }
                }
            }
        }
    }

    fun isValidMove(koorOnInt: koorOnInt): Boolean {
        // Проверяем границы массива
        if (koorOnInt.x < 0 || koorOnInt.x >= pole.mass.size || koorOnInt.y < 0 || koorOnInt.y >= pole.mass[0].size) {
            //println("koorOnInt.x < 0 || koorOnInt.x >= pole.mass.size || koorOnInt.y < 0 || koorOnInt.y >= pole.mass[0].size")
            return false
        }

        // Проверяем, что не вода
        if (pole.mass[koorOnInt.x][koorOnInt.y].land == Terrain.WATER) {
            //println("pole.mass[koorOnInt.x][koorOnInt.y].land == Terrain.WATER")
            return false
        }

        return true
    }

    override fun update() {

    }

    private fun save_update_json(){
        try {
            var list_data_players: ArrayList<player_data> = ArrayList<player_data>()

            for (player in listPlayers) {
                var units = ArrayList<Unit_data>()
                for (unit in player.units) {
                    units.add(
                        Unit_data(
                            koorOnInt = koorOnInt(unit.koorOnPole.x, unit.koorOnPole.y),
                            type = unit.type,
                            Movement = unit.Movement,
                            size = unit.size.value

                        )
                    )
                }

                val data_player = player_data(
                    name = player.name,
                    color = player.color,
                    units = units,
                    buildings = player.buildings,
                    kazna = player.kazna,
                    income = player.income,
                    sale = player.sale
                )
                list_data_players.add(data_player)
            }

            // Преобразуем HashMap<Terrain, Economic> в Map<String, Economic>
            val economicMap = mutableMapOf<String, Economic>()
            economic.forEach { (terrain, eco) ->
                economicMap[terrain.name] = eco
            }

            var data_pole = ArrayList<ArrayList<Data_cell>>()
            for (i in pole.mass) {
                val mass = ArrayList<Data_cell>()
                for (cell in i) {
                    var number: Int? = null
                    for (player in listPlayers.indices) {
                        if (listPlayers[player] == cell.player) {
                            number = player
                        }
                    }
                    val data_cell: Data_cell = Data_cell(
                        player = number,
                        protection = cell.protection,
                        land = cell.land
                    )
                    mass.add(data_cell)
                }
                data_pole.add(mass)
            }

            val poleData = Pole_data(pole = data_pole)

            val gameMap = GameMap_data(
                economic = economicMap,
                listPlayer = list_data_players,
                hod_player = hod_player,
                pole = poleData
            )

            val gameStorage = GameStorage(context)
            val name_json = "game_map1.json"
            gameStorage.saveToJsonGameMap(name_json, gameMap)

            // путь к файлу
            val filePath = File(context.filesDir, name_json).absolutePath
            println("Карта сохранена в: $filePath")
            isSaved=true
            isDownload=null
        } catch (e: Error){
            isSaved=false
            isDownload=null
        }
    }

    private fun load_json() {
        try {
            val gameStorage = GameStorage(context)
            val gameMap = gameStorage.loadFromJsonGameMap("game_map1.json")

            // Обратное преобразование из Map<String, Economic> в HashMap<Terrain, Economic>
            val terrainMap = HashMap<Terrain, Economic>()
            for ((terrainName, economicId) in gameMap!!.economic) {
                when (terrainName) {
                    "NONE" -> terrainMap[Terrain.NONE] = economicId
                    "WATER" -> terrainMap[Terrain.WATER] = economicId
                    "LAND" -> terrainMap[Terrain.LAND] = economicId
                    "FARM" -> terrainMap[Terrain.FARM] = economicId
                    "TOWER" -> terrainMap[Terrain.TOWER] = economicId
                    "HARD_TOWER" -> terrainMap[Terrain.HARD_TOWER] = economicId
                    "UNIT1" -> terrainMap[Terrain.UNIT1] = economicId
                    "UNIT2" -> terrainMap[Terrain.UNIT2] = economicId
                    "UNIT3" -> terrainMap[Terrain.UNIT3] = economicId
                    "UNIT4" -> terrainMap[Terrain.UNIT4] = economicId
                    //else -> println("Warning: Unknown terrain type: $terrainName")
                }
            }

            economic = terrainMap // сделан economic

            hod_player = gameMap.hod_player // сделан hod_player

            // Очищаем список игроков
            listPlayers.clear()

            for (playerid in gameMap.listPlayer.indices) {
                val player = Player(
                    name = gameMap.listPlayer[playerid].name,
                    pole = pole,// пока что старый
                    color = gameMap.listPlayer[playerid].color,
                    economic = economic, // уже обновлен
                    image_mass = image_mass,
                    gameScene = this,
                    context = context
                )

                player.kazna = gameMap.listPlayer[playerid].kazna
                player.income = gameMap.listPlayer[playerid].income
                player.sale = gameMap.listPlayer[playerid].sale

                listPlayers.add(player)
            } // сделаны игроки

            val poleData = gameMap.pole.pole

            for (i in poleData.indices) {
                for (j in poleData[i].indices) {
                    if (poleData[i][j].player != null) {
                        println(poleData[i][j].player!!)
                        pole.mass[i][j].player = listPlayers[poleData[i][j].player!!]
                        pole.mass[i][j].protection = poleData[i][j].protection
                        pole.mass[i][j].land = poleData[i][j].land
                    } else {
                        pole.mass[i][j].player = null
                        pole.mass[i][j].protection = 0
                        pole.mass[i][j].land = Terrain.LAND
                    }
                }
            }

            for (player in listPlayers.indices) {

                listPlayers[player].pole = pole //обновляем pole у игроков

                listPlayers[player].units.clear()

                for (unit in gameMap.listPlayer[player].units.indices) {
                    listPlayers[player].units.add(
                        Unit(
                            koorOnPole = gameMap.listPlayer[player].units[unit].koorOnInt,
                            type = gameMap.listPlayer[player].units[unit].type,
                            Movement = gameMap.listPlayer[player].units[unit].Movement
                        )
                    )
                }

                for (build in gameMap.listPlayer[player].buildings.indices) {
                    listPlayers[player].buildings.add(
                        Build(
                            koorOnPole = gameMap.listPlayer[player].buildings[build].koorOnPole,
                            type = gameMap.listPlayer[player].buildings[build].type
                        )
                    )
                }
            }

            forceRender()
            game.forceUpdate++
            isDownload=true
            isSaved=null
            println("Игра успешно загружена")
        } catch (e: Error){
            isDownload=false
            isSaved=null
        }
    }

    override fun onTouchEvent(event: MotionEvent) {
        val listPLayersHadCells = getListPlayersHadCells()

        if (!isPauseMenu) {
            listPlayers[hod_player].onTouch(event)
            updateProtectionPole()
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { }

            MotionEvent.ACTION_UP -> {
                if (event.pointerCount == 1) {
                    val mx = event.x.toInt()
                    val my = event.y.toInt()

                    if (!isPauseMenu) {
                        // Сбрасываем выделение всех кнопок
                        for (i in unitButtonsMap) {
                            i.value.type.value = false
                        }
                        for (i in buildButtonMap) {
                            i.value.type.value = false
                        }

                        // Обработка кнопки buildings
                        if (!delivereNextHod && buildSettings.click(mx, my)) {

                            update_price_farm()

                            delivereBuild = !delivereBuild
                            if (delivereBuild) {
                                selectAddBulid = Terrain.NONE
                            }
                            delivereUnit = false
                        }

                        // Обработка кнопки units
                        if (!delivereNextHod && unitSettings.click(mx, my)) {
                            delivereUnit = !delivereUnit
                            if (delivereUnit) {
                                selectAddUnit = Terrain.NONE
                            }
                            delivereBuild = false
                        }

                        if (delivereNextHod) {
                            if (check_mark.click(mx, my)) {
                                nextHodPlayer()
                            }
                            if (cross.click(mx, my)) {
                                delivereNextHod = false
                                delivereUnit = false
                                delivereBuild = false
                            }
                        }

                        // Обработка выбора конкретного юнита/здания
                        if (delivereUnit) {
                            for (i in unitButtonsMap) {
                                if (i.value.click(mx, my)) {
                                    selectAddUnit = i.key
                                    println(selectAddUnit)
                                    i.value.type.value = true
                                    break
                                }
                            }
                        }

                        if (delivereBuild) {
                            for (i in buildButtonMap) {
                                if (i.value.click(mx, my)) {
                                    selectAddBulid = i.key
                                    i.value.type.value = true
                                    break
                                }
                            }
                        }

                        if (listPLayersHadCells.size != 1) {
                            if (button_point_back.click(mx, my)) {
                                isPauseMenu = true
                            }
                            if (button_next_hod.click(mx, my)) {
                                delivereNextHod = true
                                forceRender()
                            }

                        } else {
                            if (exit_win.click(mx, my)) {
                                pole = Pole(context = context)
                                createPLayers()
                                game.CurrentScene = "Menu"
                            }
                        }

                        if (delivereBuild || delivereUnit || delivereNextHod) {
                            mini_fon_for_down_game.y = (screenY - screenY * 0.25).toInt()
                            mini_fon_for_down_game.dy = (screenY * 0.25).toInt()
                            pole.ogranichenie.max.y = (screenY - screenY * 0.25).toInt()
                        } else {
                            mini_fon_for_down_game.y = (screenY - screenY * 0.1).toInt()
                            mini_fon_for_down_game.dy = (screenY * 0.1).toInt()
                            pole.ogranichenie.max.y =
                                pole.ogranichenie.min.y + (pole.mass[0].size) * pole.minDx + pole.minDx / 2
                        }
                    }
                    else {
                        // Меню паузы

                        if (buttonSave.click(mx,my)){
                            save_update_json()

                        }
                        if (buttonDownload.click(mx,my)){
                            load_json()
                        }

                        if (button_return_in_game.click(mx,my)){
                            isPauseMenu = false
                            isSaved=null
                            isDownload=null
                            return
                        }
                        if (button_setting.click(mx,my)){
                            game.updateScene("Setting")
                            return
                        }
                        if (button_is_look_rupes.click(mx,my)){
                            game.updateScene("Rules")
                        }

                        if (button_return_in_menu.click(mx,my)){
                            game.updateScene("Menu")
                            return
                        }
                    }
                }
            }
        }

        pole.onTouch(event)
        game.forceUpdate++

    }

    @Composable
    override fun render() {
        val currentRenderTrigger = remember { renderTrigger }
        val listPLayersHadCells = getListPlayersHadCells()
        val textMeasurer = rememberTextMeasurer()

        val transparentHexagonBitmap = ImageBitmap.Companion.imageResource(id = R.drawable.transparent_hexagon)
        // win
        if (listPLayersHadCells.size==1){
            Image(
                painter = painterResource(id = R.drawable.background_win),// Укажите ваш файл
                contentDescription = "фон", // Описание для доступности (обязательно!)
                contentScale = ContentScale.Companion.FillBounds,
                modifier = Modifier.Companion.fillMaxSize()
            )

            mini_fon_for_win.Render()
            exit_win.Render()
            val winnerPlayer = listPLayersHadCells[0]

            val imageBitmaps = remember {
                pole.image_mass.map { resourceId ->
                    BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
                }
            }

            Canvas(
                modifier = Modifier.Companion
                    .fillMaxSize()
            ) {
                TextRender(
                    textMeasurer,
                    "Конец игры",
                    (pole.display.x * 0.5).toInt(),
                    (pole.display.y * 0.18).toInt(),
                    40,
                    true,
                    center = true
                )
                TextRender(
                    textMeasurer,
                    "Победитель",
                    (pole.display.x * 0.5).toInt(),
                    (pole.display.y * 0.25).toInt(),
                    35,
                    center = true
                )
                TextRender(
                    textMeasurer,
                    listPlayers[winnerPlayer].name,
                    (pole.display.x * 0.5).toInt(),
                    (pole.display.y * 0.31).toInt(),
                    30,
                    center = true

                )
                drawImage(
                    image = imageBitmaps[listPlayers[winnerPlayer].color],
                    dstOffset = IntOffset(
                        (pole.display.x * 0.7).toInt(),
                        (pole.display.y * 0.31).toInt()
                    ),
                    dstSize = IntSize(pole.dx, pole.dx)
                )
            }
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.ocean),
                contentDescription = "фон",
                contentScale = ContentScale.Companion.FillBounds,
                modifier = Modifier.Companion.fillMaxSize()
            )

            pole.Render()

            if (delivereUnit || delivereBuild && !delivereNextHod) {
                Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                    RenderConstructionCells(transparentHexagonBitmap)
                }
            }

            // Отображаем текущего игрока
            for (i in listPlayers.indices) {
                var renderPlayerIndicator = false
                if (i == hod_player) {
                    renderPlayerIndicator = true
                }
                listPlayers[i].Render(renderPlayerIndicator)
            }

            mini_fon_for_up_game.Render()
            leo_koin.Render()

            Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                TextRender(
                    textMeasurer,
                    name = listPlayers[hod_player].name,
                    x = (screenX * 0.5).toInt(),
                    y = (screenY * 0.06).toInt(),
                    size = 25,
                    bold = true,
                    center = true
                )

                TextRender(
                    textMeasurer,
                    name = listPlayers[hod_player].kazna.toString(),
                    x = (screenX * 0.15).toInt(),
                    y = (screenY * 0.06).toInt(),
                    size = 25,
                    bold = true
                )

                var textIncome = "+"
                if (listPlayers[hod_player].income - listPlayers[hod_player].sale < 0) {
                    textIncome = ""
                }
                TextRender(
                    textMeasurer,
                    name = "$textIncome${listPlayers[hod_player].income - listPlayers[hod_player].sale}",
                    x = (screenX * 0.8).toInt(),
                    y = (screenY * 0.06).toInt(),
                    size = 25,
                    bold = true,
                    center = true
                )
            }

            mini_fon_for_down_game.Render()
            button_point_back.Render()
            button_next_hod.Render()
            if (delivereNextHod) {
                check_mark.Render()
                cross.Render()
                Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                    TextRender(
                        textMeasurer,
                        name = "Вы хотите закончить ход?",
                        x = (screenX * 0.5).toInt(),
                        y = (screenY * 0.79).toInt(),
                        size = 25,
                        center = true
                    )
                }
            }

            if (delivereUnit && !delivereNextHod) {

                Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                    TextRender(
                        textMeasurer,
                        name = "Цена",
                        x = (screenX * 0.15).toInt(),
                        y = (screenY * 0.83).toInt(),
                        size = 25,
                        center = true
                    )

                    TextRender(
                        textMeasurer,
                        name = "Доход",
                        x = (screenX * 0.15).toInt(),
                        y = (screenY * 0.86).toInt(),
                        size = 25,
                        center = true
                    )
                }

                for (i in unitButtonsMap) {
                    i.value.Render()
                    val economicBuild = economic.getValue(i.key)
                    Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                        TextRender(
                            textMeasurer,
                            name = "${economicBuild.price}",
                            x = i.value.x + i.value.dx / 2,
                            y = (i.value.y + i.value.dx).toInt(),
                            size = 25,
                            center = true
                        )

                        TextRender(
                            textMeasurer,
                            name = "${(economicBuild.income - economicBuild.sale)}",
                            x = i.value.x + i.value.dx / 2,
                            y = (screenY * 0.86).toInt(),
                            size = 25,
                            center = true
                        )
                    }
                }
            }

            if (delivereBuild && !delivereNextHod) {
                Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                    TextRender(
                        textMeasurer,
                        name = "Цена",
                        x = (screenX * 0.15).toInt(),
                        y = (screenY * 0.83).toInt(),
                        size = 25,
                        center = true
                    )

                    TextRender(
                        textMeasurer,
                        name = "Доход",
                        x = (screenX * 0.15).toInt(),
                        y = (screenY * 0.86).toInt(),
                        size = 25,
                        center = true
                    )
                }
                for (i in buildButtonMap) {
                    i.value.Render()
                    val economicBuild = economic.getValue(i.key)
                    Canvas(modifier = Modifier.Companion.fillMaxSize()) {
                        TextRender(
                            textMeasurer,
                            name = "${economicBuild.price}",
                            x = i.value.x + i.value.dx / 2,
                            y = (i.value.y + i.value.dx).toInt(),
                            size = 25,
                            center = true
                        )

                        TextRender(
                            textMeasurer,
                            name = "${(economicBuild.income - economicBuild.sale)}",
                            x = i.value.x + i.value.dx / 2,
                            y = (screenY * 0.86).toInt(),
                            size = 25,
                            center = true
                        )
                    }
                }
            }

            // Отображаем кнопки выбора зданий и юнитов
            buildSettings.Render()
            unitSettings.Render()
        }

        if (isPauseMenu){
            backGround_for_pause_menu.Render(alpha = 0.7f)
            mini_fon_for_pause_menu.Render(alpha = 1f)

            buttonDownload.Render()
            buttonSave.Render()

            Canvas(modifier = Modifier.fillMaxSize()) {
                if (isSaved!=null) {
                    TextRender(
                        textMeasurer,
                        name = if (isSaved!!) "Игра успешна сохранена" else "не получилось сохранить игру",
                        x = screenX / 2,
                        y = (screenY * 0.25).toInt(),
                        color = Color.White,
                        center = true
                    )
                }else if (isDownload!=null){
                    TextRender(
                        textMeasurer,
                        name = if (isDownload!!) "Игра успешна загружена" else "не получилось загрузить игру",
                        x = screenX / 2,
                        y = (screenY * 0.25).toInt(),
                        color = Color.White,
                        center = true
                    )
                }
            }

            button_return_in_game.Render()
            button_setting.Render()
            button_is_look_rupes.Render()
            button_return_in_menu.Render()
        }
    }

    fun DrawScope.RenderConstructionCells(possibleBitmap: ImageBitmap){
        for (i in pole.mass.indices){
            for (j in pole.mass[i].indices){
                if (pole.mass[i][j].player == listPlayers[hod_player] && isValidMove(
                        koorOnInt(
                            i,
                            j
                        )
                    )) {
                    continue
                }else {
                    RenderImage(i, j, pole.dx, pole.dx, possibleBitmap,pole = pole)
                }
            }
        }
    }

    fun update_price_farm(){
        var kolvoFarms = 0
        for (i in listPlayers[hod_player].buildings) {
            if (i.type == Terrain.FARM) {
                kolvoFarms++
            }
        }
        val basePrice = economic[Terrain.immutableFARM]?.price ?: 200
        val priceFarms = basePrice + kolvoFarms * 20
        val farm = Economic(
            price = priceFarms,
            protection = 0,
            income = 50,
            sale = 0,
            attack = 0
        )
        economic[Terrain.FARM] = farm
    }

    fun getListPlayersHadCells(): List<Int> {
        val list = arrayListOf<Int>()

        for (i in pole.mass.indices){
            for (j in pole.mass[i].indices){
                val cell = pole.mass[i][j]
                for (player in listPlayers.indices) {
                    if (cell.player == listPlayers[player]){
                        list.add(player)
                        break
                    }
                }
            }
        }

        return list.toSet().toList()
    }

    fun nextHodPlayer() {
        delivereBuild = false
        delivereUnit = false
        delivereNextHod = false
        selectAddUnit = Terrain.NONE
        selectAddBulid = Terrain.NONE

        listPlayers[hod_player].selectedUnit=null

        mini_fon_for_down_game.y=(screenY-screenY*0.1).toInt()
        mini_fon_for_down_game.dy=(screenY*0.1).toInt()
        pole.ogranichenie.max.y=pole.ogranichenie.min.y+(pole.mass[0].size)*pole.minDx+pole.minDx/2

        // Сбрасываем выделение кнопок
        for (i in unitButtonsMap) {
            i.value.type.value = false
        }
        for (i in buildButtonMap) {
            i.value.type.value = false
        }

        hod_player++
        if (hod_player >= listPlayers.size) {
            hod_player = 0
        }

        for (i in listPlayers[hod_player].units) {
            i.Movement = 2
        }

        listPlayers[hod_player].getIncome()
        listPlayers[hod_player].getSale()
        listPlayers[hod_player].getKazna()

        if (listPlayers[hod_player].kazna+(listPlayers[hod_player].income-listPlayers[hod_player].sale)<0){
            listPlayers[hod_player].units.clear()

            listPlayers[hod_player].getIncome()
            listPlayers[hod_player].getSale()
            listPlayers[hod_player].getKazna()
        }
    }

    override fun onEnter() {
        isPauseMenu = false
        isSaved=null
        isDownload=null
        hod_player = 0

        // Сбрасываем состояния выбора
        delivereBuild = false
        delivereUnit = false
        selectAddUnit = Terrain.NONE
        selectAddBulid = Terrain.NONE

        // Сбрасываем настройки игроков и поля
        pole = Pole(context = context)
        createPLayers()

        for (player in listPlayers){
            player.kazna=30
            player.income=0
            player.sale=0
        }
        // Обновляем экономику
        listPlayers[hod_player].getKazna()

        forceRender()
    }

    override fun onExit() {

    }
}