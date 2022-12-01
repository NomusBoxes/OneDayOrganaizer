package ru.nechaevskij.onedayorganaizer

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.TouchDelegate
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.serialization.json.Json
import ru.nechaevskij.onedayorganaizer.databinding.ActivityMainBinding
import java.io.File
import java.io.FileWriter
import kotlin.math.roundToInt




class MainActivity : AppCompatActivity() {



    //region Поля MainActivity (список карточек внутри)
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding




    var listOfEvents = mutableListOf<EventCard>()// Здесь хранятся все карточки событий
    var dayStart: Time = Time(0, 0)
    var prepTime: Time = Time(0, 0)


    //endregion

    @SuppressLint("ResourceType", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_OneDayOrganaizer_NoActionBar)
        //region Дефолтная инициализация
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        //endregion

        if (getSharedPreferences("startSettings", Context.MODE_PRIVATE) !=null){
            dayStart = Time(
                getSharedPreferences("startSettings", Context.MODE_PRIVATE).getInt("startHour", 0),
                getSharedPreferences("startSettings", Context.MODE_PRIVATE).getInt("startMinute", 0)
            )
            prepTime = Time(
                getSharedPreferences("startSettings", Context.MODE_PRIVATE).getInt("prepHour", 0),
                getSharedPreferences("startSettings", Context.MODE_PRIVATE).getInt("prepMinute", -1)
            )
        }
        loadData()


        if ((dayStart.hours == 0 && dayStart.minutes == 0) || (prepTime.hours == 0 && prepTime.minutes == 0)){
            openStartMenu()
        }
        else{
            initializeTable()
            updateCards()
        }


        /*//region Инициализация полей таблицы
        for (i in getStartTime().hours until 25){
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.xml.table_row, null)
            val mainText = view.findViewById<TextView>(R.id.hour)
            if (i!=24) {
                view.findViewById<RelativeLayout>(R.id.relLayout).id = i
                mainText.text=view.findViewById<RelativeLayout>(i).id.toString()
            }
            else {
                mainText.text=(0).toString()
                view.findViewById<RelativeLayout>(R.id.relLayout).id = i
            }
            findViewById<LinearLayout>(R.id.inScrollLayout).addView(view)
        }
        //endregion*/

        //Обработка нажатия на кнопку создания
        binding.fab.setOnClickListener {
            val newDialogFragment = EventCreationDialog(this, this)
            newDialogFragment.show()

            //Действия по закрытию
            newDialogFragment.setOnCancelListener {
                if (newDialogFragment.eventName.isNotEmpty() && newDialogFragment.eventDuration.isNotEmpty()) { //если данные не пусты, то добавить новую карточку
                    listOfEvents.add(
                        EventCard(
                            newDialogFragment.eventName,
                            Time(newDialogFragment.eventDuration)
                        )
                    )
                    listOfEvents.last().addTo(this)
                }
            }
        }

        //Удаление всех событий
        val deleteAll = findViewById<Button>(R.id.deleteAll)
        deleteAll.setOnClickListener{
            for (i in 0 until listOfEvents.size){
                listOfEvents[i].removeFrom(this)
            }
            listOfEvents.clear()
            File(this.filesDir.path.toString() + "/eventsList.json").writeText("")
        }

        val refreshBt = findViewById<Button>(R.id.refreshBt)
        refreshBt.setOnClickListener{
            updateCards()
        }

        updateCards()

    }

    private fun updateCards(){
        if (listOfEvents.isNotEmpty()) {

            /*for (i in 0 until listOfEvents.size) {
                listOfEvents[i].removeFrom(this)
            }
*/
            for (i in 0 until listOfEvents.size) {
                listOfEvents[i].addTo(this)
            }
        }
    }

    //region Вспомогательные функции

    @SuppressLint("ResourceType")
    private fun initializeTable(){
        for (i in dayStart.hours until 25){
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.xml.table_row, null)
            val mainText = view.findViewById<TextView>(R.id.hour)
            if (i!=24) {
                view.findViewById<RelativeLayout>(R.id.relLayout).id = i
                view.findViewById<TableRow>(R.id.zero).id = i*10
                view.findViewById<TableRow>(R.id.ten).id = i*10+1
                view.findViewById<TableRow>(R.id.twenty).id = i*10+2
                view.findViewById<TableRow>(R.id.thirty).id = i*10+3
                view.findViewById<TableRow>(R.id.forty).id = i*10+4
                view.findViewById<TableRow>(R.id.fifty).id = i*10+5
                if (i < 10) mainText.text="0"+view.findViewById<RelativeLayout>(i).id.toString()
                else mainText.text=view.findViewById<RelativeLayout>(i).id.toString()
            }
            else {
                mainText.text=(0).toString()
                view.findViewById<RelativeLayout>(R.id.relLayout).id = i
            }
            findViewById<LinearLayout>(R.id.inScrollLayout).addView(view)
        }
    }

    fun dpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = this.resources.displayMetrics
        return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    private fun openStartMenu() {
        val startMenu = StartMenuActivity(this)
        startMenu.show()
        startMenu.setOnCancelListener {
            dayStart = startMenu.start
            prepTime = startMenu.prepTime
            getSharedPreferences("startSettings", Context.MODE_PRIVATE).edit()
                .putInt("startHour", dayStart.hours)
                .putInt("startMinute", dayStart.minutes)
                .putInt("prepHour", prepTime.hours)
                .putInt("prepMinute", prepTime.minutes)
                .apply()

            Log.d("prefData",
                getSharedPreferences("startHour", Context.MODE_PRIVATE).getInt("prepMinute", -1)
                    .toString()
            )
            initializeTable()
        }
    }

    //endregion

    //region Обработчики событий
    override fun onPause() {
        super.onPause()
        saveData()
    }
    override fun onResume() {
        super.onResume()

        loadData()
    }
    override fun onDestroy() {
        super.onDestroy()
        saveData()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Toast.makeText(this, "options", Toast.LENGTH_SHORT).show()

        getSharedPreferences("startSettings", Context.MODE_PRIVATE).edit().clear().apply()
        //openStartMenu()
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    //endregion

    //region Обработки информации о карточках - сохранение, загрузка и пр.
    private fun saveData(){
        val file = File(this.filesDir.path.toString() + "/eventsList.json")
        file.setWritable(true)
        for (event in listOfEvents){
            if (!fileHasString(file, toJson(event))) {
                FileWriter(file, true).use {
                    it.write(toJson(event) + '\n')
                }
            }
        }
        file.useLines { lines ->
            lines.forEach {
                Log.d("Data saved", it)
            }
        }
    }
    private fun loadData(){
        val file = File(this.filesDir.path.toString() + "/eventsList.json")
        //file.writeText("")
        if (file.length() != 0L) {
            file.useLines { lines ->
                lines.forEach {
                    Log.d("line was read:", it)
                    listOfEvents.add(fromJsonToObject(it))
                }
            }
        }
    }
    private fun fileHasString(file : File, str: String):Boolean{
        file.useLines { lines ->
            lines.forEach {
                if (it == str) return true
            }
        }
        return false
    }
    private fun toJson(eventCard: EventCard): String {
        // Обратите внимание, что мы вызываем Serializer, который автоматически сгенерирован из нашего класса
        // Сразу после того, как мы добавили аннотацию @Serializer
        return Json.encodeToString(EventCard.serializer(), eventCard)
    }
    private fun fromJsonToObject(jsonString: String): EventCard {
        return Json.decodeFromString(EventCard.serializer(), jsonString)
    }
    //endregion





}

