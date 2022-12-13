package com.canal.android.test.ui.di

import androidx.fragment.app.FragmentActivity
import com.canal.android.test.domain.model.NavigateTo
import com.canal.android.test.ui.common.FragmentNavigator
import com.canal.android.test.ui.common.UiNavigator
import com.canal.android.test.ui.detailpage.model.DetailPageViewModel
import com.canal.android.test.ui.player.PlayerViewModel
import com.canal.android.test.ui.player.mapper.MediaUiMapper
import com.canal.android.test.ui.programs.ProgramsViewModel
import com.canal.android.test.ui.programs.mapper.ProgramsUiMapper
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiViewModelsModule = module {
    single {
        ProgramsUiMapper()
    }
    viewModel {
        ProgramsViewModel(
                getProgramsUseCase = get(),
                programsUiMapper = get()
        )
    }
    single {
        MediaUiMapper()
    }
    viewModel { (navigateTo: NavigateTo.QuickTime) ->
        PlayerViewModel(
                navigateTo = navigateTo,
                getMediaUseCase = get(),
                mediaUiMapper = get()
        )
    }

    viewModel { (navigateTo: NavigateTo.DetailPage) ->
        DetailPageViewModel(
                navigateTo = navigateTo
        )
    }
}

val uiNavigatorModule = module {
    single<UiNavigator> {
        FragmentNavigator()
    }
}

val koinUiModules = listOf(
        uiViewModelsModule,
        uiNavigatorModule
)
