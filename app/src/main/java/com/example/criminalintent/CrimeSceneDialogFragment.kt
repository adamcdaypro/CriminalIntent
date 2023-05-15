package com.example.criminalintent

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.example.criminalintent.databinding.FragmentCrimeSceneDialogBinding
import java.io.File

class CrimeSceneDialogFragment : DialogFragment() {

    private var _binding: FragmentCrimeSceneDialogBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val args: CrimeSceneDialogFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentCrimeSceneDialogBinding.inflate(layoutInflater)
        val photoFile = File(requireContext().applicationContext.filesDir, args.photoFileName)
        val bitmap = BitmapFactory.decodeFile(photoFile.path)
        binding.crimeSceneImageView.setImageBitmap(bitmap)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}