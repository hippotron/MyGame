package com.example.mygame

import android.content.Context
import android.graphics.BitmapFactory
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

import kotlin.random.Random
import com.example.mygame.GlobalParam.TextRender
import com.example.mygame.ui.theme.Economic

class GameScene(override var game: GameEngine, val context: Context) : Scene {


    val contexts = context
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels

    var pole = Pole(context = contexts)

    val button_point_back = ButtonImage((screenXpx*0.9).toInt(),(screenYpx*0.1).toInt(),
        (screenXpx*0.05).toInt(),(screenYpx*0.1).toInt(),R.drawable.point)
    val button_next_hod = ButtonImage((screenXpx*0.8).toInt(),(screenYpx*0.9).toInt(),
        (screenXpx*0.14).toInt(),(screenYpx*0.07).toInt(),R.drawable.arrow_next_hod)

    val mini_fon = ButtonImage((screenXpx*0.1).toInt(),(screenYpx*0.1).toInt(),
        (screenXpx*0.8).toInt(),(screenYpx*0.8).toInt(),R.drawable.mini_fon)

    val exit_win = ButtonImage((screenXpx*0.65).toInt(),(screenYpx*0.77).toInt(),
        (screenXpx*0.2).toInt(),(screenYpx*0.1).toInt(),R.drawable.image_return_mirror)

    //var touchRender by mutableStateOf(0)

    var hod_player by mutableStateOf(0)

    // Используем lateinit var вместо val, чтобы пересоздавать Player при смене Pole
    lateinit var player1: Player
    lateinit var player2: Player
    lateinit var listPlayers: ArrayList<Player>
    var economic = HashMap<Terrain, Economic>()

    var image_mass= HashMap<Terrain,ImageBitmap>()

    init {
        createPLayers()
        initImage()
        initEconomic()
    }

    fun initImage(){

        //image_mass[Terrain.LAND]=-1
        image_mass[Terrain.FARM]=BitmapFactory.decodeResource(context.resources, R.drawable.farm).asImageBitmap()
        image_mass[Terrain.TOWER]=BitmapFactory.decodeResource(context.resources, R.drawable.tower).asImageBitmap()
        image_mass[Terrain.HARD_TOWER]=BitmapFactory.decodeResource(context.resources, R.drawable.tower_hard).asImageBitmap()

        image_mass[Terrain.UNIT1]=BitmapFactory.decodeResource(context.resources, R.drawable.skeleton).asImageBitmap()
        image_mass[Terrain.UNIT2]=BitmapFactory.decodeResource(context.resources, R.drawable.barbarian).asImageBitmap()
        image_mass[Terrain.UNIT3]=BitmapFactory.decodeResource(context.resources, R.drawable.knight).asImageBitmap()
        image_mass[Terrain.UNIT4]=BitmapFactory.decodeResource(context.resources, R.drawable.hard_khight).asImageBitmap()

    }


    fun initEconomic(){
        val farm = Economic(price = 120, protection = 1, income = 70, sale = 0, attack = 0)
        economic[Terrain.FARM]=farm

        val tower = Economic(price = 150, protection = 3, income = 0, sale = 75, attack = 0)
        economic[Terrain.TOWER]=tower

        val hard_tower = Economic(price = 300, protection = 4, income = 0, sale = 150, attack = 0)
        economic[Terrain.HARD_TOWER]=hard_tower

        val unit1 = Economic(price = 100, protection = 1, income = 0, sale = 50, attack = 1)
        val unit2 = Economic(price = 200, protection = 2, income = 0, sale = 100, attack = 2)
        val unit3 = Economic(price = 300, protection = 3, income = 0, sale = 150, attack = 3)
        val unit4 = Economic(price = 400, protection = 4, income = 0, sale = 200, attack = 4)

        economic[Terrain.UNIT1] = unit1
        economic[Terrain.UNIT2] = unit2
        economic[Terrain.UNIT3] = unit3
        economic[Terrain.UNIT4] = unit4

        val cell = Economic(price = 0, protection = 0, income = 10, sale = 0, attack = 0)
        economic[Terrain.LAND]=cell
        var kk=economic[Terrain.FARM]
        println(kk?.price)
    }

    fun createPLayers(){
        addPlayers()
        for (player in listPlayers){
            val (x,y)=randomXY()
            println("x=$x y=$y")
            pole.mass[x][y].player=player
            cellPlayerInit(x,y,player)
            /*
            pole.mass[1][1].player=player
            cellPlayerInit(1,1,pole.mass[1][1].player!!)
            */
        }
    }

    fun cellPlayerInit(X: Int, Y: Int, player: Player) {
        val cells = cells_around(X, Y)

        for (i in cells){
            pole.mass[i.x][i.y].player=player
        }
    }

    fun randomXY(): Pair<Int, Int> {
        var attempts = 0
        val maxAttempts: Int = 100
        var a =0
        var b=0
        while (attempts < maxAttempts) {
            a = Random.nextInt(1, pole.mass.size-1)
            b = Random.nextInt(1, pole.mass[0].size-1)

            if (randomCells(a, b)==true) {
                println("ok")
                break
            }
            println("attempts=$attempts, x=$a, y=$b")
            attempts++
        }
        return Pair(a, b)
    }

    fun randomCells(X: Int, Y: Int): Boolean{
        val cells = cells_around(X, Y)
        for (i in cells){
            if (pole.mass[i.x][i.y].player!=null ){
                return false
            }
        }
        return true
    }

