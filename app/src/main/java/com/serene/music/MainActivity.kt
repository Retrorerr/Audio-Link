package com.serene.music

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.serene.music.service.MusicServiceConnection
import com.serene.music.ui.navigation.SereneNavGraph
import com.serene.music.ui.theme.SereneTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var musicServiceConnection: MusicServiceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SereneTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SereneNavGraph(musicServiceConnection = musicServiceConnection)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        musicServiceConnection.connect()
    }

    override fun onStop() {
        super.onStop()
        musicServiceConnection.disconnect()
    }
}
