package ru.nechaevskij.onedayorganaizer

import kotlinx.serialization.Serializer

@kotlinx.serialization.Serializable
class Time(var hours: Int, var minutes: Int){

    var asOneNumber: Int = toMinutes()

    constructor(strTime: String) : this(0, 0) {
        hours = strTime.substring(0,2).toInt()
        minutes = strTime.substring(3,5).toInt()
        var asOneNumber: Int = toMinutes()
    }

    override fun toString(): String {
        var hrs = ""
        var mnts = ""
        if (hours < 10){
            hrs = StringBuilder().append("0").append(hours.toString()).toString()
        }
        else hrs = hours.toString()
        if (minutes < 10){
            mnts = StringBuilder().append("0").append(minutes.toString()).toString()
        }
        else mnts = minutes.toString()
        return StringBuilder().append(hrs).append(":").append(mnts).toString()
    }

    operator fun plus(increment: Time): Time{
        return Time(
            hours+increment.hours, minutes+increment.minutes
        )
    }

    operator fun minus(decrement: Time): Time{
        return Time(
            hours-decrement.hours, minutes-decrement.minutes
        )
    }

    fun toMinutes(): Int{
        return hours*60+minutes
    }

    fun roundTo24Format() : Time{
        return Time(hours%24, minutes%60)
    }
}