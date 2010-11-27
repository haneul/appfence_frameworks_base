/* //device/libs/android_runtime/android_util_Process.cpp
**
** Copyright 2006, The Android Open Source Project
**
** Licensed under the Apache License, Version 2.0 (the "License"); 
** you may not use this file except in compliance with the License. 
** You may obtain a copy of the License at 
**
**     http://www.apache.org/licenses/LICENSE-2.0 
**
** Unless required by applicable law or agreed to in writing, software 
** distributed under the License is distributed on an "AS IS" BASIS, 
** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
** See the License for the specific language governing permissions and 
** limitations under the License.
*/

#define LOG_TAG "ProcessName"

#include <utils/Log.h>
#include <cutils/process_name.h>

#include <android_runtime/AndroidRuntime.h>

#include "JNIHelp.h"

namespace android {

/**
 * See frameworks/base/core/jni/android_database_SQLiteStatement.cpp, 
 *   frameworks/base/core/java/android/database/sqlite/SQLiteStatement.java:
 */
static jstring android_util_ProcessName_getProcessName(JNIEnv* env, jobject clazz)
{
    LOGW("phornyac: getProcessName native: entered");
    const char * c_process_name = get_process_name();
    jstring j_process_name = env->NewStringUTF(c_process_name);
    LOGW("phornyac: getProcessName native: returning jstring for "
            "process_name=%s", c_process_name);
    return j_process_name;
}

static const JNINativeMethod methods[] = {
    {"getProcessName",       "()Ljava/lang/String;",
        (void*)android_util_ProcessName_getProcessName}
};

const char* const kProcessNamePathName = "android/util/ProcessName";

int register_android_util_ProcessName(JNIEnv* env)
{
    jclass clazz;

    clazz = env->FindClass(kProcessNamePathName);
    LOG_FATAL_IF(clazz == NULL, "Unable to find class android.util.ProcessName");

    return AndroidRuntime::registerNativeMethods(
        env, kProcessNamePathName,
        methods, NELEM(methods));
}

}  //namespace android
