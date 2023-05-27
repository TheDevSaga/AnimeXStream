package com.arosara.anisaw.ui.main.home.source.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.arosara.anisaw.ui.main.home.source.HomeDataSource
import com.arosara.anisaw.ui.main.home.source.InvalidAnimeTypeException
import com.arosara.anisaw.utils.Result
import com.arosara.anisaw.utils.constants.C
import com.arosara.anisaw.utils.di.AppModules
import com.arosara.anisaw.utils.di.DispatcherModule
import com.arosara.anisaw.utils.model.AnimeMetaModel
import com.arosara.anisaw.utils.parser.HtmlParser
import com.arosara.anisaw.utils.rertofit.NetworkInterface
import okhttp3.ResponseBody
import java.lang.Exception
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class HomeRemoteRepository
@Inject constructor(
    private val homeNetworkService: NetworkInterface.HomeDataService,
    @AppModules.RequestHeader
    val header: Map<String, String>,
    @DispatcherModule.IoDispatcher
    val ioDispatcher: CoroutineDispatcher
) : HomeDataSource {


    override suspend fun getHomeData(page: Int, type: Int): Result<ArrayList<AnimeMetaModel>> {
        return withContext(ioDispatcher) {
            try {
                val response = callServiceBasedOnType(page, type).string()
                val data = HtmlParser.parseDataBasedOnType(response, type)
                Result.Success(data)
            } catch (exc: Exception) {
                Result.Error(exc)
            }
        }
    }

    override suspend fun saveData(animeList: ArrayList<AnimeMetaModel>) {
        //Will Implement Later for Network Saving
    }

    override suspend fun removeData() {
        //Will Implement for later Removing from Network
    }

    private suspend fun callServiceBasedOnType(page: Int, type: Int): ResponseBody {
        return when (type) {
            C.TYPE_RECENT_SUB -> homeNetworkService.fetchRecentSubOrDub(
                header,
                page,
                C.RECENT_SUB
            )
            C.TYPE_RECENT_DUB -> homeNetworkService.fetchRecentSubOrDub(
                header,
                page,
                C.RECENT_DUB
            )
            C.TYPE_MOVIE -> homeNetworkService.fetchMovies(header, page)
            C.TYPE_NEW_SEASON -> homeNetworkService.fetchNewestSeason(header, page)
            C.TYPE_POPULAR_ANIME -> homeNetworkService.fetchPopularFromAjax(header, page)
            else -> throw InvalidAnimeTypeException()
        }
    }

}