/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.forcetower.unes.feature.siecomp.day

import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.unes.core.model.event.Tag
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import com.forcetower.unes.databinding.ItemEventSessionBinding
import com.forcetower.unes.databinding.ItemEventSessionTagBinding
import com.forcetower.unes.feature.shared.inflater
import com.forcetower.unes.widget.UnscrollableFlexboxLayoutManager
import org.threeten.bp.ZoneId
import timber.log.Timber

class EScheduleDayAdapter(
    private val tagViewPool: RecyclerView.RecycledViewPool,
    private val zone: ZoneId
): ListAdapter<SessionWithData, SessionHolder>(SessionDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionHolder {
        val binding = ItemEventSessionBinding.inflate(parent.inflater(), parent, false)
        return SessionHolder(binding, tagViewPool, zone)
    }

    override fun onBindViewHolder(holder: SessionHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TagsAdapter: ListAdapter<Tag, TagHolder>(TagDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagHolder {
        val binding = ItemEventSessionTagBinding.inflate(parent.inflater(), parent, false)
        return TagHolder(binding)
    }

    override fun onBindViewHolder(holder: TagHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

class SessionHolder(
    private val binding: ItemEventSessionBinding,
    private val tagViewPool: RecyclerView.RecycledViewPool,
    private val zone: ZoneId
): RecyclerView.ViewHolder(binding.root) {
    init {
        binding.recyclerTags.apply {
            setRecycledViewPool(tagViewPool)
            layoutManager = UnscrollableFlexboxLayoutManager(binding.root.context).apply {
                recycleChildrenOnDetach = true
            }
            itemAnimator = DefaultItemAnimator()
        }
    }

    fun bind(session: SessionWithData) {
        binding.data = session
        binding.zone = zone
        binding.executePendingBindings()
    }
}

class TagHolder(
    private val binding: ItemEventSessionTagBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(tag: Tag) {
        binding.tag = tag
        binding.executePendingBindings()
    }
}

object SessionDiff: DiffUtil.ItemCallback<SessionWithData>() {
    override fun areItemsTheSame(oldItem: SessionWithData, newItem: SessionWithData): Boolean {
        return oldItem.session.uid == newItem.session.uid
    }

    override fun areContentsTheSame(oldItem: SessionWithData, newItem: SessionWithData): Boolean {
        return oldItem == newItem
    }
}

object TagDiff: DiffUtil.ItemCallback<Tag>() {
    override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem.uid == newItem.uid
    }

    override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
        return oldItem == newItem
    }

}