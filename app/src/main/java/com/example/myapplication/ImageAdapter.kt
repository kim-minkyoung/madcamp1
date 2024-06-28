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

        holder.chooseButton.visibility = if (isSelectMode) View.VISIBLE else View.GONE
        holder.chooseButton.isSelected = selectedImages.contains(uri)

        holder.chooseButton.setOnClickListener {
            if (selectedImages.contains(uri)) {
                selectedImages.remove(uri)
                holder.chooseButton.setBackgroundResource(android.R.drawable.btn_default)
            } else {
                selectedImages.add(uri)
                holder.chooseButton.setBackgroundColor(android.graphics.Color.BLACK)
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
        val chooseButton: ImageView = itemView.findViewById(R.id.chooseButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    fun deleteSelectedImages() {
        images.removeAll(selectedImages)
        selectedImages.clear()
        notifyDataSetChanged()
    }
}
