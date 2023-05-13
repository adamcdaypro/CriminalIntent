package com.example.criminalintent

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeDetailBinding
import com.example.criminalintent.model.Crime
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
        val suspect = getSuspectFromContacts(it)
        viewModel.updateSuspect(suspect)
    }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it == false) viewModel.updatePhotoFileName(null)
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

        updateViews(null)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                with(viewModel) {
                    crime.collect { crime -> updateViews(crime) }
                }
            }
        }

        setFragmentResultListener(DatePickerDialogFragment.DATE_REQUEST_KEY) { _, bundle ->
            val date = bundle.getSerializable(DatePickerDialogFragment.DATE_KEY) as Date
            viewModel.updateCrimeDate(date)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateViews(crime: Crime?) {
        if (crime == null) {
            binding.content.visibility = View.GONE
            binding.loadingProgressBar.visibility = View.VISIBLE
        } else {
            binding.content.visibility = View.VISIBLE
            binding.loadingProgressBar.visibility = View.GONE
            updateTitleTextView(crime)
            updateCrimeDateButton(crime)
            updateCrimeSolvedCheckBox(crime)
            updateSendCrimeReportButton(crime)
            updateChoseSuspectButton(crime)
            updatePhotoImageView()
            updateCameraImageButton()
        }
    }

    private fun updateTitleTextView(crime: Crime) {
        binding.crimeTitle.apply {
            setText(crime.title)
            doAfterTextChanged { viewModel.updateCrimeTitle(it.toString()) }
        }
    }

    private fun updateCrimeDateButton(crime: Crime) {
        binding.crimeDateButton.apply {
            text =
                DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(crime.date)
            binding.crimeDateButton.setOnClickListener {
                findNavController().navigate(CrimeDetailFragmentDirections.selectDate(crime.date))
            }
        }
    }

    private fun updateCrimeSolvedCheckBox(crime: Crime) {
        binding.crimeSolvedCheckBox.apply {
            isChecked = crime.isSolved
            setOnCheckedChangeListener { _, isChecked -> viewModel.updateCrimeSolved(isChecked) }
        }
    }

    private fun updateSendCrimeReportButton(crime: Crime) {
        binding.sendCrimeReportButton.apply {
            val intent = Intent(Intent.ACTION_SEND).apply { type = "text/plain" }
            isEnabled = (canResolveIntent(intent))

            setOnClickListener {
                val crimeReport = getCrimeReport(crime)
                val crimeReportSubject = getString(R.string.crime_report_subject)
                intent.putExtra(Intent.EXTRA_SUBJECT, crimeReportSubject)
                intent.putExtra(Intent.EXTRA_TEXT, crimeReport)

                val chooserIntent =
                    Intent.createChooser(intent, getString(R.string.crime_report_subject))
                startActivity(chooserIntent)
            }
        }
    }

    private fun updateChoseSuspectButton(crime: Crime) {
        binding.chooseSuspectButton.apply {
            val testIntent = selectSuspect.contract.createIntent(requireContext(), null)
            isEnabled = canResolveIntent(testIntent)

            if (crime.suspect.isNotBlank()) {
                text = crime.suspect
            }
            setOnClickListener { selectSuspect.launch(null) }
        }
    }

    private fun updatePhotoImageView() {

    }

    private fun updateCameraImageButton() {
        binding.cameraImageButton.apply{
            val testIntent = takePhoto.contract.createIntent(requireContext(), Uri.parse(""))
            isEnabled = canResolveIntent(testIntent)

            setOnClickListener {
                val photoName = "IMG_${Date()}.JPG"
                val photoFile = File(requireContext().applicationContext.filesDir, photoName)
                val photoUri = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.criminalintent.fileProvider",
                    photoFile
                )
                viewModel.updatePhotoFileName(photoName)
                takePhoto.launch(photoUri)
            }
        }
    }

    private fun getCrimeReport(crime: Crime): String {
        val solvedString = when (crime.isSolved) {
            true -> getString(R.string.crime_report_solved)
            false -> getString(R.string.crime_report_unsolved)
        }
        val dateString =
            DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT).format(crime.date)
        val suspectString = when (crime.suspect) {
            "" -> getString(R.string.crime_report_no_suspect)
            else -> getString(R.string.crime_report_suspect, crime.suspect)
        }
        return getString(
            R.string.crime_report,
            crime.title,
            dateString,
            solvedString,
            suspectString
        )
    }

    private fun getSuspectFromContacts(uri: Uri?): String {
        if (uri == null) return ""

        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val queryCursor = requireActivity().contentResolver.query(
            uri,
            queryFields,
            null,
            null,
            null
        )
        val suspect = queryCursor?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else ""
        }
        return suspect ?: ""
    }

    private fun canResolveIntent(intent: Intent): Boolean {
        val packageManager = requireActivity().packageManager
        val resolvedActivity = packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolvedActivity != null
    }
}
