package com.example.suiting.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.suiting.presentation.theme.WearAppTheme

class MainActivity : ComponentActivity() {

    private var musicService: MusicService? = null
    private var isBound = false

    private var musicList by mutableStateOf<List<MusicItem>>(emptyList())
    private var currentIndex by mutableIntStateOf(0)
    private var isPlaying by mutableStateOf(false)
    private var progress by mutableFloatStateOf(0f)
    private var volume by mutableFloatStateOf(0.8f)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            musicService = (binder as MusicService.MusicBinder).getService()
            isBound = true
            musicService?.onProgressUpdate = { pos, dur ->
                if (dur > 0) progress = pos.toFloat() / dur.toFloat()
            }
            musicService?.onCompletionListener = { playNext() }
            musicService?.setVolume(volume)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.any { it }) loadMusic()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Bind to service
        val intent = Intent(this, MusicService::class.java)
        startService(intent)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

        requestPermissionsIfNeeded()

        setContent {
            WearAppTheme {
                val navController = rememberSwipeDismissableNavController()

                AppScaffold {
                    SwipeDismissableNavHost(
                        navController = navController,
                        startDestination = "list"
                    ) {
                        composable("list") {
                            MusicListScreen(
                                musicList = musicList,
                                onMusicClick = { idx ->
                                    currentIndex = idx
                                    playCurrentSong()
                                    navController.navigate("player")
                                }
                            )
                        }
                        composable("player") {
                            if (musicList.isNotEmpty()) {
                                PlayerScreen(
                                    music = musicList[currentIndex],
                                    isPlaying = isPlaying,
                                    progress = progress,
                                    volume = volume,
                                    onPlayPause = { togglePlayPause() },
                                    onNext = { playNext(); },
                                    onPrevious = { playPrevious(); },
                                    onVolumeChange = { v ->
                                        volume = v
                                        musicService?.setVolume(v)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestPermissionsIfNeeded() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        val needRequest = perms.any {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        if (needRequest) {
            permissionLauncher.launch(perms)
        } else {
            loadMusic()
        }
    }

    private fun loadMusic() {
        musicList = MusicRepository.loadAllMusic(this)
    }

    private fun playCurrentSong() {
        if (musicList.isEmpty()) return
        val song = musicList[currentIndex]
        musicService?.playMusic(song.data)
        isPlaying = true
        progress = 0f
    }

    private fun togglePlayPause() {
        musicService?.let {
            val nowPlaying = it.pauseResume()
            isPlaying = nowPlaying
        }
    }

    private fun playNext() {
        if (musicList.isEmpty()) return
        currentIndex = (currentIndex + 1) % musicList.size
        playCurrentSong()
    }

    private fun playPrevious() {
        if (musicList.isEmpty()) return
        currentIndex = if (currentIndex == 0) musicList.size - 1 else currentIndex - 1
        playCurrentSong()
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
        super.onDestroy()
    }
}
