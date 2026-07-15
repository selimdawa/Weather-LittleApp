package com.littleapp.weather.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.littleapp.weather.R

fun Context.applyAppTheme() {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
    if (sharedPreferences.getString("color_option", "BASIC") == "BASIC") {
        setTheme(R.style.Theme_MainApp)
    }
}

inline fun <reified T : Activity> Context.launchActivity(
    noinline intentBuilder: Intent.() -> Unit = {},
) {
    val intent = Intent(this, T::class.java).apply(intentBuilder)
    startActivity(intent)
}

fun Fragment.isPermissionGranted(p: String): Boolean =
    ContextCompat.checkSelfPermission(requireContext(), p) == PackageManager.PERMISSION_GRANTED

fun View.startRotation() {
    val rotate = RotateAnimation(
        0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
    ).apply {
        duration = 1000
        repeatCount = Animation.INFINITE
        interpolator = LinearInterpolator()
    }
    startAnimation(rotate)
}

fun View.stopRotation() {
    clearAnimation()
}