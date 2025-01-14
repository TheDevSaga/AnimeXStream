package com.arosara.anisaw.ui.main.animeinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import com.arosara.anisaw.R
import com.arosara.anisaw.databinding.FragmentAnimeinfoBinding
import com.arosara.anisaw.databinding.LoadingBinding
import com.arosara.anisaw.ui.main.animeinfo.di.AnimeInfoFactory
import com.arosara.anisaw.ui.main.animeinfo.epoxy.AnimeInfoController
import com.arosara.anisaw.utils.ItemOffsetDecoration
import com.arosara.anisaw.utils.Tags.GenreTags
import com.arosara.anisaw.utils.Utils
import com.arosara.anisaw.utils.model.AnimeInfoModel
import com.arosara.anisaw.utils.model.EpisodeModel
import javax.inject.Inject

@AndroidEntryPoint
class AnimeInfoFragment : Fragment(), AnimeInfoController.EpisodeClickListener {

    private lateinit var binding: FragmentAnimeinfoBinding
    private lateinit var loadingBinding: LoadingBinding

    private val episodeController: AnimeInfoController by lazy {
        AnimeInfoController(this)
    }

    @Inject
    lateinit var animeInfoFactory: AnimeInfoFactory

    private val args: AnimeInfoFragmentArgs by navArgs()

    private val viewModel: AnimeInfoViewModel by viewModels {
        AnimeInfoViewModel.provideFactory(
            animeInfoFactory, args.categoryUrl
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAnimeinfoBinding.inflate(inflater)
        loadingBinding = LoadingBinding.bind(binding.root)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setPreviews()
        setupRecyclerView()
        setObserver()
        transitionListener()
        setOnClickListeners()
    }

    private fun setPreviews() {
        val imageUrl = AnimeInfoFragmentArgs.fromBundle(requireArguments()).animeImageUrl
        val animeTitle = AnimeInfoFragmentArgs.fromBundle(requireArguments()).animeName
        binding.animeInfoTitle.text = animeTitle
        binding.animeInfoImage.apply {
            Glide.with(this).load(imageUrl).into(this)
        }


    }

    private fun setObserver() {
        viewModel.animeInfoModel.observe(viewLifecycleOwner) {
            it?.let {
                updateViews(it)
            }
        }

        viewModel.episodeList.observe(viewLifecycleOwner) {
            it?.let {
                binding.animeInfoRoot.visibility = View.VISIBLE
                episodeController.setData(it)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {

            if (it.isLoading) {
                loadingBinding.loading.visibility = View.VISIBLE
            } else {
                loadingBinding.loading.visibility = View.GONE
            }
        }



        viewModel.isFavourite.observe(viewLifecycleOwner) {
            if (it) {
                binding.favourite.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_favorite,
                        null
                    )
                )
            } else {
                binding.favourite.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        resources,
                        R.drawable.ic_unfavorite,
                        null
                    )
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(R.transition.shared_element)

    }

    private fun updateViews(animeInfoModel: AnimeInfoModel) {

//        Glide.with(this)
//            .load(animeInfoModel.imageUrl)
//            .transition(withCrossFade())
//            .into(rootView.animeInfoImage)

        binding.animeInfoReleased.text = animeInfoModel.releasedTime
        binding.animeInfoStatus.text = animeInfoModel.status
        binding.animeInfoType.text = animeInfoModel.type
        binding.animeInfoTitle.text = animeInfoModel.animeTitle
        binding.toolbarText.text = animeInfoModel.animeTitle
        binding.flowLayout.removeAllViews()
        animeInfoModel.genre.forEach {
            binding.flowLayout.addView(
                GenreTags(requireContext()).getGenreTag(
                    genreName = it.genreName,
                    genreUrl = it.genreUrl
                )
            )
        }


        episodeController.setAnime(animeInfoModel.animeTitle)
        binding.animeInfoSummary.text = animeInfoModel.plotSummary
        binding.favourite.visibility = View.VISIBLE
        binding.typeLayout.visibility = View.VISIBLE
        binding.releasedLayout.visibility = View.VISIBLE
        binding.statusLayout.visibility = View.VISIBLE
        binding.animeInfoRoot.visibility = View.VISIBLE
    }

    private fun setupRecyclerView() {
        episodeController.spanCount = Utils.calculateNoOfColumns(requireContext(), 150f)
        binding.animeInfoRecyclerView.adapter = episodeController.adapter
        val itemOffsetDecoration = ItemOffsetDecoration(context, R.dimen.episode_offset_left)
        binding.animeInfoRecyclerView.addItemDecoration(itemOffsetDecoration)
        binding.animeInfoRecyclerView.apply {
            layoutManager =
                GridLayoutManager(context, Utils.calculateNoOfColumns(requireContext(), 150f))
            (layoutManager as GridLayoutManager).spanSizeLookup = episodeController.spanSizeLookup

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

    private fun setOnClickListeners() {
        binding.favourite.setOnClickListener {
            onFavouriteClick()
        }

        binding.back.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun onFavouriteClick() {
        if (viewModel.isFavourite.value!!) {
            Snackbar.make(
                binding.root,
                getText(R.string.removed_from_favourites),
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(binding.root, getText(R.string.added_to_favourites), Snackbar.LENGTH_SHORT)
                .show()
        }
        viewModel.toggleFavourite()
    }

    override fun onResume() {
        super.onResume()
        if (episodeController.isWatchedHelperUpdated()) {
            episodeController.setData(viewModel.episodeList.value)
        }
    }

    override fun onEpisodeClick(episodeModel: EpisodeModel) {
        findNavController().navigate(
            AnimeInfoFragmentDirections.actionAnimeInfoFragmentToVideoPlayerActivity(
                episodeUrl = episodeModel.episodeurl,
                animeName = AnimeInfoFragmentArgs.fromBundle(requireArguments()).animeName,
                episodeNumber = episodeModel.episodeNumber
            )
        )
    }

}