package com.example.rutin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class DayFragment : Fragment() {

    companion object {
        private const val ARG_DAY_INDEX = "day_index"
        fun newInstance(index: Int): DayFragment {
            return DayFragment().also {
                it.arguments = Bundle().apply { putInt(ARG_DAY_INDEX, index) }
            }
        }
    }

    private var dayIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_day, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dayIndex = arguments?.getInt(ARG_DAY_INDEX) ?: 0
        val day = WorkoutData.days[dayIndex]

        view.findViewById<TextView>(R.id.tvFocusLabel).text = day.focus
        view.findViewById<TextView>(R.id.tvExerciseCount).text = day.exercises.size.toString()
        view.findViewById<TextView>(R.id.tvTotalSeries).text =
            day.exercises.sumOf { it.sets }.toString()

        view.findViewById<View>(R.id.btnReset).setOnClickListener {
            ProgressManager.resetDay(requireContext(), dayIndex)
            val container = view.findViewById<LinearLayout>(R.id.exerciseContainer)
            repeat(WorkoutData.days[dayIndex].exercises.size) { i ->
                val item = container.getChildAt(i)
                item?.findViewById<CheckBox>(R.id.checkDone)?.isChecked = false
                item?.findViewById<TextView>(R.id.tvExerciseName)?.alpha = 1.0f
                item?.alpha = 1.0f
            }
            updateProgress(view)
        }
    }

    private fun updateProgress(view: View) {
        val (done, total) = ProgressManager.getProgress(requireContext(), dayIndex)
        view.findViewById<TextView>(R.id.tvProgressLabel).text = "$done / $total completados"
    }

    override fun onResume() {
        super.onResume()
        val container = view?.findViewById<LinearLayout>(R.id.exerciseContainer) ?: return
        container.removeAllViews()
        val day = WorkoutData.days[dayIndex]
        val inflater = LayoutInflater.from(requireContext())

        day.exercises.forEachIndexed { index, exercise ->
            val itemView = inflater.inflate(R.layout.item_exercise, container, false)
            val nameView = itemView.findViewById<TextView>(R.id.tvExerciseName)
            val checkBox = itemView.findViewById<CheckBox>(R.id.checkDone)

            itemView.findViewById<TextView>(R.id.tvNumber).text = (index + 1).toString()
            nameView.text = exercise.name
            itemView.findViewById<TextView>(R.id.tvSetsReps).text = exercise.setsRepsText()

            val saved = ProgressManager.isChecked(requireContext(), dayIndex, index)
            checkBox.isChecked = saved
            nameView.alpha = if (saved) 0.4f else 1.0f
            itemView.alpha = if (saved) 0.7f else 1.0f

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                ProgressManager.saveCheck(requireContext(), dayIndex, index, isChecked)
                nameView.alpha = if (isChecked) 0.4f else 1.0f
                itemView.alpha = if (isChecked) 0.7f else 1.0f
                updateProgress(requireView())
            }

            container.addView(itemView)
        }

        view?.let { updateProgress(it) }
    }
}