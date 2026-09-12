package com.example.mygame.Scene.ForScene
// пакет, где лежат движок и оверлей перехода

import androidx.compose.runtime.getValue
// чтение свойства `by mutableStateOf` (get)

import androidx.compose.runtime.mutableStateOf
// состояние, при изменении которого Compose перерисует экран

import androidx.compose.runtime.setValue
// запись в свойство `by mutableStateOf` (set)

enum class TransitionPhase {
    // фазы анимации перехода
    Idle,
    // перехода нет, оверлей не рисуется
    Close,
    // круг сжимается, экран закрывается чернотой
    Open,
    // круг расширяется, новая сцена открывается
}

class GameEngine(){
    // общий объект игры: текущая сцена и переход
    var CurrentScene by mutableStateOf("Menu")
    // имя сцены на экране; старт — меню
    var PreviousScene by mutableStateOf("Menu") // Добавляем
    // сцена, на которую вернёт goBackScene
    var forceUpdate by mutableStateOf(0)
    // счётчик принудительной перерисовки

    var backScene: Boolean = false
    // true, если идём назад, а не на новую сцену

    var isTransitioning by mutableStateOf(false)
    // идёт переход — тачи в MainActivity блокируются
    var transitionPhase by mutableStateOf(TransitionPhase.Idle)
    // текущая фаза iris
    var transitionToken by mutableStateOf(0)
    // номер запуска анимации, чтобы оверлей начал фазу заново
    var pendingScene: String? = null
    // сцена, которую покажем в чёрном кадре
    var phaseDurationMs: Long = 920L
    // длительность Close и Open
    var blackHoldMs: Long = 80L
    // пауза полной черноты перед сменой сцены
    // 920*2+80=1920 -> 19.2 секунды

    fun updateScene(scene: String) {
        startTransition(scene, goingBack = false)
    }
    // переход вперёд на указанную сцену

    fun goBackScene() {
        startTransition(PreviousScene, goingBack = true)
    }
    // переход назад

    fun onClosePhaseFinished() {
        if (transitionPhase != TransitionPhase.Close) return
        // защита: меняем сцену только после Close
        val next = pendingScene ?: return
        // нет цели — выходим
        CurrentScene = next
        // в черноте подставляем новую сцену
        transitionToken++
        // новый токен, чтобы оверлей запустил Open с нуля
        transitionPhase = TransitionPhase.Open
        // старт открытия
    }

    fun onOpenPhaseFinished() {
        if (transitionPhase != TransitionPhase.Open) return
        isTransitioning = false
        transitionPhase = TransitionPhase.Idle
        pendingScene = null
        // переход закончен, можно снова нажимать
    }

    private fun startTransition(target: String, goingBack: Boolean) {
        if (isTransitioning) return
        // второй переход, пока идёт первый, игнорируем
        if (target == CurrentScene) return
        // та же сцена — нечего анимировать
        backScene = goingBack
        if (!goingBack) {
            PreviousScene = CurrentScene
        }
        // при ходе вперёд запоминаем, куда потом вернуться
        pendingScene = target
        isTransitioning = true
        transitionToken++
        transitionPhase = TransitionPhase.Close
        // CurrentScene ещё не меняем — сначала закрываем iris
    }
}