package com.example.mygame

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mygame.Terrain

object GameHudColors {
    val parchmentFill = Color(0xF5F3E6C8)
    val parchmentStroke = Color(0xFFC4A06A)
    val titleBrown = Color(0xFF3B2412)
    val bodyBrown = Color(0xFF6B4A2A)
    val incomeGreen = Color(0xFF2F6B32)
    val costRed = Color(0xFF9B2C2C)
    val tabInactiveBrown = Color(0xFF6B3E1F)
    val ctaGreen = Color(0xFF2F6B32)
    val buttonBrown = Color(0xFF6B3E1F)
    val selectGold = Color(0xFFC4A06A)
}

class GameHudLayout(val screenX: Int, val screenY: Int) {
    val topH = (screenY * 0.085).toInt()
    val bottomCollapsedH = (screenY * 0.12).toInt()
    val shopH = (screenY * 0.28).toInt()

    val bottomBarY: Int get() = screenY - bottomCollapsedH
    val tabH: Int = (screenY * 0.09).toInt()
    val tabW: Int = (screenX * 0.20).toInt()
    val tabX0: Int = (screenX * 0.04).toInt()
    val tabX1: Int = tabX0 + tabW + (screenX * 0.02).toInt()
    val tabY: Int get() = bottomBarY + (screenY * 0.015).toInt()

    val endTurnW: Int = (screenX * 0.48).toInt()
    val endTurnH: Int = tabH
    val endTurnX: Int = screenX - (screenX * 0.04).toInt() - endTurnW

    val hamburgerSize: Int = (topH * 0.72).toInt().coerceAtLeast((screenX * 0.075).toInt())
    val hamburgerX: Int = screenX - (screenX * 0.03).toInt() - hamburgerSize
    val hamburgerY: Int = (topH - hamburgerSize) / 2

    fun selectedUnitChipH(): Int = (screenY * 0.058).toInt()
    fun selectedUnitChipY(): Int = bottomBarY - selectedUnitChipH() - (screenY * 0.01).toInt()
    fun selectedUnitChipX(): Int = (screenX * 0.04).toInt()
    fun selectedUnitChipW(): Int = (screenX * 0.92).toInt()

    fun expandedBottomH(): Int = shopH + bottomCollapsedH
    fun expandedBottomY(): Int = screenY - expandedBottomH()

    val pauseCardX = (screenX * 0.13).toInt()
    val pauseCardY = (screenY * 0.16).toInt()
    val pauseCardW = (screenX * 0.74).toInt()
    val pauseCardH = (screenY * 0.57).toInt()

    val winCardX = (screenX * 0.14).toInt()
    val winCardY = (screenY * 0.22).toInt()
    val winCardW = (screenX * 0.72).toInt()
    val winCardH = (screenY * 0.52).toInt()

    fun shopCardsY(): Int = expandedBottomY() + (screenY * 0.035).toInt()
    fun unitCardW(): Int = (screenX * 0.225).toInt()
    fun unitCardH(): Int = (screenY * 0.22).toInt()
    fun unitCardX(index: Int): Int = (screenX * (0.03 + index * 0.24)).toInt()

    fun buildCardW(): Int = (screenX * 0.29).toInt()
    fun buildCardX(index: Int): Int = (screenX * (0.04 + index * 0.31)).toInt()

    fun endTurnModalW(): Int = (screenX * 0.78).toInt()
    fun endTurnModalH(): Int = (screenY * 0.22).toInt()
    fun endTurnModalX(): Int = (screenX - endTurnModalW()) / 2
    fun endTurnModalY(): Int = (screenY - endTurnModalH()) / 2
}

class GameHudRender {

    private val PicRender = PictureRender()

    @Composable
    private fun Int.toScreenDp(): Dp = with(LocalDensity.current) { toDp() }

    @Composable
    private fun Modifier.parchmentCard(): Modifier {
        val shape = RoundedCornerShape(14.dp)
        return this
            .clip(shape)
            .background(GameHudColors.parchmentFill)
            .border(1.5.dp, GameHudColors.parchmentStroke, shape)
    }

