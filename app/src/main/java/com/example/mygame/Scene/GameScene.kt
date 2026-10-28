package com.example.mygame.Scene

import android.content.Context
import android.graphics.BitmapFactory
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.example.mygame.ButtonImage
import com.example.mygame.Economic
import com.example.mygame.GameHudLayout
import com.example.mygame.GameHudRender
import com.example.mygame.GameStorage
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.GlobalParam.RenderImage
import com.example.mygame.PackageForKoor.ScreenRel
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
import com.example.mygame.SoundPlayer
import com.example.mygame.data.GameMap_data
import com.example.mygame.data.Pole_data
import com.example.mygame.data.player_data
import com.example.mygame.data.profile_data
import java.io.File
import kotlin.collections.iterator
import android.os.Handler
import android.os.Looper

class GameScene(override var game: GameEngine, val context: Context) : Scene {
    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val layout = GameHudLayout(screenX, screenY)
    private val ScRel = ScreenRel(context)
    private val hudRender = GameHudRender(ScRel)
    private val hitboxDrawable = R.drawable.transparent_hexagon

    var pole = Pole(context = context)

    val button_point_back = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val button_next_hod = ButtonImage(0, 0, 1, 1, hitboxDrawable)

    var mini_fon_for_down_game = ButtonImage(
        0, layout.bottomBarY,
        screenX, layout.bottomCollapsedH, hitboxDrawable
    )

    val exit_win = ButtonImage(0, 0, 1, 1, hitboxDrawable)

    val check_mark = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val cross = ButtonImage(0, 0, 1, 1, hitboxDrawable)

    val button_return_in_game = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val button_setting = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val button_is_look_rupes = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val button_return_in_menu = ButtonImage(0, 0, 1, 1, hitboxDrawable)

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

    val buildSettings = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val unitSettings = ButtonImage(0, 0, 1, 1, hitboxDrawable)

    val buttonDownload = ButtonImage(0, 0, 1, 1, hitboxDrawable)
    val buttonSave = ButtonImage(0, 0, 1, 1, hitboxDrawable)

    var unitButtonsMap = HashMap<Terrain, ButtonImage>()
    var buildButtonMap = HashMap<Terrain, ButtonImage>()

    var isSaved: Boolean? by mutableStateOf(null)
    var isDownload: Boolean? by mutableStateOf(null)

    private val soundPlayer = SoundPlayer(context)
    var isSoundVictory = true

    init {
        createPLayers()
        initImage()
        initEconomic2()
        applyHudHitboxes()
        syncBottomPanel(false)
    }

