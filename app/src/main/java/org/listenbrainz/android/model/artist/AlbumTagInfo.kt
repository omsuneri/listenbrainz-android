package org.listenbrainz.android.model.artist

import com.google.gson.annotations.SerializedName

data class AlbumTagInfo(
    val count: Int? = null,
    @SerializedName("genre_mbid") val genreMbid: String? = null,
    val tag: String? = null
)