package com.example.mygame.Scene



import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import com.example.mygame.ButtonImage
import com.example.mygame.ClientForServer.GameApiClient
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.GameStorage
import com.example.mygame.GlobalParam.TextRender
import com.example.mygame.R
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.SoundPlayer
import com.example.mygame.data.profile_data

class MenuScene(override var game: GameEngine, val context: Context): Scene {
    val displayMetrics = context.resources.displayMetrics
    val screenX = displayMetrics.widthPixels
    val screenY = displayMetrics.heightPixels

    val button_new_game = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.41).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.new_game
    )

    val button_setting = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.515).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.settings
    )

    val button_is_look_rupes = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.62).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.is_look_rules
    )

    val button_authors = ButtonImage(
        (screenX * 0.2).toInt(), (screenY * 0.725).toInt(),
        (screenX * 0.6).toInt(), (screenY * 0.1).toInt(), R.drawable.authors
    )

    val button_propfil = ButtonImage(
        (screenX * 0.05).toInt(), (screenY * 0.05).toInt(),
        (screenX * 0.2).toInt(), (screenY * 0.1).toInt(), R.drawable.profile
    )

    // 👇 ПЕРЕМЕННЫЕ
    var isKeyboardOpen = false

    private var editText: EditText? = null

    val gameStorage = GameStorage(context)
    private val gameApiClient = GameApiClient()

    var trigerUpdate by mutableStateOf(0)

    val soundPlayer = SoundPlayer(context)

    override fun update() {
        // ничего
    }

    @Composable
    override fun render() {
        val textMeasurer = rememberTextMeasurer()

        // фон
        Image(
            painter = painterResource(id = R.drawable.background_menu),
            contentDescription = "фон",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        // кнопки
        button_propfil.Render()
        button_new_game.Render()
        button_setting.Render()
        button_is_look_rupes.Render()
        button_authors.Render()

        Canvas(modifier = Modifier.fillMaxSize()) {
            trigerUpdate // для обновления render
            //println("render, ${profile_data.DEFAULT_PROFILE.playerName}")
            TextRender(
                textMeasurer,
                name = profile_data.DEFAULT_PROFILE.playerName,
                (screenX*0.5).toInt(),
                (screenY*0.08).toInt(),
                center=true
            )
        }
        //println("render")

    }

    override suspend fun onTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val a = 500
                val b = 55

                //val result = gameApiClient.myClass(a = a, b = b)

                //println("a,b =$result")

                game.forceUpdate
                if (event.pointerCount == 1){
                    val mx = event.x.toInt()
                    val my = event.y.toInt()

                    if (button_new_game.click(mx,my)==true){
                        soundPlayer.play(R.raw.start_game)
                        game.updateScene("Game")
                    }

                    if (button_setting.click(mx,my)==true){
                        soundPlayer.play(R.raw.button)
                        game.updateScene("Setting")
                    }

                    if (button_is_look_rupes.click(mx,my)){
                        soundPlayer.play(R.raw.button)
                        game.updateScene("Rules")
                    }

                    if (button_authors.click(mx,my)==true){
                        soundPlayer.play(R.raw.button)
                        game.updateScene("Authors")
                    }

                    if (button_propfil.click(mx,my)){
                        if (!isKeyboardOpen) {
                            showKeyboard()
                        } else {
                            hideKeyboard()
                        }
                        soundPlayer.play(R.raw.button)
                        //println("ll, text = ${profile_data.DEFAULT_PROFILE.playerName}")
                        game.forceUpdate++
                    }
                }
            game.forceUpdate++
            }
        }
    }

    private fun showKeyboard() {
        isKeyboardOpen = true

        // Создаем EditText если его нет
        if (editText == null) {
            editText = EditText(context).apply {

                inputType = InputType.TYPE_CLASS_TEXT // меняет кнопку след строка на enter
                maxLines = 1  // только одна строка, не более

                isFocusable = true
                isFocusableInTouchMode = true

                // Максимальная длина текста
                filters = arrayOf(android.text.InputFilter.LengthFilter(10))
                // Размер текста
                textSize = 24f

                val params = FrameLayout.LayoutParams(
                    (screenX * 0.35).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ) // это размер прямоугольника в котором можно выбрать элемент в тексте
                params.leftMargin = (screenX * 0.35).toInt() // koor по x
                params.topMargin = (screenY * 0.1).toInt() // koor по y
                layoutParams = params // применяем координаты и прямоугольник

                //для телефона
                // Обработка Enter на клавиатуре
                setOnEditorActionListener { v, actionId, event ->
                    //println("setOnEditorActionListener++")
                    if (actionId == EditorInfo.IME_ACTION_DONE ) // enter
                    {

                        hideKeyboard()

                        profile_data.DEFAULT_PROFILE.playerName = text.toString()
                        //println("enter телефон, ${profile_data.DEFAULT_PROFILE.playerName}")
                        gameStorage.saveToJsonProfile()
                        trigerUpdate++
                        true
                    } else {
                        false
                    }
                }

                // Для ПК
                // Обработка физической клавиши Enter
                setOnKeyListener { _, keyCode, event ->
                    if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        hideKeyboard()

                        profile_data.DEFAULT_PROFILE.playerName = text.toString()
                        //println("enter клавиатура пк, ${profile_data.DEFAULT_PROFILE.playerName}")
                        gameStorage.saveToJsonProfile()
                        trigerUpdate++
                        true
                    } else {
                        false
                    }
                }
            }
        }

        val rootView = (context as? Activity)?.findViewById<View>(android.R.id.content) as? ViewGroup
        if (editText?.parent == null) {
            rootView?.addView(editText)
        }

        // открываем клавиатуру
        editText?.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        game.forceUpdate++
    }
    private fun hideKeyboard() {
        isKeyboardOpen = false

        gameStorage.saveToJsonProfile()

        // Скрываем клавиатуру принудительно
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText?.windowToken, 0)

        // Убираем фокус с EditText
        editText?.clearFocus()

        // Удаляем EditText
        val rootView = (context as? Activity)?.findViewById<View>(android.R.id.content) as? ViewGroup
        rootView?.removeView(editText)
        editText = null

        game.forceUpdate++
    }

    override fun onEnter() {
        isKeyboardOpen = false
    }

    override fun onExit() {
        hideKeyboard()
        // Удаляем EditText при выходе
        val rootView = (context as? Activity)?.findViewById<View>(android.R.id.content) as? ViewGroup
        rootView?.removeView(editText)
        editText = null
    }
}