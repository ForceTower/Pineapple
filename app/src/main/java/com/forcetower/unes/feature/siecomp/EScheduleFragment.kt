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

package com.forcetower.unes.feature.siecomp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.forcetower.unes.core.injection.Injectable
import com.forcetower.unes.core.vm.EventViewModel
import com.forcetower.unes.core.vm.UViewModelFactory
import com.forcetower.unes.databinding.FragmentSiecompScheduleBinding
import com.forcetower.unes.feature.shared.UFragment
import com.forcetower.unes.feature.shared.provideActivityViewModel
import com.forcetower.unes.feature.siecomp.ETimeUtils.SIECOMPDays
import com.forcetower.unes.feature.siecomp.day.EScheduleDayFragment
import com.google.android.material.tabs.TabLayout
import timber.log.Timber
import javax.inject.Inject

class EScheduleFragment: UFragment(), Injectable {

    companion object {
        private val COUNT = SIECOMPDays.size
    }

    @Inject
    lateinit var factory: UViewModelFactory
    private lateinit var viewModel: EventViewModel
    private lateinit var binding: FragmentSiecompScheduleBinding

    private lateinit var viewPager: ViewPager
    private lateinit var tabs: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = provideActivityViewModel(factory)

        return FragmentSiecompScheduleBinding.inflate(inflater, container, false).also {
            binding = it
            tabs = binding.tabLayout
            viewPager = binding.pagerSchedule
        }.apply {
            setLifecycleOwner(this@EScheduleFragment)
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.offscreenPageLimit = COUNT - 1

        tabs.setupWithViewPager(viewPager)

        viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                Timber.d("Position selected: $position")
            }
        })

        viewPager.adapter = EScheduleAdapter(childFragmentManager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getSessions().observe(this, Observer {
            Timber.d("Loading all event status: ${it.status}")
        })
    }

    inner class EScheduleAdapter(fm: FragmentManager): FragmentPagerAdapter(fm) {
        override fun getCount() = COUNT

        override fun getItem(position: Int): Fragment {
            return EScheduleDayFragment.newInstance(position)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return SIECOMPDays[position].formatMonthDay()
        }

    }
}