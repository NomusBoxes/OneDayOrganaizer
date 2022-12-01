package ru.nechaevskij.onedayorganaizer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import ru.nechaevskij.onedayorganaizer.databinding.ActivityStartMenuBinding


class StartMenuActivity(context: Context) : Dialog(context) {

    private lateinit var binding: ActivityStartMenuBinding

    var start: Time = Time(0, 0)
    var prepTime: Time = Time(0, 0)

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wakeUpPicker.setIs24HourView(true)
        //binding.wakeUpPicker.hour = 6
        //binding.wakeUpPicker.minute = 0
        binding.prepTimePicker.setIs24HourView(true)
        binding.prepTimePicker.hour = 1
        binding.prepTimePicker.minute = 0

        binding.button2.setOnClickListener{
                start=Time(binding.wakeUpPicker.hour, binding.wakeUpPicker.minute)
                prepTime = Time(binding.prepTimePicker.hour, binding.prepTimePicker.minute)
                cancel()
        }
    }



}

