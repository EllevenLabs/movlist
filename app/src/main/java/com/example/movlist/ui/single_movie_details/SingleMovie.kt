package com.example.movlist.ui.single_movie_details

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.movlist.data.api.POSTER_BASE_URL
import com.example.movlist.data.api.TheMovieDBClient
import com.example.movlist.data.api.TheMovieDBInterface
import com.example.movlist.data.repository.NetworkState
import com.example.movlist.data.vo.MovieDetails
import com.example.movlist.databinding.ActivitySingleMovieBinding


import java.text.NumberFormat
import java.util.Locale


class SingleMovie : AppCompatActivity() {

    private lateinit var binding: ActivitySingleMovieBinding
    private lateinit var movieRepository: MovieDetailsRepository
    private lateinit var viewModel: SingleMovieViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingleMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val movieId: Int = intent.getIntExtra("id", 1)
        val apiService : TheMovieDBInterface = TheMovieDBClient.getClient()
        movieRepository = MovieDetailsRepository(apiService)

        viewModel = getViewModel(movieId)

        viewModel.movieDetails.observe(this, Observer {
            bindUI(it)
        })

        viewModel.networkState.observe(this, Observer {
            binding.progressBar.visibility = if (it == NetworkState.LOADING) View.VISIBLE else View.GONE
            binding.txtError.visibility = if (it == NetworkState.ERROR) View.VISIBLE else View.GONE
        })

    }

    private fun bindUI(it: MovieDetails) {
        binding.movieTitle.text = it.title
        binding.movieTagLine.text = it.tagline
        binding.movieReleaseDate.text = it.releaseDate
        binding.movieRating.text = it.voteAverage.toString()
        binding.movieRuntime.text = "${it.runtime} minutes"
        binding.movieOverview.text = it.overview

        val formatCurrency = NumberFormat.getCurrencyInstance(Locale.US)
        binding.movieBudget.text = formatCurrency.format(it.budget)
        binding.movieRevenue.text = formatCurrency.format(it.revenue)

        val moviePosterURL = POSTER_BASE_URL + it.posterPath
        Glide.with(this)
            .load(moviePosterURL)
            .into(binding.ivMoviePoster)
    }

    private fun getViewModel(movieId: Int): SingleMovieViewModel {
        return ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SingleMovieViewModel(movieRepository, movieId) as T
            }
        }).get(SingleMovieViewModel::class.java)
    }
}
