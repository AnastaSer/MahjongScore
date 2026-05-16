package com.mahjong.onehandscores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MahjongViewModel : ViewModel() {
    fun calculate(
        request: CalculationRequest,
        onResult: (CalculationResponse?, String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.calculate(request)
                onResult(response, null)
            } catch (e: IOException) {
                onResult(null, "Network error: ${e.message}")
            } catch (e: HttpException) {
                onResult(null, "Server error: ${e.response()?.errorBody()?.string()}")
            }
        }
    }
}