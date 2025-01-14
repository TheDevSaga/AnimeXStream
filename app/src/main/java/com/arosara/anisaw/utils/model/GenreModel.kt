package com.arosara.anisaw.utils.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GenreModel(
    @PrimaryKey
    var genreUrl: String ="",
    var genreName: String = ""
) : RealmObject()