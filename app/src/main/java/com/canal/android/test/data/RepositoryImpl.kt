package com.canal.android.test.data

import com.canal.android.test.data.mapper.MediaMapper
import com.canal.android.test.data.mapper.ProgramMapper
import com.canal.android.test.domain.Repository
import com.canal.android.test.domain.model.Media
import com.canal.android.test.domain.model.Program
import io.reactivex.Single

class RepositoryImpl(
        private val apiDataSource: ApiDataSource,
        private val programMapper: ProgramMapper,
        private val mediaMapper: MediaMapper
) : Repository {

    override fun getPrograms(url: String): Single<List<Program>> {
        return apiDataSource.getPrograms(url)
                .map { programsApi ->
                    programsApi.map { programApi ->
                        programMapper.toDomain(programApi)
                    }
                }
    }

    override fun getMedia(url: String): Single<Media> =
            apiDataSource.getMedia(url)
                    .map {
                        mediaMapper.toDomain(it)
                    }
}