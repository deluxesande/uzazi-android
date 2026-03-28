package com.uzazi.app.core.utils

import java.util.*

object NightModeDetector {
    fun isNightTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour >= 22 || hour < 5
    }
}
