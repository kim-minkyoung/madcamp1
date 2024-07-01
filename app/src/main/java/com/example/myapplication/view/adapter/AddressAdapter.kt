package com.example.myapplication.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAddressBinding

class AddressAdapter(
    private val context: Context,
    private val addressList: MutableList<String>,
    private val emptyStateTextView: TextView
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    // SharedPreferences instance
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("address_prefs", Context.MODE_PRIVATE)
    }

    init {
        // Load saved addresses from SharedPreferences when adapter is initialized
        loadAddresses()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.bind(address)

        // Set click listener for delete button
        holder.binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(context, address, position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun addAddress(address: String) {
        addressList.add(address)
        saveAddresses()
        notifyDataSetChanged()
        updateEmptyState()
    }

    private fun deleteAddress(position: Int) {
        addressList.removeAt(position)
        saveAddresses()
        notifyItemRemoved(position)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (addressList.isEmpty()) {
            emptyStateTextView.visibility = TextView.VISIBLE
        } else {
            emptyStateTextView.visibility = TextView.GONE
        }
    }

    private fun saveAddresses() {
        val editor = sharedPreferences.edit()
        editor.putStringSet("addresses", addressList.toSet())
        editor.apply()
    }

    private fun loadAddresses() {
        val savedAddresses = sharedPreferences.getStringSet("addresses", setOf())
        addressList.clear()
        addressList.addAll(savedAddresses ?: emptySet())
        notifyDataSetChanged()
        updateEmptyState()
    }

    private fun showDeleteConfirmationDialog(context: Context, address: String, position: Int) {
        AlertDialog.Builder(context)
            .setMessage("\"$address\"을(를) 내 저장소에서 정말로 삭제하시겠어요?")
            .setPositiveButton("예") { dialog, which ->
                deleteAddress(position)
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: String) {
            binding.address = address
            binding.executePendingBindings()
        }
    }
}