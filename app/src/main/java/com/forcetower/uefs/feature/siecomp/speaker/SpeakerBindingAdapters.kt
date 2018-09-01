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

package com.forcetower.uefs.feature.siecomp.speaker

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.forcetower.uefs.R
import com.forcetower.uefs.core.model.event.Speaker

/**
 * Loads a [Speaker]'s photo or picks a default avatar if no photo is specified.
 */
@BindingAdapter(value = ["speakerImage"], requireAll = false)
fun speakerImage(
        imageView: ImageView,
        speaker: Speaker?
) {
    speaker ?: return

    val placeholderId = when (speaker.name[0].toLowerCase()) {
        in 'a'..'i' -> R.drawable.ic_default_avatar_1
        in 'j'..'r' -> R.drawable.ic_default_avatar_2
        else -> R.drawable.ic_default_avatar_3
    }

    if (speaker.image.isBlank()) {
        imageView.setImageResource(placeholderId)
    } else {
        val imageLoad = Glide.with(imageView)
                .load(speaker.image)
                .apply(
                        RequestOptions()
                                .placeholder(placeholderId)
                                .circleCrop()
                )
        imageLoad.into(imageView)
    }
}

/**
 * An interface for responding to image loading completion.
 */
interface ImageLoadListener {
    fun onImageLoaded()
    fun onImageLoadFailed()
}
