package com.canal.android.test.exoplayer.exception

sealed class ExoplayerException(message: String, cause: Throwable?) : RuntimeException(message, cause)

class CustomExoplayerException(message: String, cause: Throwable?) : ExoplayerException(message, cause)

data class ManifestTypeUnknown(override val message: String) : ExoplayerException(message, null)
