package com.arosara.anisaw.ui.main.animeinfo.epoxy

import com.airbnb.epoxy.TypedEpoxyController
import com.arosara.anisaw.utils.model.EpisodeModel

class AnimeInfoController(val episodeListener: EpisodeClickListener) :
    TypedEpoxyController<ArrayList<EpisodeModel>>() {
    var animeName: String = ""
    private lateinit var isWatchedHelper: com.arosara.anisaw.utils.helper.WatchedEpisode
    override fun buildModels(data: ArrayList<EpisodeModel>?) {
        data?.forEach {
            EpisodeModel_()
                .id(it.episodeurl)
                .episodeModel(it)
                .clickListener { model, _, _, _ ->
                    episodeListener.onEpisodeClick(model.episodeModel())
                }
                .spanSizeOverride { totalSpanCount, _, _ ->
                    totalSpanCount / totalSpanCount
                }
                .watchedProgress(isWatchedHelper.getWatchedDuration(it.episodeurl.hashCode()))
                .addTo(this)
        }
    }

    fun setAnime(animeName: String) {
        this.animeName = animeName
        isWatchedHelper = com.arosara.anisaw.utils.helper.WatchedEpisode(animeName)
    }

    fun isWatchedHelperUpdated(): Boolean {
        return ::isWatchedHelper.isInitialized
    }

    interface EpisodeClickListener {
        fun onEpisodeClick(episodeModel: EpisodeModel)
    }

}