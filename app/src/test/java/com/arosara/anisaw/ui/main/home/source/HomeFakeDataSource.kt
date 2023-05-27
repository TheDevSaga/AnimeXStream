package com.arosara.anisaw.ui.main.home.source

import com.arosara.anisaw.utils.Result
import com.arosara.anisaw.utils.constants.C
import com.arosara.anisaw.utils.model.AnimeMetaModel
import com.arosara.anisaw.utils.parser.NetworkResponse
import java.lang.Exception

class HomeFakeDataSource : HomeDataSource {
    override suspend fun getHomeData(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {

        val data = listOf(
            AnimeMetaModel(
                title = "One Piece",
                typeValue = type,
                categoryUrl = "URL",
                genreList = null,
                ID = 123,
                episodeNumber = "900",
            )
        )
        return try {
            getDataBasedOnType(type)
            Result.Success(ArrayList(data))
        } catch (exc: Exception) {
            Result.Error(exc)
        }

    }

    override suspend fun saveData(animeList: ArrayList<AnimeMetaModel>) {
        TODO("Not yet implemented")
    }

    override suspend fun removeData() {
        TODO("Not yet implemented")
    }

    private fun getDataBasedOnType(type: Int): String {
        return when (type) {
            C.TYPE_RECENT_SUB, C.TYPE_RECENT_DUB -> NetworkResponse.recentSubResponse
            C.TYPE_MOVIE, C.TYPE_NEW_SEASON -> NetworkResponse.movieResponse
            C.TYPE_POPULAR_ANIME -> NetworkResponse.popularResponse
            else -> throw InvalidAnimeTypeException()
        }
    }


}