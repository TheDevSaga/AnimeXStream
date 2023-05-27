package com.arosara.anisaw.ui.main.home.source

import com.arosara.anisaw.utils.Result
import com.arosara.anisaw.utils.model.AnimeMetaModel

interface HomeDataSource {
    suspend fun getHomeData(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>>
    suspend fun saveData(animeList: ArrayList<AnimeMetaModel>)
    suspend fun removeData()
}

class InvalidAnimeTypeException(message: String = "Invalid Anime Type") : Exception(message)