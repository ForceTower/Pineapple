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

package com.forcetower.unes.feature.schedule

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.unes.core.storage.database.accessors.LocationWithGroup
import com.forcetower.unes.databinding.ItemScheduleLineClassBinding
import com.forcetower.unes.databinding.ItemScheduleLineDayBinding
import com.forcetower.unes.feature.shared.inflater
import com.forcetower.unes.feature.shared.toLongWeekDay
import com.forcetower.unes.feature.shared.toWeekDay


private const val SCHEDULE_LINE_DAY_HOLDER: Int = 1
private const val SCHEDULE_LINE_CLASS_HOLDER: Int = 2

class ScheduleLineAdapter(
    private val pool: RecyclerView.RecycledViewPool
): ListAdapter<ScheduleDay, DayLineHolder>(DayLineDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayLineHolder {
        val binding = ItemScheduleLineDayBinding.inflate(parent.inflater(), parent, false)
        return DayLineHolder(binding, pool)
    }

    override fun onBindViewHolder(holder: DayLineHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int) = SCHEDULE_LINE_DAY_HOLDER

    fun adaptList(locations: List<LocationWithGroup>) {
        val map = locations.groupBy { it.location!!.day.trim() }
        val list = ArrayList<ScheduleDay>()

        for (i in 1..7) {
            val day = i.toWeekDay()
            val longDay = i.toLongWeekDay()
            val classes = map[day]
            if (classes != null) list.add(ScheduleDay(longDay, classes))
        }

        submitList(list)
    }

}

class ScheduleLineClassAdapter: ListAdapter<LocationWithGroup, LocationLineHolder>(ClassLineDiff) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationLineHolder {
        val binding = ItemScheduleLineClassBinding.inflate(parent.inflater(), parent, false)
        return LocationLineHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationLineHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int) = SCHEDULE_LINE_CLASS_HOLDER
}

class DayLineHolder(
    private val binding: ItemScheduleLineDayBinding,
    pool: RecyclerView.RecycledViewPool
): RecyclerView.ViewHolder(binding.root) {
    private val adapter by lazy { ScheduleLineClassAdapter() }
    init {
        binding.recyclerDay.setRecycledViewPool(pool)
        binding.recyclerDay.adapter = adapter
    }

    fun bind(day: ScheduleDay) {
        binding.textScheduleDay.text = day.day
        adapter.submitList(day.location)
    }
}

class LocationLineHolder(
    private val binding: ItemScheduleLineClassBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(location: LocationWithGroup) {
        binding.location = location
    }
}

object DayLineDiff: DiffUtil.ItemCallback<ScheduleDay>() {
    override fun areItemsTheSame(oldItem: ScheduleDay, newItem: ScheduleDay) = oldItem.day == newItem.day
    override fun areContentsTheSame(oldItem: ScheduleDay, newItem: ScheduleDay) = oldItem.location == newItem.location
}

object ClassLineDiff: DiffUtil.ItemCallback<LocationWithGroup>() {
    override fun areItemsTheSame(oldItem: LocationWithGroup, newItem: LocationWithGroup) = oldItem.location!!.uid == newItem.location!!.uid
    override fun areContentsTheSame(oldItem: LocationWithGroup, newItem: LocationWithGroup) = oldItem.location == newItem.location
}

data class ScheduleDay(
    val day: String,
    val location: List<LocationWithGroup>
)


