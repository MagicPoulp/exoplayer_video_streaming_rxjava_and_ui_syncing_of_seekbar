package com.canal.android.test.data.api.model

data class MediaApi(
        val detail: DetailApi?
)

data class DetailApi(
        val informations: InformationsApi?,
)

data class InformationsApi(
        val contentID: String?,
        val title: String?,
        val subtitle: String?,
        val URLImage: String?,
        val URLLogoChannel: String?,
        val videoURLs: List<VideoUrlApi>?
)

data class VideoUrlApi(
        val videoURL: String?,
        val encryption: String?,
        val drmURL: String?
)
