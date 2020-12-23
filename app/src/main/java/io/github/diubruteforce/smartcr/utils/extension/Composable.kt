package io.github.diubruteforce.smartcr.utils.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AmbientContext
import dagger.hilt.android.internal.managers.ViewComponentManager
import io.github.diubruteforce.smartcr.MainActivity

@Composable
fun getMainActivity(): MainActivity {
    val context = AmbientContext.current

    return try {
        context as MainActivity
    } catch (ex: ClassCastException) {
        val fragment = context as ViewComponentManager.FragmentContextWrapper
        fragment.fragment.requireActivity() as MainActivity
    }
}