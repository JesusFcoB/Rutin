package com.example.rutin

import android.content.Context

object ProgressManager {

    private const val PREFS = "gym_progress"

    private fun key(dayIndex: Int, exerciseIndex: Int): String = "day${dayIndex}_ex${exerciseIndex}"

    fun saveCheck(context: Context, dayIndex: Int, exerciseIndex: Int, checked: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(key(dayIndex, exerciseIndex), checked).apply()
    }

    fun isChecked(context: Context, dayIndex: Int, exerciseIndex: Int): Boolean {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(key(dayIndex, exerciseIndex), false)
    }

    fun resetDay(context: Context, dayIndex: Int) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val count = WorkoutData.days[dayIndex].exercises.size
        repeat(count) { i -> editor.putBoolean(key(dayIndex, i), false) }
        editor.apply()
    }

    fun getProgress(context: Context, dayIndex: Int): Pair<Int, Int> {
        val total = WorkoutData.days[dayIndex].exercises.size
        val done = (0 until total).count { isChecked(context, dayIndex, it) }
        return Pair(done, total)
    }
}