package com.littleapp.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.littleapp.weather.model.WeatherModel

@Database(entities = [WeatherModel::class], version = 1, exportSchema = false)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun dao(): WeatherDao
}