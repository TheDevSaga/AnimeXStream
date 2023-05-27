package com.arosara.anisaw.utils.rertofit

import io.reactivex.Observable
import com.arosara.anisaw.utils.constants.C
import okhttp3.ResponseBody
import retrofit2.http.*

class NetworkInterface {


    interface HomeDataService {

        @GET("https://ajax.gogocdn.net/ajax/page-recent-release.html")
        suspend fun fetchRecentSubOrDub(
            @HeaderMap header: Map<String, String>,
            @Query("page") page: Int,
            @Query("type") type: Int
        ): ResponseBody

        @GET("https://ajax.gogocdn.net/ajax/page-recent-release-ongoing.html")
        suspend fun fetchPopularFromAjax(
            @HeaderMap header: Map<String, String>,
            @Query("page") page: Int
        ): ResponseBody

        @GET("/anime-movies.html")
        suspend fun fetchMovies(
            @HeaderMap header: Map<String, String>,
            @Query("page") page: Int
        ): ResponseBody

        @GET("/new-season.html")
        suspend fun fetchNewestSeason(
            @HeaderMap header: Map<String, String>,
            @Query("page") page: Int
        ): ResponseBody
    }


    interface EpisodeDataService {
        @GET
        suspend fun fetchEpisodeMediaUrl(
            @HeaderMap header: Map<String, String>,
            @Url url: String
        ): ResponseBody

        @GET
        suspend fun fetchAjaxUrlForM3U8(
            @HeaderMap header: Map<String, String>,
            @Url url: String
        ): ResponseBody

        @GET
        @Headers("X-Requested-With:XMLHttpRequest")
        suspend fun fetchM3U8Url(
            @Url url: String
        ): ResponseBody

    }

    interface FetchAnimeInfo {
        @GET
        suspend fun get(
            @HeaderMap header: Map<String, String>,
            @Url url: String
        ): ResponseBody
    }

    interface FetchEpisodeList {

        @GET(C.EPISODE_LOAD_URL)
        suspend fun get(
            @HeaderMap header: Map<String, String>,
            @Query("ep_start") startEpisode: Int = 0,
            @Query("ep_end") endEpisode: Int=9999,
            @Query("id") id: String,
            @Query("default_ep") defaultEp: Int = 0,
            @Query("alias") alias: String
        ): ResponseBody
    }

    interface FetchSearchData {

        @GET(C.SEARCH_URL)
        fun get(
            @HeaderMap header: Map<String, String>,
            @Query("keyword") keyword: String,
            @Query("page") page: Int
        ): Observable<ResponseBody>
    }

}