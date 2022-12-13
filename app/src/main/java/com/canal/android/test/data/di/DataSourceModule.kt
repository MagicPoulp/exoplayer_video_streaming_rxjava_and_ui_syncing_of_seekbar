package com.canal.android.test.data.di

import com.canal.android.test.data.ApiDataSource
import com.canal.android.test.data.RepositoryImpl
import com.canal.android.test.data.api.ApiDataSourceImpl
import com.canal.android.test.data.mapper.OnClickMapper
import com.canal.android.test.data.mapper.ProgramMapper
import com.canal.android.test.data.api.retrofit.ApiRetrofitFactory
import com.canal.android.test.data.mapper.MediaMapper
import com.canal.android.test.domain.Repository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val apiRetrofitModule = module {
    single {
        ApiRetrofitFactory(androidContext()).buildApiService(
                ApiRetrofitFactory(androidContext()).buildApiRetrofit()
        )
    }
}

val apiDataSourceModule = module {
    single {
        ApiDataSourceImpl(
                apiService = get()
        ) as ApiDataSource
    }
}

val apiMapperModule = module {
    single {
        OnClickMapper()
    }
    single {
        ProgramMapper(onClickMapper = get())
    }
    single {
        MediaMapper()
    }
}

val repositoryModule = module {
    single {
        RepositoryImpl(
                apiDataSource = get(),
                programMapper = get(),
                mediaMapper = get()
        ) as Repository
    }
}

val koinDataSourceModules = listOf(
        apiRetrofitModule,
        apiDataSourceModule,
        apiMapperModule,
        repositoryModule
)