    fun applyHudHitboxes() {
        button_point_back.x = layout.hamburgerX
        button_point_back.y = layout.hamburgerY
        button_point_back.dx = layout.hamburgerSize
        button_point_back.dy = layout.hamburgerSize

        buildSettings.x = layout.tabX0
        buildSettings.y = layout.tabY
        buildSettings.dx = layout.tabW
        buildSettings.dy = layout.tabH

        unitSettings.x = layout.tabX1
        unitSettings.y = layout.tabY
        unitSettings.dx = layout.tabW
        unitSettings.dy = layout.tabH

        button_next_hod.x = layout.endTurnX
        button_next_hod.y = layout.tabY
        button_next_hod.dx = layout.endTurnW
        button_next_hod.dy = layout.endTurnH

        val modalBtnW = (screenX * 0.22).toInt()
        val modalBtnH = (screenY * 0.055).toInt()
        val modalBtnY = layout.endTurnModalY() + layout.endTurnModalH() - modalBtnH - (screenY * 0.04).toInt()
        val modalInnerPad = (layout.endTurnModalW() * 0.12).toInt()
        cross.x = layout.endTurnModalX() + modalInnerPad
        cross.y = modalBtnY
        cross.dx = modalBtnW
        cross.dy = modalBtnH
        check_mark.x = layout.endTurnModalX() + layout.endTurnModalW() - modalInnerPad - modalBtnW
        check_mark.y = modalBtnY
        check_mark.dx = modalBtnW
        check_mark.dy = modalBtnH

        val pad = (layout.pauseCardW * 0.08).toInt()
        val saveW = (layout.pauseCardW * 0.38).toInt()
        val saveH = (layout.screenY * 0.07).toInt()
        buttonSave.x = layout.pauseCardX + pad
        buttonSave.y = layout.pauseCardY + (layout.pauseCardH * 0.22).toInt()
        buttonSave.dx = saveW
        buttonSave.dy = saveH
        buttonDownload.x = layout.pauseCardX + layout.pauseCardW - pad - saveW
        buttonDownload.y = buttonSave.y
        buttonDownload.dx = saveW
        buttonDownload.dy = saveH

        val colX = layout.pauseCardX + pad
        val colW = layout.pauseCardW - pad * 2
        val colH = (screenY * 0.068).toInt()
        var colY = buttonSave.y + saveH + (screenY * 0.055).toInt()
        button_return_in_game.x = colX
        button_return_in_game.y = colY
        button_return_in_game.dx = colW
        button_return_in_game.dy = colH
        colY += colH + (screenY * 0.01).toInt()
        button_setting.x = colX
        button_setting.y = colY
        button_setting.dx = colW
        button_setting.dy = colH
        colY += colH + (screenY * 0.01).toInt()
        button_is_look_rupes.x = colX
        button_is_look_rupes.y = colY
        button_is_look_rupes.dx = colW
        button_is_look_rupes.dy = colH
        colY += colH + (screenY * 0.01).toInt()
        button_return_in_menu.x = colX
        button_return_in_menu.y = colY
        button_return_in_menu.dx = colW
        button_return_in_menu.dy = colH

        exit_win.x = layout.winCardX + (layout.winCardW * 0.12).toInt()
        exit_win.y = layout.winCardY + layout.winCardH - (screenY * 0.12).toInt()
        exit_win.dx = (layout.winCardW * 0.76).toInt()
        exit_win.dy = (screenY * 0.08).toInt()

        initButtonUnit()
        initButtonBuild()
    }

    fun syncBottomPanel(shopOpen: Boolean) {
        if (shopOpen) {
            mini_fon_for_down_game.y = layout.expandedBottomY()
            mini_fon_for_down_game.dy = layout.expandedBottomH()
            pole.ogranichenie.max.y = layout.expandedBottomY()
        } else {
            mini_fon_for_down_game.y = layout.bottomBarY
            mini_fon_for_down_game.dy = layout.bottomCollapsedH
            pole.ogranichenie.max.y =
                pole.ogranichenie.min.y + (pole.mass[0].size) * pole.minDx + pole.minDx / 2
        }
    }

    fun initButtonUnit() {
        unitButtonsMap.clear()
        val unitOrder = listOf(Terrain.UNIT1, Terrain.UNIT2, Terrain.UNIT3, Terrain.UNIT4)
        val w = layout.unitCardW()
        val h = layout.unitCardH()
        val y = layout.shopCardsY()
        unitOrder.forEachIndexed { index, terrain ->
            unitButtonsMap[terrain] = ButtonImage(
                layout.unitCardX(index),
                y,
                w,
                h,
                hitboxDrawable,
                name2 = hitboxDrawable,
                type = mutableStateOf(false)
            )
        }
    }

    fun initButtonBuild() {
        buildButtonMap.clear()
        val buildOrder = listOf(Terrain.FARM, Terrain.TOWER, Terrain.HARD_TOWER)
        val w = layout.buildCardW()
        val h = layout.unitCardH()
        val y = layout.shopCardsY()
        buildOrder.forEachIndexed { index, terrain ->
            buildButtonMap[terrain] = ButtonImage(
                layout.buildCardX(index),
                y,
                w,
                h,
                hitboxDrawable,
                name2 = hitboxDrawable,
                type = mutableStateOf(false)
            )
        }
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

        val starts = randomIslandStarts()
        val origins = listOf(starts.first, starts.second)
        for (player in listPlayers.indices) {
            val cell = origins.getOrNull(player) ?: continue
            pole.mass[cell.x][cell.y].player = listPlayers[player]
        }
        for (player in listPlayers.indices) {
            val cell = origins.getOrNull(player) ?: continue
            val count = 2 + kotlin.random.Random.nextInt(2)
            claimStartCluster(listPlayers[player], cell, count)
            listPlayers[player].units.add(
                Unit(
                    koorOnInt(cell.x, cell.y),
                    Terrain.UNIT1,
                    pole.dx,
                    2
                )
            )

            //pole.mass[x][y].player=player
            //cellPlayerInit(x,y,player)

            /*
            pole.mass[1][1].player=player
            cellPlayerInit(1,1,pole.mass[1][1].player!!)
            */
        }
    }

