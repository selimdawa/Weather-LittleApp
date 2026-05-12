package com.littleapp.weather.fragmennts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.littleapp.weather.Unit.DATA
import com.littleapp.weather.databinding.FragmentHoursBinding
import com.littleapp.weather.adatpers.WeatherAdapter
import com.littleapp.weather.models.MainViewModel
import com.littleapp.weather.models.WeatherModel
import org.json.JSONArray
import org.json.JSONObject

class HoursFragment : Fragment() {

    lateinit var binding: FragmentHoursBinding
    lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHoursBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            adapter.submitList(getHoursList(it))
        }
    }

    fun initRcView() = with(binding) {
        adapter = WeatherAdapter(null)
        rcViewHours.layoutManager = LinearLayoutManager(requireContext())
        rcViewHours.adapter = adapter
    }

    private fun getHoursList(wItem: WeatherModel): List<WeatherModel> {
        val list = ArrayList<WeatherModel>()
        val hoursArray = JSONArray(wItem.hours)
        for (i in 0 until hoursArray.length()) {
            val item = WeatherModel(
                wItem.city,
                (hoursArray[i] as JSONObject).getString("time"),
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("text"),
                (hoursArray[i] as JSONObject).getString("temp_c")
                    .toFloat().toInt().toString() + "°C", DATA.EMPTY, DATA.EMPTY,
                (hoursArray[i] as JSONObject).getJSONObject("condition")
                    .getString("icon"), DATA.EMPTY
            )
            list.add(item)
        }
        return list
    }

    companion object {
        @JvmStatic
        fun newInstance() = HoursFragment()
    }
}