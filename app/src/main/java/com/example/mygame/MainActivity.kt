package com.example.mygame

import android.os.Bundle
import android.view.View
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mygame.Scene.AuthorsScene
import com.example.mygame.Scene.ForScene.GameEngine
import com.example.mygame.Scene.GameScene
import com.example.mygame.Scene.MenuScene
import com.example.mygame.Scene.RulesScene
import com.example.mygame.Scene.ForScene.Scene
import com.example.mygame.Scene.SettingScene
import com.example.mygame.data.profile_data
import java.util.UUID

class MainActivity : ComponentActivity() {
    private var gameEngine: GameEngine = GameEngine()
    val mapScene = mutableMapOf<String, Scene>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Фиксируем вертикальную ориентацию
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        mapScene["Menu"] = MenuScene(gameEngine, this)
        val gameScene = GameScene(gameEngine, this)
        mapScene["Game"] = gameScene

        mapScene["Setting"] = SettingScene(gameEngine, this)
        mapScene["Rules"] = RulesScene(gameEngine, this,gameScene.economic)
        mapScene["Authors"] = AuthorsScene(gameEngine, this)

        gameEngine.CurrentScene = "Menu"

        // ЗАГРУЗКА ПРОФИЛЯ - ИСПРАВЛЕНО
        val gameStorage = GameStorage(this)
        val loadSuccess = gameStorage.loadFromJsonProfile()

        if (!loadSuccess) {
            // Если файла нет или ошибка загрузки, создаем профиль по умолчанию
            profile_data.DEFAULT_PROFILE = profile_data("player", UUID.randomUUID())
            gameStorage.saveToJsonProfile()
        }

        // Можно вызывать любые @Composable функции
        setContent {
            val currentScene = gameEngine.CurrentScene
            val forceUpdate = gameEngine.forceUpdate

            Box(modifier = Modifier.fillMaxSize()) {
                gameEngine.forceUpdate
                val scene = mapScene[currentScene]
                val previousScene = remember { mutableStateOf<String?>(null) }

                LaunchedEffect(currentScene) {
                    if (!gameEngine.backScene) {
                        // Вызываем onExit для предыдущей сцены
                        mapScene[previousScene.value]?.onExit()

                        // Вызываем onEnter для новой сцены
                        scene?.onEnter()
                        println(gameEngine.backScene)
                    }
                    previousScene.value = currentScene
                }

                scene?.update()

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        View(context)
                    },
                    update = { view ->
                        view.setOnTouchListener { _, event ->
                            scene?.onTouchEvent(event)
                            true
                        }
                    }
                )

                scene?.render()
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    // В Activity
    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        // Добавьте эти флаги для Android 10+
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
    }

    // В onWindowFocusChanged
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }
}