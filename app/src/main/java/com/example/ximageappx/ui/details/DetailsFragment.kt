package com.example.ximageappx.ui.details

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ximageappx.R
import com.example.ximageappx.bitmapToUri
import com.example.ximageappx.databinding.FragmentDetailsBinding
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast
import java.io.*
import java.util.*


class DetailsFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    // loads details fragment
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var tmpLiked = false

        val binding = FragmentDetailsBinding.bind(view)


        binding.apply {
            val photo = args.photo


            Glide.with(this@DetailsFragment)
                .load(photo.url)
                .error(R.drawable.ic_no_image)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed( // error
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        return false
                    }


                    override fun onResourceReady( // ok
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar.isVisible = false
                        textViewCreator.isVisible = true
                        textViewDescription.isVisible = photo.description != ""
                        likeButton.isVisible = true
                        wallpaperButton.isVisible = true
                        shareButton.isVisible = true

                        imageViewBg.setImageDrawable(resource)
                        imageViewBg.setBlur(5)


                        return false
                    }
                })
                .into(imageView)

            // clickable username
            textViewDescription.text = photo.description

            val uri = Uri.parse(photo.url)//.links.html)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            textViewCreator.apply {
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true

            }

            firebaseService.listenToPhotoCreator(photo.user) { user ->
                val login = user.login
                val profilePhotoUrl = user.profilePhotoUrl
                textViewCreator.text = "Photo by $login"
                if (profilePhotoUrl != "null")
                    Glide.with(ivItemProfImage).load(profilePhotoUrl)
                        .error(R.drawable.default_profile_image).circleCrop()
                        .into(ivItemProfImage)
                else
                    ivItemProfImage.setImageResource(R.drawable.default_profile_image)
            }

            firebaseService.getLikedState(photoId = photo.id, callback = { liked ->
                likeButton.setImageResource(if (liked) R.drawable.ic_like_liked else R.drawable.ic_like)
            }, callback2 = {
                context?.showToast("Oops")
            })

            // share photo (link)
            shareButton.setOnClickListener {
                val bmap = imageView.drawable.toBitmap()
                val tmpUri = bitmapToUri(bmap, requireContext())

                try {
                    val shareIntent = Intent()
                    shareIntent.action = Intent.ACTION_SEND
                    shareIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    shareIntent.putExtra(Intent.EXTRA_STREAM, tmpUri)
                    shareIntent.type = "image/*"
                    startActivity(Intent.createChooser(shareIntent, "Share image via"))
                } catch (e: Exception) {
                    e.printStackTrace()
                    context?.showToast("Oops! Something went wrong")
                }
            }

            // change liked state
            likeButton.setOnClickListener {
                firebaseService.setLikedValue(photo.id, tmpLiked)
                firebaseService.getLikedState(photo.id, { liked ->
                    likeButton.setImageResource(if (liked) R.drawable.ic_like_liked else R.drawable.ic_like)
                    tmpLiked = liked
                })
            }

            // set image as a wallpaper
            wallpaperButton.setOnClickListener {
                context?.showToast("Wait")

                val bmap = imageView.drawable.toBitmap()
                val m = WallpaperManager.getInstance(context)

                try {
                    m.setBitmap(bmap)
                    context?.showToast("WallPaper set")
                } catch (e: IOException) {
                    context?.showToast("Setting WallPaper Failed!!")
                }
            }
        }
    }
}
