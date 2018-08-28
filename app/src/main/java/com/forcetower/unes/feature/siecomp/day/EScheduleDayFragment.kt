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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.doOnNextLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.unes.core.injection.Injectable
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import com.forcetower.unes.core.vm.EventViewModel
import com.forcetower.unes.core.vm.UViewModelFactory
import com.forcetower.unes.databinding.FragmentSiecompScheduleDayBinding
import com.forcetower.unes.feature.shared.UFragment
import com.forcetower.unes.feature.shared.clearDecorations
import com.forcetower.unes.feature.shared.provideActivityViewModel
import com.forcetower.unes.feature.siecomp.ETimeUtils
import com.forcetower.unes.feature.siecomp.ETimeUtils.SIECOMP_TIMEZONE
import javax.inject.Inject

class EScheduleDayFragment: UFragment(), Injectable {

    companion object {
        private const val ARG_EVENT_DAY = "arg.EVENT_DAY"

        fun newInstance(day: Int): EScheduleDayFragment {
            val args = bundleOf(ARG_EVENT_DAY to day)
            return EScheduleDayFragment().apply { arguments = args }
        }
    }

    @Inject
    lateinit var factory: UViewModelFactory
    private lateinit var viewModel: EventViewModel
    private lateinit var binding: FragmentSiecompScheduleDayBinding

    private val eventDay: Int by lazy {
        val args = arguments?: throw IllegalStateException("No Arguments")
        args.getInt(ARG_EVENT_DAY)
    }

    private lateinit var adapter: EScheduleDayAdapter
    private val tagViewPool = RecyclerView.RecycledViewPool()
    private val sessionViewPool = RecyclerView.RecycledViewPool()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = provideActivityViewModel(factory)
        binding = FragmentSiecompScheduleDayBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@EScheduleDayFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = EScheduleDayAdapter(tagViewPool, ETimeUtils.SIECOMP_TIMEZONE)
        binding.recyclerDaySchedule.apply {
            setRecycledViewPool(sessionViewPool)
            (layoutManager as LinearLayoutManager).recycleChildrenOnDetach = true
            (itemAnimator as DefaultItemAnimator).run {
                supportsChangeAnimations = false
                addDuration = 160L
                moveDuration = 160L
                changeDuration = 160L
                removeDuration = 120L
            }
        }

        //TODO Maybe move to current event
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getSessionsFromDay(eventDay).observe(this, Observer {
            it?: return@Observer
            populateInterface(it)
        })
    }

    private fun populateInterface(data: List<SessionWithData>) {
        adapter.submitList(data)
        binding.recyclerDaySchedule.run {
            doOnNextLayout {
                clearDecorations()
                if (data.isNotEmpty()) {
                    addItemDecoration(
                        ScheduleItemHeaderDecoration(
                            it.context, data, SIECOMP_TIMEZONE
                        )
                    )
                }
            }
        }
    }

}