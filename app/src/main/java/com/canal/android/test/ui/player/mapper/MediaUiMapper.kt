package com.canal.android.test.ui.player.mapper

import com.canal.android.test.domain.model.Media
import com.canal.android.test.ui.common.BaseUiMapper
import com.canal.android.test.ui.common.ImageHelper
import com.canal.android.test.ui.player.model.MediaUi

class MediaUiMapper : BaseUiMapper<Media, MediaUi>() {

    override fun toUi(domain: Media): MediaUi {
        return MediaUi(
                title = domain.title,
                subtitle = domain.subtitle,
                urlImage = ImageHelper.setImageResolution(domain.urlImage, 480, 270),
                manifestUrl = domain.manifestUrl,
                encrypted = domain.encrypted,
                drmRequestUrl = domain.drmRequestUrl
        )
    }
}