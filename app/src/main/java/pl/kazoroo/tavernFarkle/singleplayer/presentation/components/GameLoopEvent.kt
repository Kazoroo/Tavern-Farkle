package pl.kazoroo.tavernFarkle.singleplayer.presentation.components

sealed class GameLoopEvent {
    data object Pass : GameLoopEvent()
    data object ScoreAndRoll : GameLoopEvent()
    data object Skucha : GameLoopEvent()
    data class GameEnd(val isWin: Boolean) : GameLoopEvent()
    data object PlayerStatusLeft : GameLoopEvent()
    data object PlayerStatusPaused : GameLoopEvent()
}
