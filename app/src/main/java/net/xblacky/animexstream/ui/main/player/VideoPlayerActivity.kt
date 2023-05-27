package net.xblacky.animexstream.ui.main.player

import android.app.AppOpsManager
import android.app.PictureInPictureParams
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import net.xblacky.animexstream.R
import net.xblacky.animexstream.utils.model.Content
import timber.log.Timber
import java.lang.Exception
import android.view.WindowInsetsController

import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.findFragment
import androidx.lifecycle.Observer
import net.xblacky.animexstream.databinding.ActivityVideoPlayerBinding
import net.xblacky.animexstream.utils.preference.Preference
import javax.inject.Inject


@AndroidEntryPoint
class VideoPlayerActivity : AppCompatActivity(), VideoPlayerListener {

    private val viewModel: VideoPlayerViewModel by viewModels()

    private lateinit var  binding : ActivityVideoPlayerBinding
    private lateinit var  videoPlayerFragment: VideoPlayerFragment

    @Inject
    lateinit var preference: Preference
    private var episodeNumber: String? = ""
    private var animeName: String? = ""
    private lateinit var content: Content

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        videoPlayerFragment = supportFragmentManager.findFragmentById(R.id.playerFragment) as VideoPlayerFragment

        getExtra(intent)
        setObserver()
        goFullScreen()
    }



    override fun onNewIntent(intent: Intent?) {
        videoPlayerFragment.playOrPausePlayer(
            playWhenReady = false,
            loseAudioFocus = false
        )
        videoPlayerFragment.saveWatchedDuration()
        getExtra(intent)
        super.onNewIntent(intent)

    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }


    override fun onResume() {
        super.onResume()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            goFullScreen()
        }
    }

    private fun getExtra(intent: Intent?) {
        val url = intent?.extras?.getString("episodeUrl")
        episodeNumber = intent?.extras?.getString("episodeNumber")
        animeName = intent?.extras?.getString("animeName")
        viewModel.updateEpisodeContent(
            Content(
                animeName = animeName ?: "",
                episodeUrl = url,
                episodeName = "\"$episodeNumber\"",
                urls = ArrayList()
            )
        )
        viewModel.fetchEpisodeData()
    }

    @Suppress("DEPRECATION")
    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager
                .hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE
                )
            && hasPipPermission()
            && videoPlayerFragment.isVideoPlaying()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val params = PictureInPictureParams.Builder()
                this.enterPictureInPictureMode(params.build())
            } else {
                this.enterPictureInPictureMode()
            }
        }
    }

    override fun onStop() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
            && hasPipPermission()
        ) {
            finishAndRemoveTask()
        }
        super.onStop()
    }

    override fun finish() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            finishAndRemoveTask()
        }
        super.finish()

        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_in_down)
    }

    fun enterPipModeOrExit() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
            && packageManager
                .hasSystemFeature(
                    PackageManager.FEATURE_PICTURE_IN_PICTURE
                )
            && videoPlayerFragment.isVideoPlaying()
            && hasPipPermission()
        ) {
            try {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val params = PictureInPictureParams.Builder()
                    this.enterPictureInPictureMode(params.build())
                } else {
                    this.enterPictureInPictureMode()
                }
            } catch (ex: Exception) {
                Timber.e(ex)
            }

        } else {
            finish()

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
//        binding.playerActivityContainer.exoPlayerView.useController = !isInPictureInPictureMode
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    private fun hasPipPermission(): Boolean {
        val appsOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                appsOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
                appsOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_PICTURE_IN_PICTURE,
                    android.os.Process.myUid(),
                    packageName
                ) == AppOpsManager.MODE_ALLOWED
            }
            else -> {
                false
            }
        }
    }

    private fun setObserver() {

        viewModel.content.observe(this, Observer {
            this.content = it
            it?.let {
                if (it.urls.isNotEmpty()) {
                    videoPlayerFragment.updateContent(it)
                }
            }
        })
        viewModel.isLoading.observe(this, Observer {
            videoPlayerFragment.showLoading(it.isLoading)
        })
        viewModel.errorModel.observe(this, Observer {
            videoPlayerFragment.showErrorLayout(
                it.show,
                it.errorMsgId,
                it.errorCode
            )
        })

        viewModel.cdnServer.observe(this) {
            Timber.e("Referrer : $it")
            preference.setReferrer(it)
        }
    }

    override fun onBackPressed() {
        enterPipModeOrExit()
    }

    private fun goFullScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

    }

    override fun updateWatchedValue(content: Content) {
        viewModel.saveContent(content)
    }

    override fun playNextEpisode() {
        viewModel.updateEpisodeContent(
            Content(
                episodeUrl = content.nextEpisodeUrl,
                episodeName = "\"EP ${incrimentEpisodeNumber(content.episodeName!!)}\"",
                urls = ArrayList(),
                animeName = content.animeName
            )
        )
        viewModel.fetchEpisodeData()

    }

    override fun playPreviousEpisode() {

        viewModel.updateEpisodeContent(
            Content(
                episodeUrl = content.previousEpisodeUrl,
                episodeName = "\"EP ${decrimentEpisodeNumber(content.episodeName!!)}\"",
                urls = ArrayList(),
                animeName = content.animeName
            )
        )
        viewModel.fetchEpisodeData()
    }

    private fun incrimentEpisodeNumber(episodeName: String): String {
        return try {
            Timber.e("Episode Name $episodeName")
            val episodeString = episodeName.substring(
                episodeName.lastIndexOf(' ') + 1,
                episodeName.lastIndex
            )
            var episodeNumber = Integer.parseInt(episodeString)
            episodeNumber++
            episodeNumber.toString()

        } catch (obe: ArrayIndexOutOfBoundsException) {
            ""
        }
    }

    private fun decrimentEpisodeNumber(episodeName: String): String {
        return try {
            val episodeString = episodeName.substring(
                episodeName.lastIndexOf(' ') + 1,
                episodeName.lastIndex
            )
            var episodeNumber = Integer.parseInt(episodeString)
            episodeNumber--
            episodeNumber.toString()

        } catch (obe: ArrayIndexOutOfBoundsException) {
            ""
        }
    }


    fun refreshM3u8Url() {
        viewModel.fetchEpisodeData(forceRefresh = true)
    }

}