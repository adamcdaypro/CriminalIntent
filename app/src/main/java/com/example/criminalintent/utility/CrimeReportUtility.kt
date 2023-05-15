package com.example.criminalintent.utility

import android.content.Context
import com.example.criminalintent.R
import com.example.criminalintent.model.Crime
import java.text.DateFormat

object CrimeReportUtility {

    fun getCrimeReport(crime: Crime, context: Context): String {
        val solvedString = when (crime.isSolved) {
            true -> context.getString(R.string.crime_report_solved)
            false -> context.getString(R.string.crime_report_unsolved)
        }
        val dateString =
            DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(crime.date)
        val suspectString = when (crime.suspect) {
            "" -> context.getString(R.string.crime_report_no_suspect)
            else -> context.getString(R.string.crime_report_suspect, crime.suspect)
        }
        return context.getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectString
        )
    }

}