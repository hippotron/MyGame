package com.example.mygame.Scene.ForScene
// пакет, где лежат движок и оверлей перехода

import androidx.compose.foundation.Canvas
// холст, на котором рисуется чёрная шторка

import androidx.compose.foundation.layout.fillMaxSize
// модификатор «займи весь размер родителя»

import androidx.compose.runtime.Composable
// пометка, что это UI-функция Compose

import androidx.compose.runtime.LaunchedEffect
// запуск корутины при появлении или смене ключей

import androidx.compose.runtime.getValue
// чтение свойства `by mutableStateOf` (get)

import androidx.compose.runtime.mutableStateOf
// состояние, при изменении которого Compose перерисует экран

import androidx.compose.runtime.remember
// сохранить t между кадрами

import androidx.compose.runtime.setValue
// запись в свойство `by mutableStateOf` (set)

import androidx.compose.ui.Modifier
// цепочка размеров и поведения виджета

import androidx.compose.ui.geometry.Rect
// прямоугольник, в который вписан круг-дыра

import androidx.compose.ui.graphics.ClipOp
// как клипать: рисовать всё, кроме круга

import androidx.compose.ui.graphics.Color
// цвет заливки (чёрный)

import androidx.compose.ui.graphics.Path
// контур круглой дыры

import androidx.compose.ui.graphics.drawscope.clipPath
// вырезать по этому контуру

import kotlin.math.sqrt
// корень для радиуса до угла экрана

import kotlinx.coroutines.delay
// пауза в цикле анимации и в чёрном кадре

@Composable
fun SceneTransitionOverlay(game: GameEngine) {
    // оверлей перехода, читает состояние из движка
    val phase = game.transitionPhase
    // сейчас Idle, Close или Open
    if (phase == TransitionPhase.Idle) return
    // перехода нет: ничего не рисуем и не запускаем таймер

    val token = game.transitionToken
    // номер запуска фазы; меняется при старте Close и при старте Open
    var t by remember(token, phase) { mutableStateOf(0f) }
    // прогресс 0…1; при смене token или phase создаётся новое t = 0

    Canvas(modifier = Modifier.fillMaxSize()) {
        // холст на весь экран; лямбда — кадр отрисовки
        val dx = size.width / 2f
        // X центра экрана
        val dy = size.height / 2f
        // Y центра экрана
        val maxR = sqrt(dx * dx + dy * dy)
        // расстояние от центра до угла, чтобы чёрное закрыло весь экран
        val p = t * t * (3f - 2f * t)
        // сглаживание: старт и конец медленнее, середина быстрее
        val radius = if (phase == TransitionPhase.Close) {
            maxR * (1f - p)
            // Close: дыра от полного экрана к нулю (шторка закрывается)
        } else {
            maxR * p
            // Open: дыра от нуля к полному экрану (шторка открывается)
        }
        val hole = Path()
        // пустой контур дыры
        hole.addOval(
            Rect(
                dx - radius,
                // левый край круга
                dy - radius,
                // верхний край круга
                dx + radius,
                // правый край круга
                dy + radius
                // нижний край круга
            )
        )
        clipPath(hole, ClipOp.Difference) {
            // рисовать всё, кроме круга: круг остаётся прозрачным
            drawRect(Color.Black)
            // чёрный прямоугольник на весь холст
        }
    }

    LaunchedEffect(token, phase) {
        // корутина стартует заново при смене токена или фазы
        t = 0f
        // прогресс с нуля
        val start = System.currentTimeMillis()
        // момент старта фазы в мс
        while (true) {
            // крутим кадры, пока фаза не дойдёт до 1
            val now = System.currentTimeMillis()
            // текущее время
            t = (now - start).toFloat() / game.phaseDurationMs.toFloat()
            // доля прошедшего времени; смена t перерисовывает Canvas
            if (t >= 1f) {
                // время фазы вышло
                t = 1f
                // зафиксировать последний кадр
                break
                // выйти из цикла
            }
            delay(16)
            // пауза ~1 кадр (около 60 fps)
        }
        if (phase == TransitionPhase.Close) {
            // закрытие закончилось
            delay(game.blackHoldMs)
            // коротко подержать полный чёрный экран
            game.onClosePhaseFinished()
            // движок меняет сцену и включает Open
        } else if (phase == TransitionPhase.Open) {
            // открытие закончилось
            game.onOpenPhaseFinished()
            // движок ставит Idle, переход кончен
        }
    }
}
