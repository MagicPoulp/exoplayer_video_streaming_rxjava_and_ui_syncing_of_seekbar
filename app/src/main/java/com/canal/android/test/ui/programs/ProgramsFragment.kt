package com.canal.android.test.ui.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.canal.android.test.databinding.FragmentProgramsBinding
import com.canal.android.test.ui.programs.view.adapter.ProgramsAdapter
import com.canal.android.test.ui.programs.view.adapter.ProgramsDecorator
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProgramsFragment : Fragment() {

    private val viewModel: ProgramsViewModel by viewModel()
    private val recyclerAdapter = ProgramsAdapter()

    private var _binding: FragmentProgramsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProgramsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.programsRecycler.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = recyclerAdapter
            addItemDecoration(ProgramsDecorator(view.context))
        }

        viewModel.uiData.observe(viewLifecycleOwner) { pageProgramsUi ->
            recyclerAdapter.submitList(pageProgramsUi.programs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}