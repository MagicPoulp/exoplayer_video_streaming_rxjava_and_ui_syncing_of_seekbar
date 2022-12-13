package com.canal.android.test.ui.player.model

data class MediaUi(
        val title: String?,
        val subtitle: String?,
        val urlImage: String?,
        val manifestUrl: String,
        val drmRequestUrl: String?,
        val encrypted: Boolean
)