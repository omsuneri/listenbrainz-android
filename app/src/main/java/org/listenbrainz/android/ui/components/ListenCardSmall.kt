package org.listenbrainz.android.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import org.listenbrainz.android.R
import org.listenbrainz.android.model.Metadata
import org.listenbrainz.android.model.ResponseError
import org.listenbrainz.android.model.feed.FeedListenArtist
import org.listenbrainz.android.ui.screens.feed.SocialDropdownDefault
import org.listenbrainz.android.ui.theme.ListenBrainzTheme

/**Small configuration of listen card.
 * This composable has fixed height used from [ListenBrainzTheme.sizes].
 * @param enableTrailingContent True means card will reserve space for trailing content.
 * @param trailingContent **MUST** use the given modifier in its top level layout.
 * @param dropDown The dropdown we want to show when icon is clicked.
 * @param dropDownState State of the dropdown icon. Usually, in case of a lazy list, we would want to supply a
 * mutable state list or map with index/key rather than maintaining a state for each listen card. False means
 * dropdown should remain closed.
 * @param enableBlurbContent True means card will shape itself to accommodate content whether it is empty or not.
 * @author jasje*/
@Composable
fun ListenCardSmall(
    modifier: Modifier = Modifier,
    trackName: String,
    artists: List<FeedListenArtist?>,
    coverArtUrl: String?,
    listenCount: Int? = null,
    @DrawableRes errorAlbumArt: Int = R.drawable.ic_coverartarchive_logo_no_text,
    enableDropdownIcon: Boolean = false,
    onDropdownIconClick: () -> Unit = {},
    dropDown: @Composable () -> Unit = {},
    enableTrailingContent: Boolean = false,
    trailingContent: @Composable (modifier: Modifier) -> Unit = {},
    enableBlurbContent: Boolean = false,
    blurbContent: @Composable (ColumnScope.(modifier: Modifier) -> Unit) = {},
    color: Color = ListenBrainzTheme.colorScheme.level1,
    titleColor: Color = ListenBrainzTheme.colorScheme.listenText,
    subtitleColor: Color = titleColor.copy(alpha = 0.7f),
    goToArtistPage: (String) -> Unit,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = true) { onClick() },
        shape = ListenBrainzTheme.shapes.listenCardSmall,
        shadowElevation = 4.dp,
        color = color
    ) {
        Column {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(ListenBrainzTheme.sizes.listenCardHeight),
                contentAlignment = Alignment.CenterStart
            ) {
        
                val (mainContentFraction, trailingContentFraction, dropDownButtonFraction) = remember(enableDropdownIcon, enableTrailingContent) {
                    when {
                        enableDropdownIcon && enableTrailingContent -> Triple(0.60f, 0.80f, 0.20f)    // 0.20f (0.08f in whole) for dropdown and 0.80f (0.32f in whole) for trailing content
                        enableDropdownIcon && !enableTrailingContent -> Triple(0.92f, 0f, 1f) // 0.10f for dropdown
                        !enableDropdownIcon && enableTrailingContent -> Triple(0.70f, 1f, 0f)   // 0.30f for trailing content
                        else -> Triple(1f, 0f, 0f)
                    }
                }
        
                Row(
                    modifier = Modifier.fillMaxWidth(mainContentFraction),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
            
                    AlbumArt(coverArtUrl, errorAlbumArt)
            
                    Spacer(modifier = Modifier.width(ListenBrainzTheme.paddings.coverArtAndTextGap))
            
                    TitleAndSubtitle(
                        modifier = Modifier.padding(end = 6.dp),
                        title = trackName,
                        artists = artists,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        goToArtistPage = goToArtistPage
                    )
                }

                Box(
                    modifier = modifier
                        .fillMaxWidth(1f - mainContentFraction)
                        .align(Alignment.CenterEnd),
                    contentAlignment = Alignment.Center
                ) {
                    if(listenCount != null){
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ListenBrainzTheme.colorScheme.followerChipSelected)
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = listenCount.toString(),
                                color = Color.Black
                            )
                        }
                    }
                    // Trailing content
                    if (enableTrailingContent) {
                        trailingContent(
                            Modifier
                                .fillMaxWidth(trailingContentFraction)
                                .align(Alignment.CenterStart)
                                .padding(end = 6.dp)
                        )
                    }
            
                    // Dropdown Icon
                    if (enableDropdownIcon) {
                        DropdownButton(
                            modifier = Modifier
                                .fillMaxWidth(dropDownButtonFraction)
                                .align(Alignment.CenterEnd),
                            onDropdownIconClick = {
                                onDropdownIconClick()
                            }
                        )
                        dropDown()
                    }
            
                }
        
            }
            
            if (enableBlurbContent) {
                HorizontalDivider()
                blurbContent(Modifier.padding(ListenBrainzTheme.paddings.insideCard))
            }
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
@Composable
inline fun ListenCardSmallDefault(
    modifier: Modifier = Modifier,
    metadata: Metadata,
    coverArtUrl: String?,
    listenCount: Int? = null,
    @DrawableRes errorAlbumArt: Int = R.drawable.ic_coverartarchive_logo_no_text,
    enableTrailingContent: Boolean = false,
    noinline trailingContent: @Composable (modifier: Modifier) -> Unit = {},
    enableBlurbContent: Boolean = false,
    noinline blurbContent: @Composable (ColumnScope.(modifier: Modifier) -> Unit) = {},
    color: Color = ListenBrainzTheme.colorScheme.level1,
    titleColor: Color = ListenBrainzTheme.colorScheme.listenText,
    subtitleColor: Color = titleColor.copy(alpha = 0.7f),
    noinline onDropdownError: suspend CoroutineScope.(error: ResponseError) -> Unit,
    noinline onDropdownSuccess: suspend CoroutineScope.(message: String) -> Unit,
    noinline goToArtistPage: (String) -> Unit,
    noinline onClick: () -> Unit,
) {
    metadata.trackMetadata?.let {
        var isDropdownExpanded by remember { mutableStateOf(false) }

        ListenCardSmall(
            modifier = modifier,
            trackName = metadata.trackMetadata.trackName,
            artists = metadata.trackMetadata.mbidMapping?.artists ?: listOf(
                FeedListenArtist(metadata.trackMetadata.artistName , null, "")
            ),
            coverArtUrl = coverArtUrl,
            listenCount = listenCount,
            errorAlbumArt = errorAlbumArt,
            enableDropdownIcon = true,
            onDropdownIconClick = {
                isDropdownExpanded = !isDropdownExpanded
            },
            dropDown = {
                SocialDropdownDefault(
                    isExpanded = isDropdownExpanded,
                    metadata = metadata,
                    onError = onDropdownError,
                    onSuccess = onDropdownSuccess,
                    onDropdownDismiss = { isDropdownExpanded = !isDropdownExpanded },
                )
            },
            enableTrailingContent = enableTrailingContent,
            trailingContent = trailingContent,
            enableBlurbContent = enableBlurbContent,
            blurbContent = blurbContent,
            color = color,
            titleColor = titleColor,
            subtitleColor = subtitleColor,
            goToArtistPage = goToArtistPage,
            onClick = onClick
        )
    }
}

