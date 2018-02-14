package com.idleoffice.coinwatch.moshi.adapter

import com.idleoffice.coinwatch.moshi.annotations.Currency
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.math.BigDecimal


class CurrencyAdapter {
    @ToJson fun toJson(currency : @Currency Double) : Double {
        return currency
    }

    @FromJson fun fromJson(currency: @Currency Double) : Double {
        return BigDecimal(currency).setScale(2, BigDecimal.ROUND_HALF_DOWN).toDouble()
    }
}