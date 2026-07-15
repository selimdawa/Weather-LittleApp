package com.littleapp.weather.di

import android.content.Context
import androidx.room.Room
import com.littleapp.weather.db.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, WeatherDatabase::class.java, "weather.db").build()

    @Provides
    fun provideDao(db: WeatherDatabase) = db.dao()
}