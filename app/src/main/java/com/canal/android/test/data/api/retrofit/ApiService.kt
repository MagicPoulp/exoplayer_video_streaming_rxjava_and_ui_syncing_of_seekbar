package com.canal.android.test.data.api.retrofit

import com.canal.android.test.data.api.model.MediaApi
import com.canal.android.test.data.api.model.ProgramApi
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun getPrograms(
            @Url urlPage: String
    ): Single<List<ProgramApi>>

    @GET
    fun getMedia(
            @Url urlPage: String
    ): Single<MediaApi>

}
