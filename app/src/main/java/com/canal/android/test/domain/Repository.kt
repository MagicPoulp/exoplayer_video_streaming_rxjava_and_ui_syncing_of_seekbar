package com.canal.android.test.domain

import com.canal.android.test.domain.model.Program
import io.reactivex.Single

interface Repository {

    fun getPrograms(url: String): Single<List<Program>>
}