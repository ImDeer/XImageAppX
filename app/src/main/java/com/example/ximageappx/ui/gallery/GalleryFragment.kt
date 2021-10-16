package com.example.ximageappx.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.example.ximageappx.R
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.databinding.FragmentGalleryBinding
import com.example.ximageappx.services.firebaseservice.IFirebaseService
import com.example.ximageappx.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GalleryFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_gallery),
    PhotoPostAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!firebaseService.isAuthenticated()) {
            val action = GalleryFragmentDirections.actionGalleryFragmentToLogInFragment()
            findNavController().navigate(action)
        } else {
            _binding = FragmentGalleryBinding.bind(view)

            val adapter = PhotoPostAdapter(this, firebaseService)

            binding.apply {
                recyclerView.setHasFixedSize(true)
                recyclerView.itemAnimator = null
                recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                    header = PhotoPostLoadStateAdapter { adapter.retry() },
                    footer = PhotoPostLoadStateAdapter { adapter.retry() }
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

        val addItem = menu.findItem(R.id.action_add)
        addItem.setOnMenuItemClickListener {
            startCrop()
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val action =
                GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(result.uriContent!!)
            findNavController().navigate(action)
        } else {
            context?.showToast(result.error.toString())
        }
    }

    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
            }
        )
    }
}