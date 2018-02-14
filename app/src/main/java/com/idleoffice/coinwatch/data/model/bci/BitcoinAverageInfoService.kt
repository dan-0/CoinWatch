package com.idleoffice.coinwatch.data.model.bci

import com.idleoffice.coinwatch.BuildConfig
import com.idleoffice.coinwatch.toHexString
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


interface BitcoinAverageInfoService {
    companion object {
        internal const val BITCOIN_INFO_URL = "https://apiv2.bitcoinaverage.com/"

        // TODO reimplement when this all one day fails. The server is currently not caring about authentication, and is actively denying it
        fun generateKey() : @AuthToken String {
            val time = Date().time
            var payload = time.toString() + "." + BuildConfig.bitcoinaveragePublicKey
            val sha256Mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(BuildConfig.bitcoinaveragePrivateKey.toByteArray(), "HmacSHA256")
            sha256Mac.init(secretKeySpec)
            val hashHex = sha256Mac.doFinal(payload.toByteArray()).toHexString()
            return payload + "." + hashHex
        }

        @Target(AnnotationTarget.TYPE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class AuthToken

        @Target(AnnotationTarget.TYPE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Symbol

        @Target(AnnotationTarget.TYPE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class Period

        enum class PeriodUnit(val value : @Period String) {
            DAILY("daily"),
            MONTHLY("monthly"),
            ALL_TIME("alltime")
        }

        enum class SymbolPair(val value : @Symbol String) {
            BTC_USD("BTCUSD")
        }
    }

    @GET("indices/local/history/{symbol}")
    fun getInfo(
            @Path("symbol") symbol: @Symbol String,
            @Query("period") period : @Period String

    ) : Call<List<BitcoinAverageInfo>>
}