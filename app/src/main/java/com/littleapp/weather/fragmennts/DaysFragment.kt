package com.littleapp.weather.fragmennts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.littleapp.weather.databinding.FragmentDaysBinding
import com.littleapp.weather.adatpers.WeatherAdapter
import com.littleapp.weather.models.MainViewModel
import com.littleapp.weather.models.WeatherModel

class DaysFragment : Fragment(), WeatherAdapter.Listener {

    private var _binding: FragmentDaysBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WeatherAdapter(this)
        binding.rcView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DaysFragment.adapter
        }

        model.liveDataList.observe(viewLifecycleOwner) { weatherList ->
            adapter.submitList(weatherList)
        }
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }
}