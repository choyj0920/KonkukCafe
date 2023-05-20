package com.kounkukcafe.kounkukcafe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kounkukcafe.kounkukcafe.apiutil.Cafe
import com.kounkukcafe.kounkukcafe.databinding.ItemCafeBinding

class CafeAdapter(private val cafes: List<Cafe>,val cafeListActivity: CafeListActivity) : RecyclerView.Adapter<CafeAdapter.CafeViewHolder>() {

    inner class CafeViewHolder(private val binding: ItemCafeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(index:Int) {
            val cafe=cafes[index]
            binding.cafeName.text = cafe.name
            binding.cafeRating.text = cafe.rating
            binding.cafePhone.text = cafe.phone
            binding.cafeAddress.text = cafe.adr
            binding.cafeText.text = cafe.desc
            binding.layout.setOnClickListener {
                this@CafeAdapter.cafeListActivity.mapFocusCafe(index)
            }

            // Add more bindings for other views
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CafeViewHolder {
        val binding = ItemCafeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CafeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CafeViewHolder, position: Int) {
        val cafe = cafes[position]
        holder.bind(position)


    }

    override fun getItemCount() = cafes.size
}
