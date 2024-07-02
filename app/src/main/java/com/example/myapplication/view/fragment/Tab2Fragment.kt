import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentTab2Binding

class Tab2Fragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imageAdapter: ImageAdapter
    private var imageList = mutableListOf<Uri>()

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private var _binding: FragmentTab2Binding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab2Binding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferences = requireContext().getSharedPreferences("ImagePrefs", Context.MODE_PRIVATE)

        // Shared Preferences에서 이미지 URI 불러오기
        loadImagesFromPreferences()

        // RecyclerView 설정
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        imageAdapter = ImageAdapter(imageList, sharedPreferences, requireContext()) // Context를 추가합니다.
        recyclerView.adapter = imageAdapter


        // 갤러리에서 이미지를 선택하기 위한 런처 등록
        pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    if (data.clipData != null) {
                        for (i in 0 until data.clipData!!.itemCount) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imageList.add(imageUri)
                        }
                    } else if (data.data != null) {
                        val imageUri = data.data!!
                        imageList.add(imageUri)
                    }
                    imageAdapter.notifyDataSetChanged()
                    saveImagesToPreferences() // Shared Preferences에 이미지 URI 저장
                }
            }
        }

        // 이미지 추가 버튼 클릭 리스너 설정
        binding.addImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        // 선택 모드 활성화/비활성화 버튼 클릭 리스너 설정
        binding.selectButton.setOnClickListener {
            imageAdapter.isSelectMode = !imageAdapter.isSelectMode
            imageAdapter.notifyDataSetChanged()
        }

        // 선택된 이미지 삭제 버튼 클릭 리스너 설정
        binding.deleteButton.setOnClickListener {
            if (imageAdapter.isSelectMode) {
                imageAdapter.deleteSelectedImages()
                imageAdapter.isSelectMode = false
                imageAdapter.notifyDataSetChanged()
                saveImagesToPreferences() // Shared Preferences에 이미지 URI 저장
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Shared Preferences에서 이미지 URI 불러오기
    private fun loadImagesFromPreferences() {
        val savedImages = sharedPreferences.getStringSet("images", emptySet()) ?: emptySet()
        imageList.clear()
        imageList.addAll(savedImages.map { Uri.parse(it) })
    }

    // Shared Preferences에 이미지 URI 저장하기
    private fun saveImagesToPreferences() {
        val editor = sharedPreferences.edit()
        val uriStrings = imageList.map { it.toString() }.toSet()
        editor.putStringSet("images", uriStrings)
        editor.apply()
    }
}



