import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentTab2Binding

class Tab2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imageList = mutableListOf<Uri>()  // 이미지 URI를 저장할 리스트

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val PICK_IMAGES_REQUEST_CODE = 101  // 이미지 선택 요청 코드

    private var _binding: FragmentTab2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // 뷰 바인딩 초기화
        _binding = FragmentTab2Binding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView 초기화 및 설정
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)  // 그리드 레이아웃 매니저 설정
        imageAdapter = ImageAdapter(imageList)
        recyclerView.adapter = imageAdapter

        // 이미지 선택 결과 처리를 위한 ActivityResultLauncher 초기화
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    if (data.clipData != null) {
                        // 다중 이미지 선택 시
                        for (i in 0 until data.clipData!!.itemCount) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imageList.add(imageUri)
                        }
                    } else if (data.data != null) {
                        // 단일 이미지 선택 시
                        val imageUri = data.data!!
                        imageList.add(imageUri)
                    }
                    imageAdapter.notifyDataSetChanged()  // 어댑터에 데이터 변경을 알림
                }
            }
        }

        // 이미지 선택 버튼 클릭 시 갤러리 앱 열기
        binding.addImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // 뷰 바인딩 해제
    }

    // RecyclerView 어댑터 클래스
    private inner class ImageAdapter(private val images: List<Uri>) :
        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
            // 이미지 항목 레이아웃을 인플레이트하여 뷰홀더 반환
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_item, parent, false)
            return ImageViewHolder(view)
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            // 이미지 뷰홀더에 이미지 URI 설정
            val imageUri = images[position]
            holder.imageView.setImageURI(imageUri)
        }

        override fun getItemCount(): Int {
            // 이미지 목록의 크기 반환
            return images.size
        }

        // 이미지 뷰홀더 클래스
        inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val imageView: ImageView = itemView.findViewById(R.id.imageView)  // 이미지 뷰 홀더 내 이미지 뷰 초기화
        }
    }
}

//package com.example.myapplication
//
//import android.app.Activity
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.provider.MediaStore
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import androidx.activity.result.ActivityResultLauncher
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.example.myapplication.databinding.FragmentTab2Binding
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//
//class Tab2Fragment : Fragment() {
//
//    private val PICK_IMAGES_REQUEST_CODE = 101
//    private lateinit var layoutManager: GridLayoutManager
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ImageAdapter
//    private lateinit var imagesList: MutableList<String>
//
//    private var _binding: FragmentTab2Binding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var imageAdapter: ImageAdapter
//    private val imageList = mutableListOf<Uri>()
//
//    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
//
//    override fun onCreateView(
//        imagesList = ArrayList()
//        recyclerView=findViewById(R.id.recyclerView)
//        layoutManager=GridLayoutManager(context:this,spanCount:3)
//        adapter = ImageAdapter(imagesList)
//    recyclerView. layoutManager = layoutManager
//    recyclerView.adapter = adapter
//    val selectImagesButton = findViewById<Button>(R.id.selectImagesButton)
//    selectImagesButton.setOnClickListener {
//        selectImagesFromGallery()
//    }
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        _binding = FragmentTab2Binding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        imageAdapter = ImageAdapter(imageList)
//        binding.recyclerView.layoutManager = GridLayoutManager(context, 3)
//        binding.recyclerView.adapter = imageAdapter
//
//        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == Activity.RESULT_OK) {
//                result.data?.data?.let { uri ->
//                    imageList.add(uri)
//                    imageAdapter.notifyItemInserted(imageList.size - 1)
//                }
//            }
//        }
//
//        binding.addImg.setOnClickListener {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            pickImageLauncher.launch(intent)
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//
//    private inner class ImageAdapter(private val images: List<Uri>) :
//        RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
//
//        inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//            val imageView: ImageView = view.findViewById(R.id.imageView)
//        }
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
//            val view = LayoutInflater.from(parent.context)
//                .inflate(R.layout.image_item, parent, false)
//            return ImageViewHolder(view)
//        }
//
//        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
//            holder.imageView.setImageURI(images[position])
//        }
//
//        override fun getItemCount(): Int = images.size
//    }
//    private fun selectImagesFromGallery(){
//    val intent = Intent (Intent. ACTION_GET_CONTENT)
//    intent. type = "image/*"
//    intent.putExtra(Intent. EXTRA_ALLOW_MULTIPLE, true)
//    startActivityForResult(Intent. createChooser(intent, "Select Images"),PICK_IMAGES_REQUEST_CODE
//    }
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == PICK_IMAGES_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
//            val imagesList = mutableListOf<String>() // Assuming imagesList is declared somewhere
//            if (data.clipData != null) {
//                for (i in 0 until data.clipData!!.itemCount) {
//                    val imageUri = data.clipData!!.getItemAt(i).uri.toString()
//                    imagesList.add(imageUri)
//                }
//            } else {
//                val imageUri = data.data.toString()
//                imagesList.add(imageUri)
//            }
//            adapter.notifyDataSetChanged()
//        }
//    }
//
//}
