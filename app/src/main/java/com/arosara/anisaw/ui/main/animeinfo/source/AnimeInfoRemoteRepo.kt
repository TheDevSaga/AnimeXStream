package com.arosara.anisaw.ui.main.animeinfo.source

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.di.DispatcherModule
import com.arosara.anisaw.utils.model.AnimeInfoModel
import com.arosara.anisaw.utils.model.EpisodeModel
import com.arosara.anisaw.utils.parser.HtmlParser
import com.arosara.anisaw.utils.rertofit.NetworkInterface
import com.arosara.anisaw.utils.rertofit.RetrofitHelper
import javax.inject.Inject

class AnimeInfoRemoteRepo @Inject constructor(@DispatcherModule.IoDispatcher val dispatcher: CoroutineDispatcher) {
    private val retrofit = RetrofitHelper.getRetrofitInstance()


    suspend fun fetchAnimeInfo(categoryUrl: String): AnimeInfoModel {
        return withContext(dispatcher) {
            val animeInfoService = retrofit.create(NetworkInterface.FetchAnimeInfo::class.java)
            val data = animeInfoService.get(Utils.getHeader(), categoryUrl)
            HtmlParser.parseAnimeInfo(data.string())
        }

    }

    suspend fun fetchEpisodeList(
        id: String,
        alias: String
    ): ArrayList<EpisodeModel> {

        return withContext(dispatcher) {
            val animeEpisodeService = retrofit.create(NetworkInterface.FetchEpisodeList::class.java)
            val data = animeEpisodeService.get(
                id = id,
                alias = alias,
                header = Utils.getHeader()
            )
            HtmlParser.fetchEpisodeList(data.string())
        }
    }
}