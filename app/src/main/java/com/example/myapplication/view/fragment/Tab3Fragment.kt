package com.example.myapplication.view.fragment

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTab3Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode

class Tab3Fragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var _binding: FragmentTab3Binding? = null
    private val binding get() = _binding!!
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 위치 권한 요청
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        // FusedLocationSource 초기화
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // MapView 초기화 및 설정
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // BottomSheetBehavior 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
            } else -> {
            // No location access granted
            Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // naverMap locationSource에 FusedLocationSource 적용
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}



//위치 함수 추가 전 코드
//package com.example.myapplication.view.fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil.setContentView
//import androidx.fragment.app.Fragment
//import com.example.myapplication.R
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.example.myapplication.databinding.FragmentTab3Binding
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.naver.maps.map.MapView
//import com.naver.maps.map.NaverMap
//import com.naver.maps.map.OnMapReadyCallback
//import com.naver.maps.map.util.FusedLocationSource
//
//class Tab3Fragment : Fragment(), OnMapReadyCallback {
//
//
//
//    private var _binding: FragmentTab3Binding? = null
//    private val binding get() = _binding!!
//    private lateinit var mapView: MapView
//    private lateinit var naverMap: NaverMap
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentTab3Binding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//        mapView = binding.mapView
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)
//
//
//    }
//
//
//
//
//    override fun onMapReady(naverMap: NaverMap) {
//        this.naverMap = naverMap
//        // NaverMap 객체가 준비되었을 때 실행할 작업들을 여기에 추가합니다.
//        // 예를 들어, 맵 설정, 마커 추가, 이벤트 핸들러 등을 초기화합니다.
//
//        // naverMap locationSource 에 FusedLocationSource 적용
//
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mapView.onStart()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mapView.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mapView.onPause()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        mapView.onSaveInstanceState(outState)
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mapView.onStop()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        mapView.onDestroy()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView.onLowMemory()
//    }
//}
