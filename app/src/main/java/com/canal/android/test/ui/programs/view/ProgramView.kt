package com.canal.android.test.ui.programs.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.canal.android.test.databinding.LayoutProgramBinding

class ProgramView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var binding : LayoutProgramBinding = LayoutProgramBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        radius = 40f
    }

    fun setData(
        title: String,
        subtitle: String,
        urlImage: String,
        urlLogoChannel: String?
    ) {
        binding.programTitle.text = title

        binding.programSubtitle.text = subtitle

        Glide.with(this)
            .load(urlImage)
            .centerCrop()
            .into(binding.programImage)

        urlLogoChannel?.let { url ->
            Glide.with(this)
                .load(url)
                .fitCenter()
                .into(binding.programChannelImage)
        }
    }
}