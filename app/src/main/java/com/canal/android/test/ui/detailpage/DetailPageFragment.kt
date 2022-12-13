package com.canal.android.test.ui.detailpage

import android.view.LayoutInflater
import android.view.ViewGroup
import com.canal.android.test.databinding.FragmentDetailPageBinding
import com.canal.android.test.ui.common.BaseFragment
import com.canal.android.test.ui.detailpage.model.DetailPageUi
import com.canal.android.test.ui.detailpage.model.DetailPageViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class DetailPageFragment : BaseFragment<DetailPageUi, FragmentDetailPageBinding>() {

    override val viewModel: DetailPageViewModel by viewModel { parametersOf(navigateTo) }

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentDetailPageBinding
        get() = FragmentDetailPageBinding::inflate
}