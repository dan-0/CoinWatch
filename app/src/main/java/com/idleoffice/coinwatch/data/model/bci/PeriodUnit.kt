package com.idleoffice.coinwatch.data.model.bci

import com.idleoffice.coinwatch.data.annotations.Period

enum class PeriodUnit(val value : @Period String) {
    DAILY("daily"),
    MONTHLY("monthly"),
    ALL_TIME("alltime")
}