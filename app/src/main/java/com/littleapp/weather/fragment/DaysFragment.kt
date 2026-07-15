package com.littleapp.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.littleapp.weather.R
import com.littleapp.weather.databinding.FragmentDaysBinding
import com.littleapp.weather.adatper.WeatherAdapter
import com.littleapp.weather.model.MainViewModel
import com.littleapp.weather.model.WeatherModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DaysFragment : Fragment(), WeatherAdapter.Listener {

    private var _binding: FragmentDaysBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

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