@Composable
private fun DropdownButton(modifier: Modifier = Modifier, onDropdownIconClick: () -> Unit) {
    IconButton(
        modifier = modifier,
        onClick = onDropdownIconClick
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_options),
            contentDescription = "",
            tint = ListenBrainzTheme.colorScheme.hint,
            modifier = Modifier.padding(horizontal = ListenBrainzTheme.paddings.insideCard)
        )
    }
}

@Composable
private fun AlbumArt(
    coverArtUrl: String?,
    errorAlbumArt: Int = R.drawable.ic_coverartarchive_logo_no_text
) {
    // Use this for previews
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(coverArtUrl)
            .build(),
        fallback = painterResource(id = errorAlbumArt),
        modifier = Modifier.size(ListenBrainzTheme.sizes.listenCardHeight),
        contentScale = ContentScale.Fit,
        placeholder = painterResource(id = errorAlbumArt),
        filterQuality = FilterQuality.Low,
        contentDescription = "Album Cover Art"
    )
}

/** [title] corresponds to release name and [artists] corresponds to all the artists as per
 * MB's credit system.
 * The [artists] list consists of artist names and join phrases used to join multiple artists together*/
@Composable
fun TitleAndSubtitle(
    modifier: Modifier = Modifier,
    title: String,
    artists: List<FeedListenArtist?>,
    alignment: Alignment.Horizontal = Alignment.Start,
    titleColor: Color = ListenBrainzTheme.colorScheme.listenText,
    subtitleColor: Color = titleColor.copy(alpha = 0.7f),
    goToArtistPage: (String) -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = alignment
    ) {
        Text(
            text = title,
            style = ListenBrainzTheme.textStyles.listenTitle,
            color = titleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row {
            artists.forEach { artist ->
                artist?.artistCreditName?.let {
                    Text(
                        modifier = Modifier.clickable {
                            if(artist.artistMbid != null){
                                goToArtistPage(artist.artistMbid)
                            }
                        },
                        text = artist.artistCreditName + (artist.joinPhrase ?: ""),
                        style = ListenBrainzTheme.textStyles.listenSubtitle,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListenCardSmallPreview() {
    ListenBrainzTheme {
        ListenCardSmall(
            trackName = "Title",
            artists = listOf(FeedListenArtist("Artist", "", "")),
            coverArtUrl = "",
            enableDropdownIcon = true,
            enableTrailingContent = true,
            trailingContent = { modifier ->
                Column(modifier = modifier) {
                    TitleAndSubtitle(title = "Userrrrrrrrrrrrrr", goToArtistPage = {}, artists = listOf(FeedListenArtist("Artist", "", "")),)
                }
            },
            enableBlurbContent = true,
            goToArtistPage = {},
            blurbContent = {
                Column(modifier = it) {
                    Text(text = "Blurb Content", color = ListenBrainzTheme.colorScheme.text)
                }
            },
        ) {}
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListenCardSmallNoBlurbContentPreview() {
    ListenBrainzTheme {
        ListenCardSmall(
            trackName = "Title",
            artists = listOf(FeedListenArtist("Artist", "", "")),
            coverArtUrl = "",
            enableDropdownIcon = true,
            enableTrailingContent = true,
            trailingContent = { modifier ->
                Column(modifier = modifier) {
                    TitleAndSubtitle(title = "Userrrrrrrrrrrrrr", goToArtistPage = {}, artists = listOf(FeedListenArtist("Artist", "", "")),)
                }
            },
            goToArtistPage = {},
            enableBlurbContent = false
        ) {}
    }
}

@Preview
@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
private fun ListenCardSmallSimplePreview() {
    ListenBrainzTheme {
        ListenCardSmall(
            trackName = "Title",
            artists = listOf(FeedListenArtist("Artist", "", "")),
            coverArtUrl = "",
            enableDropdownIcon = true,
            enableTrailingContent = false,
            goToArtistPage = {},
            enableBlurbContent = false
        ) {}
    }
}