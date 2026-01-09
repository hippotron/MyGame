package com.example.mygame

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

//class MainActivity - объявление класса вашей основной Activity
// : ComponentActivity() - наследование от ComponentActivity (специальный класс Activity для Jetpack Compose)
class MainActivity : ComponentActivity() {
    private  var  gameEngine : GameEngine = GameEngine()

    val mapScene: MutableMap<String, Scene> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mapScene.put("Menu", MenuScene(gameEngine,this))
        mapScene.put("Game", GameScene(gameEngine,this))
        mapScene.put("Setting", SettingScene(gameEngine,this))
        mapScene.put("Authors", AuthorsScene(gameEngine,this))

        gameEngine.CurrentScene="Game";

        // Можно вызывать любые @Composable функции
        setContent {
            val currentScene = gameEngine.CurrentScene
            val forceUpdate = gameEngine.forceUpdate

            Box(modifier = Modifier.fillMaxSize()) {
                val scene = mapScene[currentScene]

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

                // Компонент будет перерисовываться при изменении forceUpdate
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

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
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



