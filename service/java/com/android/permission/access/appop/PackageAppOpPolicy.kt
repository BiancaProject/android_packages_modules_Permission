/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.permission.access.appop

import com.android.permission.access.AccessState
import com.android.permission.access.AccessUri
import com.android.permission.access.AppOpUri
import com.android.permission.access.PackageUri
import com.android.permission.access.UserState
import com.android.permission.access.collection.* // ktlint-disable no-wildcard-imports
import com.android.permission.access.external.PackageState

class PackageAppOpPolicy : BaseAppOpPolicy(PackageAppOpPersistence()) {
    override val subjectScheme: String
        get() = PackageUri.SCHEME

    override val objectScheme: String
        get() = AppOpUri.SCHEME

    override fun getModes(subject: AccessUri, state: AccessState): IndexedMap<String, Int>? {
        subject as PackageUri
        return state.userStates[subject.userId]?.packageAppOpModes?.get(subject.packageName)
    }

    override fun getOrCreateModes(subject: AccessUri, state: AccessState): IndexedMap<String, Int> {
        subject as PackageUri
        return state.userStates.getOrPut(subject.userId) { UserState() }
            .packageAppOpModes.getOrPut(subject.packageName) { IndexedMap() }
    }

    override fun removeModes(subject: AccessUri, state: AccessState) {
        subject as PackageUri
        state.userStates[subject.userId]?.packageAppOpModes?.remove(subject.packageName)
    }

    override fun onPackageRemoved(
        packageState: PackageState,
        oldState: AccessState,
        newState: AccessState
    ) {
        newState.userStates.forEachIndexed { _, _, userState ->
            userState.packageAppOpModes -= packageState.packageName
        }
    }
}