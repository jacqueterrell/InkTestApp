package com.example.inktestapp.userInterface.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.inktestapp.databinding.SettingsLayoutBinding
import com.example.inktestapp.utils.AppSharedPrefs
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: SettingsLayoutBinding
    private var sharedPrefs: AppSharedPrefs = KoinJavaComponent.get(AppSharedPrefs::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpUI()
        setUpListeners()
    }

    private fun setUpUI() {
        binding.sliderSensitivity.value = sharedPrefs.penSensitivityValue
        binding.switchInkEnhancement.isChecked = sharedPrefs.hasEnabledSmoothPen
        binding.switchTheme.isChecked = sharedPrefs.hasEnabledDarkTheme
    }

    private fun setUpListeners() {
        binding.sliderSensitivity.addOnChangeListener { slider, value, fromUser ->
            val scalingValue = value
            sharedPrefs.penSensitivityValue = scalingValue
        }
        binding.switchTheme.setOnCheckedChangeListener { compoundButton, isChecked ->
            sharedPrefs.hasEnabledDarkTheme = isChecked
        }
        binding.switchInkEnhancement.setOnCheckedChangeListener { compoundButton, isChecked ->
            sharedPrefs.hasEnabledSmoothPen = isChecked
        }
    }
}