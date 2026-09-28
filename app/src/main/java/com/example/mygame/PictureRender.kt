package com.example.mygame

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class PictureRender {

    @Composable
    fun RenderHeader(slide: Int) {
        RenderFull(R.drawable.background_menu)
        RenderFull(R.drawable.rules_parchment)
        RenderImage(R.drawable.rules_fleur, 204.dp, 110.dp, 28.dp)
        RenderText("Правила", 0.dp, 145.dp, 44, color = Color(0xFF3B2412), center = true)
        val title = when (slide) {
            1 -> "Начало игры"
            2 -> "Юниты"
            3 -> "Здания"
            else -> "Бой и ход"
        }
        RenderText(title, 0.dp, 195.dp, 26, color = Color(0xFF6B4A2A), center = true)
    }

    @Composable
    fun RenderUnits(){
        RenderSkeleton(
            45.dp,
            240.dp,
            165.dp,
            250.dp
        )
        RenderBarbarian(
            215.dp,
            240.dp,
            165.dp,
            250.dp
        )
        RenderKnight(
            45.dp,
            493.dp,
            165.dp,
            250.dp
        )
        RenderPaladin(
            215.dp,
            493.dp,
            165.dp,
            250.dp
        )
        RenderTextPaladin()
    }

    @Composable
    fun RenderStart() {
        RenderFactCard(45.dp, 240.dp, R.drawable.rules_icon_coin_crown, "80 монет")
        RenderFactCard(166.dp, 240.dp, R.drawable.rules_icon_hexes, "7 клеток")
        RenderFactCard(280.dp, 240.dp, R.drawable.rules_icon_income, "+50 доход")
        RenderIncomeCard()
    }

    @Composable
    private fun RenderFactCard(koorX: Dp, koorY: Dp, icon: Int, label: String) {
        Box(
            modifier = Modifier
                .offset(x = koorX, y = koorY)
                .size(width = 110.dp, height = 114.dp)
                .parchmentCard()
        ) {
            RenderImage(icon, ((110.dp)/2-(74.dp)/2), 8.dp, 74.dp)
            RenderText(label, 0.dp, 85.dp, 20, color = Color(0xFF3B2412), center = true)
        }
    }

    @Composable
    private fun RenderIncomeCard() {
        Box(
            modifier = Modifier
                .offset(x = 45.dp, y = 368.dp)
                .size(width = 345.dp, height = 150.dp)
                .parchmentCard()
        ) {
            RenderImage(R.drawable.rules_icon_leaf_hex, 10.dp, 8.dp, 85.dp)
            RenderText("Доход", 105.dp, 8.dp, 26, color = Color(0xFF3B2412))
            RenderText("Клетка даёт +10 монет \nза ход ", 105.dp, 38.dp, 21, bold = false)
            RenderText("(Юниты и здания \nденьги не приносят)", 10.dp, 90.dp, 21, bold = false, center = true)
        }
    }

    @Composable
    fun RenderBuildings() {
        RenderBuildCard(45.dp, "Ферма", R.drawable.rules_build_farm, "200", "Доход", "+30", R.drawable.rules_stat_bag, Color(0xFF2F6B32), "0")
        RenderBuildCard(166.dp, "Башня", R.drawable.rules_build_tower, "120", "За ход", "-50", R.drawable.rules_stat_boot, Color(0xFF9B2C2C), "2")
        RenderBuildCard(280.dp, "Усиленная\nбашня", R.drawable.rules_build_hard_tower, "350", "За ход", "-200", R.drawable.rules_stat_boot, Color(0xFF9B2C2C), "3")
        RenderRuleCard(468.dp, 80.dp, R.drawable.rules_stat_coin, "Цена фермы", "200 + 20 × n\nn — число уже построенных ферм.", 40.dp)
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
                .offset(x = koorX, y = 240.dp)
                .size(width = 110.dp, height = 220.dp)
                .parchmentCard()
        ) {
            RenderImage(image, 19.dp, 4.dp, 72.dp)
            RenderText(title, 0.dp, 78.dp, 16, color = Color(0xFF3B2412), center = true)

            RenderImage(R.drawable.rules_stat_coin, 4.dp, 122.dp, 18.dp)
            RenderText("Цена", 24.dp, 122.dp, 14)
            RenderText(price, 72.dp, 122.dp, 14, color = Color(0xFF3B2412))

            RenderImage(turnIcon, 4.dp, 148.dp, 18.dp)
            RenderText(turnLabel, 24.dp, 148.dp, 14)
            RenderText(turn, 68.dp, 148.dp, 14, color = turnColor)

            RenderImage(R.drawable.rules_stat_shield, 4.dp, 174.dp, 18.dp)
            RenderText("Защита", 24.dp, 174.dp, 14)
            RenderText(defense, 80.dp, 174.dp, 14, color = Color(0xFF3B2412))
        }
    }

    @Composable
    fun RenderBattle() {
        RenderRuleCard(
            240.dp, 168.dp, R.drawable.rules_stat_shield, "Защита территории",
            "Клетки вокруг юнита или башни\nполучают их защиту.", 124.dp
        ) { RenderProtectHexes() }
        RenderRuleCard(
            416.dp, 168.dp, R.drawable.rules_stat_boot, "Ход юнита",
            "Сначала юнит ходит на соседнюю клетку,\nзатем с неё — на следующую соседнюю.", 124.dp
        ) { RenderMoveSteps() }
        RenderRuleCard(592.dp, 90.dp, R.drawable.rules_icon_star, "Захват", "Ступив на клетку, юнит присваивает её игроку.", 42.dp)
        RenderRuleCard(690.dp, 90.dp, R.drawable.rules_stat_bag, "Начало хода", "Доход идёт в казну. Юниты снова могут ходить.", 42.dp)
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
                .offset(x = 45.dp, y = y)
                .size(width = 345.dp, height = height)
                .parchmentCard()
        ) {
            RenderImage(icon, 8.dp, 8.dp, 28.dp)
            RenderText(title, 42.dp, 8.dp, 20, color = Color(0xFF3B2412))
            diagram()
            RenderText(text, 8.dp, textY, 16, bold = false)
        }
    }

    @Composable
    private fun RenderProtectHexes() {
        RenderHex(158.dp, 34.dp, true)
        RenderHex(132.dp, 48.dp, true)
        RenderHex(184.dp, 48.dp, true)
        RenderHex(158.dp, 62.dp, false)
        RenderHex(132.dp, 76.dp, true)
        RenderHex(184.dp, 76.dp, true)
        RenderHex(158.dp, 90.dp, true)
        RenderImage(R.drawable.tower, 164.dp, 68.dp, 18.dp)
    }

    @Composable
    private fun RenderHex(x: Dp, y: Dp, mark: Boolean) {
        RenderImage(R.drawable.two, x, y, 28.dp)
        if (mark) RenderText("2", x + 8.dp, y + 4.dp, 12, color = Color(0xFF3B2412))
    }

    @Composable
    private fun RenderMoveSteps() {
        RenderImage(R.drawable.two, 16.dp, 52.dp, 48.dp)
        RenderImage(R.drawable.skeleton, 22.dp, 58.dp, 36.dp)
        RenderText("1", 74.dp, 48.dp, 14, color = Color(0xFF3B2412))
        RenderImage(R.drawable.arrow_right, 70.dp, 68.dp, 22.dp)
        RenderImage(R.drawable.two, 108.dp, 52.dp, 48.dp)
        RenderText("2", 166.dp, 48.dp, 14, color = Color(0xFF3B2412))
        RenderImage(R.drawable.arrow_right, 162.dp, 68.dp, 22.dp)
        RenderImage(R.drawable.two, 200.dp, 52.dp, 48.dp)
    }

    @Composable
    private fun RenderTextPaladin() {
        Box(
            modifier = Modifier
                .offset(x = 45.dp, y = 750.dp)

                .size(width = 340.dp, height = 55.dp)
                .parchmentCard()
        ){
            RenderImage(R.drawable.rules_stat_boot, 5.dp, 2.dp, 45.dp)
            RenderText("Паладин может ходить на \n клетку с любой защитой", 50.dp, 2.dp,20)
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
            RenderImage(R.drawable.rules_unit_skeleton, 23.dp, 3.dp, 119.dp)
            RenderText("Скелет", 37.dp, 123.dp, 26, color = Color(0xFF3B2412))   // color 0xFF6B4A2A

            // цена
            RenderImage(R.drawable.rules_stat_coin, 5.dp, 156.dp, 29.dp)
            RenderText("Цена", 37.dp, 156.dp, 20)   // color 0xFF6B4A2A
            RenderText("80", 136.dp, 156.dp, 20)    // color 0xFF3B2412

            // за ход
            RenderImage(R.drawable.rules_stat_boot, 5.dp, 186.dp, 29.dp)
            RenderText("За ход", 37.dp, 186.dp, 20) // color 0xFF6B4A2A
            RenderText("-20", 129.dp, 186.dp, 20, color = Color(0xFF9B2C2C))   // color 0xFF9B2C2C

            // защита
            RenderImage(R.drawable.rules_stat_shield, 5.dp, 216.dp, 29.dp)
            RenderText("Защита", 37.dp, 216.dp, 20) // color 0xFF6B4A2A
            RenderText("1", 148.dp, 216.dp, 20)     // color 0xFF3B2412
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
            RenderImage(R.drawable.rules_unit_barbarian, 23.dp, 3.dp, 119.dp)
            RenderText("Варвар", 37.dp, 123.dp, 26, color = Color(0xFF3B2412))

            // цена
            RenderImage(R.drawable.rules_stat_coin, 5.dp, 156.dp, 29.dp)
            RenderText("Цена", 37.dp, 156.dp, 20)
            RenderText("220", 129.dp, 156.dp, 20)

            // за ход
            RenderImage(R.drawable.rules_stat_boot, 5.dp, 186.dp, 29.dp)
            RenderText("За ход", 37.dp, 186.dp, 20)
            RenderText("-80", 129.dp, 186.dp, 20, color = Color(0xFF9B2C2C))

            // защита
            RenderImage(R.drawable.rules_stat_shield, 5.dp, 216.dp, 29.dp)
            RenderText("Защита", 37.dp, 216.dp, 20)
            RenderText("2", 148.dp, 216.dp, 20)
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
            RenderImage(R.drawable.rules_unit_knight, 23.dp, 3.dp, 119.dp)
            RenderText("Рыцарь", 37.dp, 123.dp, 26, color = Color(0xFF3B2412))

            // цена
            RenderImage(R.drawable.rules_stat_coin, 5.dp, 156.dp, 29.dp)
            RenderText("Цена", 37.dp, 156.dp, 20)
            RenderText("600", 129.dp, 156.dp, 20)

            // за ход
            RenderImage(R.drawable.rules_stat_boot, 5.dp, 186.dp, 29.dp)
            RenderText("За ход", 37.dp, 186.dp, 20)
            RenderText("-300", 117.dp, 186.dp, 20, color = Color(0xFF9B2C2C))

            // защита
            RenderImage(R.drawable.rules_stat_shield, 5.dp, 216.dp, 29.dp)
            RenderText("Защита", 37.dp, 216.dp, 20)
            RenderText("3", 148.dp, 216.dp, 20)
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
            RenderImage(R.drawable.rules_unit_paladin, 23.dp, 3.dp, 119.dp)
            RenderText("Паладин", 37.dp, 123.dp, 26, color = Color(0xFF3B2412))

            // цена
            RenderImage(R.drawable.rules_stat_coin, 5.dp, 156.dp, 29.dp)
            RenderText("Цена", 37.dp, 156.dp, 20)
            RenderText("1500", 117.dp, 156.dp, 20)

            // за ход
            RenderImage(R.drawable.rules_stat_boot, 5.dp, 186.dp, 29.dp)
            RenderText("За ход", 37.dp, 186.dp, 20)
            RenderText("-800", 117.dp, 186.dp, 20, color = Color(0xFF9B2C2C))

            // защита
            RenderImage(R.drawable.rules_stat_shield, 5.dp, 216.dp, 29.dp)
            RenderText("Защита", 37.dp, 216.dp, 20)
            RenderText("4", 148.dp, 216.dp, 20)
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

    @Composable
    fun RenderImage(id: Int, X: Dp, Y: Dp, size: Dp) {
        Image(
            painter = painterResource(id = id),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .offset(x = X, y = Y)
                .size(size)
                .clip(RoundedCornerShape(10.dp))
            //.padding(6.dp)
        )
    }
    @Composable
    fun RenderText(text: String, X: Dp, Y: Dp, textSize: Int, bold: Boolean = true, color: Color = Color(0xFF6B4A2A), center: Boolean = false){
        Text(
            text = text,
            color = color,
            modifier = Modifier
                .fillMaxSize()
                .offset(x = X, y = Y),
            fontSize = textSize.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            overflow = TextOverflow.Ellipsis,
            textAlign =  if (center )TextAlign.Center else null
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
}
