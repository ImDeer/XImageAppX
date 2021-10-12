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
import com.example.ximageappx.data.User
import com.example.ximageappx.databinding.ItemUnsplashImageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UnsplashPhotoAdapter(private val listener: OnItemClickListener) :
    PagingDataAdapter<PhotoPost, UnsplashPhotoAdapter.PhotoViewHolder>(PHOTO_COMPARATOR) {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    val ref = FirebaseDatabase.getInstance().getReference("users")
    val likedPosts =
        FirebaseDatabase.getInstance().getReference("likes/$uid")//.child("$uid/likedPosts")

//    private val _user = FirebaseDatabase.getInstance().getReference("users")

    //    private lateinit var _user: DatabaseReference
    private var mUser: User = User()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            ItemUnsplashImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position) ?: return

        holder.bind(currentItem)
    }

    inner class PhotoViewHolder(
        private val binding: ItemUnsplashImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        var liked = false

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
                        if (!liked) {
                            likedPosts.child(item.id).setValue(true)
                        } else {
                            likedPosts.child(item.id).removeValue()
                        }
                    }
                }
            }
        }

        private fun getLikedState(photo: PhotoPost) {
            likedPosts.child(photo.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        liked = dataSnapshot.exists() && dataSnapshot.value == true
                        if (liked) {
                            binding.likeButton.setImageResource(R.drawable.ic_like_liked)
                        } else {
                            binding.likeButton.setImageResource(R.drawable.ic_like)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })
        }

        private fun getPhotoCreator(photo: PhotoPost) {
            ref.child(photo.user)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (dataSnapshot.exists())
                        {
                            mUser.login = dataSnapshot.child("login").value.toString()
                            mUser.profilePhotoUrl =
                                dataSnapshot.child("profilePhotoUrl").value.toString()
                            binding.textViewUserName.text = mUser.login
                            if (mUser.profilePhotoUrl != "null")
                                Glide.with(binding.ivItemProfImage).load(mUser.profilePhotoUrl)
                                    .error(R.drawable.default_profile_image).circleCrop()
                                    .into(binding.ivItemProfImage)
                            else
                                binding.ivItemProfImage.setImageResource(R.drawable.default_profile_image)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
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