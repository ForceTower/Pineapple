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

package com.forcetower.uefs.feature.shared

import android.view.View
import android.view.View.*
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.forcetower.uefs.widget.CustomSwipeRefreshLayout

@BindingAdapter("clipToCircle")
fun clipToCircle(view: View, clip: Boolean) {
    view.clipToOutline = clip
    view.outlineProvider = if (clip) CircularOutlineProvider else null
}

@BindingAdapter("swipeRefreshColors")
fun setSwipeRefreshColors(swipeRefreshLayout: CustomSwipeRefreshLayout, colorResIds: IntArray) {
    swipeRefreshLayout.setColorSchemeColors(*colorResIds)
}

@BindingAdapter("invisibleUnless")
fun invisibleUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) VISIBLE else INVISIBLE
}

@BindingAdapter("goneUnless")
fun goneUnless(view: View, visible: Boolean) {
    view.visibility = if (visible) VISIBLE else GONE
}

@BindingAdapter("goneIf")
fun goneIf(view: View, gone: Boolean) {
    view.visibility = if (gone) GONE else VISIBLE
}

@BindingAdapter("pageMargin")
fun pageMargin(viewPager: ViewPager, pageMargin: Float) {
    viewPager.pageMargin = pageMargin.toInt()
}

@BindingAdapter("refreshing")
fun swipeRefreshing(refreshLayout: CustomSwipeRefreshLayout, refreshing: Boolean) {
    refreshLayout.isRefreshing = refreshing
}

@BindingAdapter("onSwipeRefresh")
fun onSwipeRefresh(view: CustomSwipeRefreshLayout, function: SwipeRefreshLayout.OnRefreshListener) {
    view.setOnRefreshListener(function)
}