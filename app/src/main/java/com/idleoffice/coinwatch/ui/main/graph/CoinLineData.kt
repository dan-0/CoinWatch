package com.idleoffice.coinwatch.ui.main.graph

import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.idleoffice.coinwatch.data.model.bci.BitcoinAverageInfo


class CoinLineData(val bcInfo : List<BitcoinAverageInfo>) {

    var size = bcInfo.size

    private fun priceToFloat(price : String?) : Float? {
        return price?.toFloatOrNull()
    }

    fun getLineData() : LineData {
        val entries = MutableList(size,
                init = {
                    val price: Float = priceToFloat(bcInfo[it].average) ?: 0f
                    return@MutableList Entry(it.toFloat(), price)
                })

        val ds = LineDataSet(entries, "Price in USD")
        return LineData(ds)
    }
}