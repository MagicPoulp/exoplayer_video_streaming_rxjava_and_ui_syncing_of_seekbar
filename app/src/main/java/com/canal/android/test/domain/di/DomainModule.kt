package com.canal.android.test.domain.di

import com.canal.android.test.domain.usecase.GetMediaUseCase
import com.canal.android.test.domain.usecase.GetProgramsUseCase
import org.koin.dsl.module

val domainUseCaseModule = module {
    factory {
        GetProgramsUseCase(
                repository = get())
    }
    factory {
        GetMediaUseCase(
                repository = get()
        )
    }
}

val koinDomainModules = listOf(
        domainUseCaseModule
)