package com.developerpaul123.starview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider

class StarViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_star_view)
        val starView = findViewById<StarView>(R.id.starView)
        val pointsSlider =
            findViewById<Slider>(R.id.numberOfPointsSlider)
        pointsSlider.addOnChangeListener { _, value, _ ->
            starView.numberOfPoints = value.toInt();
        }

        val radiusSlider = findViewById<Slider>(R.id.cornerRadiusSlider)
        radiusSlider.addOnChangeListener { _, value, _ ->
            starView.cornerRadius = value
        }

        val fillSlider = findViewById<Slider>(R.id.fillPercentageSlider)
        fillSlider.addOnChangeListener {_, value, _ ->
            starView.fillPercentage = value / 100f
        }
    }
}
