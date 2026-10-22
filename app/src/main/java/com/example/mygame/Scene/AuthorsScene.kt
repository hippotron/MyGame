package com.example.mygame.Scene

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mygame.ButtonImage
import com.example.mygame.PackageForKoor.ScreenRel
import com.example.mygame.PictureRender
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer

class AuthorsScene(override var game: GameEngine, context: Context) : Scene {
    private val appContext = context

    override fun update() {

    }
    val displayMetrics = context.resources.displayMetrics
    val screenXpx = displayMetrics.widthPixels
    val screenYpx = displayMetrics.heightPixels
    private val ScRel = ScreenRel(context)

    private val steamLeftPx = (screenXpx * 0.0900f).toInt()
    private val steamRightPx = (screenXpx * 0.9100f).toInt()
    private val steamTopPx = (screenYpx * 0.5974f).toInt()
    private val steamBottomPx = (screenYpx * 0.6905f).toInt()

    // Тот же размер, что у кнопок главного меню
    val button_return = ButtonImage(
        (screenXpx * 0.15).toInt(), (screenYpx * 0.04).toInt(),
        (screenXpx * 0.7).toInt(), (screenYpx * 0.095).toInt(), R.drawable.image_return
    )

    private val soundPlayer = SoundPlayer(context)
    private val PicRender = PictureRender()

    @Composable
    override fun render() {
        Image(
            painter = painterResource(id = R.drawable.background_menu),
            contentDescription = "фон",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .offset(x = ScRel.x(9.00f), y = ScRel.y(18.26f))
                .size(width = ScRel.x(82.00f), height = ScRel.y(55.98f))
                .parchmentCard()
        ) {
            PicRender.RenderImage(R.drawable.author_photo, ScRel.x(24.60f), ScRel.y(1.95f), ScRel.x(32.80f))
            PicRender.RenderText(
                "Лев Абасов",
                ScRel.x(0f), ScRel.y(18.08f), ScRel.font(3.03f),
                color = Color(0xFF3B2412),
                center = true
            )
            PicRender.RenderText(
                "Автор и разработчик",
                ScRel.x(0f), ScRel.y(22.41f), ScRel.font(1.95f),
                color = Color(0xFF6B4A2A),
                center = true
            )
            PicRender.RenderText(
                "Это моя первая мобильная игра.\nСделал её сам на Kotlin для Android:\nкод, интерфейс и оформление.",
                ScRel.x(0f), ScRel.y(26.09f), ScRel.font(1.84f),
                bold = false,
                color = Color(0xFF6B4A2A),
                center = true
            )
            PicRender.RenderText(
                "Люблю доводить мелочи до конца:\nостров, юниты и эта карточка.",
                ScRel.x(0f), ScRel.y(35.19f), ScRel.font(1.84f),
                bold = false,
                color = Color(0xFF6B4A2A),
                center = true
            )
            PicRender.RenderText(
                "Steam\nsteamcommunity.com/profiles/\n76561199827418152",
                ScRel.x(0f), ScRel.y(41.47f), ScRel.font(1.84f),
                color = Color(0xFF3B2412),
                center = true
            )
            PicRender.RenderText(
                "2026",
                ScRel.x(0f), ScRel.y(50.78f), ScRel.font(1.95f),
                color = Color(0xFF3B2412),
                center = true
            )
        }

        //PicRender.RenderText("Авторы", ScRel.x(0f), ScRel.y(13.93f), ScRel.font(3.03f), color = Color.White, center = true)

        button_return.Render()
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

    override suspend fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

            }
            MotionEvent.ACTION_UP -> {
                val mx = event.x.toInt()
                val my = event.y.toInt()

                if (event.pointerCount == 1) {
                    if (button_return.click(mx,my)==true){
                        //Log.d("","ttt")
                        //soundPlayer.play(R.raw.button)
                        game.goBackScene()
                    } else if (hitSteam(mx, my)) {
                        openSteam()
                    }
                }
                game.forceUpdate++
            }
        }
    }

    private fun hitSteam(mx: Int, my: Int): Boolean {
        return mx in steamLeftPx..steamRightPx && my in steamTopPx..steamBottomPx
    }

    private fun openSteam() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(STEAM_URL))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            appContext.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
        }
    }

    override fun onEnter() {

    }

    override fun onExit() {

    }

    private companion object {
        const val STEAM_URL = "https://steamcommunity.com/profiles/76561199827418152/"
    }
}
