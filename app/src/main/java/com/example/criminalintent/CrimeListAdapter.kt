package com.example.criminalintent

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.criminalintent.databinding.ListItemCrimeBinding
import com.example.criminalintent.model.Crime
import com.google.android.material.snackbar.Snackbar

class CrimeListAdapter(
    private val crimes: List<Crime>
) : RecyclerView.Adapter<CrimeListAdapter.CrimeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeViewHolder {
        val binding = ListItemCrimeBinding.inflate(LayoutInflater.from(parent.context))
        return CrimeViewHolder(binding)
    }

    override fun getItemCount() = crimes.size

    override fun onBindViewHolder(holder: CrimeViewHolder, position: Int) {
        holder.bind(crimes[position])
    }

    class CrimeViewHolder(private val binding: ListItemCrimeBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(crime: Crime) {
            with (binding) {
                crimeTitleTextView.text = crime.title
                crimeDateTextView.text = crime.date.toString()

                root.setOnClickListener {
                    Snackbar.make(it, "${crime.title} clicked", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

}