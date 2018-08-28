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

package com.forcetower.unes.core.util

import android.graphics.Color
import com.forcetower.unes.core.model.event.Session
import com.forcetower.unes.core.model.event.Speaker
import com.forcetower.unes.core.model.event.Tag
import com.forcetower.unes.core.model.unes.Contributor
import com.forcetower.unes.core.storage.database.accessors.SessionSpeakerTalker
import com.forcetower.unes.core.storage.database.accessors.SessionTagged
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import org.threeten.bp.ZonedDateTime

object MockUtils {

    @JvmStatic
    fun contributors(): List<Contributor> {
        val c1 = Contributor(1, "João Paulo Sena", "Programação do núcleo", "https://avatars.githubusercontent.com/ForceTower", "http://facebook.com/ForceTower")
        val c2 = Contributor(2, "Alberto Junior", "Muitas Idéias Geniais", "https://avatars.githubusercontent.com/AlbertoJunior", "http://facebook.com/alberto.junior.995")
        val c3 = Contributor(3, "Galuber Silva", "Tradução dos Textos (en)", "https://avatars.githubusercontent.com/sglauber", "http://facebook.com/Gss.14")
        return listOf(c1, c2, c3)
    }

    fun siecomp(): List<SessionWithData> {
        val speaker1 = Speaker(1, "Angelo Duarte")
        val speaker2 = Speaker(2, "Fernando Kappa")

        val tag1 = Tag(1, "Palestra", "Desempenho", Color.GREEN, false)
        val tag2 = Tag(2, "Palestra", "Medíocre", Color.RED, false)
        val tag3 = Tag(3, "Workshop", "Hardware", Color.BLUE, false)
        val tag4 = Tag(4, "Workshop", "Micro Controladores", Color.CYAN, false)
        val tag5 = Tag(5, "Workshop", "Programação", Color.MAGENTA, false)
        val tag6 = Tag(6, "Palestra", "Machine Learning", Color.YELLOW, false)
        val tag7 = Tag(7, "Palestra", "Data Science", Color.GRAY, false)


        val session1 = Session(1, 0, ZonedDateTime.now().minusMinutes(30), ZonedDateTime.now().plusHours(2), "Aumentando seu desempenho pessoal", "Auditório 3 - Módulo 4", "Duarte chamando todos de mediocre, padrão", "")
        val session2 = Session(2, 0, ZonedDateTime.now().plusHours(2), ZonedDateTime.now().plusHours(4), "Almoço", "Bandejão", "A hora de comer é sagrada", "")
        val session3 = Session(3, 0, ZonedDateTime.now().minusMinutes(15), ZonedDateTime.now().plusHours(1), "Machine Learning mudando a sua vida", "Laboratório de Programação - MP56", "Mecanizando sua vida toda")

        val sst1 = SessionSpeakerTalker().apply { speakers = listOf(speaker1) }
        val st1 = SessionTagged().apply { tag = listOf(tag1) }
        val st2 = SessionTagged().apply { tag = listOf(tag2) }

        val sst2 = SessionSpeakerTalker().apply { speakers = listOf(speaker2) }
        val st3 = SessionTagged().apply { tag = listOf(tag3) }
        val st4 = SessionTagged().apply { tag = listOf(tag4) }

        val sst3 = SessionSpeakerTalker().apply { speakers = listOf(speaker2, speaker1) }
        val st5 = SessionTagged().apply { tag = listOf(tag5) }
        val st6 = SessionTagged().apply { tag = listOf(tag6) }
        val st7 = SessionTagged().apply { tag = listOf(tag7) }

        val sd1 = SessionWithData().apply {
            session = session1
            speakersRel = listOf(sst1)
            displayTags = listOf(st1, st2)
        }

        val sd2 = SessionWithData().apply {
            session = session2
            speakersRel = listOf(sst2)
            displayTags = listOf(st3, st4)
        }

        val sd3 = SessionWithData().apply {
            session = session3
            speakersRel = listOf(sst3)
            displayTags = listOf(st5, st6, st7)
        }

        return listOf(sd1, sd2, sd3)
    }

}
