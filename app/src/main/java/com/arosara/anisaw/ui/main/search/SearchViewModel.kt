package com.arosara.anisaw.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import com.arosara.anisaw.utils.CommonViewModel2
import com.arosara.anisaw.utils.constants.C
import com.arosara.anisaw.utils.model.AnimeMetaModel
import com.arosara.anisaw.utils.parser.HtmlParser
import okhttp3.ResponseBody

class SearchViewModel : CommonViewModel2() {

    private val searchRepository = SearchRepository()
    private var _searchList: MutableLiveData<ArrayList<AnimeMetaModel>> = MutableLiveData()
    private var pageNumber: Int = 1
    private var keyword: String = ""
    private var _canNextPageLoaded = true
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()
    var searchList: LiveData<ArrayList<AnimeMetaModel>> = _searchList

    fun fetchSearchList(keyword: String) {
        if (keyword.length >= 3 && this.keyword != keyword) {
            pageNumber = 1
            this.keyword = keyword
            val list = _searchList.value
            list?.clear()
            _searchList.value = list
            if (!super.isLoading()) {
                compositeDisposable.add(
                    searchRepository.fetchSearchList(
                        keyword,
                        pageNumber
                    ).subscribeWith(getSearchObserver(C.TYPE_SEARCH_NEW))
                )
                updateLoadingState(loading = Loading.LOADING, e = null, isListEmpty = isListEmpty())
            }
        }
    }

    fun fetchNextPage() {
        if (_canNextPageLoaded && !super.isLoading()) {
            compositeDisposable.add(
                searchRepository.fetchSearchList(
                    keyword,
                    pageNumber
                ).subscribeWith(getSearchObserver(C.TYPE_SEARCH_UPDATE))
            )
            updateLoadingState(loading = Loading.LOADING, e = null, isListEmpty = isListEmpty())
        }


    }

    private fun getSearchObserver(searchType: Int): DisposableObserver<ResponseBody> {
        return object : DisposableObserver<ResponseBody>() {
            override fun onComplete() {
                updateLoadingState(
                    loading = Loading.COMPLETED,
                    e = null,
                    isListEmpty = isListEmpty()
                )
            }

            override fun onNext(response: ResponseBody) {
                try{
                    val list =
                        HtmlParser.parseMovie(response = response.string(), typeValue = C.TYPE_DEFAULT)
                    if (list.isNullOrEmpty() || list.size < 20) {
                        _canNextPageLoaded = false
                    }
                    if (searchType == C.TYPE_SEARCH_NEW) {
                        _searchList.value = list
                    } else if (searchType == C.TYPE_SEARCH_UPDATE) {
                        val updatedList = _searchList.value
                        updatedList?.addAll(list)
                        _searchList.value = updatedList
                    }
                    pageNumber++
                }catch (exc: HtmlParser.ParserListFetchException){
                    updateLoadingState(loading = Loading.ERROR, e = exc, isListEmpty = isListEmpty())
                }

            }

            override fun onError(e: Throwable) {
                updateLoadingState(loading = Loading.ERROR, e = e, isListEmpty = isListEmpty())
            }

        }
    }


    override fun onCleared() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
        super.onCleared()
    }

    private fun isListEmpty(): Boolean {
        return _searchList.value.isNullOrEmpty()
    }

}