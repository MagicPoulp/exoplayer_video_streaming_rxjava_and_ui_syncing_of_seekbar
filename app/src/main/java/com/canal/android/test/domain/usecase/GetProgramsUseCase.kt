package com.canal.android.test.domain.usecase

import com.canal.android.test.domain.Repository
import com.canal.android.test.domain.model.Program
import io.reactivex.Single

class GetProgramsUseCase(
        private val repository: Repository
) : () -> Single<List<Program>> {

    override operator fun invoke(): Single<List<Program>> {
        return repository.getPrograms(URL)
    }

    companion object {
        private const val URL = "https://static.canal-plus.net/exoplayer/api/programmes.json"
    }
}