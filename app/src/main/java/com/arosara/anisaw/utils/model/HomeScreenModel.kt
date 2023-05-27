package com.arosara.anisaw.utils.model

import kotlin.collections.ArrayList

data class HomeScreenModel(
    var typeValue: Int,
    var type: String = "",
    var animeList: ArrayList<AnimeMetaModel>? = null
)