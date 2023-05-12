package com.example.criminalintent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.model.Crime
import java.text.DateFormat
import java.util.UUID

class CrimeListAdapter(
    private val crimes: List<Crime>,
    private val onCrimeClicked: (id: UUID) -> Unit,
    private val onCrimeLongClicked: (id: UUID) -> Boolean
) : RecyclerView.Adapter<CrimeListAdapter.CrimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val binding =
            ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CrimeViewHolder(binding, onCrimeClicked, onCrimeLongClicked)
    }

    override fun getItemCount() = crimes.size

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.bind(crimes[position])
    }

    class CrimeViewHolder(
        private val binding: ListItemCrimeBinding,
        private val onCrimeClicked: (id: UUID) -> Unit,
        private val onCrimeLongClicked: (id: UUID) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(crime: Crime) {
            with(binding) {
                crimeTitleTextView.text = crime.title
                val date = DateFormat.getDateTimeInstance(
                    DateFormat.FULL, DateFormat.SHORT
                ).format(crime.date)
                crimeDateTextView.text = date

                crimeSolvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE

                root.setOnClickListener { onCrimeClicked(crime.id) }

                root.setOnLongClickListener { onCrimeLongClicked(crime.id) }
            }
        }
    }

}