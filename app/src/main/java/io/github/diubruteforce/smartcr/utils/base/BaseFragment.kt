package io.github.diubruteforce.smartcr.utils.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


abstract class BaseFragment : Fragment(), BaseFragmentCallback {
    private val handler = CoroutineExceptionHandler { _, exception ->
        Timber.e(exception)
    }

    // This method will had handler a default Exception handler
    // By this we will catch all the exception inside the
    // coroutine
    protected fun launchInLifecycleScope(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) = lifecycleScope.launch(context = context + handler, start = start, block = block)

    override fun onBackPress() {
        findNavController().navigateUp()
    }
}

interface BaseFragmentCallback {
    fun onBackPress()
}

class BaseFragmentCallbackStub : BaseFragmentCallback {
    override fun onBackPress() {}
}