package com.example.movlist.ui.popular_movie

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedList.Config
import com.example.movlist.data.api.POST_PER_PAGE
import com.example.movlist.data.api.TheMovieDBInterface
import com.example.movlist.data.repository.MovieDataSource
import com.example.movlist.data.repository.MovieDataSourceFactory
import com.example.movlist.data.repository.NetworkState
import com.example.movlist.data.vo.Movie
import io.reactivex.disposables.CompositeDisposable
import androidx.lifecycle.Transformations

//video number 10 transformation


class MoviePageListRepository (private val apiService: TheMovieDBInterface){

    lateinit var moviePagedList: LiveData<PagedList<Movie>>
    lateinit var moviesDataSourceFactory: MovieDataSourceFactory

    fun fetchLiveMoviePagedList (compositeDisposable: CompositeDisposable) :LiveData<PagedList<Movie>>{
        moviesDataSourceFactory = MovieDataSourceFactory(apiService, compositeDisposable)

        val config: Config = Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        moviePagedList = LivePagedListBuilder(moviesDataSourceFactory, config).build()

        return moviePagedList
    }

    fun getNetworkState(): LiveData<NetworkState>{
        return Transformations.switchMap<MovieDataSource, NetworkState>(
            moviesDataSourceFactory.moviesLiveDataSource, MovieDataSource::networkState)
    }
}
