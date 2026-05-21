package com.example.rutin

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class Exercise(
    val name: String,
    val sets: Int,
    val repsMin: Int,
    val repsMax: Int
) {
    fun setsRepsText(): String {
        return if (repsMin == repsMax) "$sets series · $repsMin reps"
        else "$sets series · $repsMin-$repsMax reps"
    }
}

data class WorkoutDay(
    val title: String,
    val focus: String,
    val exercises: MutableList<Exercise>
)

object WorkoutData {

    val days = mutableListOf(
        WorkoutDay(
            title = "Push",
            focus = "Enfocado en movimientos de empuje",
            exercises = mutableListOf(
                Exercise("Press de banca con barra o mancuernas", 4, 8, 10),
                Exercise("Press militar (hombro) con mancuernas", 3, 10, 12),
                Exercise("Aperturas en polea o máquina (pecho)", 3, 12, 15),
                Exercise("Elevaciones laterales (hombro medio)", 4, 15, 15),
                Exercise("Extensión de tríceps en polea alta", 3, 12, 15)
            )
        ),
        WorkoutDay(
            title = "Pull",
            focus = "Enfocado en movimientos de tracción",
            exercises = mutableListOf(
                Exercise("Dominadas o Jalón al pecho", 4, 8, 10),
                Exercise("Remo con barra o en máquina", 3, 10, 12),
                Exercise("Facepull (hombro posterior)", 3, 15, 15),
                Exercise("Curl de bíceps con barra", 3, 10, 12),
                Exercise("Curl martillo con mancuernas", 3, 12, 12)
            )
        ),
        WorkoutDay(
            title = "Legs",
            focus = "Enfocado en tren inferior",
            exercises = mutableListOf(
                Exercise("Sentadilla con barra", 4, 8, 10),
                Exercise("Prensa de piernas", 3, 10, 12),
                Exercise("Curl femoral (máquina)", 3, 12, 15),
                Exercise("Extensiones de cuádriceps", 3, 12, 15),
                Exercise("Elevación de talones (pantorrilla)", 4, 15, 20)
            )
        )
    )

    fun addExercise(dayIndex: Int, exercise: Exercise) {
        days[dayIndex].exercises.add(exercise)
    }

    fun removeExercise(dayIndex: Int, exerciseIndex: Int) {
        days[dayIndex].exercises.removeAt(exerciseIndex)
    }

    fun updateExercise(dayIndex: Int, exerciseIndex: Int, exercise: Exercise) {
        days[dayIndex].exercises[exerciseIndex] = exercise
    }

    fun saveToPrefs(context: Context) {
        val json = JSONArray()
        days.forEach { day ->
            val dayObj = JSONObject()
            dayObj.put("title", day.title)
            dayObj.put("focus", day.focus)
            val exArray = JSONArray()
            day.exercises.forEach { ex ->
                val exObj = JSONObject()
                exObj.put("name", ex.name)
                exObj.put("sets", ex.sets)
                exObj.put("repsMin", ex.repsMin)
                exObj.put("repsMax", ex.repsMax)
                exArray.put(exObj)
            }
            dayObj.put("exercises", exArray)
            json.put(dayObj)
        }
        context.getSharedPreferences("workout_data", Context.MODE_PRIVATE)
            .edit().putString("days", json.toString()).apply()
    }

    fun loadFromPrefs(context: Context) {
        val prefs = context.getSharedPreferences("workout_data", Context.MODE_PRIVATE)
        val jsonStr = prefs.getString("days", null) ?: return
        try {
            val json = JSONArray(jsonStr)
            days.clear()
            for (i in 0 until json.length()) {
                val dayObj = json.getJSONObject(i)
                val exArray = dayObj.getJSONArray("exercises")
                val exercises = mutableListOf<Exercise>()
                for (j in 0 until exArray.length()) {
                    val exObj = exArray.getJSONObject(j)
                    exercises.add(Exercise(
                        exObj.getString("name"),
                        exObj.getInt("sets"),
                        exObj.getInt("repsMin"),
                        exObj.getInt("repsMax")
                    ))
                }
                days.add(WorkoutDay(
                    dayObj.getString("title"),
                    dayObj.getString("focus"),
                    exercises
                ))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}