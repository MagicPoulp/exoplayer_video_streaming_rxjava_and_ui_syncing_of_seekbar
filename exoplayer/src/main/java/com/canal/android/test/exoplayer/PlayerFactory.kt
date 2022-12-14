package com.canal.android.test.exoplayer

import android.content.Context
import androidx.media3.common.util.UnstableApi

@UnstableApi object PlayerFactory {

    fun getPlayerInstance(context: Context): PlayerCoreImpl = PlayerCoreImpl.getInstance(context)
}