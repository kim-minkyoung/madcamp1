import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.google.android.gms.location.places.Place

class PlaceAdapter(private val places: List<Place>, private val onClick: (Place) -> Unit) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.nameTextView.text = place.name
        holder.itemView.setOnClickListener { onClick(place) }
    }

    override fun getItemCount(): Int = places.size
}
