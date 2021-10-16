package com.example.ximageappx.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.ximageappx.R
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.databinding.ItemUnsplashImageBinding
import com.example.ximageappx.services.firebaseservice.IFirebaseService


class PhotoPostAdapter(
    private val listener: OnItemClickListener,
    val firebaseService: IFirebaseService
) : PagingDataAdapter<PhotoPost, PhotoPostAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return PhotoViewHolder(binding, firebaseService)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return

        holder.bind(currentItem)
    }

    inner class PhotoViewHolder(
        private val binding: ItemUnsplashImageBinding,
        val firebaseService: IFirebaseService
    ) : RecyclerView.ViewHolder(binding.root) {

        private var tmpLiked = false

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        listener.onItemClick(item)
                    }
                }
            }

            binding.likeButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        firebaseService.setLikedValue(item.id, tmpLiked)
                    }
                }
            }
        }

        private fun getLikedState(photo: PhotoPost) {
            firebaseService.getLikedState(photoId = photo.id, callback = { liked ->
                binding.likeButton.setImageResource(if (liked) R.drawable.ic_like_liked else R.drawable.ic_like)
                tmpLiked = liked
            })
        }

        private fun getPhotoCreator(photo: PhotoPost) {

            firebaseService.listenToPhotoCreator(photo.user) { user ->
                val profilePhotoUrl = user.profilePhotoUrl
                binding.textViewUserName.text = user.login
                if (profilePhotoUrl != "null")
                    Glide.with(binding.ivItemProfImage).load(profilePhotoUrl)
                        .error(R.drawable.default_profile_image).circleCrop()
                        .into(binding.ivItemProfImage)
                else
                    binding.ivItemProfImage.setImageResource(R.drawable.default_profile_image)
            }
        }


        fun bind(photo: PhotoPost) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.url)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_no_image)
                    .into(imageView)
                getLikedState(photo)
                getPhotoCreator(photo)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(photo: PhotoPost)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<PhotoPost>() {
            override fun areItemsTheSame(oldItem: PhotoPost, newItem: PhotoPost) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PhotoPost, newItem: PhotoPost) =
                oldItem == newItem
        }
    }
}