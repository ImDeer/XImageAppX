package com.example.ximageappx.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.ximageappx.BuildConfig
import com.example.ximageappx.R
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.databinding.FragmentGalleryBinding
import com.example.ximageappx.services.IFirebaseService
import com.example.ximageappx.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File


@AndroidEntryPoint
class GalleryFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (firebaseService.getCurrentUser() == null) {
            val action = GalleryFragmentDirections.actionGalleryFragmentToLogInFragment()
            findNavController().navigate(action)
        } else {
            _binding = FragmentGalleryBinding.bind(view)

            val adapter = UnsplashPhotoAdapter(this)

            binding.apply {
                recyclerView.setHasFixedSize(true)
                recyclerView.itemAnimator = null
                recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                    footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
                )
                buttonRetry.setOnClickListener {
                    adapter.retry()
                }
            }

            lifecycleScope.launch {
                viewModel.photos.collect {
                    adapter.submitData(it)
                }
            }

            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    binding.apply {
                        progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                        recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                        buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                        textViewError.isVisible = loadState.source.refresh is LoadState.Error

                        // empty view
                        if (loadState.source.refresh is LoadState.NotLoading &&
                            loadState.append.endOfPaginationReached &&
                            adapter.itemCount < 1
                        ) {
                            recyclerView.isVisible = false
                            textViewEmpty.isVisible = true
                        } else {
                            textViewEmpty.isVisible = false
                        }
                    }
                }
            }
            setHasOptionsMenu(true)
        }
    }

    override fun onItemClick(photo: PhotoPost) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallery, menu)

        val profileItem = menu.findItem(R.id.action_profile)
        profileItem.setOnMenuItemClickListener {
            val action = GalleryFragmentDirections.actionGalleryFragmentToProfileFragment()
            findNavController().navigate(action)
            true
        }

        val addGalleryItem = menu.findItem(R.id.action_add_gallery)
        addGalleryItem.setOnMenuItemClickListener {
            if (!hasStoragePermission())
                requestStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            else
                selectImageFromGallery()
            true
        }

        val addItem = menu.findItem(R.id.action_add)
        addItem.setOnMenuItemClickListener {
            if (!hasCameraPermission())
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            else
                takePicture()
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun hasStoragePermission() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                context?.showToast("Please allow camera usage in your settings.")
            } else {
                takePicture()
            }
        }

    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                context?.showToast("Please allow storage usage in your settings.")
            } else {
                selectImageFromGallery()
            }
        }

    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    val action =
                        GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(uri)
                    findNavController().navigate(action)
                }
            }
        }
    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val action =
                    GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(it)
                findNavController().navigate(action)
            }
        }

    private var latestTmpUri: Uri? = null

    private fun takePicture() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }
}