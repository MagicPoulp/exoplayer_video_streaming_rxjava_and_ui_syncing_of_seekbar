package com.canal.android.test.ui.common

import com.canal.android.test.domain.model.NavigateTo

interface UiNavigator {
    fun displayDetailPage(navigateTo: NavigateTo.DetailPage)
    fun displayPlayer(navigateTo: NavigateTo.QuickTime)
}