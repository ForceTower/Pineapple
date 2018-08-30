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

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.forcetower.unes.core.model.unes.Access
import com.forcetower.unes.core.storage.database.UDatabase
import com.forcetower.unes.feature.shared.map
import javax.inject.Inject

class LaunchViewModel @Inject constructor(
    database: UDatabase
): ViewModel() {
    var started = false
    private val accessSrc = database.accessDao().getAccess()

    fun getAccess(): LiveData<Event<Destination>> =
        accessSrc.map {
            when (it) {
                null -> Event(Destination.LOGIN_ACTIVITY)
                else -> Event(Destination.HOME_ACTIVITY)
            }
        }
}

enum class Destination { LOGIN_ACTIVITY, HOME_ACTIVITY }

//--------------------------------------------------------------------------
open class Event<out T>(private val content: T) {
    private var hasBeenHandled = false

    fun getIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    fun peek(): T = content
}
class EventObserver<T>(private val onEventUnhandled: (T) -> Unit): Observer<Event<T>> {
    override fun onChanged(event: Event<T>?) {
        event?.getIfNotHandled()?.let { value ->
            onEventUnhandled(value)
        }
    }
}