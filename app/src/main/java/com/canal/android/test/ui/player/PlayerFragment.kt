package com.canal.android.test.ui.player

import android.R.attr.left
import android.R.attr.top
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.canal.android.test.R
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.common.PositionState
import com.canal.android.test.common.msFormatTimeHumanReadable
import com.canal.android.test.databinding.FragmentPlayerBinding
import com.canal.android.test.domain.model.MediaDetailShort
import com.canal.android.test.player.Player
import com.canal.android.test.player.model.PlayerAction
import com.canal.android.test.ui.MainActivity
import com.canal.android.test.ui.common.BaseFragment
import com.canal.android.test.ui.common.exitFullScreen
import com.canal.android.test.ui.common.setFullScreen
import com.canal.android.test.ui.player.model.MediaUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.Exception
import java.net.URL


class PlayerFragment : BaseFragment<MediaUi, FragmentPlayerBinding>() {

    override val viewModel: PlayerViewModel by viewModel { parametersOf(navigateTo) }

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayerBinding
        get() = FragmentPlayerBinding::inflate

    private var player: Player? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setFullScreen()
        // onSaveInstanceState adds persistent saving that the ViewModel does not provide, in case the app is stopped by the system
        savedInstanceState?.getLong("previousPlayerPosition", 0)?.let { viewModel.postPlayerPositionState(
            PositionState(it, -1)
        ) }

