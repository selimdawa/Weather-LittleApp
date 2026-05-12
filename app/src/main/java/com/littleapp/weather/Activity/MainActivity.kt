package com.littleapp.weather.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.littleapp.weather.R
import com.littleapp.weather.Unit.THEME
import com.littleapp.weather.databinding.ActivityMainBinding
import com.littleapp.weather.fragmennts.MainFragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        //THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //binding.toolbar.nameSpace.text = DATA.Weather

        supportFragmentManager.beginTransaction()
            .replace(R.id.placeholder, MainFragment.newInstance()).commit()
    }
}