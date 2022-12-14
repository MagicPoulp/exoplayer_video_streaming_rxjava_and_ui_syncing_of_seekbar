package com.canal.android.test.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity.setFullScreen()

        viewModel.uiData.observe(viewLifecycleOwner) { mediaUi ->
            player?.pushAction(PlayerAction.StartPlayback(mediaUi.manifestUrl))
        }

        initPlayer()
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