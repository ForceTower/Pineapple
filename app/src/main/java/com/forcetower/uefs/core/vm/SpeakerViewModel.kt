/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs.core.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.forcetower.uefs.core.model.event.Speaker
import com.forcetower.uefs.core.storage.repository.SIECOMPRepository
import com.forcetower.uefs.feature.shared.map
import com.forcetower.uefs.feature.shared.setValueIfNew
import timber.log.Timber
import javax.inject.Inject

class SpeakerViewModel @Inject constructor(
    private val repository: SIECOMPRepository
): ViewModel() {
    private val speakerId = MutableLiveData<Long?>()

    private val _speaker = MediatorLiveData<Speaker?>()
    val speaker: LiveData<Speaker?>
        get() = _speaker

    val hasProfileImage: LiveData<Boolean> = _speaker.map {
        !it?.image.isNullOrBlank() && it?.image != "null"
    }

    init {
        _speaker.addSource(speakerId) {
            Timber.d("Speaked Id set to $it")
            refreshSpeaker(it)
        }
    }

    private fun refreshSpeaker(id: Long?) {
        if (id != null) {
            val source = repository.getSpeaker(id)
            _speaker.addSource(source) { value ->
                _speaker.value = value
            }
        }
    }

    fun setSpeakerId(id: Long?) {
        speakerId.setValueIfNew(id)
    }
}