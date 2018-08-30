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

package com.forcetower.unes

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.forcetower.unes.core.vm.Destination
import com.forcetower.unes.core.vm.EventObserver
import com.forcetower.unes.core.vm.LaunchViewModel
import com.forcetower.unes.core.vm.UViewModelFactory
import com.forcetower.unes.feature.home.HomeActivity
import com.forcetower.unes.feature.login.LoginActivity
import com.forcetower.unes.feature.shared.provideViewModel
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class LauncherActivity : AppCompatActivity(), HasSupportFragmentInjector {
    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>
    @Inject
    lateinit var factory: UViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: LaunchViewModel = provideViewModel(factory)
        viewModel.getAccess().observe(this, EventObserver {
            if (!viewModel.started) {
                when (it) {
                    Destination.LOGIN_ACTIVITY -> startActivity(Intent(this, LoginActivity::class.java))
                    Destination.HOME_ACTIVITY -> startActivity(Intent(this, HomeActivity::class.java))
                }
                viewModel.started = true
                finish()
            }
        })
    }


    override fun supportFragmentInjector() = fragmentInjector
}
