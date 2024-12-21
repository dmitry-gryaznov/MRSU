import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.databinding.ItemTurnstileBinding
import com.example.mrsu.dataclasses.TurnstileHistory

class TurnstileAdapter : RecyclerView.Adapter<TurnstileAdapter.TurnstileViewHolder>() {

    private val items = mutableListOf<TurnstileHistory>()

    fun updateData(newItems: List<TurnstileHistory>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TurnstileViewHolder {
        val binding = ItemTurnstileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TurnstileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TurnstileViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class TurnstileViewHolder(private val binding: ItemTurnstileBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TurnstileHistory) {
            binding.buildingTitle.text = item.Build
            binding.status.text = item.Status
            binding.statusTime.text = item.Time.take(5) // Отображаем только часы и минуты
        }
    }
}
