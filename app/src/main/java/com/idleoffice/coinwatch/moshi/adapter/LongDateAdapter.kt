package com.idleoffice.coinwatch.moshi.adapter

import com.idleoffice.coinwatch.moshi.annotations.LongDate
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.*


class LongDateAdapter {

    @ToJson
    fun toJson(date : @LongDate Date) : Long {
        return date.time
    }

    @FromJson fun fromJson(date : Long) : @LongDate Date {
        return Date(date)
    }
}