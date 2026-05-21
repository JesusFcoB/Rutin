package com.example.rutin

import android.os.Build
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private val dayTitles = listOf("PUSH", "PULL", "LEGS")
    private val daySubtitles = listOf(
        "Pecho · Hombro · Tríceps",
        "Espalda · Bíceps · Post.",
        "Pierna completa"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WorkoutData.loadFromPrefs(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        viewPager = findViewById(R.id.viewPager)
        tabLayout = findViewById(R.id.tabLayout)

        val adapter = DayPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = dayTitles[position]
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                findViewById<TextView>(R.id.tvTitle).text = dayTitles[position]
                findViewById<TextView>(R.id.tvSubtitle).text = daySubtitles[position]
            }
        })

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            showTimePicker()
        }

        findViewById<View>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this@MainActivity, EditExercisesActivity::class.java)
            intent.putExtra("day_index", viewPager.currentItem)
            startActivity(intent)
        }
    }

    private fun showTimePicker() {
        val hour = NotificationScheduler.getSavedHour(this)
        val minute = NotificationScheduler.getSavedMinute(this)

        android.app.TimePickerDialog(this, { _, h, m ->
            NotificationScheduler.schedule(this, h, m)
            android.widget.Toast.makeText(
                this,
                "Recordatorio activado a las %02d:%02d".format(h, m),
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }, hour, minute, true).show()
    }
}