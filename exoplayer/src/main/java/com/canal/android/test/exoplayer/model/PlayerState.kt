package com.canal.android.test.exoplayer.model

data class PlayerState(
    val status: PlayerStatus = PlayerStatus.IDLE,
    val position: Long = UNSET_VALUE,
    val durationMs: Long = UNSET_VALUE,
)

enum class PlayerStatus {
    IDLE,
    PLAYING,
    PAUSED,
    BUFFERING
}

const val UNSET_VALUE = -1L