package ru.nechaevskij.onedayorganaizer

import android.app.AlarmManager
import android.app.AlarmManager.AlarmClockInfo
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import kotlinx.serialization.json.Json


class EventCreationDialog(context: Context, val activity: MainActivity) : Dialog(context) {
    lateinit var eventName:String
    lateinit var eventDuration:String

    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_menu)







        val cancelBt = findViewById<Button>(R.id.cancel_bt)
        cancelBt.setOnClickListener(){
            this.cancel()
        }

        val timePicker = findViewById<TimePicker>(R.id.timePicker)
        timePicker.setIs24HourView(true)
        timePicker.hour = 0
        timePicker.minute = 0

        val okBt = findViewById<Button>(R.id.ok_bt)
        okBt.setOnClickListener(){
            this.eventName = findViewById<EditText>(R.id.inputEvent).text.toString()

            //region Форматирование данных TimePicker'а и присваивание новых данных в поле eventTime
            val sb = StringBuilder()
            if (timePicker.hour < 10){
                sb.append("0").append(timePicker.hour)
            }
            else{
                sb.append(timePicker.hour)
            }
            sb.append(":")
            if (timePicker.minute < 10){
                sb.append("0").append(timePicker.minute)
            }
            else{
                sb.append(timePicker.minute)
            }

            this.eventDuration = sb.toString()
            //endregion

            var timeOffset = activity.dayStart + activity.prepTime


            for (event in activity.listOfEvents){
                //if (event!=activity.listOfEvents.last())
                    timeOffset += event.duration
            }


            Log.d("alarm time", Time(timeOffset.hours, timeOffset.minutes).toString())


            //region Настройка будильника
            val calendar: Calendar = Calendar.getInstance();
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            calendar.set(Calendar.MINUTE, timeOffset.minutes)
            calendar.set(Calendar.HOUR_OF_DAY, timeOffset.hours)



            val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val alarmClockInfo: AlarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, getAlarmInfoPendingIntent())
            alarmManager.setAlarmClock(alarmClockInfo, getAlarmActionPendingIntent())
            //Toast.makeText(context, StringBuilder().append(timePicker.hour).append(":").append(timePicker.minute), Toast.LENGTH_SHORT).show()

            //endregion

            this.cancel()
        }

    }

    //region Вспомогательные функции будильника
    private fun getAlarmInfoPendingIntent() : PendingIntent {
        val alarmInfoIntent = Intent(context, EventCreationDialog::class.java)
        alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context, 0, alarmInfoIntent, PendingIntent.FLAG_MUTABLE)
    }
    private fun getAlarmActionPendingIntent(): PendingIntent{
        val intent = Intent(context, AlarmActivity::class.java)
        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_MUTABLE)
    }
    private fun pickerToMilliseconds(picker: TimePicker) : Long {
        var ms: Long = 0
        ms+= picker.minute*60000
        ms+= picker.hour*3600000
        return ms
    }
    //endregion

    private fun fromJsonToObject(jsonString: String): EventCard {
        return Json.decodeFromString(EventCard.serializer(), jsonString)
    }
}