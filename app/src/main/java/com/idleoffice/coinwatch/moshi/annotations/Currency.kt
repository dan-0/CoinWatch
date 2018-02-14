package com.idleoffice.coinwatch.moshi.annotations

import com.squareup.moshi.JsonQualifier

@Target(AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
annotation class Currency