package com.arosara.anisaw.utils.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class EpisodeInfo(
    @PrimaryKey
    var episodeUrl: String = "",
    var vidCdnUrl: String = "",
    var nextEpisodeUrl: String? = null,
    var previousEpisodeUrl: String? = null
) : RealmObject()