package com.example.criminalintent

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.navArgs
import java.util.Calendar
import java.util.GregorianCalendar

class DatePickerDialogFragment : DialogFragment() {

    private val args: DatePickerDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val calendar = getCalendarInstance()
        val dateListener = getOnDateSetListener()

        return DatePickerDialog(
            requireContext(),
            dateListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_WEEK)
        )
    }

    private fun getOnDateSetListener(): OnDateSetListener {
        return OnDateSetListener { _, year, month, dayOfMonth ->
            val date = GregorianCalendar(year, month, dayOfMonth).time
            setFragmentResult(DATE_REQUEST_KEY, bundleOf(DATE_KEY to date))
        }
    }

    private fun getCalendarInstance(): Calendar {
        val date = args.date
        val calendar: Calendar = Calendar.getInstance()
        if (date != null) {
            calendar.time = date
        }
        return calendar
    }

    companion object {

        const val DATE_REQUEST_KEY = "DATE_REQUEST_KEY"
        const val DATE_KEY = "DATE_KEY"

    }
}