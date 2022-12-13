package com.canal.android.test.ui.programs.view.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.canal.android.test.domain.model.NavigateTo
import com.canal.android.test.ui.programs.model.ProgramUi
import com.canal.android.test.ui.programs.view.ProgramView

class ProgramsAdapter(
        private val navigateTo: (NavigateTo) -> Unit
) : ListAdapter<ProgramUi, ProgramsAdapter.ViewHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ProgramView(
                        parent.context
                )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val programUi = getItem(position)
        val programView = (holder.itemView as ProgramView)
        programView.setData(
                title = programUi.title,
                subtitle = programUi.subtitle,
                urlImage = programUi.urlImage,
                urlLogoChannel = programUi.urlLogoChannel
        )
        programView.setOnClickListener {
            navigateTo(programUi.navigateTo)
        }
    }

    class ViewHolder(view: ProgramView) : RecyclerView.ViewHolder(view)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ProgramUi>() {
            override fun areItemsTheSame(
                oldItem: ProgramUi,
                newItem: ProgramUi
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProgramUi,
                newItem: ProgramUi
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}