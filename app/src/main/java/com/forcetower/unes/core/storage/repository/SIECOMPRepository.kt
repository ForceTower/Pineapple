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

package com.forcetower.unes.core.storage.repository

import androidx.lifecycle.LiveData
import com.forcetower.unes.AppExecutors
import com.forcetower.unes.core.storage.database.EDatabase
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import com.forcetower.unes.core.storage.network.ServerSession
import com.forcetower.unes.core.storage.network.UService
import com.forcetower.unes.core.storage.network.adapter.ApiResponse
import com.forcetower.unes.core.storage.resource.NetworkBoundResource
import javax.inject.Inject

class SIECOMPRepository @Inject constructor(
    private val database: EDatabase,
    private val executors: AppExecutors,
    private val service: UService
) {

    fun getSessionsFromDayLocal(day: Int) = database.eventDao().getSessionsFromDay(day)

    fun getAllSessions() =
            object: NetworkBoundResource<List<SessionWithData>, List<ServerSession>>(executors) {
                override fun loadFromDb() = database.eventDao().getAllSessions()
                override fun shouldFetch(it: List<SessionWithData>?) = true
                override fun createCall() = service.siecompSessions()
                override fun saveCallResult(value: List<ServerSession>) {
                    database.eventDao().insertServerSessions(value)
                }
            }.asLiveData()
}