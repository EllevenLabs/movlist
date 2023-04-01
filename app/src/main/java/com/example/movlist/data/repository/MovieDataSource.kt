package com.example.movlist.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.example.movlist.data.api.FIRST_PAGE
import com.example.movlist.data.api.TheMovieDBInterface
import com.example.movlist.data.vo.Movie
import com.example.movlist.data.vo.MovieResponse
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MovieDataSource(
    private val apiService: TheMovieDBInterface,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Movie>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {

        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getPopularMovie(page)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response: MovieResponse? ->
                        val movieList = response?.movieList ?: emptyList()
                        callback.onResult(movieList, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    { error ->
                        networkState.postValue(NetworkState.ERROR)
                        Log.e("MovieDataSource", error.message.toString())
                    }
                )
        )

    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Movie>) {
        networkState.postValue(NetworkState.LOADING)

        compositeDisposable.add(
            apiService.getPopularMovie(params.key)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { response: MovieResponse? ->
                        if (response?.totalPages ?: 0 >= params.key) {
                            callback.onResult(response?.movieList ?: emptyList(), params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    { error ->
                        networkState.postValue(NetworkState.ERROR)
                        Log.e("MovieDataSource", error.message.toString())
                    }
                )
        )
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, Movie>){
    }
}
