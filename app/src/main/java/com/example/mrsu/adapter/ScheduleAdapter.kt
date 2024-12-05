import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mrsu.R
import com.example.mrsu.adap_inter.OnDisciplineClickListener
import com.google.android.material.imageview.ShapeableImageView

class ScheduleAdapter(private val listener: OnDisciplineClickListener) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    private val scheduleItems = Array(8) { "" to null as DisciplineInfo? } // 8 строк для расписания

    fun updateData(newItems: List<ScheduleItem>, subgroup: Int) {
        scheduleItems.fill("" to null) // Сбрасываем данные

        val lessons = newItems.flatMap { it.timeTable.lessons }
        for (lesson in lessons) {
            val index = lesson.number - 1 // Индекс строки (Number - 1)
            if (index in scheduleItems.indices) {
                // Фильтрация дисциплин по подгруппе
                val filteredDisciplines = lesson.disciplines.filter {
                    it.subgroupNumber == 0 || it.subgroupNumber == subgroup
                }

                if (filteredDisciplines.isNotEmpty()) {
                    val discipline = filteredDisciplines.first() // Берем первую дисциплину
                    val disciplineInfo = DisciplineInfo(
                        id = discipline.id,
                        title = discipline.title,
                        campusTitle = discipline.auditorium.campusTitle,
                        auditoriumNumber = discipline.auditorium.number,
                        teacherName = discipline.teacher.fio,
                        teacherPhotoUrl = discipline.teacher.photo.urlSmall
                    )
                    scheduleItems[index] = (lesson.number.toString() to disciplineInfo)
                }
            }
        }

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val lessonTitle: TextView = view.findViewById(R.id.lesson_title)
        val lessonTime: TextView = view.findViewById(R.id.lesson_time)
        val lessonNumber: TextView = view.findViewById(R.id.lesson_number)
        val location: TextView = view.findViewById(R.id.location)
        val teacherName: TextView = view.findViewById(R.id.teacher_name)
        val teacherPhoto: ShapeableImageView = view.findViewById(R.id.teacher_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pairTimes = listOf(
            "08:00 - 09:30", "09:45 - 11:15", "11:35 - 13:05",
            "13:20 - 14:50", "15:00 - 16:30", "16:40 - 18:10",
            "18:15 - 19:45", "19:50 - 21:20"
        )
        val (number, info) = scheduleItems[position]

        holder.lessonNumber.text = "${position + 1} пара"
        holder.lessonTime.text = pairTimes.getOrNull(position) ?: ""

        if (info != null) {
            // Отображаем данные о занятии
            holder.lessonTitle.visibility = View.VISIBLE
            holder.location.visibility = View.VISIBLE
            holder.teacherName.visibility = View.VISIBLE
            holder.teacherPhoto.visibility = View.VISIBLE

            holder.lessonTitle.text = info.title
            holder.location.text = "Корпус: ${info.campusTitle}; Аудитория: ${info.auditoriumNumber}"
            holder.teacherName.text = info.teacherName

            // Используем Glide для загрузки фото преподавателя
            Glide.with(holder.itemView.context)
                .load(info.teacherPhotoUrl)
                .into(holder.teacherPhoto)

            holder.itemView.setOnClickListener{
                listener.onDisciplineClick(info.id)
            }
        } else {
            // Скрываем элементы, если пары нет

            holder.lessonTitle.visibility = View.GONE
            holder.location.visibility = View.GONE
            holder.teacherName.visibility = View.GONE
            holder.teacherPhoto.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
        }
    }

    override fun getItemCount(): Int = scheduleItems.size

    data class DisciplineInfo(
        val id: Int,
        val title: String,
        val campusTitle: String,
        val auditoriumNumber: String,
        val teacherName: String,
        val teacherPhotoUrl: String
    )
}