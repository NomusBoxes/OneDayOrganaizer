package ru.nechaevskij.onedayorganaizer

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Button

class AlarmActivity : AppCompatActivity() {

    private lateinit var ringtone: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        var notificatitionUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ringtone = RingtoneManager.getRingtone(this, notificatitionUri)
        if (ringtone == null){
            notificatitionUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(this, notificatitionUri)
        }
        else{
            ringtone.volume = .5f
            ringtone.play()

        }

        val closeButton = findViewById<Button>(R.id.button)

        closeButton.setOnClickListener{
            finish()
        }
    }

    override fun onDestroy() {
        if (ringtone!=null && ringtone.isPlaying){
            ringtone.stop()
        }
        super.onDestroy()
    }


}