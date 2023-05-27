package com.arosara.anisaw.ui.main.animeinfo

import androidx.lifecycle.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import com.arosara.anisaw.ui.main.animeinfo.di.AnimeInfoFactory
import com.arosara.anisaw.ui.main.animeinfo.source.AnimeInfoRepository
import com.arosara.anisaw.utils.CommonViewModel
import com.arosara.anisaw.utils.model.AnimeInfoModel
import com.arosara.anisaw.utils.model.EpisodeModel
import com.arosara.anisaw.utils.model.FavouriteModel
import timber.log.Timber
import java.lang.Exception

class AnimeInfoViewModel @AssistedInject constructor(
    @Assisted val categoryUrl: String,
    private val animeInfoRepository: AnimeInfoRepository
) : CommonViewModel() {

    private var _animeInfoModel: MutableLiveData<AnimeInfoModel> = MutableLiveData()
    var animeInfoModel: LiveData<AnimeInfoModel> = _animeInfoModel

    private var _episodeList: MutableLiveData<ArrayList<EpisodeModel>> = MutableLiveData()
    var episodeList: LiveData<ArrayList<EpisodeModel>> = _episodeList


    private var _isFavourite: MutableLiveData<Boolean> = MutableLiveData(false)
    var isFavourite: LiveData<Boolean> = _isFavourite

    init {
        fetchAnimeInfo()
    }

    private fun fetchAnimeInfo() {
        viewModelScope.launch {
            try {
                updateLoading(loading = true)
                updateErrorModel(false, null, false)
                val data = animeInfoRepository.fetchAnimeInfo(categoryUrl)
                _animeInfoModel.value = data
                _isFavourite.value = animeInfoRepository.isFavourite(data.id)
                val episodeList =
                    animeInfoRepository.fetchEpisodeList(id = data.id, alias = data.alias)
                _episodeList.value = episodeList
                updateLoading(false)
            } catch (exc: Exception) {
                Timber.e(exc)
                updateErrorModel(show = true, e = exc, isListEmpty = false)
            }

        }
    }

    fun toggleFavourite() {
        if (_isFavourite.value!!) {
            animeInfoModel.value?.id?.let { animeInfoRepository.removeFromFavourite(it) }
            _isFavourite.value = false
        } else {
            saveFavourite()

        }
    }

    private fun saveFavourite() {
        val model = animeInfoModel.value
        animeInfoRepository.addToFavourite(
            FavouriteModel(
                ID = model?.id,
                categoryUrl = categoryUrl,
                animeName = model?.animeTitle,
                releasedDate = model?.releasedTime,
                imageUrl = model?.imageUrl
            )
        )
        _isFavourite.value = true
    }

    companion object {
        fun provideFactory(
            assistedFactory: AnimeInfoFactory,
            categoryUrl: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(categoryUrl = categoryUrl) as T
            }
        }
    }
}