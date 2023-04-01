package com.example.movlist.ui.single_movie_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.movlist.data.repository.NetworkState
import com.example.movlist.data.vo.MovieDetails
import io.reactivex.disposables.CompositeDisposable

class SingleMovieViewModel (private val movieDetailsRepository: MovieDetailsRepository, movieId:Int): ViewModel () {

    private val compositeDisposable = CompositeDisposable()

    val movieDetails : LiveData<MovieDetails> by lazy {
        movieDetailsRepository.fetchSingleMovieDetails(compositeDisposable,movieId)
    }

    val networkState: LiveData<NetworkState> by lazy {
        movieDetailsRepository.getMovieDetailsNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }


}
