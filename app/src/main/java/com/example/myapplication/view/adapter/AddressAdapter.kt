package com.example.myapplication.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAddressBinding

class AddressAdapter(private val addressList: MutableList<String>, private val emptyStateTextView: TextView) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.bind(address)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun addAddress(address: String) {
        addressList.add(address)
        notifyDataSetChanged()
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (addressList.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            emptyStateTextView.visibility = View.GONE
        }
    }

    class AddressViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: String) {
            binding.address = address
            binding.executePendingBindings()
        }
    }
}
