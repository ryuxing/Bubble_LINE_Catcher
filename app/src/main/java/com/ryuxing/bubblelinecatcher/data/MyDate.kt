package com.ryuxing.bubblelinecatcher.data

import com.ryuxing.bubblelinecatcher.App
import com.ryuxing.bubblelinecatcher.R
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MyDate {
    companion object{
        fun toTextDate(timems:Long): String {
            val time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(timems), ZoneId.systemDefault())
            val refDays  = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
            val refMonths=refDays.minusDays((refDays.dayOfMonth-1).toLong())
            val refYears= refDays.minusDays((refDays.dayOfYear-1).toLong())
            var formatter: DateTimeFormatter

            if(time > refDays){
                formatter = DateTimeFormatter.ofPattern("HH:mm")

            }else if(time > refMonths){
                formatter = DateTimeFormatter.ofPattern("d"+ App.context.resources.getString(R.string.unit_day)+" (E) HH:mm")
            }else if(time > refYears){
                formatter = DateTimeFormatter.ofPattern("MM/dd(E) HH:mm")
            }else{
                formatter = DateTimeFormatter.ofPattern("YYYY/MM/dd HH:mm")
            }
            return time.format(formatter)
        }
    }
}