    fun cells_around(X: Int, Y: Int): MutableList<koorOnInt> {
        val mass = arrayListOf<koorOnInt>()
        val cells = mutableListOf<koorOnInt>()

        mass.add(koorOnInt(X-1, Y))
        mass.add(koorOnInt(X, Y-1))
        mass.add(koorOnInt(X+1, Y))
        mass.add(koorOnInt(X, Y+1))
        if (X % 2 == 0) {
            mass.add(koorOnInt(X-1, Y-1))
            mass.add(koorOnInt(X+1, Y-1))
        } else{
            mass.add(koorOnInt(X-1, Y+1))
            mass.add(koorOnInt(X+1, Y+1))
        }

        for (i in mass){
            if (isValidMove(i)){
                cells.add(i)
            }
        }

        return cells
    }

    // Добавляем State для контроля перерисовки
    private var renderTrigger by mutableStateOf(0)

    fun forceRender() {
        renderTrigger++
    }

    private fun addPlayers() {
        player1 = Player("1", pole, 1,economic,image_mass)
        player2 = Player("2", pole, 3,economic,image_mass)
        listPlayers = arrayListOf(player1, player2)
    }

    fun isValidMove(koorOnInt: koorOnInt): Boolean {
        // Проверяем границы массива
        if (koorOnInt.x < 0 || koorOnInt.x >= pole.mass.size || koorOnInt.y < 0 || koorOnInt.y >= pole.mass[0].size) {
            return false
        }
        // Проверяем, что не вода
        if (pole.mass[koorOnInt.x][koorOnInt.y].player?.color == Terrain.WATER.value) {
            return false
        }

        return true
    }

    override fun update() {

    }

    override fun onTouchEvent(event: MotionEvent) {

        val listPLayersHadCells = getListPlayersHadCells()

        pole.onTouch(event)
        if (listPLayersHadCells.size!=1) {
            listPlayers[hod_player].onTouch(event)
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
            }

            MotionEvent.ACTION_UP -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (event.pointerCount == 1) {
                    if (listPLayersHadCells.size != 1) {
                        if (button_point_back.click(mx, my) == true) {
                            // Пересоздаем Pole
                            pole = Pole(context = contexts)
                            // Пересоздаем Player с новым Pole
                            createPLayers()
                            game.CurrentScene = "Menu"
                        }
                        if (button_next_hod.click(mx, my)) {
                            nextHodPlayer()

                            forceRender() // Добавляем вызов

                        }
                    } else if (exit_win.click(mx,my)){
                        // Пересоздаем Pole
                        pole = Pole(context = contexts)
                        // Пересоздаем Player с новым Pole
                        createPLayers()
                        game.CurrentScene = "Menu"
                    }
                }
            }
        }
        //touchRender++
        // Принудительное обновление сцены
        game.forceUpdate++
    }

    @Composable
    override fun render() {

        //val updateRender = touchRender

        // Добавляем renderTrigger в remember, чтобы Compose отслеживал изменения
        val currentRenderTrigger = remember { renderTrigger }

        val listPLayersHadCells = getListPlayersHadCells()
        val textMeasurer = rememberTextMeasurer()

        if (listPLayersHadCells.size==1){
            Image(
                painter = painterResource(id = R.drawable.background_win),// Укажите ваш файл
                contentDescription = "фон", // Описание для доступности (обязательно!)
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            mini_fon.Render()
            exit_win.Render()
            val winnerPlayer = listPLayersHadCells[0]

            val imageBitmaps = remember {
                pole.image_mass.map { resourceId ->
                    BitmapFactory.decodeResource(context.resources, resourceId).asImageBitmap()
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TextRender(textMeasurer,
                    "Конец игры",
                    pole.display.x*0.23,
                    pole.display.y*0.18,
                    40,
                    true
                )
                TextRender(textMeasurer,
                    "Победитель",
                    pole.display.x*0.26,
                    pole.display.y*0.25,
                    35
                )
                TextRender(
                    textMeasurer,
                    listPlayers[winnerPlayer].name,
                    pole.display.x*0.5,
                    pole.display.y*0.31,
                    30,
                )

                drawImage(
                    image = imageBitmaps[listPlayers[winnerPlayer].color],
                    dstOffset = IntOffset((pole.display.x*0.7).toInt(), (pole.display.y*0.31).toInt()),
                    dstSize = IntSize(pole.dx, pole.dx)
                )

            }
        }
        else {
            Image(
                painter = painterResource(id = R.drawable.background_menu),// Укажите ваш файл
                contentDescription = "фон", // Описание для доступности (обязательно!)
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            pole.Render()
            button_point_back.Render()
            button_next_hod.Render()
            for (i in listPlayers) {
                i.Render()
            }



            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                TextRender(
                    textMeasurer,
                    listPlayers[hod_player].name,
                    screenXpx*0.5,
                    screenYpx*0.05,
                    25,
                    true
                )
            }

        }

    }

    /*fun DrawScope.economikaRender(textMeasurer: TextMeasurer){
        this.TextRender(
            textMeasurer,
        )
    }*/

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

    fun nextHodPlayer(){
        for (i in listPlayers[hod_player].units){
            i.canMove = true
        }
        listPlayers[hod_player].getIncome()

        hod_player++
        if (hod_player>=listPlayers.size) {
            hod_player=0
        }
        //touchRender++

    }

    override fun onEnter() {

    }

    override fun onExit() {

    }
}
