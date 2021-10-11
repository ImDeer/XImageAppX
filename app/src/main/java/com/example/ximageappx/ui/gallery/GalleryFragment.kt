package com.example.ximageappx.ui.gallery

//import com.example.ximageappx.data.UnsplashPhoto
//import com.github.dhaval2404.imagepicker.ImagePicker
//import kotlinx.android.synthetic.main.fragment_add_post.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.example.ximageappx.R
import com.example.ximageappx.data.PhotoPost
import com.example.ximageappx.databinding.FragmentGalleryBinding
import com.example.ximageappx.services.IFirebaseService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class GalleryFragment constructor(
    private val firebaseService: IFirebaseService
) : Fragment(R.layout.fragment_gallery),
    UnsplashPhotoAdapter.OnItemClickListener {

    private val viewModel by viewModels<GalleryViewModel>()

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

//    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

//    private lateinit var uri: Uri

    //TODO это код чтобы работать с получением картинок в отдельном классе, но он не работает, потому что фрагмент не приделан к активити -> невозможно получить активити чтобы туда ее передать
//    private val imagePicker = ImagePicker(requireActivity().activityResultRegistry, this) { imageUri ->
//        if (imageUri != null) {
//            val action =
//                GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(imageUri)
//            findNavController().navigate(action)
//        }
//    }
    private val getContent: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri: Uri? ->//.TakePicture()) { imageUri: Uri? ->
            if (imageUri != null) {
                val action =
                    GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(imageUri)
                findNavController().navigate(action)
            } else
                Toast.makeText(
                    context,
                    "Something went wrong, please try again later",
                    Toast.LENGTH_SHORT
                ).show()
        }


//    private val takePicture: ActivityResultLauncher<Uri> =
//        registerForActivityResult(ActivityResultContracts.TakePicture()) { res: Boolean ->
//            if (res != false) {
//                val action =
//                    GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(uri)
//                findNavController().navigate(action)
//            } else
//                Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT).show()
//        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("galleryFragment", "onViewCreated")

        if (firebaseService.getCurrentUser() == null) {
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
                    adapter.submitData(it)//viewLifecycleOwner.lifecycle, it)//(it)
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
                        buttonRetry.isVisible = false//loadState.source.refresh is LoadState.Error && !loadState.append.endOfPaginationReached
                        textViewError.isVisible = false//loadState.source.refresh is LoadState.Error

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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (resultCode) {
//            Activity.RESULT_OK -> {
//                //Image Uri will not be null for RESULT_OK
//                val uri: Uri = data?.data!!
//
//                val action = GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(uri)
//                findNavController().navigate(action)
//                Toast.makeText(context, "imageTaken", Toast.LENGTH_SHORT).show()
//            }
//            ImagePicker.RESULT_ERROR -> {
//                Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
//            }
//            else -> {
//                Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }

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
            getContent.launch("image/*")
//            uri = Uri.fromFile(File.createTempFile("test", ".jpg"))
//            uri = FileProvider.getUriForFile(requireContext(), context?.applicationContext?.packageName+".provider", File.createTempFile("test", ".jpg"))
//            takePicture.launch(uri)
//TODO тоже туда же
//            imagePicker.pickImage()

//            ImagePicker.with(this@GalleryFragment).crop()
//                .createIntent { intent -> startActivityForResult(intent, 0) }
            true
        }
//
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

// TODO еще раз туда же
//class ImagePicker(
//    private val activityResultRegistry: ActivityResultRegistry,
//    private val lifecycleOwner: LifecycleOwner,
//    private val callback: (imageUri: Uri?) -> Unit
//) {
//
//    private val getContent: ActivityResultLauncher<String> =
//        activityResultRegistry.register(
//            REGISTRY_KEY,
//            lifecycleOwner,
//            ActivityResultContracts.GetContent(),
//            callback
//        ) //{ imageUri: Uri? ->
////            if (imageUri != null) {
////                val action =
////                    GalleryFragmentDirections.actionGalleryFragmentToAddPostFragment(imageUri)
//////                findNavController().navigate(action)
////            } //else
////                Toast.makeText(context, "Something went wrong, please try again later", Toast.LENGTH_SHORT)
////        }
//
//    fun pickImage() {
//        getContent.launch("image/*")
//    }
//
//    private companion object {
//        private const val REGISTRY_KEY = "ImagePicker"
//    }
//}