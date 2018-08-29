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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.unes.R
import com.forcetower.unes.core.injection.Injectable
import com.forcetower.unes.core.storage.database.accessors.LocationWithGroup
import com.forcetower.unes.core.vm.ScheduleViewModel
import com.forcetower.unes.core.vm.UViewModelFactory
import com.forcetower.unes.databinding.FragmentScheduleBinding
import com.forcetower.unes.feature.shared.UFragment
import com.forcetower.unes.feature.shared.provideViewModel
import com.forcetower.unes.feature.siecomp.SiecompActivity
import timber.log.Timber
import javax.inject.Inject

class ScheduleFragment: UFragment(), Injectable {
    @Inject
    lateinit var factory: UViewModelFactory

    private lateinit var viewModel: ScheduleViewModel
    private lateinit var binding: FragmentScheduleBinding

    private val linePool = RecyclerView.RecycledViewPool()
    private val lineAdapter by lazy { ScheduleLineAdapter(linePool) }
    private val blockPool = RecyclerView.RecycledViewPool()
    private val blockAdapter by lazy { ScheduleBlockAdapter(blockPool, requireContext()) }

    init {
        linePool.setMaxRecycledViews(1, 4)
        linePool.setMaxRecycledViews(2, 5)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        getTabLayout().visibility = GONE
        getToolbarTitleText().text = getString(R.string.label_schedule)
        getAppBar().elevation = 0f
        return FragmentScheduleBinding.inflate(inflater, container, false).also {
            binding = it
            binding.btnSiecompSchedule.setOnClickListener{_ -> SiecompActivity.startActivity(requireContext())}
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            recyclerScheduleLine.apply {
                setRecycledViewPool(linePool)
                adapter = lineAdapter
                itemAnimator = DefaultItemAnimator()
            }

            recyclerScheduleBlocks.apply {
                setRecycledViewPool(blockPool)
                adapter = blockAdapter
            }
        }

        viewModel = provideViewModel(factory)
        viewModel.scheduleSrc.observe(this, Observer { populateInterface(it) })
    }

    private fun populateInterface(locations: List<LocationWithGroup>) {
        Timber.d("Locations: $locations")
        binding.empty = locations.isEmpty()
        binding.executePendingBindings()
        lineAdapter.adaptList(locations)
        blockAdapter.adaptList(locations)
    }
}