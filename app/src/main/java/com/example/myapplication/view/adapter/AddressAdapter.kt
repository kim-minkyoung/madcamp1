package com.example.myapplication.view.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemAddressBinding

class AddressAdapter(
    private val addressList: MutableList<String>,
    private val emptyStateTextView: TextView
) : RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = addressList[position]
        holder.bind(address)
        holder.binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView.context, address, position)
        }
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    fun addAddress(address: String) {
        addressList.add(address)
        notifyDataSetChanged()
        updateEmptyState()
    }

    private fun deleteAddress(position: Int) {
        addressList.removeAt(position)
        notifyItemRemoved(position)
        updateEmptyState()
    }

    private fun updateEmptyState() {
        if (addressList.isEmpty()) {
            emptyStateTextView.visibility = View.VISIBLE
        } else {
            emptyStateTextView.visibility = View.GONE
        }
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
