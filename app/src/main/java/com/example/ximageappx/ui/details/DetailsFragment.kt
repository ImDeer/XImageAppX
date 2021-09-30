package com.example.ximageappx.ui.details

import android.app.WallpaperManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ximageappx.MainActivity
import com.example.ximageappx.R
import com.example.ximageappx.databinding.FragmentDetailsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.io.IOException


class DetailsFragment : Fragment(R.layout.fragment_details) {

    private val args by navArgs<DetailsFragmentArgs>()

    private val uid = FirebaseAuth.getInstance().currentUser?.uid

    // loads details fragment
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userRef = Firebase.database.getReference("users")
        val imageRef =
            Firebase.database.getReference("likes/$uid")//${args.photo.id}/liked")

//        lateinit var mUser: DatabaseReference

        var liked = false

        val binding = FragmentDetailsBinding.bind(view)


        binding.apply {
            val photo = args.photo

//            mUser = Firebase.database.getReference("users/${photo.user}")

            Glide.with(this@DetailsFragment)
                .load(photo.url)//s.full)
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
                        gradient.isVisible = true
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

//            val login = mUser.child("login").toString()
//            (activity as MainActivity).supportActionBar?.title = login//.username

//            if (mUser.profilePhotoUrl != "")
//                ivItemProfImage.setImageURI(mUser.profilePhotoUrl.toUri())
//            else
//                ivItemProfImage.setImageDrawable("@drawable/default_profile_image")

            textViewCreator.apply {
//                text = "Photo by $login"//.name} on Unsplash"
                setOnClickListener {
                    context.startActivity(intent)
                }
                paint.isUnderlineText = true

            }

            userRef.child(photo.user)
                .addValueEventListener(object : ValueEventListener {

                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        if (dataSnapshot.exists())// && dataSnapshot.value == true
                        {
                            val login = dataSnapshot.child("login").value.toString()
                            val profilePhotoUrl =
                                dataSnapshot.child("profilePhotoUrl").value.toString()
                            textViewCreator.text = "Photo by $login"
                            (activity as MainActivity).supportActionBar?.title = login
                            if (profilePhotoUrl != "null")
                                Glide.with(ivItemProfImage).load(profilePhotoUrl)
                                    .error(R.drawable.default_profile_image).circleCrop()
                                    .into(ivItemProfImage)
                            else
                                ivItemProfImage.setImageResource(R.drawable.default_profile_image)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}

                })



            imageRef.child(photo.id).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    if (dataSnapshot.exists())
                        liked = dataSnapshot.value == true//.child("liked").value == true

                    if (liked)
                        likeButton.setImageResource(R.drawable.ic_like_liked)
                    else
                        likeButton.setImageResource(R.drawable.ic_like)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value
                    Toast.makeText(context, "Oops", Toast.LENGTH_SHORT).show()

                }
            })

            // share photo (link)
            shareButton.setOnClickListener {
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, photo.url)//s.full)
//                shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
                shareIntent.type = "text/plain"
//                shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION);
//                context?.startActivities(arrayOf(Intent.createChooser(shareIntent, "Share with")))

                startActivity(Intent.createChooser(shareIntent, "Share To:"))

//                val icon: Bitmap = mBitmap
//                val share = Intent(Intent.ACTION_SEND)
//                share.type = "image/jpeg"
//                val bytes = ByteArrayOutputStream()
//                icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
//                val f =
//                    File(Environment.getExternalStorageDirectory() + File.separator.toString() + "temporary_file.jpg")
//                try {
//                    f.createNewFile()
//                    val fo = FileOutputStream(f)
//                    fo.write(bytes.toByteArray())
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//                share.putExtra(
//                    Intent.EXTRA_STREAM,
//                    Uri.parse("file:///sdcard/temporary_file.jpg")
//                )
//                startActivity(Intent.createChooser(share, "Share Image"))
            }

            // change liked state
            likeButton.setOnClickListener {
                if (!liked)
                    imageRef.child(photo.id).setValue(true)//.child("liked").setValue(true)
                else {
                    liked = false
                    imageRef.child(photo.id).removeValue()
                }

            }

            // set image as a wallpaper
            wallpaperButton.setOnClickListener {
                Toast.makeText(context, "Wait", Toast.LENGTH_SHORT).show()

                val bmap = imageView.drawable.toBitmap()
                val m = WallpaperManager.getInstance(context)

                try {
                    m.setBitmap(bmap)
                    Toast.makeText(context, "WallPaper set", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {

                    Toast.makeText(context, "Setting WallPaper Failed!!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