    private fun randomIslandStarts(): Pair<koorOnInt?, koorOnInt?> {
        val land = mutableListOf<koorOnInt>()
        for (i in pole.mass.indices) {
            for (j in pole.mass[i].indices) {
                if (pole.mass[i][j].land != Terrain.WATER) {
                    land.add(koorOnInt(i, j))
                }
            }
        }
        if (land.isEmpty()) return null to null
        if (land.size == 1) return land[0] to null
        val random = kotlin.random.Random
        var minDist = 4f
        while (minDist >= 0f) {
            repeat(40) {
                val first = land[random.nextInt(land.size)]
                val second = land[random.nextInt(land.size)]
                if (first.x == second.x && first.y == second.y) return@repeat
                if (startDistance(first, second) >= minDist) return first to second
            }
            minDist -= 1f
        }
        return land[0] to land[1]
    }

    private fun startDistance(a: koorOnInt, b: koorOnInt): Float {
        val dx = (a.x - b.x) * 0.74f
        val dy = (a.y - b.y).toFloat()
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }

    private fun claimStartCluster(player: Player, origin: koorOnInt, count: Int) {
        val claimed = mutableListOf(origin)
        pole.mass[origin.x][origin.y].player = player
        val random = kotlin.random.Random
        while (claimed.size < count) {
            val frontier = claimed
                .flatMap { cells_around(it.x, it.y) }
                .distinctBy { it.x to it.y }
                .filter { cell ->
                    claimed.none { it.x == cell.x && it.y == cell.y } &&
                        pole.mass[cell.x][cell.y].player == null
                }
            if (frontier.isEmpty()) break
            val next = frontier[random.nextInt(frontier.size)]
            pole.mass[next.x][next.y].player = player
            claimed.add(next)
        }
    }

    fun cellPlayerInit(X: Int, Y: Int, player: Player) {
        val cells = cells_around(X, Y)
        //println(cells.toString())
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
                pole = poleData,
                biomeSeed = pole.biomeSeed
            )

            val gameStorage = GameStorage(context)
            val name_json = "game_map1.json"
            gameStorage.saveToJsonGameMap(name_json, gameMap)

