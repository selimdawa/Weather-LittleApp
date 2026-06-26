package com.littleapp.weather.Adatper

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.littleapp.weather.databinding.ItemWeatherBinding
import com.littleapp.weather.Model.WeatherModel
import com.squareup.picasso.Picasso

class WeatherAdapter(private val listener: Listener?) :
    ListAdapter<WeatherModel, WeatherAdapter.Holder>(Comparator()) {

    class Holder(
        private val binding: ItemWeatherBinding,
        private val listener: Listener?
    ) : RecyclerView.ViewHolder(binding.root) {

        private var itemTemp: WeatherModel? = null

        init {
            binding.root.setOnClickListener {
                itemTemp?.let { item -> listener?.onClick(item) }
            }
        }

        fun bind(item: WeatherModel) {
            itemTemp = item
            binding.tvDate.text = item.time
            binding.tvCondition.text = item.condition
            binding.tvTemp.text = item.currentTemp.ifEmpty { "${item.maxTemp}°C / ${item.minTemp}°C" }
            Picasso.get().load("https:${item.imageUrl}").into(binding.imgListIcon)
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem.time == newItem.time
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemWeatherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(item: WeatherModel)
    }
}