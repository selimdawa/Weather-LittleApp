package com.littleapp.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.littleapp.weather.utils.DATA
import com.littleapp.weather.databinding.FragmentHoursBinding
import com.littleapp.weather.adatper.WeatherAdapter
import com.littleapp.weather.model.MainViewModel
import com.littleapp.weather.model.WeatherModel
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class HoursFragment : Fragment() {

    private var _binding: FragmentHoursBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WeatherAdapter(null)
        binding.rcViewHours.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HoursFragment.adapter
        }

        model.liveDataCurrent.observe(viewLifecycleOwner) { weatherItem ->
            adapter.submitList(getHoursList(weatherItem))
        }
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val hoursArray = JSONArray(wItem.hours)

        for (i in 0 until hoursArray.length()) {
            val hourObject = hoursArray.getJSONObject(i)
            val conditionObject = hourObject.getJSONObject("condition")
            val tempInt = hourObject.getString("temp_c").toFloat().toInt()

            val item = WeatherModel(
                city = wItem.city,
                time = hourObject.getString("time"),
                condition = conditionObject.getString("text"),
                currentTemp = "$tempInt°C",
                maxTemp = DATA.EMPTY,
                minTemp = DATA.EMPTY,
                imageUrl = conditionObject.getString("icon"),
                hours = DATA.EMPTY
            )
            list.add(item)
        }
        return list
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}