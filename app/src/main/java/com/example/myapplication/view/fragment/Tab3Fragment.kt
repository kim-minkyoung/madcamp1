package com.example.myapplication.view.fragment

import android.Manifest
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTab3Binding
import com.example.myapplication.model.viewModel.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import java.util.*
import com.example.myapplication.model.interfaces.SavePlaceListener
import com.example.myapplication.view.fragment.SavePlaceDialogFragment

class Tab3Fragment : Fragment(), OnMapReadyCallback{

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    private var _binding: FragmentTab3Binding? = null
    private val binding get() = _binding!!

    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
    private var placesClient: PlacesClient? = null
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private val marker = Marker()

    // ViewModel 인스턴스 생성
    private val viewModel: MapViewModel by viewModels()

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

        // FusedLocationSource 초기화 (현위치 가져오기)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        // MapView 초기화
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // BottomSheetBehavior 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)

        // Places API 초기화
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "YOUR_API_KEY")  // TODO: 실제 API 키로 교체
        }
        placesClient = Places.createClient(requireContext())

        // AutoCompleteTextView 설정: 주소 자동 검색을 위한 어댑터를 초기화
        autoCompleteTextView = binding.address as AutoCompleteTextView
        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(autoCompleteAdapter)

        // AutoCompleteTextView에서 키 입력을 감지하여 자동 완성 요청을 수행
        autoCompleteTextView.setOnKeyListener { _, _, _ ->
            val query = autoCompleteTextView.text.toString()
            if (query.isNotEmpty()) {
                fetchAutocompletePredictions(query)
            }
            false
        }

        // 검색 버튼 클릭 리스너를 설정
        binding.submit.setOnClickListener {
            val address = binding.address.text.toString()
            if (address.isNotEmpty()) {
                // ViewModel을 통해 주소 검색을 요청합니다.
                viewModel.searchAddress(address)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // ViewModel의 LiveData(searchAddress)를 관찰하여 주소 데이터를 업데이트
        viewModel.addressData.observe(viewLifecycleOwner, { data ->
            val (roadAddress, latitude, longitude) = data
            updateBottomSheet(roadAddress, latitude, longitude)
            moveCameraToLocation(latitude, longitude)
        })

        // ViewModel의 LiveData를 관찰하여 에러 메시지를 표시
        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })
    }

    // 자동완성 관리 함수
    private fun fetchAutocompletePredictions(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            val suggestionList = predictions.map { it.getFullText(null).toString() }
            autoCompleteAdapter.clear()
            autoCompleteAdapter.addAll(suggestionList)
            autoCompleteAdapter.notifyDataSetChanged()
        }?.addOnFailureListener { exception ->
            exception.printStackTrace()
        }
    }

    // 위치 권한 요청에 대한 결과를 처리하는 콜백
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // 정확한 위치 접근 권한이 허용됨
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // 대략적인 위치 접근 권한이 허용됨
            }
            else -> {
                // 위치 접근 권한이 거부됨
                Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    private fun updateBottomSheet(address: String, latitude: Double, longitude: Double) {
        binding.persistentBottomSheet.findViewById<TextView>(R.id.address_text_view).text = address
        binding.persistentBottomSheet.findViewById<TextView>(R.id.latitude_text_view).text =
            "위도: $latitude"
        binding.persistentBottomSheet.findViewById<TextView>(R.id.longitude_text_view).text =
            "경도: $longitude"
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        // naverMap에 FusedLocationSource를 적용합니다.
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        // 지도 클릭 이벤트 리스너를 설정합니다.
        naverMap.setOnMapClickListener { point, coord ->
            marker(coord.latitude, coord.longitude)
        }
    }

    private fun marker(latitude: Double, longitude: Double) {
        marker.position = LatLng(latitude, longitude)
        marker.map = naverMap

        getAddress(latitude, longitude)
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        // Geocoder를 초기화합니다.
        val geocoder = Geocoder(requireContext(), Locale.KOREAN)

        // Android API 레벨 33 이상에서 Geocoder를 사용하는 방법
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(
                latitude, longitude, 1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    // 다이얼로그를 띄워 장소 저장 여부를 물어봅니다.
//                    showSavePlaceDialog(address)
                }
            }
        } else { // API 레벨 33 미만에서 Geocoder를 사용하는 방법
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                // 다이얼로그를 띄워 장소 저장 여부를 물어봅니다.
//                showSavePlaceDialog(address)
            }
        }
    }

    private fun showSavePlaceDialog(address: String) {
        val dialog = SavePlaceDialogFragment.newInstance(null, address)
        dialog.show(childFragmentManager, "SavePlaceDialog")
    }

//    override fun onSavePlaceClicked(placeName: String?) {
//        // 장소 저장 처리 로직을 여기에 추가합니다.
//        if (placeName != null) {
//            // 저장 로직 예시: ViewModel을 통해 데이터베이스에 저장
//            // viewModel.savePlace(placeName, selectedAddress)
//            toast("장소가 저장되었습니다: $placeName")
//        } else {
//            toast("장소 이름을 입력하세요.")
//        }
//    }
//
//    override fun onCancelClicked() {
//        // 취소 처리 로직을 여기에 추가합니다.
//        toast("장소 저장이 취소되었습니다.")
//    }


    private fun toast(text: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSavePlaceDialog(placeName: String?, address: String?) {
        val dialog = SavePlaceDialogFragment.newInstance(placeName, address)
        dialog.show(childFragmentManager, "SavePlaceDialog")
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
