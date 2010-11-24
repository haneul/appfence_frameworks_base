/*
 * Copyright (C) 2010 ?
 * //Copyright (C) 2009 The Android Open Source Project
 *
 * FIXME: is this the correct copyright?
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

package android.util;

//import android.util.Log;

/**
 * A "trusted transformation library." Contains methods that transform
 * [sensor] data from one form to another, then uses dalvik.system.Taint
 * to modify the taint tags/labels on the data.
 */
public final class ProcessName {

    private static final String TAG = "ProcessName";

    ///**
    // * Constructor...
    // */
    //ProcessName() {
    //    //Nothing to do yet.
    //}

    /**
     * Gets the name of the calling process.
     * Static, so class doesn't have to be instantiated.
     */
    native public static String getProcessName();

}

