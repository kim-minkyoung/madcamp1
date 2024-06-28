import android.app.Activity
import android.content.Intent
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTab2Binding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView 설정
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3) // 그리드 레이아웃을 사용하여 이미지가 3열로 정렬되도록 함
        imageAdapter = ImageAdapter(imageList)
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
            }
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
