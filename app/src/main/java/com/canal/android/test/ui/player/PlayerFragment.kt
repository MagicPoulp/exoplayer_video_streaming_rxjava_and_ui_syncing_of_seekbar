package com.canal.android.test.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.canal.android.test.R
import com.canal.android.test.common.PlayerRatio
import com.canal.android.test.common.PositionState
import com.canal.android.test.common.formatHumanReadable
import com.canal.android.test.databinding.FragmentPlayerBinding
import com.canal.android.test.player.Player
import com.canal.android.test.player.model.PlayerAction
import com.canal.android.test.ui.common.BaseFragment
import com.canal.android.test.ui.common.exitFullScreen
import com.canal.android.test.ui.common.setFullScreen
import com.canal.android.test.ui.player.model.MediaUi
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlayerFragment : BaseFragment<MediaUi, FragmentPlayerBinding>() {

    override val viewModel: PlayerViewModel by viewModel { parametersOf(navigateTo) }

    override val viewBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentPlayerBinding
        get() = FragmentPlayerBinding::inflate

    private var player: Player? = null
    private var previousPlayerPosition: Long = 0
    private var mediaUrlForRestart: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setFullScreen()
        savedInstanceState?.getLong("previousPlayerPosition", 0)?.let { previousPlayerPosition = it }

        val simpleSeekBar: SeekBar? = activity?.findViewById(R.id.seek_bar)

        initPlayer()
        viewModel.uiData.observe(viewLifecycleOwner) { mediaUi ->
            mediaUrlForRestart = mediaUi.manifestUrl
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
        val seekBarText: EditText? = view?.findViewById(R.id.seek_bar_text)
        seekBar?.let { seekBar2 ->
            seekBar2.max = positionState.duration.toInt()
            seekBar2.progress = positionState.position.toInt()
        }
        seekBarText?.let { seekBarText2 ->
            var text = positionState.position.formatHumanReadable + " / " + positionState.duration.formatHumanReadable
            if (positionState.position < 0 || positionState.duration < 0) {
                text = ""
            }
            seekBarText2.setText(text)
        }
        seekBarContainer?.invalidate()
        seekBarContainer?.requestLayout()
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
        mediaUrlForRestart?.let {
            initPlayer()
            sendActionStartPlaybackWrapped(url = it, seekToPositionMs = previousPlayerPosition)
        }
    }

    private fun sendActionStartPlaybackWrapped(url: String, seekToPositionMs: Long?) {
        val callbackOnVideoSizeChanged: (PlayerRatio) -> Unit = { it ->
            viewModel.postPlayerRatio(it)
        }
        val callbackOnPositionStateChanged: (PositionState) -> Unit = { it ->
            viewModel.postPlayerPositionState(it)
        }
        player?.pushAction(PlayerAction.StartPlayback(
            manifestUrl = url,
            callbackOnVideoSizeChanged = callbackOnVideoSizeChanged,
            seekToPositionMs = seekToPositionMs,
            callbackOnPositionStateChanged = callbackOnPositionStateChanged)
        )
    }


    // https://devstreaming-cdn.apple.com/videos/streaming/examples/bipbop_16x9/bipbop_16x9_variant.m3u8
    private fun getRatioFromUrl(url: String): PlayerRatio? {
        //val regex = """/[^0-9]+_(\d+x\d+)_""".toRegex()
        //val regex = """/.*[^0123456789]+_(\d+x\d+)_""".toRegex()
        val regex = """_(\d+)x(\d+)_""".toRegex()
        val matchResult = regex.find(url)
        matchResult?.let {
            return PlayerRatio(matchResult.groups[1]?.value?.toInt()!!, matchResult.groups[2]?.value?.toInt()!!)
        }
        return null
    }

    // Question 1.7 Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
    // Using the state bundle is only for particular cases, and will not work if the app is closed, if the back button is pressed,
    // Side case, if the activity onRestart() is called with the device home button, the view is preserved, and our variable previousPlayerPosition is kept
    // To always recover the position of the player no matter what, we should store this information persistently on the internal storage
    override fun onSaveInstanceState(outState: Bundle) {
        // API 28+ has onStop() after onSaveInstanceState(), otherwise it is before
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            previousPlayerPosition = 10000 // TODO get the current position
        }
        outState.putLong("previousPlayerPosition", previousPlayerPosition)
        super.onSaveInstanceState(outState)
    }

    // Question 1.7 Respect playerFragment lifeCycle (player should be released onStop, and should playback at previous position at onStart)
    override fun onStop() {
        super.onStop()
        // API 28+ has onStop() after onSaveInstanceState(), otherwise it is before
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
            previousPlayerPosition = 10000 // TODO get the current position
        }
        player?.pushAction(PlayerAction.Release)
    }

    private fun initPlayer() {
        val context = context ?: return
        player = Player.getPlayerInstance(context).also {
            binding.playerContainer.addView(it.playerView)
        }
    }

    override fun onDestroyView() {
        activity.exitFullScreen()
        player?.pushAction(PlayerAction.Release)
        super.onDestroyView()
    }
}