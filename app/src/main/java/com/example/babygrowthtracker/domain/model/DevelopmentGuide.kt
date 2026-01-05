package com.example.babygrowthtracker.domain.model

import androidx.annotation.StringRes
import com.example.babygrowthtracker.R

data class MonthGuide(
    val month: Int,
    @StringRes val milestonesRes: Int,
    @StringRes val foodRes: Int,
    @StringRes val vaccineRes: Int
)

object GuideData {
    val list = listOf(
        MonthGuide(2, R.string.ms_2m, R.string.food_2m, R.string.vac_2m),
        MonthGuide(6, R.string.ms_6m, R.string.food_6m, R.string.vac_6m),
        MonthGuide(12, R.string.ms_12m, R.string.food_12m, R.string.vac_12m)
    )
}