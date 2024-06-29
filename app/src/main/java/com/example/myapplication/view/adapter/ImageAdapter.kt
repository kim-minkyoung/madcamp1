import android.content.SharedPreferences
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class ImageAdapter(private val images: MutableList<Uri>, private val sharedPreferences: SharedPreferences) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    var isSelectMode = false
    private val selectedImages = mutableSetOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = images[position]
        Glide.with(holder.itemView.context).load(uri).into(holder.image)

        // 선택 버튼의 가시성을 설정
        holder.chooseButton.visibility = if (isSelectMode) View.VISIBLE else View.GONE
        holder.chooseButton.isSelected = selectedImages.contains(uri)

        // 선택 버튼 클릭 리스너 설정
        holder.chooseButton.setOnClickListener {
            toggleSelection(uri, holder)
        }

        // 이미지 클릭 리스너 설정 (이미지를 클릭했을 때도 선택 상태 변경)
        holder.image.setOnClickListener {
            if (isSelectMode) {
                toggleSelection(uri, holder)
            }
        }

        // 삭제 버튼 클릭 리스너 설정
        holder.deleteButton.setOnClickListener {
            if (isSelectMode) {
                images.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, images.size)
                saveImagesToPreferences() // Shared Preferences에 이미지 URI 저장
            }
        }
    }

    // 선택 상태를 토글하는 함수
    private fun toggleSelection(uri: Uri, holder: ImageViewHolder) {
        if (selectedImages.contains(uri)) {
            selectedImages.remove(uri)
            holder.chooseButton.isSelected = false
        } else {
            selectedImages.add(uri)
            holder.chooseButton.isSelected = true
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val chooseButton: ImageView = itemView.findViewById(R.id.chooseButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    // Shared Preferences에 이미지 URI 저장하기
    private fun saveImagesToPreferences() {
        val editor = sharedPreferences.edit()
        val uriStrings = images.map { it.toString() }.toSet()
        editor.putStringSet("images", uriStrings)
        editor.apply()
    }

    fun deleteSelectedImages() {
        images.removeAll(selectedImages)
        selectedImages.clear()
        notifyDataSetChanged()
        saveImagesToPreferences() // Shared Preferences에 이미지 URI 저장
    }
}
