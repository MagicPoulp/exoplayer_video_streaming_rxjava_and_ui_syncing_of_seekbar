package com.canal.android.test.ui.common

import android.app.Activity
import android.view.View

fun Activity?.setFullScreen() {
    if (this == null) {
        return
    }
    // set player fullscreen
    this.window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
        if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0) {
            this.enterFullScreen()
        }
    }
    this.enterFullScreen()
}

private fun Activity.enterFullScreen() {
    this.window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // set layout use full space screen (navigation bar space)
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // set layout use full space screen (status bar space)
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    or View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // mImmersive mode
            )
}

fun Activity?.exitFullScreen() {
    if (this == null) {
        return
    }
    this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_VISIBLE)
    this.window.decorView.setOnSystemUiVisibilityChangeListener {}
}