package com.canal.android.test.data.mapper

import com.canal.android.test.data.api.model.MediaApi
import com.canal.android.test.domain.model.Media

class MediaMapper : BaseDomainMapper<MediaApi, Media>() {

    override fun toDomain(api: MediaApi): Media {
        val validVideo = api.detail?.informations?.videoURLs?.firstOrNull {
            it.videoURL != null &&
                    (if (it.encryption.isEncrypted()) {
                        it.drmURL.isNullOrBlank().not()
                    } else {
                        true
                    })
        }
        return Media(
                title = api.detail?.informations?.title,
                subtitle = api.detail?.informations?.subtitle,
                urlImage = api.detail?.informations?.URLImage,
                manifestUrl = consolidateValue(validVideo?.videoURL, "manifestUrl"),
                encrypted = consolidateValue(validVideo?.encryption?.isEncrypted(), "encrypted"),
                drmRequestUrl = validVideo?.drmURL
        )
    }

    private fun String?.isEncrypted(): Boolean =
            this != null && this.contentEquals(CLEAR_ENCRYPT, true).not()

    companion object {
        private const val CLEAR_ENCRYPT = "clear"
    }
}