        // Question 1.4 Seeking the thumb of seekBar seeks the stream at correct position
        val seekBar: SeekBar? = activity?.findViewById(R.id.seek_bar)
        seekBar?.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seek: SeekBar) {
                    player?.pushAction(PlayerAction.SeekTo(seek.progress.toLong()))
                }
            }
        )
        initPlayer()
        viewModel.uiData.observe(viewLifecycleOwner) { mediaUi ->
            viewModel.postMediaDetail(MediaDetailShort(
                manifestUrl = mediaUi.manifestUrl,
                title = mediaUi.title,
                subtitle = mediaUi.subtitle,
                urlImage = mediaUi.urlImage,
            ))
            // NOT DONE: if the ratio cannot be parsed from the url, we could report an error dialog and abort playing
            // We set the ratio manually so that it is initially correct without a visible tearing glitch
            val ratio = getRatioFromUrl(mediaUi.manifestUrl)
            ratio?.let {
                viewModel.postPlayerRatio(it)
            }
            sendActionStartPlaybackWrapped(url = mediaUi.manifestUrl, seekToPositionMs = null)
        }

        viewModel.playerRatio.observe(viewLifecycleOwner) { ratio ->
            updatePlayerContainerRatio(ratio, view)
        }

        // Question 1.3 Display a seekBar (with readable position/duration updated every seconds)
        viewModel.playerPositionState.observe(viewLifecycleOwner) { positionState ->
            updateSeekBar(positionState, view)
        }

        // Question 1.5 Display title/subtitles/image on top of player (be creative)
        viewModel.mediaDetail.observe(viewLifecycleOwner) { mediaDetail2 ->
            updateMediaDetail(mediaDetail2, view)
        }
    }

    private fun updatePlayerContainerRatio(ratio: PlayerRatio, view: View?) {
        val playerView: FrameLayout? = view?.findViewById(R.id.player_container)
        playerView?.let { playerView2 ->
            (playerView2.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "H," + ratio.w + ":" + ratio.h
            playerView2.invalidate()
            playerView2.requestLayout()
        }
    }

    private fun updateSeekBar(positionState: PositionState, view: View?) {
        val seekBarContainer: ConstraintLayout? = view?.findViewById(R.id.seek_bar_container)
        val seekBar: SeekBar? = view?.findViewById(R.id.seek_bar)
        val seekBarText: TextView? = view?.findViewById(R.id.seek_bar_text)
        seekBar?.let { seekBar2 ->
            seekBar2.max = positionState.duration.toInt()
            seekBar2.progress = positionState.position.toInt()
        }
        seekBarText?.let { seekBarText2 ->
            var text = positionState.position.msFormatTimeHumanReadable() + " / " + positionState.duration.msFormatTimeHumanReadable()
            if (positionState.position < 0 || positionState.duration < 0) {
                text = ""
            }
            seekBarText2.text = text
        }
        seekBarContainer?.let { container ->
            container.invalidate()
            container.requestLayout()
        }
    }

    private var track = 8

    private fun updateMediaDetail(mediaDetail: MediaDetailShort, view: View?) {
        val mediaDetailContainer: ConstraintLayout? = view?.findViewById(R.id.media_detail_container)
        val mediaDetailTitle: TextView? = view?.findViewById(R.id.media_detail_title)
        mediaDetailTitle?.let { mediaDetailTitle2 ->
            mediaDetailTitle2.text = mediaDetail.title
        }
        val mediaDetailSubtitle: TextView? = view?.findViewById(R.id.media_detail_subtitle)
        mediaDetailSubtitle?.let { mediaDetailSubtitle2 ->
            mediaDetailSubtitle2.text = mediaDetail.subtitle
        }
        // Question 1.6 Display a ui element in order to be able to select a distinct audio and text track from stream playlist
        // we just made a prototype that can change between two tracks with a button
        // we see visually that it works with the "Gear" text in the video
        // we could make a more advanced component to pick multiple tracks among the 10 available tracks
        // however, certain tracks crash the app, so we need to hard-code which track we want
        val selectTracksButton: Button? = view?.findViewById(R.id.select_track)
        selectTracksButton?.let { selectTracksButton2 ->
            selectTracksButton2.setOnClickListener {
                val trackType = 2
                val trackGroup = 0
                // Question 1.6 Display a ui element in order to be able to select a distinct audio and text track from stream playlist
                // we can make it swap between track 8 (the initial one) and track 2
                track = if (track == 8) 2 else 8
                player?.pushAction(PlayerAction.SelectTrack(trackType, trackGroup, track))
            }
        }

        val mediaDetailImage: ImageView? = view?.findViewById(R.id.media_detail_image)
        mediaDetailImage?.let { mediaDetailImage2 ->
            // we do not mix badly coroutines and RxJava, because here we do something very isolated
            // and very simple
            // And it will be disposed automatically using the lifecycle owner
            // we start an independent IO coroutine on the view lifecycle, just to download the image
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                var urlString = mediaDetail.urlImage
                val brokenUrl = "https://thumb.canalplus.pro/http/unsafe/480x270/media.canal-plus.com/wwwplus/image/38/4/1/VIGNETTE_AUTO_750644_H.jpg"
                val workingUrl = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/f1fa5f95-d548-41f5-85ba-b22ed9e8c6eb/d7x42sz-6ac29507-9f77-466a-98d9-bd856e4b3cfc.png/v1/fill/w_1024,h_746/tree_png_by_smiler4545_with_google_by_smiler4545_d7x42sz-fullview.png?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NzQ2IiwicGF0aCI6IlwvZlwvZjFmYTVmOTUtZDU0OC00MWY1LTg1YmEtYjIyZWQ5ZThjNmViXC9kN3g0MnN6LTZhYzI5NTA3LTlmNzctNDY2YS05OGQ5LWJkODU2ZTRiM2NmYy5wbmciLCJ3aWR0aCI6Ijw9MTAyNCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.PJ17jLVo6qFAqcz-JFjD6MwM8k0ytEZpWUh4IGhVp-M"
                if (mediaDetail.urlImage == brokenUrl) {
                    urlString = workingUrl
                }
                val url = URL(urlString)
                val image = try {
                    BitmapFactory.decodeStream(url.openConnection().getInputStream())
                }
                catch (t: Throwable)
                {
                    player?.pushAction(PlayerAction.Release(blockRestart = true))
                    (activity as MainActivity?)?.displayGenericErrorDialog()
                    return@launch
                }
                activity?.runOnUiThread {
                    mediaDetailImage2.setImageBitmap(image as Bitmap?)
                    mediaDetailImage2.visibility = View.VISIBLE
                    mediaDetailImage2.invalidate()
                    mediaDetailImage2.requestLayout()
                }
            }
        }
        mediaDetailContainer?.let { container ->
            container.visibility = View.VISIBLE
            container.invalidate()
            container.requestLayout()
        }
    }

    // Question 1.1 Respect video ratio of playback stream (see AnalyticsListener from Exoplayer)
    // We need a separate XML layout for the landscape if we want the 16x9 ratio to fill the sceen in landscape
    // Forcing activity?.requestedOrientation or rotating the view are not as good
    // AnalyticsListener has an UnstableAPI onVideoSizeChanged if the video ratio changes
    //
    // Question 1.7 Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
    override fun onStart() {
        super.onStart()
        // when the activity does a onRestart, onViewCreated is not called, so we must set up the player
        viewModel.mediaDetail.value?.manifestUrl?.let {
            initPlayer()
            sendActionStartPlaybackWrapped(url = it, seekToPositionMs = viewModel.playerPositionState.value?.position)
        }
    }

    private fun sendActionStartPlaybackWrapped(url: String, seekToPositionMs: Long?) {
        val callbackOnVideoSizeChanged: (PlayerRatio) -> Unit = { it ->
            viewModel.postPlayerRatio(it)
        }

        val callbackOnError: (Throwable) -> Unit = {
            Log.e("MyCanalTest", (it.message ?: "Error") + "\n" + it.stackTrace)
            player?.pushAction(PlayerAction.Release(blockRestart = true))
            (activity as MainActivity).displayGenericErrorDialog()
        }
        val callbackOnPositionStateChanged: (PositionState) -> Unit = { it ->
            // TESTING: this command below callbackOnError(Throwable("")) will trigger several
            // exceptions if we tap the third media in the list (that has a wrong image url not hard-coded)
            // the Observable.interval will call callbackOnError() every second
            // Moreover, the Coroutine decoding the image will have an exception because the URL is false
            // With breakpoints can see that all couroutines and Observables are disposed by the
            // PlayerFragment's onStop() that calls a Release action
            //callbackOnError(Throwable(""))
            viewModel.postPlayerPositionState(it)
        }
        player?.pushAction(PlayerAction.StartPlayback(
            manifestUrl = url,
            callbackOnVideoSizeChanged = callbackOnVideoSizeChanged,
            seekToPositionMs = seekToPositionMs,
            callbackOnPositionStateChanged = callbackOnPositionStateChanged,
            callbackOnError = callbackOnError
        ))
    }

    // https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8
    private fun getRatioFromUrl(url: String): PlayerRatio? {
        val regex = """_(\d+)x(\d+)_""".toRegex()
        val matchResult = regex.find(url)
        matchResult?.let {
            return PlayerRatio(matchResult.groups[1]?.value?.toInt()!!, matchResult.groups[2]?.value?.toInt()!!)
        }
        return null
    }

    // Question 1.7 Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
    // Using the state bundle is only for particular cases, and will not work if the app is closed, if the back button is pressed,
    // Side case, if the activity onRestart() is called with the device home button, the view is preserved, and our variable is kept in viewModel.mediaDetail
    // To always recover the position of the player no matter what, we should store this information persistently on the internal storage
    //
    // onSaveInstanceState adds persistent saving that the ViewModel does not provide, in case the app is stopped by the system
    // according to the link below, both are complementary, and onSaveInstanceState should only save small UI state things
    // https://medium.com/androiddevelopers/viewmodels-persistence-onsaveinstancestate-restoring-ui-state-and-loaders-fc7cc4a6c090#:~:text=ViewModels%20are%20not%20a%20replacement,that%20require%20lengthy%20serialization%2Fdeserialization.
    override fun onSaveInstanceState(outState: Bundle) {
        // API 28+ has onStop() after onSaveInstanceState(), otherwise it is before
        viewModel.playerPositionState.value?.position?.let {
            outState.putLong("previousPlayerPosition", it)
        }
        super.onSaveInstanceState(outState)
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
            player?.pushAction(PlayerAction.Release())
        }
    }

    // Question 1.7 Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
    override fun onStop() {
        super.onStop()
        // API 28+ has onStop() after onSaveInstanceState(), otherwise it is before
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            player?.pushAction(PlayerAction.Release())
        }
    }

    private fun initPlayer() {
        val context = context ?: return
        player = Player.getPlayerInstance(context).also {
            binding.playerContainer.addView(it.playerView)
        }
    }

    override fun onDestroyView() {
        activity.exitFullScreen()
        player?.pushAction(PlayerAction.Release(blockRestart = true))
        super.onDestroyView()
    }
}