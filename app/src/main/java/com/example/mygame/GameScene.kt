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
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

import kotlin.random.Random
import com.example.mygame.GlobalParam.TextRender

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

    var hod_player=0

    // Используем lateinit var вместо val, чтобы пересоздавать Player при смене Pole
    lateinit var player1: Player
    lateinit var player2: Player
    lateinit var listPlayers: ArrayList<Player>

    init {
        createPLayers()
    }

    fun createPLayers(){
        addPlayers()
        for (player in listPlayers){
            val (x,y)=randomXY()

            pole.mass[x][y].player=player
            cellPlayerInit(x,y,pole.mass[x][y].player!!)
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
                break
            }

            attempts++
        }
        return Pair(a, b)
    }

    fun randomCells(X: Int, Y: Int): Boolean{
        val cells = cells_around(X, Y)
        for (i in cells){
            if (pole.mass[i.x][i.y].player?.color != 0){
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

    private fun addPlayers() {
        player1 = Player("1", 100,40, pole, 1)
        player2 = Player("2",100 , 40, pole, 3)
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

    @Composable
    override fun render() {

        val listPLayersHadCells = getListPlayersHadCells()

        if (listPLayersHadCells.size==1){
            Image(
                painter = painterResource(id = R.drawable.background_win),// Укажите ваш файл
                contentDescription = "фон", // Описание для доступности (обязательно!)
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            mini_fon.Render()
            exit_win.Render()
            val textMeasurer = rememberTextMeasurer()
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
        }

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
                    if (listPLayersHadCells.size!=1) {
                        if (button_point_back.click(mx, my) == true) {
                            // Пересоздаем Pole
                            pole = Pole(context = contexts)
                            // Пересоздаем Player с новым Pole
                            createPLayers()
                            game.CurrentScene = "Menu"
                        }
                        if (button_next_hod.click(mx, my)) {
                            nextHodPlayer()
                        }
                    } else if (exit_win.click(mx,my)){
                        // Пересоздаем Pole
                        pole = Pole(context = contexts)
                        // Пересоздаем Player с новым Pole
                        createPLayers()
                        game.CurrentScene = "Menu"
                    }
                }

                // Принудительное обновление сцены
                game.forceUpdate++
            }
        }
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

    fun playersHadCells(): List<Player> {
        // Список для хранения игроков, у которых еще есть клетки
        val playersWithCells = ArrayList<Player>()

        // Проходим по всем клеткам поля
        for (i in pole.mass.indices) {
            for (j in pole.mass[i].indices) {
                val cell = pole.mass[i][j]
                val player = cell.player

                if (player != null) {
                    // Проверяем, нет ли уже этого игрока в списке
                    var alreadyInList = false
                    for (p in playersWithCells) {
                        if (p == player) {
                            alreadyInList = true
                            break
                        }
                    }

                    // Если игрока еще нет в списке, добавляем
                    if (!alreadyInList) {
                        playersWithCells.add(player)
                    }
                }
            }
        }
        return playersWithCells.toList()
    }

    fun nextHodPlayer(){
        for (i in listPlayers[hod_player].units){
            i.canMove = true
        }

        hod_player++
        if (hod_player>=listPlayers.size) {
            hod_player=0
        }
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }
}
