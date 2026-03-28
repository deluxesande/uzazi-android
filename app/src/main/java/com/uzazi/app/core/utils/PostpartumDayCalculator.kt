package com.uzazi.app.core.utils

import java.util.concurrent.TimeUnit

object PostpartumDayCalculator {
    fun calculate(deliveryDate: Long): Int {
        val diff = System.currentTimeMillis() - deliveryDate
        return (TimeUnit.MILLISECONDS.toDays(diff).toInt() + 1).coerceAtLeast(1)
    }
}
