/*
 * Copyright (C) 2010 The Android Open Source Project
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

package com.ctv.settings.network.mcast;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;

public abstract class ProgressCategoryBase extends PreferenceCategory {
    public ProgressCategoryBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Turn on/off the progress indicator and text on the right.
     *
     * @param progressOn whether or not the progress should be displayed
     */
    public abstract void setProgress(boolean progressOn);
}