package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.criminalintent.databinding.FragmentCrimeListBinding
import com.example.criminalintent.model.Crime
import kotlinx.coroutines.launch

class CrimeListFragment : Fragment() {

    private var _binding: FragmentCrimeListBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "Cannot access binding because it is null" }

    private val viewModel: CrimeListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.crimes.collect { crimes -> updateViews(crimes) }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @Deprecated("Deprecated in Java") // TODO fix this
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime_list, menu)
    }

    @Deprecated("Deprecated in Java") // TODO fix this
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val newId = viewModel.addCrime()
                findNavController().navigate(CrimeListFragmentDirections.showCrimeDetails(newId))
                true
            }
            else -> onOptionsItemSelected(item)
        }
    }

    private fun updateViews(crimes: List<Crime>) {
        with(binding.crimesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = CrimeListAdapter(crimes,
                { id -> findNavController().navigate(CrimeListFragmentDirections.showCrimeDetails(id)) },
                { id ->
                    viewModel.deleteCrime(id)
                    true
                }
            )
        }
    }

}