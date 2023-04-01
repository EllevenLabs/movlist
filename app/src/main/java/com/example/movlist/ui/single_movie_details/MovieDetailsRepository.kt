package com.example.movlist.ui.single_movie_details

import androidx.lifecycle.LiveData
import com.example.movlist.data.api.TheMovieDBInterface
import com.example.movlist.data.repository.MovieDetailsNetworkDataSource
import com.example.movlist.data.repository.NetworkState
import com.example.movlist.data.vo.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class MovieDetailsRepository (private val apiService: TheMovieDBInterface) {
    lateinit var movieDetailsNetworkDataSource: MovieDetailsNetworkDataSource
    fun fetchSingleMovieDetails (compositeDisposable: CompositeDisposable, movieId: Int) : LiveData<MovieDetails> {
        movieDetailsNetworkDataSource =
            MovieDetailsNetworkDataSource(apiService, compositeDisposable)
        movieDetailsNetworkDataSource.fetchMovieDetails(movieId)

        return movieDetailsNetworkDataSource.downloadedMovieResponse
    }

    fun getMovieDetailsNetworkState(): LiveData<NetworkState> {
        return movieDetailsNetworkDataSource.networkState
    }
}
