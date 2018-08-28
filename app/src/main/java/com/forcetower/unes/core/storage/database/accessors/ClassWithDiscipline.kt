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

package com.forcetower.unes.core.storage.database.accessors

import androidx.room.Embedded
import androidx.room.Relation
import com.forcetower.unes.core.model.unes.Class
import com.forcetower.unes.core.model.unes.Discipline

data class ClassWithDiscipline(
    @Embedded
    var clazz: Class?,
    @Relation(parentColumn = "discipline_id", entityColumn = "uid")
    var disciplines: List<Discipline>
) {
    constructor(): this(null, ArrayList())

    fun singleDiscipline() = disciplines[0]
}
