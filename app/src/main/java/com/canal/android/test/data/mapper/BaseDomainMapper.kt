package com.canal.android.test.data.mapper

abstract class BaseDomainMapper<API, DOMAIN> {

    abstract fun toDomain(api: API): DOMAIN

    protected inline fun <reified T : Any> consolidateValue(value: T?, field: String? = null): T {
        if (value == null) {
            val fieldName = field ?: T::class.java.simpleName
            throw MandatoryFieldException("$fieldName is mandatory")
        }
        return value
    }

    protected class MandatoryFieldException(technicalMessage: String) :
            Exception(technicalMessage)
}