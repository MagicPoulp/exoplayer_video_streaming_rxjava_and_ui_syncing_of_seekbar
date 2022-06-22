package com.canal.android.test.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed class NavigateTo : Parcelable {

    @Parcelize
    data class QuickTime(
        val urlMedias: String
    ) : NavigateTo()

    @Parcelize
    data class DetailPage(
        val urlPage: String
    ) : NavigateTo()
}