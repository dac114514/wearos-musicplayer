package com.example.suiting.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TitleCard

@Composable
fun MusicListScreen(
    musicList: List<MusicItem>,
    onMusicClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberScalingLazyListState()

    ScreenScaffold(scrollState = listState) {
        ScalingLazyColumn(
            state = listState,
            modifier = modifier.fillMaxSize()
        ) {
            item {
                ListHeader {
                    Text(
                        text = "随听",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            if (musicList.isEmpty()) {
                item {
                    Text(
                        text = "未找到音乐\n请确保已授予存储权限",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                musicListItems(musicList, onMusicClick)
            }
        }
    }
}

// Extracted to avoid `this` scope confusion inside the DSL
private fun ScalingLazyListScope.musicListItems(
    musicList: List<MusicItem>,
    onMusicClick: (Int) -> Unit
) {
    items(
        count = musicList.size,
        key = { index -> musicList[index].id }
    ) { index ->
        val music = musicList[index]
        TitleCard(
            title = {
                Text(
                    text = music.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            onClick = { onMusicClick(index) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = music.artist,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
