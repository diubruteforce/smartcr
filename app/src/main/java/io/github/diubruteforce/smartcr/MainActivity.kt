package io.github.diubruteforce.smartcr

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.platform.setContent
import dagger.hilt.android.AndroidEntryPoint
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartCRTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                }
            }
        }
    }

    /*
    * This method is called by fragment to get the theme
    * By changing this theme I am changing the splash theme
    * In Splash theme I have green background for the window
    * copied from https://stackoverflow.com/a/39150319/6307259
    * */
    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.Theme_SmartCR_NoActionBar, true)

        return theme
    }
}