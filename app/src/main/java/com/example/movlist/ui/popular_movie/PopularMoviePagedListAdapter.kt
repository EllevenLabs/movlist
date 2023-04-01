package com.example.movlist.ui.popular_movie


import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movlist.data.api.POSTER_BASE_URL
import com.example.movlist.data.repository.NetworkState
import com.example.movlist.data.vo.Movie
import com.example.movlist.databinding.MovieListItemBinding
import com.example.movlist.databinding.NetworkStateItemBinding
import com.example.movlist.ui.single_movie_details.SingleMovie
import kotlinx.coroutines.NonDisposableHandle
import kotlinx.coroutines.NonDisposableHandle.parent


class PopularMoviePagedListAdapter : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(MovieDiffCallback()){

    val  MOVIE_VIEW_TYPE = 1
    val NETWORK_VIEW_TYPE = 2

    private var networkState: NetworkState? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIE_VIEW_TYPE) {
            (holder ad MovieDiffCallback.MovieItemViewHolder).bind(getItem(position),context)
        }
        else {
            (holder as MovieDiffCallback.NetworkStateItemViewHolder).bind(networkState)
        }
    }

    private fun hasExtraRow():Boolean {
        return networkState!= null && networkState!= NetworkState.LOADED
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount -1){
            NETWORK_VIEW_TYPE
        } else {
            MOVIE_VIEW_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater:LayoutInflater = LayoutInflater.from(NonDisposableHandle.parent.context)
        val view: View

        if (viewType == MOVIE_VIEW_TYPE) {
            view = layoutInflater.inflate(R.layout.movie_list_item, parent, false)
            return MovieItemViewHolder(view)
        }else {
            view = layoutInflater.inflate(R.layout.network_state_item,parent, false)
            return NetworkStateItemViewHolder(view)
        }
    }

    class MovieDiffCallback : DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

        class MovieItemViewHolder(private val binding: MovieListItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(movie: Movie) {
                binding.cvMovieTitle.text = movie.title
                binding.cvMovieReleaseDate.text = movie.releaseDate

                val moviePosterURL: String = POSTER_BASE_URL + movie.posterPath
                Glide.with(itemView.context)
                    .load(moviePosterURL)
                    .into(binding.cvIvMoviePoster);

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, SingleMovie::class.java)
                    intent.putExtra("id", movie?.id)
                    itemView.context.startActivity(intent)
                }
            }
        }

        class NetworkStateItemViewHolder(private val binding: NetworkStateItemBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(networkState: NetworkState?) {
                if (networkState != null && networkState == NetworkState.LOADING) {
                    binding.progressBarItem.visibility = View.VISIBLE
                } else {
                    binding.progressBarItem.visibility = View.GONE
                }
                if (networkState != null && networkState == NetworkState.ERROR) {
                    binding.errorMsgItem.visibility = View.VISIBLE
                    binding.errorMsgItem.text = networkState.msg
                } else if (networkState != null && networkState == NetworkState.END_OF_LIST) {
                    binding.errorMsgItem.visibility = View.VISIBLE
                    binding.errorMsgItem.text = networkState.msg
                } else {
                    binding.errorMsgItem.visibility = View.GONE
                }
            }
        }

        fun setNetworkState(newNetworkState: NetworkState) {
            val previousState:NetworkState? = this.setNetworkState
            val hadExtraRow:Boolean = hasExtraRow()
            this.networkState() = newNetworkState
            val hasExtraRow:Boolean = hasExtraRow()

            if ((hadExtraRow! = hasExtraRow){
                if (hadExtraRow) {
                    notifyItemRenoved(super.getItemCount())
                } else {
                    notifyItemInserted(super.getItemCount())
                }
            }
            }else if (hasExtraRow && previousState != newNetworkState) {
                notifyItemChanged(position:itemCount - 1)
        }
        }
    }
