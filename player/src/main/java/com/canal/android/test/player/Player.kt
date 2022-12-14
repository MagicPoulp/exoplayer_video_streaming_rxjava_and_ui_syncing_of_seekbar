package com.canal.android.test.player

import android.content.Context
import android.view.SurfaceView
import com.canal.android.test.player.model.PlayerAction

interface Player {

    val playerView: SurfaceView

    fun pushAction(action: PlayerAction)

    companion object {
        fun getPlayerInstance(
            context: Context
        ): Player = PlayerImpl(
            context = context
        )
    }
}