package com.littleapp.weather.fragment

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import coil.load
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.littleapp.weather.R
import com.littleapp.weather.adatper.ViewPagerAdapter
import com.littleapp.weather.databinding.FragmentMainBinding
import com.littleapp.weather.model.MainViewModel
import com.littleapp.weather.model.WeatherModel
import com.littleapp.weather.utils.DATA
import com.littleapp.weather.utils.DialogManager
import com.littleapp.weather.utils.isPermissionGranted
import com.littleapp.weather.utils.startRotation
import com.littleapp.weather.utils.stopRotation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONObject
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val model: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)
    private val fList by lazy { listOf(HoursFragment.newInstance(), DaysFragment.newInstance()) }
    private val tList by lazy { listOf(getString(R.string.hours), getString(R.string.days)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) checkLocation() else Toast.makeText(
                    requireContext(), "Permission denied", Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observeData()
        checkPermission()
        binding.ibSync.startRotation()
        checkLocation()
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    model.savedWeather.collect { weather ->
                        weather?.let { model.updateCurrent(it) }
                    }
                }
                launch {
                    model.liveDataCurrent.collect { weather ->
                        weather?.let { updateUI(it) }
                    }
                }
            }
        }
    }

    private fun updateUI(weather: WeatherModel) {
        val maxMin = "${weather.maxTemp}°C / ${weather.minTemp}°C"
        with(binding) {
            tvCity.text = weather.city
            tvData.text = weather.time
            tvCondition.text = weather.condition
            tvCurrentTemp.text = weather.currentTemp.ifEmpty { maxMin }
            tvMaxMin.text = if (weather.currentTemp.isEmpty()) DATA.EMPTY else maxMin
            imgIcon.load("https:${weather.imageUrl}")
        }
    }

    private fun getWeatherRequest(city: String) {
        val url = "${DATA.BASE_URL_WEATHER}${DATA.API_KEY_WEATHER}&q=$city&days=3&aqi=no&alerts=no"
        val request = StringRequest(
            Request.Method.GET,
            url,
            { result ->
                binding.ibSync.stopRotation()
                parseWeatherData(result)
            },
            { error ->
                binding.ibSync.stopRotation()
                Timber.d(error)
            },
        )
        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val list = parseDays(mainObject)
        parseCurrentDate(mainObject, list)
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")

        for (i in 0 until daysArray.length()) {
            val day = daysArray.getJSONObject(i)
            val dayInfo = day.getJSONObject("day")
            val condition = dayInfo.getJSONObject("condition")

            list.add(
                WeatherModel(
                    city = name,
                    time = day.getString("date"),
                    condition = condition.getString("text"),
                    currentTemp = DATA.EMPTY,
                    maxTemp = dayInfo.getString("maxtemp_c").toFloat().toInt().toString(),
                    minTemp = dayInfo.getString("mintemp_c").toFloat().toInt().toString(),
                    imageUrl = condition.getString("icon"),
                    hours = day.getJSONArray("hour").toString(),
                )
            )
        }
        model.updateList(list)
        return list
    }

    private fun parseCurrentDate(mainObject: JSONObject, weatherItem: List<WeatherModel>) {
        if (weatherItem.isEmpty()) return
        val current = mainObject.getJSONObject("current")
        val condition = current.getJSONObject("condition")
        val firstDay = weatherItem[0]

        val item = WeatherModel(
            city = mainObject.getJSONObject("location").getString("name"),
            time = current.getString("last_updated"),
            condition = condition.getString("text"),
            currentTemp = "${current.getString("temp_c")}°C",
            maxTemp = firstDay.maxTemp,
            minTemp = firstDay.minTemp,
            imageUrl = condition.getString("icon"),
            hours = firstDay.hours
        )
        model.updateCurrent(item)
        model.saveWeather(item)
    }

    private fun init() {
        binding.vp.adapter = ViewPagerAdapter(requireActivity(), fList)
        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, pos ->
            tab.text = tList[pos]
        }.attach()

        binding.ibSync.setOnClickListener {
            binding.ibSync.startRotation()
            checkLocation()
        }
        binding.ibSearch.setOnClickListener {
            DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    name?.let {
                        binding.ibSync.startRotation()
                        getWeatherRequest(it)
                    }
                }
            })
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            binding.ibSync.stopRotation()
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            binding.ibSync.stopRotation()
            return
        }
        LocationServices.getFusedLocationProviderClient(requireContext())
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
            .addOnCompleteListener { task ->
                task.result?.let {
                    getWeatherRequest("${it.latitude},${it.longitude}")
                } ?: binding.ibSync.stopRotation()
            }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
            pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}