package com.arosara.anisaw.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import com.arosara.anisaw.ui.main.home.di.HomeRepositoryModule
import com.arosara.anisaw.ui.main.home.source.HomeRepository
import com.arosara.anisaw.utils.Event
import com.arosara.anisaw.utils.Result
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.constants.C
import com.arosara.anisaw.utils.di.DispatcherModule
import com.arosara.anisaw.utils.model.AnimeMetaModel
import com.arosara.anisaw.utils.model.HomeScreenModel
import com.arosara.anisaw.utils.model.UpdateModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @HomeRepositoryModule.HomeRepo private val repository: HomeRepository,
    @DispatcherModule.MainDispatcher val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private var _animeList: MutableLiveData<ArrayList<HomeScreenModel>> =
        MutableLiveData(makeEmptyArrayList())
    val animeList: LiveData<ArrayList<HomeScreenModel>> = _animeList

    private val _scrollToTopEvent: MutableLiveData<Event<Boolean>> = MutableLiveData(Event(false))
    val scrollToTopEvent: LiveData<Event<Boolean>> = _scrollToTopEvent

    private var _updateModel: MutableLiveData<UpdateModel> = MutableLiveData()
    var updateModel: LiveData<UpdateModel> = _updateModel
//    private lateinit var database: DatabaseReference

    init {
        fetchHomeList()
    }

    fun fetchHomeList() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { fetchRecentSub() }
            withContext(Dispatchers.IO) { fetchRecentDub() }
            withContext(Dispatchers.IO) { fetchPopular() }
            withContext(Dispatchers.IO) { fetchNewSeason() }
            withContext(Dispatchers.IO) { fetchMovies() }
        }

    }




    private fun updateData(result: Result<ArrayList<AnimeMetaModel>>, typeValue: Int) {
        if (result is Result.Success) {
            val homeScreenModel = HomeScreenModel(
                typeValue = typeValue,
                type = Utils.getTypeName(typeValue),
                animeList = result.data
            )
            val newList = animeList.value!!
            try {
                newList[getPositionByType(typeValue)] = homeScreenModel

            } catch (_: IndexOutOfBoundsException) {
            }
            _animeList.postValue(newList)
        }
    }

    private fun getPositionByType(typeValue: Int): Int {
        return when (typeValue) {
            C.TYPE_RECENT_SUB -> C.RECENT_SUB_POSITION
            C.TYPE_RECENT_DUB -> C.RECENT_DUB_POSITION
            C.TYPE_MOVIE -> C.MOVIE_POSITION
            C.TYPE_NEW_SEASON -> C.NEWEST_SEASON_POSITION
            C.TYPE_POPULAR_ANIME -> C.POPULAR_POSITION
            else -> 0
        }
    }


    private fun makeEmptyArrayList(): ArrayList<HomeScreenModel> {
        var i = 1
        val arrayList: ArrayList<HomeScreenModel> = ArrayList()
        while (i <= 6) {
            arrayList.add(
                HomeScreenModel(
                    typeValue = i
                )
            )
            i++
        }
        return arrayList
    }

    private suspend fun fetchRecentSub() {
        repository.fetchHomeData(1, C.RECENT_SUB).collect {
            updateData(it, C.TYPE_RECENT_SUB)
        }
    }

    private suspend fun fetchRecentDub() {
        repository.fetchHomeData(1, C.TYPE_RECENT_DUB).collect {
            updateData(it, C.TYPE_RECENT_DUB)
        }
    }

    private suspend fun fetchMovies() {
        repository.fetchHomeData(1, C.TYPE_MOVIE).collect {
            updateData(it, C.TYPE_MOVIE)
        }
    }

    private suspend fun fetchPopular() {
        repository.fetchHomeData(1, C.TYPE_POPULAR_ANIME).collect {
            updateData(it, C.TYPE_POPULAR_ANIME)
        }
    }

    override fun onCleared() {
        viewModelScope.launch(dispatcher) {
            repository.removeOldData()
        }
        super.onCleared()
    }

    private suspend fun fetchNewSeason() {
        repository.fetchHomeData(1, C.TYPE_NEW_SEASON).collect {
            updateData(it, C.TYPE_NEW_SEASON)
        }
    }

}