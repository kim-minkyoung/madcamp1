package com.example.myapplication.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAddressBinding
import com.example.myapplication.model.data.Address
import com.example.myapplication.model.viewModel.MapViewModel

class AddressAdapter(
    private val context: Context,
    private val addressList: MutableList<Address>,
    private val emptyStateTextView: TextView,
    private val viewModel: MapViewModel
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    val currentList: List<Address>
        get() = addressList
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

        holder.binding.addressTextView.setOnClickListener {
            viewModel.onAddressClicked(address)
        }
        holder.binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(context, address, position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun addAddress(address: Address) {
        addressList.add(address)
        saveAddresses()
        notifyDataSetChanged()
        updateEmptyState()
    }

    fun deleteAddress(position: Int) {
        if (addressList.isNotEmpty()) {
            if (addressList.size == 1) {
                // 마지막 주소를 삭제하는 경우
                addressList.removeAt(position)
                notifyItemRemoved(position)
            } else {
                // 중간 주소를 삭제하는 경우
                addressList.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, addressList.size)
            }
        }
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
        val addressStrings = addressList.map { address ->
            "${address.specificAddress ?: ""}|${address.roadAddress}|${address.latitude}|${address.longitude}"
        }
        editor.putStringSet("addresses", addressStrings.toSet())
        editor.apply()
    }

    private fun loadAddresses() {
        val savedAddresses = sharedPreferences.getStringSet("addresses", setOf())
        addressList.clear()
        savedAddresses?.forEach { addressString ->
            val parts = addressString.split('|')
            if (parts.size == 4) {
                val specificAddress = if (parts[0].isNotEmpty()) parts[0] else null
                val roadAddress = parts[1]
                val latitude = parts[2].toDoubleOrNull()
                val longitude = parts[3].toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    addressList.add(Address(specificAddress, roadAddress, latitude, longitude))
                }
            }
        }
        notifyDataSetChanged()
        updateEmptyState()
    }

    private fun showDeleteConfirmationDialog(context: Context, address: Address, position: Int) {
        AlertDialog.Builder(context)
            .setMessage("\"${address.specificAddress ?: address.roadAddress}\"을(를) 내 저장소에서 정말로 삭제하시겠어요?")
            .setPositiveButton("예") { dialog, which ->
                deleteAddress(position)
            }
            .setNegativeButton("아니요", null)
            .show()
    }

    class AddressViewHolder(val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            binding.address = address.specificAddress ?: address.roadAddress
            binding.executePendingBindings()
        }
    }
}