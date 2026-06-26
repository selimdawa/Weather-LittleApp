package com.littleapp.weather.Fragment

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
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.tabs.TabLayoutMediator
import com.littleapp.weather.DialogManager
import com.littleapp.weather.Unit.DATA
import com.littleapp.weather.Adatper.vpAdapter
import com.littleapp.weather.databinding.FragmentMainBinding
import com.littleapp.weather.isPermissionGranted
import com.littleapp.weather.Model.MainViewModel
import com.littleapp.weather.Model.WeatherModel
import com.squareup.picasso.Picasso
import org.json.JSONObject
import timber.log.Timber

class MainFragment : Fragment() {

    private lateinit var pLauncher: ActivityResultLauncher<String>
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val model: MainViewModel by activityViewModels()
    private val fList = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
    private val tList = listOf("Hours", "Days")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            Toast.makeText(requireContext(), "Permission is: $isGranted", Toast.LENGTH_LONG).show()
            if (isGranted) checkLocation()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        init()
        updateCurrentCard()
        checkLocation()
    }

    override fun onResume() {
        super.onResume()
        checkLocation()
    }

    private fun updateCurrentCard() {
        model.liveDataCurrent.observe(viewLifecycleOwner) { weather ->
            val maxMin = "${weather.maxTemp}°C / ${weather.minTemp}°C"
            binding.tvCity.text = weather.city
            binding.tvData.text = weather.time
            binding.tvCondition.text = weather.condition
            binding.tvCurrentTemp.text = weather.currentTemp.ifEmpty { maxMin }
            binding.tvMaxMin.text = if (weather.currentTemp.isEmpty()) DATA.EMPTY else maxMin
            Picasso.get().load("https:${weather.imageUrl}").into(binding.imgIcon)
        }
    }

    private fun getWeatherRequest(city: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "${DATA.BASE_URL_WEATHER}${DATA.API_KEY_WEATHER}&q=$city&days=3&aqi=no&alerts=no"

        val request = StringRequest(Request.Method.GET, url, { result ->
            parseWeatherData(result)
        }, { error ->
            Timber.d(error)
        })
        queue.add(request)
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

            val item = WeatherModel(
                city = name,
                time = day.getString("date"),
                condition = condition.getString("text"),
                currentTemp = DATA.EMPTY,
                maxTemp = dayInfo.getString("maxtemp_c").toFloat().toInt().toString(),
                minTemp = dayInfo.getString("mintemp_c").toFloat().toInt().toString(),
                imageUrl = condition.getString("icon"),
                hours = day.getJSONArray("hour").toString()
            )
            list.add(item)
        }
        model.liveDataList.value = list
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
        model.liveDataCurrent.value = item
    }

    private fun init() {
        val adapter = vpAdapter(activity as FragmentActivity, fList)
        binding.vp.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.vp) { tab, pos ->
            tab.text = tList[pos]
        }.attach()

        binding.ibSync.setOnClickListener { checkLocation() }
        binding.ibSearch.setOnClickListener {
            DialogManager.searchByNameDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    name?.let { city -> getWeatherRequest(city) }
                }
            })
        }
    }

    private fun checkLocation() {
        if (isLocationEnabled()) {
            getLocation()
        } else {
            DialogManager.locationSettingsDialog(requireContext(), object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }

    private fun isLocationEnabled(): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun getLocation() {
        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        LocationServices.getFusedLocationProviderClient(requireContext())
            .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener { task ->
                task.result?.let { location ->
                    getWeatherRequest("${location.latitude},${location.longitude}")
                }
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

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}