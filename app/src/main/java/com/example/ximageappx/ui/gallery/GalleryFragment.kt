package com.example.ximageappx.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.paging.LoadState
import com.example.ximageappx.R
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.data.UnsplashPhoto
import com.example.ximageappx.databinding.FragmentGalleryBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("galleryFragment", "onViewCreated")

        if (mAuth.currentUser == null) {
            Log.d("galleryFragment", "currentUserNull")
            val action = GalleryFragmentDirections.actionGalleryFragmentToLogInFragment()
            findNavController().navigate(action)
        } else {

            _binding = FragmentGalleryBinding.bind(view)

            val adapter = UnsplashPhotoAdapter(this)

            binding.apply {
                Log.d("galleryFragment", "binding.apply")
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
                Log.d("galleryFragment", "viewModel.photos.collect")

                viewModel.photos.collect {
                    Log.d("galleryFragment", "submitData")

                    adapter.submitData(it)//viewLifecycleOwner.lifecycle, it)//(it)
                    Log.d("galleryFragment", "submitData(it)")

                }
            }

//        viewModel.photos.observe(viewLifecycleOwner) {
//            adapter.submitData(viewLifecycleOwner.lifecycle, it)
//        }


//            adapter.addLoadStateListener { loadState ->
//                binding.apply {
//                    progressBar.isVisible = loadState.source.refresh is LoadState.Loading
//                    recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
//                    buttonRetry.isVisible = false//loadState.source.refresh is LoadState.Error
//                    textViewError.isVisible = false//loadState.source.refresh is LoadState.Error
//
//                    // empty view
//                    if (loadState.source.refresh is LoadState.NotLoading &&
//                        loadState.append.endOfPaginationReached &&
//                        adapter.itemCount < 1
//                    ) {
//                        recyclerView.isVisible = false
//                        textViewEmpty.isVisible = true
//                    } else {
//                        textViewEmpty.isVisible = false
//                    }
//                }
//            }

            lifecycleScope.launch {
                adapter.loadStateFlow.collectLatest { loadState ->
                    binding.apply {
                        progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                        recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                        buttonRetry.isVisible =
                            loadState.source.refresh is LoadState.Error //&& !loadState.append.endOfPaginationReached
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
//                progressBar.isVisible = loadStates.refresh is LoadState.Loading
//                progressBarLoadMore.isVisible = loadStates.append is LoadState.Loading
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

//        val homeItem = menu.findItem(R.id.action_home)
//        homeItem.setOnMenuItemClickListener {
//            binding.recyclerView.scrollToPosition(0)
//            viewModel.searchPhotos("")
//            true
//        }

        val profileItem = menu.findItem(R.id.action_profile)
        profileItem.setOnMenuItemClickListener {
            val action = GalleryFragmentDirections.actionGalleryFragmentToProfileFragment()
            findNavController().navigate(action)
            true
        }

        val addItem = menu.findItem(R.id.action_add)
        addItem.setOnMenuItemClickListener {

            true
        }

//        val searchItem = menu.findItem(R.id.action_search)
//        val searchView = searchItem.actionView as SearchView
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//
//                binding.recyclerView.scrollToPosition(0)
//                viewModel.searchPhotos(query ?: "")
//                searchView.clearFocus()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                return true
//            }
//        })


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}