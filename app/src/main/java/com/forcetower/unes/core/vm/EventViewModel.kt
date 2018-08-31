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

package com.forcetower.unes.core.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import com.forcetower.unes.core.storage.repository.SIECOMPRepository
import com.forcetower.unes.core.storage.resource.Resource
import com.forcetower.unes.core.storage.resource.Status
import timber.log.Timber
import javax.inject.Inject

class EventViewModel @Inject constructor(
    private val repository: SIECOMPRepository
): ViewModel() {
    private var loading: Boolean = false

    val refreshing: MutableLiveData<Boolean> = MutableLiveData()
    val refreshSource: MediatorLiveData<Resource<List<SessionWithData>>> = MediatorLiveData()

    fun getSessionsFromDayLocal(day: Int) = repository.getSessionsFromDayLocal(day)

    fun loadSessions() {
        if (!loading) {
            loading = true
            val source = repository.getAllSessions()
            refreshSource.addSource(source) {
                when (it.status) {
                    Status.SUCCESS, Status.ERROR -> {
                        Timber.d("Status SIECOMP Schedule: ${it.status}")
                        refreshSource.removeSource(source)
                        refreshing.value = false
                        loading = false
                    }
                    Status.LOADING -> {
                        refreshing.value = true
                        loading = true
                    }
                }
            }
        }
    }
}