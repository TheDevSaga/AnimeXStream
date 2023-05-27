package com.arosara.anisaw.ui.main.player.source.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.di.DispatcherModule
import com.arosara.anisaw.utils.model.EpisodeInfo
import com.arosara.anisaw.utils.parser.HtmlParser
import com.arosara.anisaw.utils.rertofit.NetworkInterface
import okhttp3.ResponseBody
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class EpisodeRemoteRepository @Inject constructor(
    private val service: NetworkInterface.EpisodeDataService,
    @DispatcherModule.IoDispatcher val dispatcher: CoroutineDispatcher
) {

    suspend fun fetchEpisodeData(url: String): EpisodeInfo {
        return withContext(dispatcher) {
            val response = service.fetchEpisodeMediaUrl(Utils.getHeader(), url)
            HtmlParser.parseMediaUrl(response = response.string())
        }
    }

    //Return Params for Ajax Url
    suspend fun fetchAjaxUrl(url: String): String {
        return withContext(dispatcher) {
            val response = service.fetchAjaxUrlForM3U8(Utils.getHeader(), url)
            HtmlParser.parseEncryptAjaxParameters(response = response.string(),url)

        }
    }

    suspend fun fetchM3U8Url(url: String): ResponseBody {
        return withContext(dispatcher) {
            service.fetchM3U8Url(url)
        }
    }
}