package com.canal.android.test.ui.common

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.NavHostFragment
import com.canal.android.test.R
import com.canal.android.test.domain.model.NavigateTo
import com.canal.android.test.ui.MainActivity.Companion.PROPERTY_ACTIVITY
import org.koin.java.KoinJavaComponent.getKoin

class FragmentNavigator : UiNavigator {

    private val activity: AppCompatActivity?
        get() = getKoin().getProperty(PROPERTY_ACTIVITY)

    private fun getNavHost(): NavHostFragment? {
        return activity?.supportFragmentManager?.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment?
    }

    override fun displayDetailPage(navigateTo: NavigateTo.DetailPage) {
        val arguments =
                BaseFragment.createArguments(navigateTo)
        navigateTo(
                resId = R.id.fragment_detail_page,
                arguments = arguments
        )
    }

    override fun displayPlayer(navigateTo: NavigateTo.QuickTime) {
        val arguments =
                BaseFragment.createArguments(navigateTo)
        navigateTo(
                resId = R.id.fragment_player,
                arguments = arguments
        )
    }

    fun navigateTo(
            @IdRes resId: Int,
            arguments: Bundle?
    ) {
        val navController = getNavHost()?.navController ?: return
        navController.navigate(resId, arguments)
    }
}