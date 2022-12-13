package com.canal.android.test.ui.programs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.canal.android.test.databinding.FragmentProgramsBinding
import com.canal.android.test.ui.common.BaseFragment
import com.canal.android.test.ui.programs.model.PageProgramsUi
import com.canal.android.test.ui.programs.view.adapter.ProgramsAdapter
import com.canal.android.test.ui.programs.view.adapter.ProgramsDecorator
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProgramsFragment : BaseFragment<PageProgramsUi, FragmentProgramsBinding>() {

    override val viewModel: ProgramsViewModel by viewModel()
    private val recyclerAdapter = ProgramsAdapter(
            navigateTo = { navigateTo(it) }
    )

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentProgramsBinding
        get() = FragmentProgramsBinding::inflate

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
}