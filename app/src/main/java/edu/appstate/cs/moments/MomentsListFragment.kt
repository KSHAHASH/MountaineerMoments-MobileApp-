package edu.appstate.cs.moments

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.appstate.cs.moments.databinding.FragmentMomentsListBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MomentsListFragment: Fragment() {
    private var _binding: FragmentMomentsListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val momentsListViewModel: MomentsListViewModel by viewModels()

    private val goToRegularMoment = { momentId: UUID ->
        findNavController().navigate(
            MomentsListFragmentDirections.showMomentDetail(momentId)
        )
    }

    private val goToSharedMoment = { momentId: UUID ->
        // TODO: Where should this go?
        findNavController().navigate(
            MomentsListFragmentDirections.showMomentDetail(momentId)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMomentsListBinding.inflate(inflater, container, false)
        binding.momentsRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                momentsListViewModel.uiState.collect { uiState ->
                    binding.momentsRecyclerView.adapter = MomentsListAdapter(
                        uiState.moments, // TODO: What about shared moments?
                        goToRegularMoment,
                        goToSharedMoment)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_moments_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_moment -> {
                showNewMoment()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showNewMoment() {
        viewLifecycleOwner.lifecycleScope.launch {
            val newMoment = Moment(
                id = UUID.randomUUID(),
                title = "",
                description = "",
                timestamp = Date()
            )
            momentsListViewModel.addMoment(newMoment)
            findNavController().navigate(
                MomentsListFragmentDirections.showMomentDetail(newMoment.id)
            )
        }
    }
}