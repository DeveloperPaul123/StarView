package com.developerpaul123.starview

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.developerpaul123.starview.databinding.ActivityStarViewBinding
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener

class StarViewActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityStarViewBinding;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityStarViewBinding.inflate(layoutInflater)
        val view = viewBinding.root
        setContentView(view)

        viewBinding.numberOfPointsSlider.addOnChangeListener { _, value, _ ->
            viewBinding.starView.numberOfPoints = value.toInt();
        }

        viewBinding.cornerRadiusSlider.addOnChangeListener { _, value, _ ->
            viewBinding.starView.cornerRadius = value
        }

        viewBinding.fillPercentageSlider.addOnChangeListener { _, value, _ ->
            viewBinding.starView.fillPercentage = value / 100f
        }

        viewBinding.outlineThicknessSlider.addOnChangeListener { _, value, _ ->
            viewBinding.starView.outlineThickness = value;
        }

        // start with current star color
        viewBinding.starFillColorTileView.setPaintColor(viewBinding.starView.starColor)
        viewBinding.starFillColorTileView.setOnClickListener { tileView ->
            buildColorPickerDialog(tileView.id)
                .show()
        }

        // start with current outline color
        viewBinding.starOutlineColorTileView.setPaintColor(viewBinding.starView.starOutlineColor)
        viewBinding.starOutlineColorTileView.setOnClickListener { tileView ->
            buildColorPickerDialog(tileView.id)
                .show()
        }

        // start with current background color
        viewBinding.starBackgroundColorTileView.setPaintColor(viewBinding.starView.starBackgroundColor)
        viewBinding.starBackgroundColorTileView.setOnClickListener { tileView ->
            buildColorPickerDialog(tileView.id)
                .show()
        }

        viewBinding.fillDirectionRadioGroup.check(
            when (viewBinding.starView.fillDirection) {
                StarView.FillDirection.LeftToRight -> R.id.ltrRadioButton
                StarView.FillDirection.RightToLeft -> R.id.rtlRadioButton
                StarView.FillDirection.TopToBottom -> R.id.ttbRadioButton
                StarView.FillDirection.BottomToTop -> R.id.bttRadioButton
            }
        )
        viewBinding.fillDirectionRadioGroup.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.ltrRadioButton -> viewBinding.starView.fillDirection =
                    StarView.FillDirection.LeftToRight;
                R.id.rtlRadioButton -> viewBinding.starView.fillDirection =
                    StarView.FillDirection.RightToLeft;
                R.id.ttbRadioButton -> viewBinding.starView.fillDirection =
                    StarView.FillDirection.TopToBottom;
                R.id.bttRadioButton -> viewBinding.starView.fillDirection =
                    StarView.FillDirection.BottomToTop;
            }
        }
    }

    private fun buildColorPickerDialog(@IdRes tileViewId: Int) = ColorPickerDialog.Builder(this)
        .setTitle("ColorPicker Dialog")
        .setPositiveButton("Confirm",
            ColorEnvelopeListener { envelope, _ ->
                when (tileViewId) {
                    R.id.starFillColorTileView -> {
                        viewBinding.starView.starColor = envelope.color
                        viewBinding.starFillColorTileView.setPaintColor(envelope.color)
                    }
                    R.id.starBackgroundColorTileView -> {
                        viewBinding.starView.starBackgroundColor = envelope.color
                        viewBinding.starBackgroundColorTileView.setPaintColor(envelope.color)
                    }
                    R.id.starOutlineColorTileView -> {
                        viewBinding.starView.starOutlineColor = envelope.color
                        viewBinding.starOutlineColorTileView.setPaintColor(envelope.color)
                    }
                }

            })
        .setNegativeButton(
            "Cancel"
        ) { dialogInterface, _ -> dialogInterface.dismiss() }
        .attachAlphaSlideBar(tileViewId == R.id.starBackgroundColorTileView)
        .attachBrightnessSlideBar(true)
        .setBottomSpace(12)
}
