package com.canal.android.test.data

import com.canal.android.test.data.api.model.MediaApi
import com.canal.android.test.data.api.model.ProgramApi
import io.reactivex.Single

interface ApiDataSource {

    fun getPrograms(url: String): Single<List<ProgramApi>>

    fun getMedia(url: String): Single<MediaApi>
}