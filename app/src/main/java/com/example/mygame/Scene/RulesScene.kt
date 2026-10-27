package com.example.mygame.Scene

import android.content.Context
import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mygame.ButtonImage
import com.example.mygame.Economic
import com.example.mygame.PackageForKoor.ScreenRel
import com.example.mygame.PictureRender
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer
import com.example.mygame.Terrain

class RulesScene(
    override var game: GameEngine,
    val context: Context,
    val economic: HashMap<Terrain, Economic>
) : Scene {

    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_return = ButtonImage(
        (screenX * 0.18).toInt(), (screenY * 0.012).toInt(),
        (screenX * 0.64).toInt(), (screenY * 0.072).toInt(), R.drawable.rules_btn_back
    )

    val button_arrow_right = ButtonImage(
        (screenX * 0.735).toInt(), (screenY * 0.888).toInt(),
        (screenX * 0.12).toInt(), (screenY * 0.058).toInt(), R.drawable.rules_arrow_right
    )

    val button_arrow_left = ButtonImage(
        (screenX * 0.145).toInt(), (screenY * 0.888).toInt(),
        (screenX * 0.12).toInt(), (screenY * 0.058).toInt(), R.drawable.rules_arrow_left
    )

    var slide by mutableStateOf(1)

    private val soundPlayer = SoundPlayer(context)

    private val PicRender = PictureRender()
    private val ScRel = ScreenRel(context)

    override fun update() {}

    override suspend fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (slide >= 2) {
                    if (button_arrow_left.click(mx, my)) {
                        soundPlayer.play(R.raw.button)
                        slide--
                    }
                }
                if (slide <= 3) {
                    if (button_arrow_right.click(mx, my)) {
                        soundPlayer.play(R.raw.button)
                        slide++
                    }
                }
                if (button_return.click(mx, my)) {
                    soundPlayer.play(R.raw.button)
                    game.goBackScene()
                }
                game.forceUpdate++
            }
        }
    }

    @Composable
    override fun render() {
        Box(modifier = Modifier.fillMaxSize()) {
            RenderHeader(slide)

            if (slide >= 2) button_arrow_left.Render()
            if (slide <= 3) button_arrow_right.Render()
            button_return.Render()

            if (slide == 1) {
                RenderStart()
            }
            if (slide == 2) {
                RenderUnits()
            }
            if (slide == 3) {
                RenderBuildings()
            }
            if (slide == 4) {
                RenderBattle()
            }
        }
    }

    @Composable
    fun RenderHeader(slide: Int) {
        RenderFull(R.drawable.background_menu)
        RenderFull(R.drawable.rules_parchment)
        PicRender.RenderImage(R.drawable.rules_fleur, ScRel.x(45f), ScRel.y(11.91f), ScRel.x(10f))
        PicRender.RenderText("Правила", ScRel.x(0f), ScRel.y(15.70f), ScRel.font(4.77f), color = Color(0xFF3B2412), center = true)
        val title = when (slide) {
            1 -> "Начало игры"
            2 -> "Юниты"
            3 -> "Здания"
            else -> "Бой и ход"
        }
        PicRender.RenderText(title, ScRel.x(0f), ScRel.y(21.12f), ScRel.font(2.82f), color = Color(0xFF6B4A2A), center = true)
    }

    @Composable
    fun RenderUnits(){
        RenderSkeleton(
            ScRel.x(10.94f),
            ScRel.y(25.99f),
            ScRel.x(40.10f),
            ScRel.y(27.07f)
        )
        RenderBarbarian(
            ScRel.x(52.26f),
            ScRel.y(25.99f),
            ScRel.x(40.10f),
            ScRel.y(27.07f)
        )
        RenderKnight(
            ScRel.x(10.94f),
            ScRel.y(53.39f),
            ScRel.x(40.10f),
            ScRel.y(27.07f)
        )
        RenderPaladin(
            ScRel.x(52.26f),
            ScRel.y(53.39f),
            ScRel.x(40.10f),
            ScRel.y(27.07f)
        )
        RenderTextPaladin()
    }

    @Composable
    fun RenderStart() {
        RenderFactCard(ScRel.x(10.94f), ScRel.y(25.99f), R.drawable.rules_icon_coin_crown, "80 монет")
        RenderFactCard(ScRel.x(37.67f), ScRel.y(25.99f), R.drawable.rules_icon_hexes, "7 клеток")
        RenderFactCard(ScRel.x(64.41f), ScRel.y(25.99f), R.drawable.rules_icon_income, "+50 доход")
        RenderIncomeCard()
    }

    @Composable
    private fun RenderFactCard(koorX: Dp, koorY: Dp, icon: Int, label: String) {
        Box(
            modifier = Modifier
                .offset(x = koorX, y = koorY)
                .size(width = ScRel.x(25.52f), height = ScRel.y(11.91f))
                .parchmentCard()
        ) {
            PicRender.RenderImage(icon, ScRel.x(4.38f), ScRel.y(0.87f), ScRel.x(17.99f))
            PicRender.RenderText(label, ScRel.x(0f), ScRel.y(9.21f), ScRel.font(2.17f), color = Color(0xFF3B2412), center = true)
        }
    }

    @Composable
    private fun RenderIncomeCard() {
        Box(
            modifier = Modifier
                .offset(x = ScRel.x(10.94f), y = ScRel.y(39.85f))
                .size(width = ScRel.x(78.99f), height = ScRel.y(16.24f))
                .parchmentCard()
        ) {
            PicRender.RenderImage(R.drawable.rules_icon_leaf_hex, ScRel.x(2.43f), ScRel.y(0.87f), ScRel.x(20.66f))
            PicRender.RenderText("Доход", ScRel.x(25.52f), ScRel.y(0.87f), ScRel.font(2.82f), color = Color(0xFF3B2412))
            PicRender.RenderText("Клетка даёт +10 \nмонет за ход ", ScRel.x(25.52f), ScRel.y(4.12f), ScRel.font(2.27f), bold = false)
            PicRender.RenderText("(Юниты и здания деньги \nне приносят)", ScRel.x(2.43f), ScRel.y(10.29f), ScRel.font(2.27f), bold = false, center = true)
        }
    }

    @Composable
    fun RenderBuildings() {
        RenderBuildCard(ScRel.x(10.94f), "Ферма", R.drawable.rules_build_farm, "200", "Доход", "+30", R.drawable.rules_stat_bag, Color(0xFF2F6B32), "0")
        RenderBuildCard(ScRel.x(37.67f), "Башня", R.drawable.rules_build_tower, "120", "За ход", "-50", R.drawable.rules_stat_boot, Color(0xFF9B2C2C), "2")
        RenderBuildCard(ScRel.x(65.63f), "Усиленная\nбашня", R.drawable.rules_build_hard_tower, "350", "За ход", "-200", R.drawable.rules_stat_boot, Color(0xFF9B2C2C), "3")
        RenderRuleCard(ScRel.y(50.90f), ScRel.y(8.66f), R.drawable.rules_stat_coin, "Цена фермы", "200 + 20 × n\nn — число уже построенных ферм.", ScRel.y(4.33f))
    }

    @Composable
    private fun RenderBuildCard(
        koorX: Dp,
        title: String,
        image: Int,
        price: String,
        turnLabel: String,
        turn: String,
        turnIcon: Int,
        turnColor: Color,
        defense: String
    ) {
        Box(
            modifier = Modifier
                .offset(x = koorX, y = ScRel.y(25.99f))
                .size(width = ScRel.x(25.52f), height = ScRel.y(23.82f))
                .parchmentCard()
        ) {
            PicRender.RenderImage(image, ScRel.x(4.62f), ScRel.y(0.43f), ScRel.x(17.50f))
            PicRender.RenderText(title, ScRel.x(0f), ScRel.y(8.45f), ScRel.font(1.73f), color = Color(0xFF3B2412), center = true)

            PicRender.RenderImage(R.drawable.rules_stat_coin, ScRel.x(0.97f), ScRel.y(13.21f), ScRel.x(4.38f))
            PicRender.RenderText("Цена", ScRel.x(5.83f), ScRel.y(13.21f), ScRel.font(1.52f))
            PicRender.RenderText(price, ScRel.x(17.50f), ScRel.y(13.21f), ScRel.font(1.52f), color = Color(0xFF3B2412))

            PicRender.RenderImage(turnIcon, ScRel.x(0.97f), ScRel.y(16.03f), ScRel.x(4.38f))
            PicRender.RenderText(turnLabel, ScRel.x(5.83f), ScRel.y(16.03f), ScRel.font(1.52f))
            PicRender.RenderText(turn, ScRel.x(16.53f), ScRel.y(16.03f), ScRel.font(1.52f), color = turnColor)

            PicRender.RenderImage(R.drawable.rules_stat_shield, ScRel.x(0.97f), ScRel.y(18.84f), ScRel.x(4.38f))
            PicRender.RenderText("Защита", ScRel.x(5.83f), ScRel.y(18.84f), ScRel.font(1.52f))
            PicRender.RenderText(defense, ScRel.x(19.44f), ScRel.y(18.84f), ScRel.font(1.52f), color = Color(0xFF3B2412))
        }
    }

    @Composable
    fun RenderBattle() {
        RenderRuleCard(
            ScRel.y(25.99f), ScRel.y(18.19f), R.drawable.rules_stat_shield, "Защита территории",
            "Клетки вокруг юнита или башни\nполучают их защиту.", ScRel.y(13.43f)
        ) { RenderProtectHexes() }
        RenderRuleCard(
            ScRel.y(45.05f), ScRel.y(18.19f), R.drawable.rules_stat_boot, "Ход юнита",
            "Сначала юнит ходит на соседнюю клетку,\nзатем с неё — на следующую соседнюю.", ScRel.y(13.43f)
        ) { RenderMoveSteps() }
        RenderRuleCard(ScRel.y(64.11f), ScRel.y(9.75f), R.drawable.rules_icon_star, "Захват", "Ступив на клетку, юнит присваивает её игроку.", ScRel.y(4.55f))
        RenderRuleCard(ScRel.y(74.72f), ScRel.y(9.75f), R.drawable.rules_stat_bag, "Начало хода", "Доход идёт в казну. Юниты снова могут ходить.", ScRel.y(4.55f))
    }

    @Composable
    private fun RenderRuleCard(
        y: Dp,
        height: Dp,
        icon: Int,
        title: String,
        text: String,
        textY: Dp,
        diagram: @Composable () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .offset(x = ScRel.x(10.94f), y = y)
                .size(width = ScRel.x(80.21f), height = height)
                .parchmentCard()
        ) {
            PicRender.RenderImage(icon, ScRel.x(1.94f), ScRel.y(0.87f), ScRel.x(6.81f))
            PicRender.RenderText(title, ScRel.x(10.21f), ScRel.y(0.87f), ScRel.font(2.17f), color = Color(0xFF3B2412))
            diagram()
            PicRender.RenderText(text, ScRel.x(1.94f), textY, ScRel.font(1.73f), bold = false)
        }
    }

    @Composable
    private fun RenderProtectHexes() {
        RenderHex(ScRel.x(38.40f), ScRel.y(3.68f), true)
        RenderHex(ScRel.x(32.08f), ScRel.y(5.20f), true)
        RenderHex(ScRel.x(44.72f), ScRel.y(5.20f), true)
        RenderHex(ScRel.x(38.40f), ScRel.y(6.71f), false)
        RenderHex(ScRel.x(32.08f), ScRel.y(8.23f), true)
        RenderHex(ScRel.x(44.72f), ScRel.y(8.23f), true)
        RenderHex(ScRel.x(38.40f), ScRel.y(9.75f), true)
        PicRender.RenderImage(R.drawable.tower, ScRel.x(39.86f), ScRel.y(7.36f), ScRel.x(4.38f))
    }

    @Composable
    private fun RenderHex(x: Dp, y: Dp, mark: Boolean) {
        PicRender.RenderImage(R.drawable.hex_cream, x, y, ScRel.x(6.81f))
        if (mark) PicRender.RenderText("2", x + ScRel.x(1.94f), y + ScRel.y(0.43f), ScRel.font(1.30f), color = Color(0xFF3B2412))
    }

    @Composable
    private fun RenderMoveSteps() {
        PicRender.RenderImage(R.drawable.hex_cream, ScRel.x(3.89f), ScRel.y(5.63f), ScRel.x(11.67f))
        PicRender.RenderImage(R.drawable.skeleton, ScRel.x(5.35f), ScRel.y(6.28f), ScRel.x(8.75f))
        PicRender.RenderText("1", ScRel.x(17.99f), ScRel.y(5.20f), ScRel.font(1.52f), color = Color(0xFF3B2412))
        PicRender.RenderImage(R.drawable.arrow_right, ScRel.x(17.01f), ScRel.y(7.36f), ScRel.x(5.35f))
        PicRender.RenderImage(R.drawable.hex_cream, ScRel.x(26.25f), ScRel.y(5.63f), ScRel.x(11.67f))
        PicRender.RenderText("2", ScRel.x(40.35f), ScRel.y(5.20f), ScRel.font(1.52f), color = Color(0xFF3B2412))
        PicRender.RenderImage(R.drawable.arrow_right, ScRel.x(39.38f), ScRel.y(7.36f), ScRel.x(5.35f))
        PicRender.RenderImage(R.drawable.hex_cream, ScRel.x(48.61f), ScRel.y(5.63f), ScRel.x(11.67f))
    }

    @Composable
    private fun RenderTextPaladin() {
        Box(
            modifier = Modifier
                .offset(x = ScRel.x(10.94f), y = ScRel.y(81.22f))

                .size(width = ScRel.x(82.64f), height = ScRel.y(5.96f))
                .parchmentCard()
        ){
            PicRender.RenderImage(R.drawable.rules_stat_boot, ScRel.x(1.22f), ScRel.y(0.22f), ScRel.x(10.94f))
            PicRender.RenderText("Паладин может ходить на \n клетку с любой защитой", ScRel.x(12.15f), ScRel.y(0.22f), ScRel.font(2.17f))
        }
    }

    @Composable
    private fun RenderSkeleton(KoorX: Dp, KoorY: Dp, SizeX: Dp, SizeY: Dp) {
        Box(
            modifier = Modifier
                .offset(x = KoorX, y = KoorY)
                .size(width = SizeX, height = SizeY)
                .parchmentCard()

        ) {
            // скелет
            PicRender.RenderImage(R.drawable.rules_unit_skeleton, ScRel.x(5.59f), ScRel.y(0.32f), ScRel.x(28.92f))
            PicRender.RenderText("Скелет", ScRel.x(8.99f), ScRel.y(13.32f), ScRel.font(2.82f), color = Color(0xFF3B2412))   // color 0xFF6B4A2A

            // цена
            PicRender.RenderImage(R.drawable.rules_stat_coin, ScRel.x(1.22f), ScRel.y(16.89f), ScRel.x(7.05f))
            PicRender.RenderText("Цена", ScRel.x(8.99f), ScRel.y(16.89f), ScRel.font(2.17f))   // color 0xFF6B4A2A
            PicRender.RenderText("80", ScRel.x(33.06f), ScRel.y(16.89f), ScRel.font(2.17f))    // color 0xFF3B2412

            // за ход
            PicRender.RenderImage(R.drawable.rules_stat_boot, ScRel.x(1.22f), ScRel.y(20.14f), ScRel.x(7.05f))
            PicRender.RenderText("За ход", ScRel.x(8.99f), ScRel.y(20.14f), ScRel.font(2.17f)) // color 0xFF6B4A2A
            PicRender.RenderText("-20", ScRel.x(31.35f), ScRel.y(20.14f), ScRel.font(2.17f), color = Color(0xFF9B2C2C))   // color 0xFF9B2C2C

            // защита
            PicRender.RenderImage(R.drawable.rules_stat_shield, ScRel.x(1.22f), ScRel.y(23.39f), ScRel.x(7.05f))
            PicRender.RenderText("Защита", ScRel.x(8.99f), ScRel.y(23.39f), ScRel.font(2.17f)) // color 0xFF6B4A2A
            PicRender.RenderText("1", ScRel.x(35.97f), ScRel.y(23.39f), ScRel.font(2.17f))     // color 0xFF3B2412
        }
    }

    @Composable
    private fun RenderBarbarian(KoorX: Dp, KoorY: Dp, SizeX: Dp, SizeY: Dp) {
        Box(
            modifier = Modifier
                .offset(x = KoorX, y = KoorY)
                .size(width = SizeX, height = SizeY)
                .parchmentCard()

        ) {
            // варвар
            PicRender.RenderImage(R.drawable.rules_unit_barbarian, ScRel.x(5.59f), ScRel.y(0.32f), ScRel.x(28.92f))
            PicRender.RenderText("Варвар", ScRel.x(8.99f), ScRel.y(13.32f), ScRel.font(2.82f), color = Color(0xFF3B2412))

            // цена
            PicRender.RenderImage(R.drawable.rules_stat_coin, ScRel.x(1.22f), ScRel.y(16.89f), ScRel.x(7.05f))
            PicRender.RenderText("Цена", ScRel.x(8.99f), ScRel.y(16.89f), ScRel.font(2.17f))
            PicRender.RenderText("220", ScRel.x(31.35f), ScRel.y(16.89f), ScRel.font(2.17f))

            // за ход
            PicRender.RenderImage(R.drawable.rules_stat_boot, ScRel.x(1.22f), ScRel.y(20.14f), ScRel.x(7.05f))
            PicRender.RenderText("За ход", ScRel.x(8.99f), ScRel.y(20.14f), ScRel.font(2.17f))
            PicRender.RenderText("-80", ScRel.x(31.35f), ScRel.y(20.14f), ScRel.font(2.17f), color = Color(0xFF9B2C2C))

            // защита
            PicRender.RenderImage(R.drawable.rules_stat_shield, ScRel.x(1.22f), ScRel.y(23.39f), ScRel.x(7.05f))
            PicRender.RenderText("Защита", ScRel.x(8.99f), ScRel.y(23.39f), ScRel.font(2.17f))
            PicRender.RenderText("2", ScRel.x(35.97f), ScRel.y(23.39f), ScRel.font(2.17f))
        }
    }

    @Composable
    private fun RenderKnight(KoorX: Dp, KoorY: Dp, SizeX: Dp, SizeY: Dp) {
        Box(
            modifier = Modifier
                .offset(x = KoorX, y = KoorY)
                .size(width = SizeX, height = SizeY)
                .parchmentCard()

        ) {
            // рыцарь
            PicRender.RenderImage(R.drawable.rules_unit_knight, ScRel.x(5.59f), ScRel.y(0.32f), ScRel.x(28.92f))
            PicRender.RenderText("Рыцарь", ScRel.x(8.99f), ScRel.y(13.32f), ScRel.font(2.82f), color = Color(0xFF3B2412))

            // цена
            PicRender.RenderImage(R.drawable.rules_stat_coin, ScRel.x(1.22f), ScRel.y(16.89f), ScRel.x(7.05f))
            PicRender.RenderText("Цена", ScRel.x(8.99f), ScRel.y(16.89f), ScRel.font(2.17f))
            PicRender.RenderText("600", ScRel.x(31.35f), ScRel.y(16.89f), ScRel.font(2.17f))

            // за ход
            PicRender.RenderImage(R.drawable.rules_stat_boot, ScRel.x(1.22f), ScRel.y(20.14f), ScRel.x(7.05f))
            PicRender.RenderText("За ход", ScRel.x(8.99f), ScRel.y(20.14f), ScRel.font(2.17f))
            PicRender.RenderText("-300", ScRel.x(28.44f), ScRel.y(20.14f), ScRel.font(2.17f), color = Color(0xFF9B2C2C))

            // защита
            PicRender.RenderImage(R.drawable.rules_stat_shield, ScRel.x(1.22f), ScRel.y(23.39f), ScRel.x(7.05f))
            PicRender.RenderText("Защита", ScRel.x(8.99f), ScRel.y(23.39f), ScRel.font(2.17f))
            PicRender.RenderText("3", ScRel.x(35.97f), ScRel.y(23.39f), ScRel.font(2.17f))
        }
    }

    @Composable
    private fun RenderPaladin(KoorX: Dp, KoorY: Dp, SizeX: Dp, SizeY: Dp) {
        Box(
            modifier = Modifier
                .offset(x = KoorX, y = KoorY)
                .size(width = SizeX, height = SizeY)
                .parchmentCard()

        ) {
            // паладин
            PicRender.RenderImage(R.drawable.rules_unit_paladin, ScRel.x(5.59f), ScRel.y(0.32f), ScRel.x(28.92f))
            PicRender.RenderText("Паладин", ScRel.x(8.99f), ScRel.y(13.32f), ScRel.font(2.82f), color = Color(0xFF3B2412))

            // цена
            PicRender.RenderImage(R.drawable.rules_stat_coin, ScRel.x(1.22f), ScRel.y(16.89f), ScRel.x(7.05f))
            PicRender.RenderText("Цена", ScRel.x(8.99f), ScRel.y(16.89f), ScRel.font(2.17f))
            PicRender.RenderText("1500", ScRel.x(28.44f), ScRel.y(16.89f), ScRel.font(2.17f))

            // за ход
            PicRender.RenderImage(R.drawable.rules_stat_boot, ScRel.x(1.22f), ScRel.y(20.14f), ScRel.x(7.05f))
            PicRender.RenderText("За ход", ScRel.x(8.99f), ScRel.y(20.14f), ScRel.font(2.17f))
            PicRender.RenderText("-800", ScRel.x(28.44f), ScRel.y(20.14f), ScRel.font(2.17f), color = Color(0xFF9B2C2C))

            // защита
            PicRender.RenderImage(R.drawable.rules_stat_shield, ScRel.x(1.22f), ScRel.y(23.39f), ScRel.x(7.05f))
            PicRender.RenderText("Защита", ScRel.x(8.99f), ScRel.y(23.39f), ScRel.font(2.17f))
            PicRender.RenderText("4", ScRel.x(35.97f), ScRel.y(23.39f), ScRel.font(2.17f))
        }
    }

    @Composable
    fun RenderFull(id: Int) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }

    private fun Modifier.parchmentCard(): Modifier {
        val fill = Color(0xF5F3E6C8)
        val stroke = Color(0xFFC4A06A)
        val shape = RoundedCornerShape(14.dp)
        return this
            .clip(shape)
            .background(fill)
            .border(1.5.dp, stroke, shape)
    }

    override fun onEnter() {
        slide = 1
    }

    override fun onExit() {}
}
