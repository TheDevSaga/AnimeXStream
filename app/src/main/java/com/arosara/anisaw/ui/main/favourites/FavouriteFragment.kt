package com.arosara.anisaw.ui.main.favourites

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.arosara.anisaw.R
import com.arosara.anisaw.databinding.FragmentFavouriteBinding
import com.arosara.anisaw.ui.main.favourites.epoxy.FavouriteController
import com.arosara.anisaw.utils.ItemOffsetDecoration
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.model.FavouriteModel

class FavouriteFragment : Fragment(), FavouriteController.EpoxySearchAdapterCallbacks,
    View.OnClickListener {
    private lateinit var binding: FragmentFavouriteBinding
    private lateinit var viewModel: FavouriteViewModel
    private val favouriteController by lazy {
        FavouriteController(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouriteBinding.inflate(inflater)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(FavouriteViewModel::class.java)
        setTransitions(view = view)
        setAdapters()
        transitionListener()
        setClickListeners()
        setObserver()


    }

    private fun setTransitions(view: View) {
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
//        exitTransition = MaterialFadeThrough().apply {
//            duration = 300
//        }
//        reenterTransition = MaterialFadeThrough().apply {
//            duration = 300
//        }
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.navHostFragmentContainer
            duration = 300
            scrimColor = Color.TRANSPARENT
            fadeMode = MaterialContainerTransform.FADE_MODE_THROUGH
            startContainerColor =
                ContextCompat.getColor(view.context, android.R.color.transparent)
            endContainerColor =
                ContextCompat.getColor(view.context, android.R.color.transparent)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            favouriteController.spanCount = 5
            (binding.recyclerView.layoutManager as GridLayoutManager).spanCount = 5
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            favouriteController.spanCount = 3
            (binding.recyclerView.layoutManager as GridLayoutManager).spanCount = 3
        }

    }

    private fun setObserver() {
        viewModel.favouriteList.observe(viewLifecycleOwner) {
            favouriteController.setData(it)
        }
    }


    private fun setAdapters() {
        favouriteController.spanCount = Utils.calculateNoOfColumns(requireContext(), 150f)
        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(context, Utils.calculateNoOfColumns(requireContext(), 150f))
            adapter = favouriteController.adapter
            (layoutManager as GridLayoutManager).spanSizeLookup = favouriteController.spanSizeLookup
        }
        binding.recyclerView.addItemDecoration(
            ItemOffsetDecoration(
                context,
                R.dimen.episode_offset_left
            )
        )

    }

    private fun getSpanCount(): Int {
        val orientation = resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            5
        } else {
            3
        }
    }

    private fun transitionListener() {
        binding.motionLayout.setTransitionListener(
            object : MotionLayout.TransitionListener {
                override fun onTransitionTrigger(
                    p0: MotionLayout?,
                    p1: Int,
                    p2: Boolean,
                    p3: Float
                ) {

                }

                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                    binding.topView.cardElevation = 0F
                }

                override fun onTransitionChange(
                    p0: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {
                    if (startId == R.id.start) {
                        binding.topView.cardElevation = 20F * progress
                        binding.toolbarText.alpha = progress
                    } else {
                        binding.topView.cardElevation = 10F * (1 - progress)
                        binding.toolbarText.alpha = (1 - progress)
                    }
                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                }

            }
        )
    }

    private fun setClickListeners() {
        binding.back.setOnClickListener(this)
    }


    override fun animeTitleClick(model: FavouriteModel, sharedTitle: View, sharedImage: View) {
        val extras = FragmentNavigatorExtras(
            sharedTitle to resources.getString(R.string.shared_anime_title),
            sharedImage to resources.getString(R.string.shared_anime_image)
        )
        findNavController().navigate(
            FavouriteFragmentDirections.actionFavouriteFragmentToAnimeInfoFragment(
                categoryUrl = model.categoryUrl!!,
                animeName = model.animeName!!,
                animeImageUrl = model.imageUrl!!
            ), extras
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                findNavController().navigateUp()
            }
        }
    }

}

