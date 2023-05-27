package com.arosara.anisaw.utils.model


open class Content(
    var urls: ArrayList<Source> = ArrayList(),
    var animeName: String = "",
    var episodeName: String? = "",
    var episodeUrl: String? = "",
    var nextEpisodeUrl: String? = null,
    var previousEpisodeUrl: String? = null,
    var watchedDuration: Long = 0,
    var duration: Long = 0,
)