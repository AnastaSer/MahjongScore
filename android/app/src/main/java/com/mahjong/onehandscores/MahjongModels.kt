package com.mahjong.onehandscores

data class CalculationRequest(
    val playerWind: String,
    val vipWind: String,
    val combinationsOrTiles: List<String>,
    val flags: List<String>,
    val rules: String
)

data class CalculationResponse(
    val score: Int,
    val isMahjong: Boolean,
    val appliedFlags: List<String>
)