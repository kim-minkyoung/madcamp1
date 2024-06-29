package com.example.myapplication.view.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.myapplication.databinding.FragmentTab3Binding
import com.google.android.material.bottomsheet.BottomSheetDialog

class Tab3Fragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTab3Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab3Binding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // BottomSheetDialogFragment 설정
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet?.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
        bottomSheet?.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
