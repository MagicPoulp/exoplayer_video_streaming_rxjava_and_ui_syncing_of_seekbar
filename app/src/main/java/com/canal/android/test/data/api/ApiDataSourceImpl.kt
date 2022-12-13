package com.canal.android.test.data.api

import com.canal.android.test.data.ApiDataSource
import com.canal.android.test.data.api.model.MediaApi
import com.canal.android.test.data.api.model.ProgramApi
import com.canal.android.test.data.api.retrofit.ApiService
import io.reactivex.Single

class ApiDataSourceImpl(
    private val apiService: ApiService
) : ApiDataSource {

    override fun getPrograms(url: String): Single<List<ProgramApi>> {
        return apiService.getPrograms(url)
    }

    override fun getMedia(url: String): Single<MediaApi> {
        return apiService.getMedia(url)
    }
}