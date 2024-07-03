package com.example.myapplication.view.fragment

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTab3Binding
import com.example.myapplication.model.data.Address
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
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.naver.maps.map.overlay.Marker
import com.example.myapplication.view.adapter.AddressAdapter

class Tab3Fragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val TAG = "Tab3Fragment"
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
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var bottomSheetSearchTextView: AutoCompleteTextView
    private lateinit var bottomSheetSearchButton: TextView
    val marker = Marker()

    private val viewModel: MapViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab3Binding.inflate(inflater, container, false)
        return binding.root
    }

    private fun searchAddressInBottomSheet(query: String) {
        val addressList = addressAdapter.currentList
        val position = addressList.indexOfFirst {
            (it.roadAddress?.contains(query, ignoreCase = true) == true) ||
                    (it.specificAddress?.contains(query, ignoreCase = true) == true)
        }

        if (position != -1) {
            binding.addressRecyclerView.scrollToPosition(position)
        } else {
            Toast.makeText(requireContext(), "해당 주소를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.setOnMapClickListener { _, coord ->
            marker.position = LatLng(coord.latitude, coord.longitude)
            marker.map = naverMap
            viewModel.reverseGeocode(coord.latitude, coord.longitude)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "YOUR_GOOGLE_API_KEY")
        }
        placesClient = Places.createClient(requireContext())
        autoCompleteTextView = binding.address as AutoCompleteTextView
        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)

        autoCompleteTextView.setOnKeyListener { _, _, _ ->
            val query = autoCompleteTextView.text.toString()
            if (query.isNotEmpty()) {
                fetchAutocompletePredictions(query)
            }
            false
        }
        binding.submit.setOnClickListener {
            val address = binding.address.text.toString()
            if (address.isNotEmpty()) {
                viewModel.searchPlaceByName(address)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetSearchTextView = binding.bottomSheetAddress
        bottomSheetSearchButton = binding.bottomSheetSearch
        bottomSheetSearchButton.setOnClickListener {
            val query = bottomSheetSearchTextView.text.toString()
            if (query.isNotEmpty()) {
                searchAddressInBottomSheet(query)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addressData.observe(viewLifecycleOwner) { data ->
            val (roadAddress, latitude, longitude) = data
            moveCameraToLocation(data.latitude, data.longitude)
            if (!viewModel.specificAddressData.value.isNullOrEmpty()) {
                // specificAddressData가 이미 있다면 showAddPlaceDialog를 호출하지 않음
                return@observe
            }
            showAddPlaceDialog(Address(null, data.roadAddress, data.latitude, data.longitude))
        }

        viewModel.specificAddressData.observe(viewLifecycleOwner) { specificValue ->
            viewModel.addressData.value?.let { data ->
                if (specificValue.isNotEmpty()) {
                    val address = Address(specificValue, data.roadAddress, data.latitude, data.longitude)
                    showAddPlaceDialog(address)
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // RecyclerView 초기화
        binding.addressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        addressAdapter = AddressAdapter(requireContext(), mutableListOf(), binding.emptyStateText, viewModel)
        binding.addressRecyclerView.adapter = addressAdapter
        autoCompleteTextView.setAdapter(autoCompleteAdapter)
        // Observe navigateToAddress LiveData to move the map
        viewModel.navigateToAddress.observe(viewLifecycleOwner) { coordinates ->
            coordinates?.let { (latitude, longitude) ->
                moveCameraToLocation(latitude, longitude)
            }
        }
    }

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
            Log.e(TAG, "Error fetching autocomplete predictions", exception)
            exception.printStackTrace()
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // 정밀한 위치 접근 허용됨
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // 대략적인 위치 접근 허용됨
            }
            else -> {
                // 위치 접근이 허용되지 않음
                Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    fun updateBottomSheet(address: Address) {
        addressAdapter.addAddress(address)
    }

    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    private var dialogShown = false

    private fun showAddPlaceDialog(address: Address) {
        if (dialogShown) return
        dialogShown = true

        val message = "이곳을 나만의 장소로 추가하시겠어요?\n${address.specificAddress ?: address.roadAddress}"

        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("네") { _, _ ->
                updateBottomSheet(address)
                Toast.makeText(requireContext(), "장소가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialogShown = false
            }
            .setNegativeButton("아니요") { _, _ ->
                dialogShown = false
            }
            .setOnDismissListener {
                dialogShown = false
            }
            .show()
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
