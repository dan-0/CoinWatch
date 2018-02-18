package com.idleoffice.coinwatch

import junit.framework.TestCase.assertEquals
import org.junit.Test

class UtilKtTest {
    @Test
    fun toHexString() {
        val b = ByteArray(5)
        b[0] = 0x01
        b[1] = 0x23
        b[2] = 0x45
        b[3] = 0xFF.toByte()
        b[4] = 0xFB.toByte()

        assertEquals("012345fffb", b.toHexString())
    }

}