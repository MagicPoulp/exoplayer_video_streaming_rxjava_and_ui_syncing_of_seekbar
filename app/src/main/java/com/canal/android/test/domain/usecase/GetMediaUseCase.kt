package com.canal.android.test.domain.usecase

import com.canal.android.test.domain.Repository
import com.canal.android.test.domain.model.Media
import io.reactivex.Single

class GetMediaUseCase(
        private val repository: Repository
) : (String) -> Single<Media> {

    override operator fun invoke(url: String): Single<Media> {
        return repository.getMedia(url)
    }
}