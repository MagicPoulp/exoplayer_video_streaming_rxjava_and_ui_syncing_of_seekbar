package com.canal.android.test.domain.model

data class Media(
        val title: String?,
        val subtitle: String?,
        val urlImage: String?,
        val manifestUrl: String,
        val drmRequestUrl: String?,
        val encrypted: Boolean
)