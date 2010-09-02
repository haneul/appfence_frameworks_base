/*
 * Copyright (C) 2006 The Android Open Source Project
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

///#define LOG_TAG "JavaBinder"
//#define LOG_NDEBUG 0

///#include "android_util_Transform.h"
#include "JNIHelp.h"

///#include <fcntl.h>
///#include <sys/stat.h>
///#include <stdio.h>

///#include <utils/Atomic.h>
///#include <binder/IInterface.h>
///#include <binder/IPCThreadState.h>
///#include <utils/Log.h>
///#include <binder/Parcel.h>
#include <binder/ProcessState.h>
///#include <binder/IServiceManager.h>

#include <android_runtime/AndroidRuntime.h>

//#undef LOGV
//#define LOGV(...) fprintf(stderr, __VA_ARGS__)

/**
 * phornyac: this file is copied from
 * frameworks/base/core/jni/android_util_Binder.cpp.
 */

using namespace android;

// ----------------------------------------------------------------------------

static jint android_transform_Transform_getArgV(JNIEnv* env, jobject clazz)
{
    ///return IPCThreadState::self()->getCallingPid();
    return 7;
}

// ----------------------------------------------------------------------------

static const JNINativeMethod gTransformMethods[] = {
     /* name, signature, funcPtr */
    ///{ "getCallingPid", "()I", (void*)android_os_Binder_getCallingPid },
    ///{ "getCallingUid", "()I", (void*)android_os_Binder_getCallingUid },
    ///{ "clearCallingIdentity", "()J", (void*)android_os_Binder_clearCallingIdentity },
    ///{ "restoreCallingIdentity", "(J)V", (void*)android_os_Binder_restoreCallingIdentity },
    ///{ "flushPendingCommands", "()V", (void*)android_os_Binder_flushPendingCommands },
    ///{ "init", "()V", (void*)android_os_Binder_init },
    ///{ "destroy", "()V", (void*)android_os_Binder_destroy }
    { "getArgV", "()I", (void*)android_transform_Transform_getArgV }
};

const char* const kTransformPathName = "android/transform/Transform";

static int int_register_android_transform_Transform(JNIEnv* env)
{
    jclass clazz;

    clazz = env->FindClass(kBinderPathName);
    LOG_FATAL_IF(clazz == NULL, "Unable to find class android.transform.Transform");

    ///gBinderOffsets.mClass = (jclass) env->NewGlobalRef(clazz);
    ///gBinderOffsets.mExecTransact
    ///    = env->GetMethodID(clazz, "execTransact", "(IIII)Z");
    ///assert(gBinderOffsets.mExecTransact);

    ///gBinderOffsets.mObject
    ///    = env->GetFieldID(clazz, "mObject", "I");
    ///assert(gBinderOffsets.mObject);

    return AndroidRuntime::registerNativeMethods(
        env, kTransformPathName,
        gTransformMethods, NELEM(gTransformMethods));
}

