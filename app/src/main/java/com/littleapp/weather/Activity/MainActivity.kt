package com.littleapp.weather.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.weather.Fragment.MainFragment
import com.littleapp.weather.R
import com.littleapp.weather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    var context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        //THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.toolbar.nameSpace.text = DATA.Weather

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, MainFragment.newInstance()).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}