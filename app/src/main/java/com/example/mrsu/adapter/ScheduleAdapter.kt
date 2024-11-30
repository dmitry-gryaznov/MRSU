import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mrsu.R

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private val scheduleItems = Array(8) { "" to "" } // Массив для 8 строк (всегда отображается)
    private var selectedSubgroup = 0 // 0 = все подгруппы, 1 = первая, 2 = вторая

    fun updateData(newItems: List<ScheduleItem>, subgroup: Int) {
        selectedSubgroup = subgroup
        scheduleItems.fill("" to "") // Сбрасываем данные

        val lessons = newItems.flatMap { it.timeTable.lessons }
        for (lesson in lessons) {
            val index = lesson.number - 1 // Индекс строки (Number - 1)
            if (index in scheduleItems.indices) {
                // Отображаем занятия только для выбранной подгруппы или если SubgroupNumber == 0
                val disciplinesInfo = lesson.disciplines
                    .filter { it.subgroupNumber == 0 || it.subgroupNumber == selectedSubgroup }
                    .joinToString("\n") { discipline ->
                        """
                        ${discipline.title} 
                        [к. ${discipline.auditorium.campusTitle} ${discipline.auditorium.number}]
                        ${discipline.teacher.fio}
                        """.trimIndent()
                    }

                // Если дисциплины подходят под фильтр, добавляем их
                scheduleItems[index] = (lesson.number.toString() to disciplinesInfo.ifBlank { "—" })
            }
        }

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pairTime: TextView = view.findViewById(R.id.pairNumber)
        val lessonInfo: TextView = view.findViewById(R.id.lessonInfo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (number, info) = scheduleItems[position]

        // Задаем время пар
        val pairTimes = listOf(
            "08:00 - 09:30", "09:45 - 11:15", "11:35 - 13:05",
            "13:20 - 14:50", "15:00 - 16:30", "16:40 - 18:10",
            "18:15 - 19:45", "19:50 - 21:20"
        )
        holder.pairTime.text = pairTimes.getOrNull(position) ?: ""

        // Отображаем либо информацию о занятиях, либо пустую строку
        holder.lessonInfo.text = if (info.isNotBlank()) info else ""
    }

    override fun getItemCount(): Int = scheduleItems.size
}