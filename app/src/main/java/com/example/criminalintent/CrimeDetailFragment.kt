package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

class CrimeDetailFragment : Fragment() {

    private var _binding: FragmentCrimeDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: CrimeDetailFragmentArgs by navArgs()

    private val viewModel: CrimeDetailViewModel by viewModels {
        CrimeDetailViewModelFactory(args.crimeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    crime.collect { crime -> updateViews(crime) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setupViews() {
        setupTitleTextView()
        setupCrimeDateButton()
        setupCrimeSolvedCheckBox()
    }

    private fun setupTitleTextView() {
        binding.crimeTitle.doAfterTextChanged {
            viewModel.setCrimeTitle(it.toString())
        }
    }

    private fun setupCrimeDateButton() {
        binding.crimeDateButton.setOnClickListener {
            viewModel.setCrimeDate(Date())
        }
    }

    private fun setupCrimeSolvedCheckBox() {
        binding.crimeSolvedCheckBox.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setCrimeSolved(isChecked)
        }
    }

    private fun updateViews(crime: Crime?) {
        if (crime == null) return

        with(binding) {
            crimeTitle.setText(crime.title)
            crimeDateButton.text =
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(crime.date)
            crimeSolvedCheckBox.isChecked = crime.isSolved
        }
    }
}