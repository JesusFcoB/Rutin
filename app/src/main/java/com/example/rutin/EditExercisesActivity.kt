package com.example.rutin

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EditExercisesActivity : AppCompatActivity() {

    private var dayIndex = 0
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exercises)

        dayIndex = intent.getIntExtra("day_index", 0)
        container = findViewById(R.id.editExerciseContainer)

        findViewById<TextView>(R.id.tvEditDayTitle).text =
            "Editar — ${WorkoutData.days[dayIndex].title}"

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<TextView>(R.id.btnAddExercise).setOnClickListener {
            showAddDialog()
        }

        renderList() // Solo renderiza, NO guarda aquí
    }

    private fun renderList() {
        container.removeAllViews()
        val exercises = WorkoutData.days[dayIndex].exercises.toMutableList()

        exercises.forEachIndexed { index, exercise ->
            val item = LayoutInflater.from(this)
                .inflate(R.layout.item_edit_exercise, container, false)

            item.findViewById<TextView>(R.id.tvEditName).text = exercise.name
            item.findViewById<TextView>(R.id.tvEditSets).text = exercise.setsRepsText()

            item.findViewById<ImageButton>(R.id.btnEdit).setOnClickListener {
                showEditDialog(index, exercise)
            }

            item.findViewById<ImageButton>(R.id.btnDelete).setOnClickListener {
                AlertDialog.Builder(this)
                    .setTitle("Eliminar ejercicio")
                    .setMessage("¿Eliminar \"${exercise.name}\"?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        WorkoutData.removeExercise(dayIndex, index)
                        saveAndRender()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }

            container.addView(item)
        }
    }

    private fun saveAndRender() {
        WorkoutData.saveToPrefs(this)
        renderList()
    }
    private fun showAddDialog() {
        showExerciseDialog(null, -1)
    }

    private fun showEditDialog(index: Int, exercise: Exercise) {
        showExerciseDialog(exercise, index)
    }

    private fun showExerciseDialog(exercise: Exercise?, index: Int) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_exercise, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val etSets = dialogView.findViewById<EditText>(R.id.etSets)
        val etRepsMin = dialogView.findViewById<EditText>(R.id.etRepsMin)
        val etRepsMax = dialogView.findViewById<EditText>(R.id.etRepsMax)

        exercise?.let {
            etName.setText(it.name)
            etSets.setText(it.sets.toString())
            etRepsMin.setText(it.repsMin.toString())
            etRepsMax.setText(it.repsMax.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (exercise == null) "Agregar ejercicio" else "Editar ejercicio")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = etName.text.toString().trim()
                val sets = etSets.text.toString().toIntOrNull() ?: 3
                val repsMin = etRepsMin.text.toString().toIntOrNull() ?: 10
                val repsMax = etRepsMax.text.toString().toIntOrNull() ?: 12

                if (name.isEmpty()) {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val newExercise = Exercise(name, sets, repsMin, repsMax)
                if (index == -1) {
                    WorkoutData.addExercise(dayIndex, newExercise)
                } else {
                    WorkoutData.updateExercise(dayIndex, index, newExercise)
                }
                saveAndRender()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}