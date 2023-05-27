package com.arosara.anisaw.utils.model

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class AnimeInfoModel(

    @PrimaryKey
    var categoryUrl: String = "",
    var animeTitle: String = "",
    var imageUrl: String = "",
    var id: String = "",
    var type: String = "",
    var releasedTime: String = "",
    var status: String = "",
    var genre: RealmList<GenreModel> = RealmList(),
    var plotSummary: String = "",
    var alias: String = "",
) : RealmObject()