package com.arosara.anisaw.ui.main.search

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.rertofit.NetworkInterface
import com.arosara.anisaw.utils.rertofit.RetrofitHelper
import okhttp3.ResponseBody

class SearchRepository {

    private val retrofit = RetrofitHelper.getRetrofitInstance()

    fun fetchSearchList(keyWord: String, pageNumber: Int): Observable<ResponseBody>{
        val searchService = retrofit.create(NetworkInterface.FetchSearchData::class.java)
        return searchService.get(Utils.getHeader(),keyWord,pageNumber).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}