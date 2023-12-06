package com.canal.android.test.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.canal.android.test.R
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        getKoin().setProperty(PROPERTY_ACTIVITY, this)
    }

    override fun onDestroy() {
        getKoin().deleteProperty(PROPERTY_ACTIVITY)
        super.onDestroy()
    }

    companion object {
        const val PROPERTY_ACTIVITY = "activity"
    }

    fun displayGenericErrorDialog() {
        runOnUiThread {
            val dialog = Dialog(this)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.generic_error_dialog)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val closeButton = dialog.findViewById<TextView>(R.id.close_button)
            closeButton.setOnClickListener {
                runOnUiThread {
                    dialog.dismiss()
                    popBackOneNavigation()
                }
            }
            dialog.show()
        }
    }

    private fun popBackOneNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.currentDestination?.id?.let {
            if (it != R.id.fragment_programs) {
                navController.popBackStack(it, true)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment)
        navController.currentDestination?.id?.let {
            if (it != R.id.fragment_programs) {
                popBackOneNavigation()
            }
        }
    }
}
