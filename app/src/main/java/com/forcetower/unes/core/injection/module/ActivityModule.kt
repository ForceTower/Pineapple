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

package com.forcetower.unes.core.injection.module

import com.forcetower.unes.feature.about.AboutActivity
import com.forcetower.unes.feature.home.HomeActivity
import com.forcetower.unes.feature.login.LoginActivity
import com.forcetower.unes.feature.siecomp.SiecompActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun bindLoginActivity(): LoginActivity
    @ContributesAndroidInjector(modules = [HomeModule::class])
    abstract fun bindHomeActivity() : HomeActivity
    @ContributesAndroidInjector(modules = [AboutModule::class])
    abstract fun bindAboutActivity(): AboutActivity
    @ContributesAndroidInjector(modules = [SiecompModule::class])
    abstract fun bindSiecompActivity(): SiecompActivity
}