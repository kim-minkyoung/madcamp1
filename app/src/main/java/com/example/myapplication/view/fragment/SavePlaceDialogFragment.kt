package com.example.myapplication.view.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class SavePlaceDialogFragment : DialogFragment() {

    private var savePlaceListener: SavePlaceListener? = null
    private var placeName: String? = null
    private var address: String? = null

    interface SavePlaceListener {
        fun onSavePlaceClicked()
        fun onCancelClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SavePlaceListener) {
            savePlaceListener = context
        } else {
            throw RuntimeException("$context must implement SavePlaceListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            placeName = it.getString(ARG_PLACE_NAME)
            address = it.getString(ARG_ADDRESS)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("저장 여부")
                .setMessage("이 장소를 나만의 장소로 저장하시겠습니까?")
                .setPositiveButton("예") { _, _ ->
                    savePlaceListener?.onSavePlaceClicked()
                }
                .setNegativeButton("아니요") { _, _ ->
                    savePlaceListener?.onCancelClicked()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        private const val ARG_PLACE_NAME = "placeName"
        private const val ARG_ADDRESS = "address"

        fun newInstance(placeName: String?, address: String?): SavePlaceDialogFragment {
            return SavePlaceDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PLACE_NAME, placeName)
                    putString(ARG_ADDRESS, address)
                }
            }
        }
    }
}