            // путь к файлу
            val filePath = File(context.filesDir, name_json).absolutePath
            //println("Карта сохранена в: $filePath")
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
            if (gameMap.biomeSeed != 0L) {
                pole.applyBiomeSeed(gameMap.biomeSeed)
            }

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
                    pole.mass[i][j].land = poleData[i][j].land
                    if (poleData[i][j].player != null) {
                        pole.mass[i][j].player = listPlayers[poleData[i][j].player!!]
                        pole.mass[i][j].protection = poleData[i][j].protection
                    } else {
                        pole.mass[i][j].player = null
                        pole.mass[i][j].protection = 0
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

    override suspend fun onTouchEvent(event: MotionEvent) {
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
                            soundPlayer.play(R.raw.button)
                            update_price_farm()

                            delivereBuild = !delivereBuild
                            if (delivereBuild) {
                                selectAddBulid = Terrain.NONE
                            }
                            delivereUnit = false
                        }

                        // Обработка кнопки units
                        if (!delivereNextHod && unitSettings.click(mx, my)) {
                            soundPlayer.play(R.raw.button)
                            delivereUnit = !delivereUnit
                            if (delivereUnit) {
                                selectAddUnit = Terrain.NONE
                            }
                            delivereBuild = false
                        }

                        if (delivereNextHod) {
                            if (check_mark.click(mx, my)) {
                                soundPlayer.play(R.raw.hod)
                                nextHodPlayer()
                            }
                            if (cross.click(mx, my)) {
                                soundPlayer.play(R.raw.button)
                                delivereNextHod = false
                                syncBottomPanel(delivereBuild || delivereUnit)
                            }
                        }

                        // Обработка выбора конкретного юнита/здания
                        if (delivereUnit) {
                            for (i in unitButtonsMap) {
                                if (i.value.click(mx, my)) {
                                    selectAddUnit = i.key
                                    //println(selectAddUnit)
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
                                syncBottomPanel(false)
                                forceRender()
                            }

                        } else {
                            if (exit_win.click(mx, my)) {
                                soundPlayer.play(R.raw.button)
                                pole = Pole(context = context)
                                createPLayers()
                                game.updateScene("Menu")
                            }
                        }

                        syncBottomPanel(delivereBuild || delivereUnit)
                    }
                    else {
                        // Меню паузы

                        if (buttonSave.click(mx,my)){
                            soundPlayer.play(R.raw.button)
                            save_update_json()
                        }
                        if (buttonDownload.click(mx,my)){
                            soundPlayer.play(R.raw.button)
                            load_json()
                        }

                        if (button_return_in_game.click(mx,my)){
                            soundPlayer.play(R.raw.button)
                            isPauseMenu = false
                            isSaved=null
                            isDownload=null
                            return
                        }
                        if (button_setting.click(mx,my)){
                            soundPlayer.play(R.raw.button)
                            game.updateScene("Setting")
                            return
                        }
                        if (button_is_look_rupes.click(mx,my)){
                            soundPlayer.play(R.raw.button)
                            game.updateScene("Rules")
                        }

                        if (button_return_in_menu.click(mx,my)){
                            soundPlayer.play(R.raw.button)
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
    fun victoryRender(winnerPlayer: Int) {
        if (isSoundVictory) {
            soundPlayer.play(R.raw.victory, loop = false, volume = 1.0f)
            isSoundVictory = false
        }

        val imageBitmaps = remember {
            pole.image_mass.map { resourceId ->
                BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
            }
        }
        val hexSize = (layout.winCardW * 0.22).toInt()

        hudRender.VictoryOverlay(
            layout = layout,
            winnerName = listPlayers[winnerPlayer].name,
            playerColorBitmap = imageBitmaps[listPlayers[winnerPlayer].color],
            hexSize = hexSize,
            exitX = exit_win.x,
            exitY = exit_win.y,
            exitW = exit_win.dx,
            exitH = exit_win.dy
        )
    }

    @Composable
    override fun render() {
        val trigger = renderTrigger
        val listPLayersHadCells = getListPlayersHadCells()
        val isWin = listPLayersHadCells.size == 1

        val transparentHexagonBitmap =
            ImageBitmap.imageResource(id = R.drawable.transparent_hexagon)

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.ocean),
                contentDescription = "фон",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            pole.Render()

            if (!isWin && (delivereUnit || delivereBuild) && !delivereNextHod) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    RenderConstructionCells(transparentHexagonBitmap)
                }
            }

            for (i in listPlayers.indices) {
                listPlayers[i].Render(i == hod_player)
            }

            if (!isWin) {
                val shopOpen = (delivereUnit || delivereBuild) && !delivereNextHod
                val player = listPlayers[hod_player]
                val netIncome = player.income - player.sale

                hudRender.TopBar(layout)
                hudRender.TopBarContent(
                    layout,
                    player.kazna,
                    player.name,
                    netIncome
                )
                hudRender.HamburgerButton(
                    layout.hamburgerX,
                    layout.hamburgerY,
                    layout.hamburgerSize
                )
                hudRender.BottomParchmentPanel(layout, shopOpen)

                val selectedIdx = player.selectedUnit
                if (
                    !shopOpen &&
                    !delivereNextHod &&
                    selectedIdx != null &&
                    selectedIdx in player.units.indices
                ) {
                    val unit = player.units[selectedIdx]
                    val unitEco = economic.getValue(unit.type)
                    hudRender.SelectedUnitChip(
                        layout = layout,
                        unitTitle = GameHudRender.unitTitle(unit.type),
                        unitIconRes = GameHudRender.unitIcon(unit.type),
                        movement = unit.Movement,
                        protection = unitEco.protection
                    )
                }

                if (delivereUnit && !delivereNextHod) {
                    hudRender.ShopUnitHeader(layout)
                    for ((terrain, btn) in unitButtonsMap) {
                        val eco = economic.getValue(terrain)
                        hudRender.UnitShopCard(
                            btn.x,
                            btn.y,
                            btn.dx,
                            btn.dy,
                            GameHudRender.unitTitle(terrain),
                            GameHudRender.unitIcon(terrain),
                            eco,
                            btn.type.value
                        )
                    }
                }

                if (delivereBuild && !delivereNextHod) {
                    hudRender.ShopUnitHeader(layout)
                    for ((terrain, btn) in buildButtonMap) {
                        val eco = economic.getValue(terrain)
                        val farmLine = if (terrain == Terrain.FARM) "+20 за каждую" else null
                        hudRender.BuildShopCard(
                            btn.x,
                            btn.y,
                            btn.dx,
                            btn.dy,
                            GameHudRender.buildTitle(terrain),
                            GameHudRender.buildIcon(terrain),
                            eco,
                            farmLine,
                            btn.type.value
                        )
                    }
                }

                hudRender.TabButton(
                    buildSettings.x,
                    buildSettings.y,
                    buildSettings.dx,
                    buildSettings.dy,
                    "Здания",
                    R.drawable.rules_build_farm,
                    delivereBuild
                )
                hudRender.TabButton(
                    unitSettings.x,
                    unitSettings.y,
                    unitSettings.dx,
                    unitSettings.dy,
                    "Юниты",
                    R.drawable.rules_unit_skeleton,
                    delivereUnit
                )
                hudRender.EndTurnButton(
                    button_next_hod.x,
                    button_next_hod.y,
                    button_next_hod.dx,
                    button_next_hod.dy
                )

                if (delivereNextHod && !isPauseMenu) {
                    hudRender.EndTurnModal(
                        layout,
                        check_mark.x,
                        check_mark.y,
                        check_mark.dx,
                        check_mark.dy,
                        cross.x,
                        cross.y,
                        cross.dx,
                        cross.dy
                    )
                }
            }

            if (isWin) {
                victoryRender(listPLayersHadCells[0])
            }

            if (isPauseMenu && !isWin) {
                val status = when {
                    isSaved == true -> "✓ Игра успешно сохранена"
                    isSaved == false -> "не получилось сохранить игру"
                    isDownload == true -> "✓ Игра успешно загружена"
                    isDownload == false -> "не получилось загрузить игру"
                    else -> null
                }
                hudRender.PauseMenu(
                    layout = layout,
                    saveX = buttonSave.x,
                    saveY = buttonSave.y,
                    saveW = buttonSave.dx,
                    saveH = buttonSave.dy,
                    loadX = buttonDownload.x,
                    loadY = buttonDownload.y,
                    loadW = buttonDownload.dx,
                    loadH = buttonDownload.dy,
                    menuButtons = listOf(
                        "Вернуться в игру" to button_return_in_game,
                        "Настройки" to button_setting,
                        "Правила" to button_is_look_rupes,
                        "В меню" to button_return_in_menu
                    ),
                    statusMessage = status
                )
            }
        }
    }

    fun DrawScope.RenderConstructionCells(possibleBitmap: ImageBitmap){
        for (i in pole.mass.indices){
            for (j in pole.mass[i].indices){
                if (pole.mass[i][j].land == Terrain.WATER) continue
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

        syncBottomPanel(false)

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

        // Сбрасываем флаг победы при входе в сцену
        isSoundVictory = true

        // Сбрасываем состояния выбора
        delivereBuild = false
        delivereUnit = false
        selectAddUnit = Terrain.NONE
        selectAddBulid = Terrain.NONE

        // Сбрасываем настройки игроков и поля
        pole = Pole(context = context)
        createPLayers()
        applyHudHitboxes()

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
        soundPlayer.release()
    }
}