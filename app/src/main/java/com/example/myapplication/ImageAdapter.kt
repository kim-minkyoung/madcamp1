import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R

class ImageAdapter(private val images: MutableList<Uri>) :
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

        holder.itemView.setBackgroundColor(
            if (selectedImages.contains(uri)) android.graphics.Color.BLACK else android.graphics.Color.TRANSPARENT
        )

        holder.selectButton.visibility = if (isSelectMode) View.VISIBLE else View.GONE
        holder.selectButton.isSelected = selectedImages.contains(uri)

        holder.selectButton.setOnClickListener {
            if (holder.selectButton.isSelected) {
                selectedImages.remove(uri)
            } else {
                selectedImages.add(uri)
            }
            notifyItemChanged(position)
        }

        holder.deleteButton.setOnClickListener {
            if (isSelectMode) {
                images.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, images.size)
            }
        }
    }

    override fun getItemCount(): Int = images.size

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.imageView)
        val selectButton: ImageView = itemView.findViewById(R.id.selectButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    fun deleteSelectedImages() {
        images.removeAll(selectedImages)
        selectedImages.clear()
        notifyDataSetChanged()
    }
}
