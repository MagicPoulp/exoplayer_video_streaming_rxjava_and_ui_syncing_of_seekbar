package com.canal.android.test.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.canal.android.test.domain.model.NavigateTo
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

abstract class BaseFragment<UI_MODEL, T : ViewBinding> : Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    abstract val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> T

    private val uiNavigator: UiNavigator by inject { parametersOf(activity) }

    lateinit var navigateTo: NavigateTo
    internal abstract val viewModel: BaseViewModel<UI_MODEL>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            (bundle.getParcelable(ARG_NAVIGATE_TO) as? NavigateTo)?.let {
                navigateTo = it
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = viewBindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun navigateTo(navigateTo: NavigateTo) {
        when (navigateTo) {
            is NavigateTo.DetailPage -> uiNavigator.displayDetailPage(navigateTo)
            is NavigateTo.QuickTime -> uiNavigator.displayPlayer(navigateTo)
        }
    }

    companion object {

        fun createArguments(navigateTo: NavigateTo): Bundle = bundleOf(Pair(ARG_NAVIGATE_TO, navigateTo))

        private const val ARG_NAVIGATE_TO = "ARG_NAVIGATE_TO"
    }
}
