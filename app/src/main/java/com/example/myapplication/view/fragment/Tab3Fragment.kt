package com.example.myapplication.view.fragment

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentTab3Binding
import com.example.myapplication.model.viewModel.MapViewModel
import com.example.myapplication.view.adapter.AddressAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapView
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.overlay.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*

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
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
    private lateinit var autoCompleteTextView: AutoCompleteTextView
    private lateinit var addressAdapter: AddressAdapter
    val marker = Marker()

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

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)

        autoCompleteTextView = binding.address as AutoCompleteTextView
        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
        autoCompleteTextView.setAdapter(autoCompleteAdapter)

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
                Log.d(TAG, "Searching for address: $address")
                viewModel.searchPlaceByName(address)
            } else {
                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.addressData.observe(viewLifecycleOwner, { data ->
            val (roadAddress, latitude, longitude) = data
            Log.d(TAG, "Address data observed: $roadAddress, lat: $latitude, lon: $longitude")
            updateBottomSheet(roadAddress, latitude, longitude)
            moveCameraToLocation(latitude, longitude)
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
            Log.e(TAG, "Error observed: $message")
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        })

        // RecyclerView 초기화
        binding.addressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        addressAdapter = AddressAdapter(requireContext(), mutableListOf(), _binding!!.emptyStateText)
        binding.addressRecyclerView.adapter = addressAdapter
        updateEmptyState()
    }

    private fun fetchAutocompletePredictions(query: String) {
        // This function can remain unchanged if you are using another service for autocomplete
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Approximate location access granted
            }
            else -> {
                // No location access granted
                Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }
    }

    fun updateBottomSheet(address: String, latitude: Double, longitude: Double) {
        addressAdapter.addAddress(address)
    }

    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
        naverMap.moveCamera(cameraUpdate)
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow

        naverMap.setOnMapClickListener { _, coord ->
            marker.position = LatLng(coord.latitude, coord.longitude)
            marker.map = naverMap
            getAddress(coord.latitude, coord.longitude)
        }
    }

    private fun getAddress(latitude: Double, longitude: Double) {
        val query = "$latitude,$longitude"
        viewModel.searchPlaceByCoordinates(query)
    }

    private fun updateEmptyState() {
        if (addressAdapter.itemCount == 0) {
            _binding!!.emptyStateText.visibility = View.VISIBLE
        } else {
            _binding!!.emptyStateText.visibility = View.GONE
        }
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



//
//
//
//
//
//
//package com.example.myapplication.view.fragment
//
//import android.Manifest
//import android.location.Geocoder
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.AutoCompleteTextView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myapplication.R
//import com.example.myapplication.databinding.FragmentTab3Binding
//import com.example.myapplication.model.viewModel.MapViewModel
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//import com.naver.maps.geometry.LatLng
//import com.naver.maps.map.CameraUpdate
//import com.naver.maps.map.MapView
//import com.naver.maps.map.NaverMap
//import com.naver.maps.map.OnMapReadyCallback
//import com.naver.maps.map.util.FusedLocationSource
//import com.naver.maps.map.LocationTrackingMode
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.AutocompletePrediction
//import com.google.android.libraries.places.api.model.AutocompleteSessionToken
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
//import com.google.android.libraries.places.api.net.PlacesClient
//import java.util.*
//import com.naver.maps.map.overlay.Marker
//import com.example.myapplication.model.interfaces.SavePlaceListener
//import com.example.myapplication.view.adapter.AddressAdapter
//import com.example.myapplication.view.fragment.SavePlaceDialogFragment
//
//class Tab3Fragment : Fragment(), OnMapReadyCallback {
//
//    companion object {
//        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
//        private const val TAG = "Tab3Fragment"
//    }
//
//    private var _binding: FragmentTab3Binding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var mapView: MapView
//    private lateinit var naverMap: NaverMap
//    private lateinit var locationSource: FusedLocationSource
//    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>
//    private var placesClient: PlacesClient? = null
//    private lateinit var autoCompleteAdapter: ArrayAdapter<String>
//    private lateinit var autoCompleteTextView: AutoCompleteTextView
//    private lateinit var addressAdapter: AddressAdapter
//    val marker = Marker()
//
//    private val viewModel: MapViewModel by viewModels()
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
//        locationPermissionRequest.launch(arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ))
//
//        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
//
//        mapView = binding.mapView
//        mapView.onCreate(savedInstanceState)
//        mapView.getMapAsync(this)
//
//        bottomSheetBehavior = BottomSheetBehavior.from(binding.persistentBottomSheet)
//
//        if (!Places.isInitialized()) {
//            Places.initialize(requireContext(), "w86eyz5x78")
//        }
//        placesClient = Places.createClient(requireContext())
//
//        autoCompleteTextView = binding.address as AutoCompleteTextView
//        autoCompleteAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
//        autoCompleteTextView.setAdapter(autoCompleteAdapter)
//
//        autoCompleteTextView.setOnKeyListener { _, _, _ ->
//            val query = autoCompleteTextView.text.toString()
//            if (query.isNotEmpty()) {
//                fetchAutocompletePredictions(query)
//            }
//            false
//        }
//
//        binding.submit.setOnClickListener {
//            val address = binding.address.text.toString()
//            if (address.isNotEmpty()) {
//                viewModel.searchPlaceByName(address)
//            } else {
//                Toast.makeText(requireContext(), "주소를 입력해주세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        viewModel.addressData.observe(viewLifecycleOwner, { data ->
//            val (roadAddress, latitude, longitude) = data
//            updateBottomSheet(roadAddress, latitude, longitude)
//            moveCameraToLocation(latitude, longitude)
//        })
//
//        viewModel.errorMessage.observe(viewLifecycleOwner, { message ->
//            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
//        })
//
//        // RecyclerView 초기화
//        binding.addressRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        addressAdapter = AddressAdapter(mutableListOf()) // 초기화할 때 빈 리스트를 전달
//        binding.addressRecyclerView.adapter = addressAdapter
//    }
//
//    private fun fetchAutocompletePredictions(query: String) {
//        val token = AutocompleteSessionToken.newInstance()
//        val request = FindAutocompletePredictionsRequest.builder()
//            .setQuery(query)
//            .setSessionToken(token)
//            .build()
//
//        placesClient?.findAutocompletePredictions(request)?.addOnSuccessListener { response ->
//            val predictions = response.autocompletePredictions
//            val suggestionList = predictions.map { it.getFullText(null).toString() }
//            autoCompleteAdapter.clear()
//            autoCompleteAdapter.addAll(suggestionList)
//            autoCompleteAdapter.notifyDataSetChanged()
//        }?.addOnFailureListener { exception ->
//            Log.e(TAG, "Error fetching autocomplete predictions", exception)
//            exception.printStackTrace()
//        }
//    }
//
//    private val locationPermissionRequest = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        when {
//            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
//                // Precise location access granted
//            }
//            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
//                // Approximate location access granted
//            }
//            else -> {
//                // No location access granted
//                Toast.makeText(requireContext(), "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
//                requireActivity().finish()
//            }
//        }
//    }
//
//    fun updateBottomSheet(address: String, latitude: Double, longitude: Double) {
//        addressAdapter.addAddress(address)
//    }
//
//    private fun moveCameraToLocation(latitude: Double, longitude: Double) {
//        val cameraUpdate = CameraUpdate.scrollTo(LatLng(latitude, longitude))
//        naverMap.moveCamera(cameraUpdate)
//    }
//
//    override fun onMapReady(naverMap: NaverMap) {
//        this.naverMap = naverMap
//        naverMap.locationSource = locationSource
//        naverMap.locationTrackingMode = LocationTrackingMode.Follow
//
//        naverMap.setOnMapClickListener { _, coord ->
//            marker.position = LatLng(coord.latitude, coord.longitude)
//            marker.map = naverMap
//            getAddress(coord.latitude, coord.longitude)
//        }
//    }
//
//    private fun getAddress(latitude: Double, longitude: Double) {
//        val geocoder = Geocoder(requireContext(), Locale.KOREAN)
//
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocation(
//                    latitude, longitude, 1
//                ) { addresses ->
//                    if (addresses.isNotEmpty()) {
//                        val address = addresses[0].getAddressLine(0)
//                        Log.d(TAG, "Address found: $address")
//                        showSavePlaceDialog(address)
//                    } else {
//                        Log.d(TAG, "No address found")
//                    }
//                }
//            } else {
//                val addresses = geocoder.getFromLocation(latitude, longitude, 1)
//                if (addresses != null && addresses.isNotEmpty()) {
//                    val address = addresses[0].getAddressLine(0)
//                    Log.d(TAG, "Address found: $address")
//                    showSavePlaceDialog(address)
//                } else {
//                    Log.d(TAG, "No address found")
//                }
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Geocoder failed", e)
//        }
//    }
//    private fun showSavePlaceDialog(address: String) {
//        val dialog = SavePlaceDialogFragment.newInstance(null, address)
//        dialog.show(childFragmentManager, "SavePlaceDialog")
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
