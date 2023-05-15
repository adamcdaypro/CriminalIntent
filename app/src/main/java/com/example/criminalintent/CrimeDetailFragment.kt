package com.example.criminalintent

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.filemanager.PhotoFileManager
import com.example.criminalintent.model.Crime
import com.example.criminalintent.utility.ContactsUtility
import com.example.criminalintent.utility.CrimeReportUtility
import com.example.criminalintent.utility.IntentResolverUtility
import com.example.criminalintent.utility.PhotoUtility
import kotlinx.coroutines.launch
import java.io.File
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

    private val selectSuspect = registerForActivityResult(ActivityResultContracts.PickContact()) {
        val suspect = ContactsUtility.getSuspectFromContacts(it, requireContext())
        viewModel.updateSuspect(suspect)
        updateViews()
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        viewModel.updatePhotoFileName(it, requireContext())
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

        updateViews()
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    crime.collect { updateViews() }
                }
            }
        }

        setFragmentResultListener(DatePickerDialogFragment.DATE_REQUEST_KEY) { _, bundle ->
            val date = bundle.getSerializable(DatePickerDialogFragment.DATE_KEY) as Date
            viewModel.updateCrimeDate(date)
        }
    }

    override fun onPause() {
        super.onPause()
        updateViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateViews() {
        val crime = viewModel.crime.value
        if (crime == null) {
            binding.content.visibility = View.GONE
            binding.loadingProgressBar.visibility = View.VISIBLE
        } else {
            binding.content.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility = View.GONE
            updateTitleTextView(crime.title)
            updateCrimeDateButton(crime.date)
            updateCrimeSolvedCheckBox(crime.isSolved)
            updateSendCrimeReportButton(crime)
            updateChoseSuspectButton(crime.suspect)
            updatePhotoImageView(crime.photoFileName)
            updateCameraImageButton()
        }
    }

    private fun updateTitleTextView(title: String) {
        binding.crimeTitle.apply {
            val currentText = this.text.toString()
            if (currentText.isEmpty()) {
                setText(title)
            } else if (currentText != title) {
                setText(currentText)
                viewModel.updateCrimeTitle(currentText)
            }
        }
    }

    private fun updateCrimeDateButton(date: Date) {
        binding.crimeDateButton.apply {
            text =
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(date)
            binding.crimeDateButton.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(date))
            }
        }
    }

    private fun updateCrimeSolvedCheckBox(isSolved: Boolean) {
        binding.crimeSolvedCheckBox.apply {
            isChecked = isSolved
            setOnCheckedChangeListener { _, isChecked -> viewModel.updateCrimeSolved(isChecked) }
        }
    }

    private fun updateSendCrimeReportButton(crime: Crime) {
        binding.sendCrimeReportButton.apply {
            isEnabled = IntentResolverUtility.actionSendEnabled(requireContext())

            setOnClickListener {
                val crimeReport = CrimeReportUtility.getCrimeReport(crime, context)
                val chooserIntent = getCrimeReportChooserIntent(crimeReport)
                startActivity(chooserIntent)
            }
        }
    }

    private fun getCrimeReportChooserIntent(crimeReport: String): Intent {
        val crimeReportSubject = getString(R.string.crime_report_subject)
        val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
        intent.putExtra(Intent.EXTRA_SUBJECT, crimeReportSubject)
        intent.putExtra(Intent.EXTRA_TEXT, crimeReport)
        return Intent.createChooser(intent, getString(R.string.crime_report_subject))
    }

    private fun updateChoseSuspectButton(suspect: String) {
        binding.chooseSuspectButton.apply {
            isEnabled = IntentResolverUtility.pickContactEnabled(requireContext())

            text = suspect
            setOnClickListener { selectSuspect.launch(null) }
        }
    }

    private fun updatePhotoImageView(photoFileName: String?) {
        if (photoFileName == null) return

        binding.crimeImageView.apply {
            doOnLayout {
                val photoFile = File(requireContext().applicationContext.filesDir, photoFileName)
                val bitmap = PhotoUtility.getScaledBitmap(
                    path = photoFile.path,
                    destinationWidth = measuredWidth,
                    destinationHeight = measuredHeight
                )
                setImageBitmap(bitmap)
            }

            setOnClickListener {
                findNavController().navigate(
                    CrimeDetailFragmentDirections.showCrimeScene(
                        photoFileName
                    )
                )
            }
        }
    }

    private fun updateCameraImageButton() {
        binding.cameraImageButton.apply {
            isEnabled = IntentResolverUtility.takePictureEnabled(requireContext())

            setOnClickListener {
                val photoFile = PhotoFileManager.createPhotoFile(requireContext())
                val photoUri = PhotoFileManager.createPhotoUri(photoFile, requireContext())
                viewModel.setNextPhotoFileName(photoFile.name)
                takePhoto.launch(photoUri)
            }
        }
    }
}
