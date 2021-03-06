/*
 * Copyright (C) 2009 The Android Open Source Project
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

package android.accounts;

import android.os.Parcelable;
import android.os.Parcel;
import android.text.TextUtils;
// added by haneul
import android.util.Log;
// begin WITH_TAINT_TRACKING
import dalvik.system.Taint;
// end WITH_TAINT_TRACKING

/**
 * Value type that represents an Account in the {@link AccountManager}. This object is
 * {@link Parcelable} and also overrides {@link #equals} and {@link #hashCode}, making it
 * suitable for use as the key of a {@link java.util.Map}
 */
public class Account implements Parcelable {
    public final String name;
    public final String type;

    private static final String TAG = "Account";

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Account)) return false;
        final Account other = (Account)o;
        return name.equals(other.name) && type.equals(other.type);
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public Account(String name, String type) {
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("the name must not be empty: " + name);
        }
        if (TextUtils.isEmpty(type)) {
            throw new IllegalArgumentException("the type must not be empty: " + type);
        }
        this.name = name;
        this.type = type;
		String processName = Taint.getProcessName();
		if(!processName.startsWith("system") && !processName.startsWith("com.android") && !processName.startsWith("com.google"))
		{
			Taint.addTaintString(this.name, Taint.TAINT_ACCOUNT);
		}
	}

    public Account(Parcel in) {
        this.name = in.readString();
        this.type = in.readString();
		String processName = Taint.getProcessName();
		if(this.name != null)
		{
			if(!processName.startsWith("system") && !processName.startsWith("com.android") && !processName.startsWith("com.google"))
			{
				Taint.addTaintString(this.name, Taint.TAINT_ACCOUNT);
			} 
		}
	}

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String toString() {
        return "Account {name=" + name + ", type=" + type + "}";
    }
}
