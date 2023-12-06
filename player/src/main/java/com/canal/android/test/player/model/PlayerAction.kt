package com.canal.android.test.player.model

import com.canal.android.test.common.PlayerRatio

sealed class PlayerAction {
    object PlayPauseClicked : PlayerAction()
    object Play : PlayerAction()
    object Pause : PlayerAction()
    data class SeekTo(val seekToPositionMs: Long) : PlayerAction()
    object StopPlayback : PlayerAction() // Stop current playback and release it's data
    object Release : PlayerAction()
    data class SelectTrack(val trackType: Int, val trackGroupIndex: Int, val trackIndex: Int) :
        PlayerAction()

    data class StartPlayback(val manifestUrl: String, val callbackOnVideoSizeChanged: ((PlayerRatio) -> Unit)?, val seekToPositionMs: Long?) : PlayerAction()
}