    @Composable
    private fun Modifier.brownButton(): Modifier {
        val shape = RoundedCornerShape(10.dp)
        return this
            .clip(shape)
            .background(GameHudColors.buttonBrown)
            .border(1.dp, GameHudColors.parchmentStroke, shape)
    }

    @Composable
    private fun Modifier.greenButton(): Modifier {
        val shape = RoundedCornerShape(10.dp)
        return this
            .clip(shape)
            .background(GameHudColors.ctaGreen)
            .border(1.dp, Color(0xFF1F4D22), shape)
    }

    @Composable
    fun DimVeil(alpha: Float = 0.45f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = alpha))
        )
    }

    @Composable
    fun TopBar(layout: GameHudLayout) {
        val shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        Box(
            modifier = Modifier
                .size(layout.screenX.toScreenDp(), layout.topH.toScreenDp())
                .clip(shape)
                .background(GameHudColors.parchmentFill)
                .border(1.5.dp, GameHudColors.parchmentStroke, shape)
        )
    }

    @Composable
    fun TopBarContent(
        layout: GameHudLayout,
        kazna: Int,
        playerName: String,
        netIncome: Int
    ) {
        val coinSize = 32.dp
        PicRender.RenderImage(R.drawable.rules_stat_coin, 16.dp, 22.dp, coinSize)
        PicRender.RenderText(
            kazna.toString(),
            52.dp,
            28.dp,
            26,
            bold = true,
            color = GameHudColors.titleBrown
        )
        PicRender.RenderText(
            "ТЕКУЩИЙ ХОД",
            0.dp,
            16.dp,
            14,
            bold = false,
            center = true,
            color = GameHudColors.bodyBrown
        )
        PicRender.RenderText(
            playerName,
            0.dp,
            34.dp,
            24,
            bold = true,
            center = true,
            color = GameHudColors.titleBrown
        )
        val incomeText = if (netIncome >= 0) "+$netIncome" else netIncome.toString()
        val incomeColor = if (netIncome >= 0) GameHudColors.incomeGreen else GameHudColors.costRed
        val incomeTextX = layout.hamburgerX.toScreenDp() - 88.dp
        PicRender.RenderText(
            incomeText,
            incomeTextX,
            28.dp,
            24,
            bold = true,
            color = incomeColor
        )
        val incomeCoinX = layout.hamburgerX.toScreenDp() - 34.dp
        PicRender.RenderImage(R.drawable.rules_stat_coin, incomeCoinX, 28.dp, 28.dp)
    }

    @Composable
    fun HamburgerButton(x: Int, y: Int, size: Int) {
        val shape = RoundedCornerShape(10.dp)
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(size.toScreenDp())
                .clip(shape)
                .background(GameHudColors.buttonBrown)
                .border(1.dp, GameHudColors.parchmentStroke, shape)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val lineW = size * 0.5f
                val left = (size - lineW) / 2f
                val top0 = size * 0.32f
                val gap = size * 0.14f
                repeat(3) { i ->
                    drawRect(
                        color = Color.White.copy(alpha = 0.92f),
                        topLeft = androidx.compose.ui.geometry.Offset(left, top0 + gap * i),
                        size = androidx.compose.ui.geometry.Size(lineW, size * 0.06f)
                    )
                }
            }
        }
    }

    @Composable
    fun BottomParchmentPanel(layout: GameHudLayout, expanded: Boolean) {
        val h = if (expanded) layout.expandedBottomH() else layout.bottomCollapsedH
        val y = if (expanded) layout.expandedBottomY() else layout.bottomBarY
        val shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)
        Box(
            modifier = Modifier
                .offset(y = y.toScreenDp())
                .size(layout.screenX.toScreenDp(), h.toScreenDp())
                .clip(shape)
                .background(GameHudColors.parchmentFill)
                .border(1.5.dp, GameHudColors.parchmentStroke, shape)
        )
    }

    @Composable
    fun TabButton(
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        label: String,
        iconRes: Int,
        active: Boolean
    ) {
        val shape = RoundedCornerShape(10.dp)
        val bg = if (active) GameHudColors.tabInactiveBrown else GameHudColors.parchmentFill
        val textColor = if (active) Color.White else GameHudColors.titleBrown
        val borderMod = if (active) {
            Modifier.border(2.dp, GameHudColors.selectGold, shape)
        } else {
            Modifier.border(1.dp, GameHudColors.parchmentStroke, shape)
        }
        val boxW = w.toScreenDp()
        val boxH = h.toScreenDp()
        val iconSize = boxW * 0.55f
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(boxW, boxH)
                .clip(shape)
                .background(bg)
                .then(borderMod)
        ) {
            PicRender.RenderImage(
                iconRes,
                (boxW - iconSize) / 2,
                2.dp,
                iconSize
            )
            PicRender.RenderText(
                label,
                0.dp,
                boxH * 0.70f,
                14,
                bold = true,
                center = true,
                color = textColor
            )
        }
    }

    @Composable
    fun EndTurnButton(x: Int, y: Int, w: Int, h: Int) {
        val shape = RoundedCornerShape(12.dp)
        val boxW = w.toScreenDp()
        val boxH = h.toScreenDp()
        val arrowSize = 32.dp
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(boxW, boxH)
                .clip(shape)
                .background(GameHudColors.ctaGreen)
                .border(1.dp, Color(0xFF1F4D22), shape)
        ) {
            PicRender.RenderImage(
                R.drawable.rules_arrow_right,
                boxW * 0.06f,
                (boxH - arrowSize) / 2,
                arrowSize
            )
            PicRender.RenderText(
                "Конец хода",
                boxW * 0.26f,
                (boxH - 24.dp) / 2,
                24,
                bold = true,
                color = Color.White
            )
        }
    }

    @Composable
    fun ShopUnitHeader(layout: GameHudLayout) {
        PicRender.RenderText(
            "Цена · За ход · Защита",
            0.dp,
            layout.shopCardsY().toScreenDp() - 18.dp,
            16,
            center = true,
            color = GameHudColors.bodyBrown
        )
    }

    @Composable
    private fun CardStatRow(
        cardW: Dp,
        rowY: Dp,
        iconRes: Int,
        label: String,
        value: String,
        valueColor: Color
    ) {
        val iconSize = 16.dp
        PicRender.RenderImage(iconRes, 4.dp, rowY, iconSize)
        PicRender.RenderText(label, 22.dp, rowY, 11, bold = false, color = GameHudColors.bodyBrown)
        PicRender.RenderText(
            value,
            cardW - 22.dp,
            rowY,
            11,
            bold = true,
            color = valueColor
        )
    }

    @Composable
    fun UnitShopCard(
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        title: String,
        iconRes: Int,
        eco: Economic,
        selected: Boolean
    ) {
        val shape = RoundedCornerShape(12.dp)
        val borderMod = if (selected) {
            Modifier.border(2.5.dp, GameHudColors.selectGold, shape)
        } else {
            Modifier.border(1.5.dp, GameHudColors.parchmentStroke, shape)
        }
        val boxW = w.toScreenDp()
        val boxH = h.toScreenDp()
        val artSize = boxW * 0.78f
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(boxW, boxH)
                .clip(shape)
                .background(GameHudColors.parchmentFill)
                .then(borderMod)
        ) {
            PicRender.RenderImage(
                iconRes,
                (boxW - artSize) / 2,
                2.dp,
                artSize
            )
            PicRender.RenderText(
                title,
                0.dp,
                boxH * 0.42f,
                16,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
            CardStatRow(
                boxW,
                boxH * 0.54f,
                R.drawable.rules_stat_coin,
                "Цена",
                eco.price.toString(),
                GameHudColors.titleBrown
            )
            CardStatRow(
                boxW,
                boxH * 0.68f,
                R.drawable.rules_stat_boot,
                "За ход",
                "-${eco.sale}",
                GameHudColors.costRed
            )
            CardStatRow(
                boxW,
                boxH * 0.82f,
                R.drawable.rules_stat_shield,
                "Защита",
                eco.protection.toString(),
                GameHudColors.titleBrown
            )
        }
    }

    @Composable
    fun BuildShopCard(
        x: Int,
        y: Int,
        w: Int,
        h: Int,
        title: String,
        iconRes: Int,
        eco: Economic,
        farmExtraLine: String?,
        selected: Boolean
    ) {
        val shape = RoundedCornerShape(12.dp)
        val borderMod = if (selected) {
            Modifier.border(2.5.dp, GameHudColors.selectGold, shape)
        } else {
            Modifier.border(1.5.dp, GameHudColors.parchmentStroke, shape)
        }
        val boxW = w.toScreenDp()
        val boxH = h.toScreenDp()
        val artSize = boxW * 0.62f
        val titleSize = if (title == "Усиленная башня") 14 else 16
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(boxW, boxH)
                .clip(shape)
                .background(GameHudColors.parchmentFill)
                .then(borderMod)
        ) {
            PicRender.RenderImage(
                iconRes,
                (boxW - artSize) / 2,
                2.dp,
                artSize
            )
            PicRender.RenderText(
                title,
                0.dp,
                boxH * 0.40f,
                titleSize,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
            CardStatRow(
                boxW,
                boxH * 0.52f,
                R.drawable.rules_stat_coin,
                "Цена",
                eco.price.toString(),
                GameHudColors.titleBrown
            )
            if (farmExtraLine != null) {
                CardStatRow(
                    boxW,
                    boxH * 0.66f,
                    R.drawable.rules_stat_bag,
                    "Доход",
                    "+${eco.income}",
                    GameHudColors.incomeGreen
                )
                PicRender.RenderText(
                    farmExtraLine,
                    0.dp,
                    boxH * 0.80f,
                    10,
                    bold = false,
                    center = true,
                    color = GameHudColors.bodyBrown
                )
            } else {
                CardStatRow(
                    boxW,
                    boxH * 0.66f,
                    R.drawable.rules_stat_boot,
                    "За ход",
                    "-${eco.sale}",
                    GameHudColors.costRed
                )
                CardStatRow(
                    boxW,
                    boxH * 0.80f,
                    R.drawable.rules_stat_shield,
                    "Защита",
                    eco.protection.toString(),
                    GameHudColors.titleBrown
                )
            }
        }
    }

    @Composable
    fun SelectedUnitChip(
        layout: GameHudLayout,
        unitTitle: String,
        unitIconRes: Int,
        movement: Int,
        protection: Int
    ) {
        val boxW = layout.selectedUnitChipW().toScreenDp()
        val boxH = layout.selectedUnitChipH().toScreenDp()
        Box(
            modifier = Modifier
                .offset(
                    layout.selectedUnitChipX().toScreenDp(),
                    layout.selectedUnitChipY().toScreenDp()
                )
                .size(boxW, boxH)
                .parchmentCard()
        ) {
            val iconSize = boxH * 0.72f
            PicRender.RenderImage(unitIconRes, 8.dp, (boxH - iconSize) / 2, iconSize)
            PicRender.RenderText(
                unitTitle,
                iconSize + 16.dp,
                (boxH - 18.dp) / 2,
                18,
                bold = true,
                color = GameHudColors.titleBrown
            )
            val statIcon = boxH * 0.5f
            PicRender.RenderImage(
                R.drawable.rules_stat_boot,
                boxW - 88.dp,
                (boxH - statIcon) / 2,
                statIcon
            )
            PicRender.RenderText(
                movement.toString(),
                boxW - 56.dp,
                (boxH - 16.dp) / 2,
                16,
                bold = true,
                color = GameHudColors.titleBrown
            )
            PicRender.RenderImage(
                R.drawable.rules_stat_shield,
                boxW - 48.dp,
                (boxH - statIcon) / 2,
                statIcon
            )
            PicRender.RenderText(
                protection.toString(),
                boxW - 16.dp,
                (boxH - 16.dp) / 2,
                16,
                bold = true,
                color = GameHudColors.titleBrown
            )
        }
    }

    @Composable
    fun EndTurnModal(
        layout: GameHudLayout,
        yesX: Int,
        yesY: Int,
        yesW: Int,
        yesH: Int,
        noX: Int,
        noY: Int,
        noW: Int,
        noH: Int
    ) {
        DimVeil(0.45f)
        val cardW = layout.endTurnModalW().toScreenDp()
        val cardH = layout.endTurnModalH().toScreenDp()
        Box(
            modifier = Modifier
                .offset(layout.endTurnModalX().toScreenDp(), layout.endTurnModalY().toScreenDp())
                .size(cardW, cardH)
                .parchmentCard()
        ) {
            PicRender.RenderImage(
                R.drawable.rules_fleur,
                (cardW - 34.dp) / 2,
                4.dp,
                34.dp
            )
            PicRender.RenderText(
                "Конец хода?",
                0.dp,
                cardH * 0.26f,
                22,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
            PicRender.RenderText(
                "Вы хотите закончить ход?",
                0.dp,
                cardH * 0.40f,
                14,
                center = true,
                color = GameHudColors.bodyBrown
            )
        }
        BrownLabelButton(noX, noY, noW, noH, "Нет", 15)
        GreenLabelButton(yesX, yesY, yesW, yesH, "Да", 15)
    }

    @Composable
    fun PauseMenu(
        layout: GameHudLayout,
        saveX: Int,
        saveY: Int,
        saveW: Int,
        saveH: Int,
        loadX: Int,
        loadY: Int,
        loadW: Int,
        loadH: Int,
        menuButtons: List<Pair<String, ButtonImage>>,
        statusMessage: String?
    ) {
        DimVeil(0.7f)
        val cardW = layout.pauseCardW.toScreenDp()
        val cardH = layout.pauseCardH.toScreenDp()
        Box(
            modifier = Modifier
                .offset(layout.pauseCardX.toScreenDp(), layout.pauseCardY.toScreenDp())
                .size(cardW, cardH)
                .parchmentCard()
        ) {
            Image(
                painter = painterResource(R.drawable.rules_fleur),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .offset((cardW - 40.dp) / 2, 4.dp)
                    .size(40.dp)
            )
            PicRender.RenderText(
                "Пауза",
                0.dp,
                52.dp,
                36,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
        }
        SmallActionButton(saveX, saveY, saveW, saveH, R.drawable.save, "Сохранить")
        SmallActionButton(loadX, loadY, loadW, loadH, R.drawable.download, "Загрузить")
        if (statusMessage != null) {
            val statusW = (layout.pauseCardW * 0.8).toInt()
            val statusH = (layout.screenY * 0.04).toInt()
            val statusX = layout.pauseCardX + (layout.pauseCardW - statusW) / 2
            val statusY = saveY + saveH + (layout.screenY * 0.008).toInt()
            Box(
                modifier = Modifier
                    .offset(statusX.toScreenDp(), statusY.toScreenDp())
                    .size(statusW.toScreenDp(), statusH.toScreenDp())
                    .border(1.5.dp, GameHudColors.incomeGreen, RoundedCornerShape(50))
            ) {
                PicRender.RenderText(
                    statusMessage,
                    0.dp,
                    (statusH.toScreenDp() - 24.dp) / 2,
                    17,
                    center = true,
                    color = GameHudColors.incomeGreen
                )
            }
        }
        for ((label, btn) in menuButtons) {
            BrownLabelButton(btn.x, btn.y, btn.dx, btn.dy, label, 18)
        }
    }

    @Composable
    fun VictoryOverlay(
        layout: GameHudLayout,
        winnerName: String,
        playerColorBitmap: ImageBitmap,
        hexSize: Int,
        exitX: Int,
        exitY: Int,
        exitW: Int,
        exitH: Int
    ) {
        DimVeil(0.55f)
        val cardW = layout.winCardW.toScreenDp()
        val cardH = layout.winCardH.toScreenDp()
        Box(
            modifier = Modifier
                .offset(layout.winCardX.toScreenDp(), layout.winCardY.toScreenDp())
                .size(cardW, cardH)
                .parchmentCard()
        ) {
            PicRender.RenderImage(
                R.drawable.rules_fleur,
                (cardW - 28.dp) / 2,
                12.dp,
                28.dp
            )
            PicRender.RenderText(
                "КОНЕЦ ИГРЫ",
                0.dp,
                cardH * 0.14f,
                14,
                center = true,
                color = GameHudColors.bodyBrown
            )
            PicRender.RenderText(
                "Победитель!",
                0.dp,
                cardH * 0.24f,
                28,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
            val hexDp = hexSize.toScreenDp()
            Image(
                bitmap = playerColorBitmap,
                contentDescription = "",
                modifier = Modifier
                    .offset((cardW - hexDp) / 2, cardH * 0.34f)
                    .size(hexDp)
            )
            PicRender.RenderText(
                winnerName,
                0.dp,
                cardH * 0.58f,
                20,
                bold = true,
                center = true,
                color = GameHudColors.titleBrown
            )
            PicRender.RenderText(
                "Все клетки захвачены",
                0.dp,
                cardH * 0.66f,
                14,
                center = true,
                color = GameHudColors.bodyBrown
            )
        }
        BrownLabelButton(exitX, exitY, exitW, exitH, "В главное меню", 18)
    }

    @Composable
    fun BrownLabelButton(x: Int, y: Int, w: Int, h: Int, label: String, textSize: Int = 18) {
        val boxH = h.toScreenDp()
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(w.toScreenDp(), boxH)
                .brownButton()
        ) {
            PicRender.RenderText(
                label,
                0.dp,
                (boxH - textSize.dp) / 2,
                textSize,
                bold = true,
                center = true,
                color = Color.White
            )
        }
    }

    @Composable
    private fun GreenLabelButton(x: Int, y: Int, w: Int, h: Int, label: String, textSize: Int = 15) {
        val boxH = h.toScreenDp()
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(w.toScreenDp(), boxH)
                .greenButton()
        ) {
            PicRender.RenderText(
                label,
                0.dp,
                boxH * 0.28f,
                textSize,
                bold = true,
                center = true,
                color = Color.White
            )
        }
    }

    @Composable
    private fun SmallActionButton(x: Int, y: Int, w: Int, h: Int, icon: Int, label: String) {
        val boxH = h.toScreenDp()
        val iconSize = boxH * 0.5f
        Box(
            modifier = Modifier
                .offset(x.toScreenDp(), y.toScreenDp())
                .size(w.toScreenDp(), boxH)
                .brownButton()
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(icon),
                    contentDescription = "",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(iconSize)
                )
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }

    companion object {
        fun unitIcon(terrain: Terrain): Int = when (terrain) {
            Terrain.UNIT1 -> R.drawable.rules_unit_skeleton
            Terrain.UNIT2 -> R.drawable.rules_unit_barbarian
            Terrain.UNIT3 -> R.drawable.rules_unit_knight
            Terrain.UNIT4 -> R.drawable.rules_unit_paladin
            else -> R.drawable.rules_unit_skeleton
        }

        fun unitTitle(terrain: Terrain): String = when (terrain) {
            Terrain.UNIT1 -> "Скелет"
            Terrain.UNIT2 -> "Варвар"
            Terrain.UNIT3 -> "Рыцарь"
            Terrain.UNIT4 -> "Паладин"
            else -> ""
        }

        fun buildIcon(terrain: Terrain): Int = when (terrain) {
            Terrain.FARM -> R.drawable.rules_build_farm
            Terrain.TOWER -> R.drawable.rules_build_tower
            Terrain.HARD_TOWER -> R.drawable.rules_build_hard_tower
            else -> R.drawable.rules_build_farm
        }

        fun buildTitle(terrain: Terrain): String = when (terrain) {
            Terrain.FARM -> "Ферма"
            Terrain.TOWER -> "Башня"
            Terrain.HARD_TOWER -> "Усиленная башня"
            else -> ""
        }
    }
}
