package ru.spbu.apmath.nalisin.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import com.arkivanov.decompose.DefaultComponentContext
import ru.spbu.apmath.nalisin.app.components.main.MainContent
import ru.spbu.apmath.nalisin.app.di.createMainDiComponent

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showContent()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            showContent()
        }
    }

    private fun showContent() {
        val diComponent =
            createMainDiComponent(componentContext = DefaultComponentContext(lifecycle = lifecycle))
        setContent {
            MainContent(component = diComponent.mainComponent)
        }
    }
}
