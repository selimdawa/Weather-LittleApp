package com.littleapp.weather.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.littleapp.weather.db.WeatherDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val dao: WeatherDao) : ViewModel() {

    private val _liveDataList = MutableStateFlow<List<WeatherModel>>(emptyList())
    val liveDataList: StateFlow<List<WeatherModel>> = _liveDataList.asStateFlow()

    private val _liveDataCurrent = MutableStateFlow<WeatherModel?>(null)
    val liveDataCurrent: StateFlow<WeatherModel?> = _liveDataCurrent.asStateFlow()

    val savedWeather: StateFlow<WeatherModel?> = dao.getLatestWeather()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun updateCurrent(weather: WeatherModel) {
        _liveDataCurrent.value = weather
    }

    fun updateList(list: List<WeatherModel>) {
        _liveDataList.value = list
    }

    fun saveWeather(weather: WeatherModel) = viewModelScope.launch {
        dao.insertWeather(weather)
    }
}