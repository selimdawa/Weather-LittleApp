package com.littleapp.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.littleapp.weather.R
import com.littleapp.weather.adatper.WeatherAdapter
import com.littleapp.weather.databinding.FragmentHoursBinding
import com.littleapp.weather.model.MainViewModel
import com.littleapp.weather.model.WeatherModel
import com.littleapp.weather.utils.DATA
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONArray

@AndroidEntryPoint
class HoursFragment : Fragment() {

    private var _binding: FragmentHoursBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observeData()
    }

    private fun initRcView() {
        adapter = WeatherAdapter(null)
        binding.rcViewHours.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HoursFragment.adapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.liveDataCurrent.collect { weatherItem ->
                    weatherItem?.let { adapter.submitList(getHoursList(it)) }
                }
            }
        }
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        if (wItem.hours.isEmpty() || (wItem.hours == DATA.EMPTY)) return emptyList()
        val hoursArray = JSONArray(wItem.hours)

        for (i in 0 until hoursArray.length()) {
            val hourObject = hoursArray.getJSONObject(i)
            val conditionObject = hourObject.getJSONObject("condition")
            val tempInt = hourObject.getString("temp_c").toFloat().toInt()

            list.add(
                WeatherModel(
                    city = wItem.city,
                    time = hourObject.getString("time"),
                    condition = conditionObject.getString("text"),
                    currentTemp = "$tempInt°C",
                    maxTemp = DATA.EMPTY,
                    minTemp = DATA.EMPTY,
                    imageUrl = conditionObject.getString("icon"),
                    hours = DATA.EMPTY,
                )
            )
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = HoursFragment()
    }
}