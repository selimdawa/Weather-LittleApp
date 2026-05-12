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

    lateinit var binding: FragmentDaysBinding
    lateinit var adapter: WeatherAdapter
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        model.liveDataList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }

    private fun init() = with(binding) {
        rcView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WeatherAdapter(this@DaysFragment)
        rcView.adapter = adapter
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(item: WeatherModel) {
        model.liveDataCurrent.value = item
    }
}
