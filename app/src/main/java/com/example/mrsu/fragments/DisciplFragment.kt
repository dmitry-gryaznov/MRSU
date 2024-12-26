package com.example.mrsu.fragments

import RecordBook
import StudentSemester
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.mrsu.R
import com.example.mrsu.databinding.ActivityDiscBinding
import com.example.mrsu.objects.RequestObj

class DisciplFragment : Fragment() {

    private lateinit var binding: ActivityDiscBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Устанавливаем DataBinding
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_disc, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Загружаем данные для отображения
        loadStudentSemester()
    }

    private fun loadStudentSemester() {
        RequestObj.getStudentSemesterRequest(
            context = requireContext(),
            onSuccess = { studentSemester ->
                Log.i("DisciplFragment", "Данные получены: $studentSemester")
                displayStudentSemester(studentSemester)
            },
            onFailure = { error ->
                Log.e("DisciplFragment", "Ошибка загрузки данных: $error")
            }
        )
    }

    private fun displayStudentSemester(studentSemester: StudentSemester) {
        // Получаем контейнер из XML
        val container = binding.container
        container.removeAllViews() // Убедимся, что контейнер пуст

        // Группируем дисциплины по факультетам
        val groupedByFaculty = studentSemester.RecordBooks.groupBy { it.Faculty }

        // Проходимся по каждому факультету и его дисциплинам
        groupedByFaculty.forEach { (faculty, recordBooks) ->
            // Создаём плашку для факультета
            val facultyHeader = LinearLayout(requireContext()).apply {
                orientation = LinearLayout.HORIZONTAL
                setBackgroundResource(R.drawable.faculty_background) // Ресурс для плашки
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Плашка на всю ширину
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 0) // Отступ сверху
                }
                setPadding(16, 16, 16, 16)
            }

            // Текст внутри плашки
            val facultyTitle = TextView(requireContext()).apply {
                setTextColor(resources.getColor(android.R.color.black, null))
                text = faculty
                textSize = 18f
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, // Текст на всю ширину плашки
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            facultyHeader.addView(facultyTitle)
            container.addView(facultyHeader)

            // Добавляем все дисциплины для текущего факультета
            recordBooks.flatMap { it.Disciplines }.forEach { discipline ->
                val button = Button(requireContext()).apply {
                    id = discipline.Id
                    text = discipline.Title
                    isAllCaps = false
                    gravity = Gravity.CENTER_VERTICAL
                    setBackgroundResource(R.drawable.rounded_button_background)
                    setPadding(20, 20, 20, 20)

                    // Переход при нажатии
                    setOnClickListener {
                        navigateToNextFragment(discipline.Id, discipline.Title)
                    }
                }

                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 16)
                }

                container.addView(button, params)
            }
        }
    }

    private fun navigateToNextFragment(id: Int, name: String) {

        // Создаём экземпляр NextFragment и передаём аргументы
        val nextFragment = RatingPlanFragment().apply {
            arguments = Bundle().apply {
                putInt("id", id)
                putString("name", name)
            }
        }

        // Выполняем транзакцию для перехода на следующий фрагмент
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nextFragment) // Убедитесь, что fragment_container соответствует вашему макету
            .addToBackStack(null) // Добавляем в back stack, чтобы можно было вернуться назад
            .commit()
    }

//    private fun updateDisciplinesWithUnreadMarks(recordBooks: List<RecordBook>) {
//        val container = binding.container
//        val allDisciplines = recordBooks.flatMap { it.Disciplines }
//
//        allDisciplines.forEach { discipline ->
//            RequestObj.getUnreadMarks(
//                context = requireContext(),
//                disciplineId = discipline.Id,
//                onSuccess = { hasUnreadMarks ->
//                    activity?.runOnUiThread {
//                        // Находим элемент кнопки для этой дисциплины
//                        val button = container.findViewById<Button>(discipline.Id)
//                        if (button != null) {
//                            val unreadIndicator = button.findViewById<View>(R.id.unreadIndicator)
//                                ?: ImageView(requireContext()).apply {
//                                    id = View.generateViewId()
//                                    setImageResource(R.drawable.ic_unread_indicator) // Иконка восклицательного знака
//                                    layoutParams = LinearLayout.LayoutParams(
//                                        48, // Размер индикатора
//                                        48
//                                    ).apply {
//                                        gravity = Gravity.CENTER_VERTICAL
//                                        marginEnd = 16
//                                    }
//                                    (button.parent as LinearLayout).addView(this, 0)
//                                }
//                            unreadIndicator.visibility = if (hasUnreadMarks) View.VISIBLE else View.GONE
//                        }
//                    }
//                },
//                onFailure = { error ->
//                    activity?.runOnUiThread {
//                        Toast.makeText(requireContext(), "Ошибка обновления: $error", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            )
//        }
//    }



}
