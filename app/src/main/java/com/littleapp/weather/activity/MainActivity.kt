package com.littleapp.weather.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.weather.databinding.ActivityMainBinding
import com.littleapp.weather.utils.THEME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(this)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}