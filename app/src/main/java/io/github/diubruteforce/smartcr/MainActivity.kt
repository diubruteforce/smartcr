package io.github.diubruteforce.smartcr

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import io.github.diubruteforce.smartcr.di.ViewModelAssistedFactoryMap
import io.github.diubruteforce.smartcr.navigation.SmartCRApp
import io.github.diubruteforce.smartcr.ui.theme.SmartCRTheme
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // This is for the workaround to use hilt and navigation compose together
    @Inject
    lateinit var viewModelAssistedFactories: ViewModelAssistedFactoryMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            SmartCRTheme {
                ProvideWindowInsets {
                    SmartCRApp()
                }
            }
        }
    }

    // region: Call
    fun makeCall(phoneNumber: String) {
        val uri = Uri.parse("tel:$phoneNumber")
        val intent = Intent(Intent.ACTION_DIAL, uri)

        startActivity(intent)
    }

    // region: ImagePicker
    private lateinit var onImagePicked: (uri: Uri?) -> Unit

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { intent ->
        Timber.tag("UploadProfile").d("Result Found $intent")
        onImagePicked.invoke(intent.data?.data)
    }

    fun pickImage(onImagePicked: (uri: Uri?) -> Unit) {
        this.onImagePicked = onImagePicked

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        imagePicker.launch(intent)
    }
    // endregion

    // region: FilePicker
    private lateinit var onFilePicked: (uri: Uri?) -> Unit

    private val filePicker = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { intent ->
        Timber.tag("UploadProfile").d("Result Found $intent")
        onFilePicked.invoke(intent.data?.data)
    }

    fun pickFile(onFilePicked: (uri: Uri?) -> Unit) {
        this.onFilePicked = onFilePicked

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        filePicker.launch(intent)
    }
    // endregion

    // region: Permission Requester
    private lateinit var onPermission: (isGranted: Boolean) -> Unit

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        onPermission.invoke(it)
    }

    fun requestPermission(permission: String, onPermission: (Boolean) -> Unit) {
        this.onPermission = onPermission

        permissionLauncher.launch(permission)
    }
    // endregion

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