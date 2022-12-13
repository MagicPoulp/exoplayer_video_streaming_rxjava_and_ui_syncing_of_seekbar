package com.canal.android.test.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.canal.android.test.R
import org.koin.android.ext.android.getKoin

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
}
