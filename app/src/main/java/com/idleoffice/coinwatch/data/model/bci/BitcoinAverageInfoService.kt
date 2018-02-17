package com.idleoffice.coinwatch.data.model.bci

import com.idleoffice.coinwatch.BuildConfig
import com.idleoffice.coinwatch.data.annotations.AuthToken
import com.idleoffice.coinwatch.data.annotations.Period
import com.idleoffice.coinwatch.data.annotations.Symbol
import com.idleoffice.coinwatch.toHexString
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


interface BitcoinAverageInfoService {
    companion object {
        internal const val BITCOIN_INFO_URL = "https://apiv2.bitcoinaverage.com/"
        fun generateKey() : @AuthToken String {
            val time = Date().time
            val payload = time.toString() + "." + BuildConfig.bitcoinaveragePublicKey
            val sha256Mac = Mac.getInstance("HmacSHA256")
            val secretKeySpec = SecretKeySpec(BuildConfig.bitcoinaveragePrivateKey.toByteArray(), "HmacSHA256")
            sha256Mac.init(secretKeySpec)
            val hashHex = sha256Mac.doFinal(payload.toByteArray()).toHexString()
            return payload + "." + hashHex
        }
    }

    @GET("indices/local/history/{symbol}")
    fun getHistoricalPrice(
            @Path("symbol") @Symbol symbol: String,
            @Query("period") @Period period : String
    ) : Observable<List<BitcoinAverageInfo>>

    @GET("indices/global/ticker/short?crypto=BTC&fiat=USD")
    fun getCurrentPrice(@Header("X-signature") @AuthToken token : String) :
            Observable<Map<String, BitcoinAverageCurrent>>
}