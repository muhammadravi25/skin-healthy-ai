package com.ravi.skinhealthyai.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ravi.skinhealthyai.R
import com.ravi.skinhealthyai.data.model.CarouselItem

class CarouselAdapter(private val items: List<CarouselItem>) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val item = items[position]
        holder.imageViewBg.setImageResource(item.imageBackground)
        holder.titleTextView.text = item.title
    }

    override fun getItemCount(): Int = items.size

    inner class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewBg: ImageView = view.findViewById(R.id.imgBg)
        val titleTextView: TextView = view.findViewById(R.id.titleCarousel)
    }
}