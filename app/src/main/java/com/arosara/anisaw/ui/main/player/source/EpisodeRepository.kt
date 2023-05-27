package com.arosara.anisaw.ui.main.player.source

import com.arosara.anisaw.ui.main.player.source.local.EpisodeLocalRepository
import com.arosara.anisaw.ui.main.player.source.remote.EpisodeRemoteRepository
import com.arosara.anisaw.utils.model.Content
import com.arosara.anisaw.utils.model.EpisodeInfo
import com.arosara.anisaw.utils.model.M3U8FromAjaxModel
import com.arosara.anisaw.utils.model.WatchedEpisode
import com.arosara.anisaw.utils.parser.HtmlParser
import timber.log.Timber
import javax.inject.Inject


class EpisodeRepository @Inject constructor(
    private val localRepo: EpisodeLocalRepository,
    private val remoteRepo: EpisodeRemoteRepository
) {


    fun saveWatchProgress(content: Content) {
        localRepo.saveWatchProgress(content = content)
    }

    suspend fun fetchEpisodeData(url: String, forceRefresh: Boolean = false): EpisodeInfo {
        var episodeInfo = localRepo.getEpisodeInfo(url)
        return if (episodeInfo != null && !forceRefresh) {
            Timber.e("Fetching Episode Data from DB  ${episodeInfo.episodeUrl}")
            episodeInfo
        } else {
            episodeInfo = remoteRepo.fetchEpisodeData(url)
            episodeInfo.episodeUrl = url
            episodeInfo
        }
    }

    suspend fun fetchAjaxParams(url: String, forceRefresh: Boolean = false): String {
        var ajaxParams = localRepo.getAjaxParams(url)
        return if (!ajaxParams.isNullOrEmpty() && !forceRefresh) {
            Timber.e("Getting Ajax Params from DB  $ajaxParams")
            ajaxParams
        } else {
            ajaxParams = remoteRepo.fetchAjaxUrl(url = url)
            localRepo.saveAjaxParams(ajaxParams = ajaxParams, vidCdnUrl = url)
            ajaxParams
        }


    }

    suspend fun fetchM3U8DataFromAjax(url: String): M3U8FromAjaxModel {
        val data = remoteRepo.fetchM3U8Url(url)
        return HtmlParser.parseEncryptedData(data.string())
    }

    fun getWatchDuration(id: Int): WatchedEpisode? {
        return localRepo.fetchWatchDuration(id)
    }


}