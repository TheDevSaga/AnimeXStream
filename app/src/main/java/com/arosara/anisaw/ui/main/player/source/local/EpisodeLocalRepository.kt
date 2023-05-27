package com.arosara.anisaw.ui.main.player.source.local

import android.net.Uri
import io.realm.Realm
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import com.arosara.anisaw.utils.di.DispatcherModule
import com.arosara.anisaw.utils.model.Content
import com.arosara.anisaw.utils.model.EpisodeInfo
import com.arosara.anisaw.utils.model.VidCdnModel
import com.arosara.anisaw.utils.model.WatchedEpisode
import com.arosara.anisaw.utils.realm.InitalizeRealm
import javax.inject.Inject

class EpisodeLocalRepository @Inject constructor(
    @DispatcherModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) {


    fun fetchWatchDuration(id: Int): WatchedEpisode? {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        return realm.where(WatchedEpisode::class.java).equalTo("id", id).findFirst()

    }

    suspend fun saveAjaxParams(vidCdnUrl: String, ajaxParams: String) {
        withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            val cdnId = Uri.parse(vidCdnUrl).getQueryParameter("id").toString()
            val vidCdnModel = VidCdnModel(
                vidCdnId = cdnId,
                ajaxParams = ajaxParams
            )
            realm.use { instance ->
                instance.executeTransaction {
                    it.insertOrUpdate(vidCdnModel)
                }
            }
        }
    }


    suspend fun getEpisodeInfo(episodeUrl: String): EpisodeInfo? {
        return withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use {
                val episodeInfo =
                    it.where(EpisodeInfo::class.java).equalTo("episodeUrl", episodeUrl).findFirst()
                if (episodeInfo != null) {
                    it.copyFromRealm(episodeInfo)
                } else {
                    null
                }
            }
        }
    }

    suspend fun saveEpisodeInfo(episodeInfo: EpisodeInfo) {
        withContext(dispatcher) {
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use { instance ->
                instance.executeTransaction {
                    it.insertOrUpdate(episodeInfo)
                }
            }
        }
    }

    suspend fun getAjaxParams(vidCdnUrl: String): String? {
        return withContext(dispatcher) {
            val cdnId = Uri.parse(vidCdnUrl).getQueryParameter("id").toString()
            val realm = Realm.getInstance(InitalizeRealm.getConfig())
            realm.use {
                val vidCdnModel =
                    it.where(VidCdnModel::class.java).equalTo("vidCdnId", cdnId).findFirst()
                vidCdnModel?.ajaxParams
            }
        }

    }


    fun saveWatchProgress(content: Content) {
        val realm = Realm.getInstance(InitalizeRealm.getConfig())
        realm.use {
            val progressPercentage: Long =
                ((content.watchedDuration.toDouble() / (content.duration).toDouble()) * 100).toLong()
            val watchedEpisode = WatchedEpisode(
                id = content.episodeUrl.hashCode(),
                watchedDuration = content.watchedDuration,
                watchedPercentage = progressPercentage,
                animeName = content.animeName

            )
            realm.executeTransactionAsync { realmAsync ->
                realmAsync.insertOrUpdate(watchedEpisode)
            }
        }
    }
}