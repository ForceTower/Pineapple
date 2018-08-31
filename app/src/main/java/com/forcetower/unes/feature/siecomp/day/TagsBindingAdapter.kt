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

import android.content.Context
import android.graphics.Color
import android.graphics.Color.TRANSPARENT
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.forcetower.unes.R
import com.forcetower.unes.core.model.event.Tag
import com.forcetower.unes.core.util.ColorUtils
import timber.log.Timber

@BindingAdapter("sessionTags")
fun sessionTags(recyclerView: RecyclerView, sessionTags: List<Tag>?) {
    recyclerView.adapter = (recyclerView.adapter as? TagsAdapter ?: TagsAdapter())
        .apply {
            submitList(sessionTags ?: emptyList())
        }
}

@BindingAdapter("tagTint")
fun tagTint(textView: TextView, color: Int) {
    // Tint the colored dot
    (textView.compoundDrawablesRelative[0]?.mutate() as? GradientDrawable)?.setColor(
            tagTintOrDefault(
                    color,
                    textView.context
            )
    )?: Timber.d("Some of them are null")
}

fun tagTintOrDefault(color: Int, context: Context): Int {
    return if (color != TRANSPARENT) {
        valueOf(color)
    } else {
        ContextCompat.getColor(context, R.color.default_tag_color)
    }
}

fun valueOf(color: Int): Int {
    return ColorUtils.modifyAlpha(color, 1)
}
