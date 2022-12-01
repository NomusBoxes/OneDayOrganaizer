package ru.nechaevskij.onedayorganaizer


import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.RelativeLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toolbar.LayoutParams
import androidx.cardview.widget.CardView
import kotlin.math.roundToInt


@kotlinx.serialization.Serializable
class EventCard(var name:String, var duration: Time) {



    var view: Int = 0
    lateinit var eventStartTime: Time


    @SuppressLint("ResourceType", "ClickableViewAccessibility", "InflateParams")
    fun addTo(activity: MainActivity) {

        //region Инициализация полей
        val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val inflatedView: View = inflater.inflate(R.xml.event_card_2, null, false)
        view = inflatedView.id
        inflatedView.findViewById<TextView>(R.id.eventName).text = name
        inflatedView.findViewById<TextView>(R.id.eventTime).text = duration.toString()


        var prevEventsTime = Time (0,0)
        for (event in activity.listOfEvents){
            prevEventsTime += event.duration
        }
        eventStartTime =  activity.dayStart+activity.prepTime + prevEventsTime - activity.listOfEvents.last().duration
        inflatedView.findViewById<TextView>(R.id.eventStartTime).text = eventStartTime.toString()

        //endregion


        var timeOffset = activity.dayStart+activity.prepTime

        for (event in activity.listOfEvents){
            if (event != activity.listOfEvents.last())
                timeOffset+=event.duration
        }


        //region Добавление карточки на экран

        //Log.d("current table to clench",(timeOffset.hours*10+(timeOffset.minutes/10-1)).toString())


        Log.d("current table to clench",(230+(timeOffset.minutes/10-1)).toString())

        val hourMargin = (activity.findViewById<RelativeLayout>(timeOffset.hours).parent as TableRow).top
        var minuteMargin: Int = 0
        minuteMargin = if (timeOffset.minutes/10 - 1 < 0) 0
        else (activity.findViewById<RelativeLayout>(timeOffset.hours).parent as TableRow).height/60*timeOffset.minutes

        val yPos = hourMargin+minuteMargin



            //(timeOffset.hours*10+(timeOffset.minutes/10-1))*activity.findViewById<TableRow>(231).height
        Log.d("view_id", view.toString())


        val relativeParams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT
        )
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
        relativeParams.topMargin = yPos
        relativeParams.leftMargin = 280
        relativeParams.rightMargin = 60
        relativeParams.height = activity.findViewById<TableRow>(231).height*duration.toMinutes()/10

        activity.findViewById<RelativeLayout>(R.id.aboveLayout).addView(inflatedView, relativeParams)

        //endregion






        //region Инициализация слушателей

        activity.findViewById<CardView>(view).setOnLongClickListener {

            removeFrom(activity)

            true
        }
        /*inflatedView.setOnClickListener {
            inflatedView.setOnTouchListener(null)
        }*/

        //endregion




        //endregion

    }


    fun removeFrom(activity: MainActivity) {

        activity.findViewById<RelativeLayout>(R.id.aboveLayout).removeView(activity.findViewById(view))

    }

    fun pxToDp(px: Int, context: Context): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
    }

    //region Рудименты
    private fun makeMovable(view: View, event: MotionEvent, activity: MainActivity){
        val X: Int = event.rawX.toInt()
        val Y: Int = event.rawY.toInt()
        val width = view.width
        val height = view.height
        when(event.action){
            MotionEvent.ACTION_MOVE -> {
                //if (view.top <
                view.top =
                    (Y-height*1.5).toInt() //- listOfEvents[listOfEvents.size-1]._xDelta
                view.left = X-width/2 //- listOfEvents[listOfEvents.size-1]._yDelta
                view.bottom = view.top + height
                view.right = view.left + width
                Log.d("Расположение", StringBuilder()
                    .append("X=")
                    .append(view.left)
                    .append("Y=")
                    .append(view.top)
                    .append(" ,")
                    .append(view.width)
                    .append(" ,")
                    .append(view.height)
                    .toString())
            }
            MotionEvent.ACTION_UP -> {
                /*for (row in activity.tableRows){
                    if (Y in row.top.. row.bottom) {
                        row.addView(view)

                    }
                }*/
                view.performClick()
            }
        }

    }
    /*public fun getTimeInMinutes(): Int {
        return startTime.hours*60+startTime.minutes
    }*/
    //endregion
}