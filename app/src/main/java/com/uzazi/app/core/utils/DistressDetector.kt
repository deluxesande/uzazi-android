package com.uzazi.app.core.utils

import java.util.Locale

object DistressDetector {
    enum class DistressLevel { NONE, MILD, SEVERE }

    private val severeKeywords = listOf(
        "want to disappear", "hurt myself", "don't want to be here", 
        "end it", "can't go on", "no reason to live", "wish i was dead"
    )

    private val mildKeywords = listOf(
        "can't cope", "so alone", "hopeless", "worthless", 
        "failing", "bad mother", "hate myself", "falling apart"
    )

    fun detect(text: String): DistressLevel {
        val lowerText = text.lowercase(Locale.getDefault())
        
        if (severeKeywords.any { lowerText.contains(it) }) {
            return DistressLevel.SEVERE
        }
        
        if (mildKeywords.any { lowerText.contains(it) }) {
            return DistressLevel.MILD
        }
        
        return DistressLevel.NONE
    }
}
