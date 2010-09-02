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

package android.transform;

// begin WITH_TAINT_TRACKING
import dalvik.system.Taint;
// end WITH_TAINT_TRACKING
import android.location.Location;
import android.util.Log;

/**
 * A "trusted transformation library." Contains methods that transform
 * [sensor] data from one form to another, then uses dalvik.system.Taint
 * to modify the taint tags/labels on the data.
 */
public final class Transform {

/*
    public static final int TAINT_CLEAR		= 0x00000000;
    ...
*/

    private static final String TAG = "TransformLibrary";

    /**
     * Constructor...
     */
    Transform() {
        //Nothing to do yet.
    }

    /**
     * Sample native method.
     */
    native public void transformSomethingNative();

    /**
     * "Transforms" an integer from a sensitive form to a less sensitive form,
     * clearing the TAINT_LOCATION tag.
     * Declared as static so it can be invoked without instantiating the class,
     * i.e. Transform.transformIntClearLocation(foo);
     *
     * @param value
     *      the integer to "transform"
     * throws ...
     */
    public static int transformIntClearLocation(int value) {
        /**
         * See frameworks/base/core/java/android/hardware/Camera.java for an
         * example like this:
         */
        int transformedInt = Taint.removeTaintInt(value, Taint.TAINT_LOCATION);
        return transformedInt;
    }

    /**
     * "Transforms" a Location to a less-sensitive zip code, clearing the
     * TAINT_LOCATION tag.
     * Declared as static so it can be invoked without instantiating the class.
     *
     * @param loc
     *      the location to transform
     * throws ...
     */
    public static int transformLocationToZip(Location loc) {
        String tstr;

        /**
         * "Transform" the Location into a zip code. For now, just pick a random
         * zip code:
         */
        int zip = 19101;

        /**
         * To set the taint for the transformed value, we must first determine
         * the taint of the original Location, which is currently done by ORing
         * together the taint values of its latitude and longitude (is this the
         * same as finding the "least upper bound"?). The location taint is then
         * removed in a second step.
         */
        int locTaint = (Taint.getTaintDouble(loc.getLatitude()) |
                Taint.getTaintDouble(loc.getLongitude()));
            /* XXX: turn this into its own function(s)... */
        tstr = "0x" + Integer.toHexString(Taint.TAINT_LOCATION);
        Log.d(TAG, "Taint.TAINT_LOCATION is: "+tstr);
        tstr = "0x" + Integer.toHexString(Taint.TAINT_LOCATION_GPS);
        Log.d(TAG, "Taint.TAINT_LOCATION_GPS is: "+tstr);
        tstr = "0x" + Integer.toHexString(locTaint);
        Log.d(TAG, "Calculated location taint tag: "+tstr);

        /* Remove TAINT_LOCATION_GPS, but not TAINT_LOCATION: */
        int taintedZip = Taint.addTaintInt(zip, locTaint);
        int detaintedZip = Taint.removeTaintInt(taintedZip,
                Taint.TAINT_LOCATION_GPS);
        tstr = "0x" + Integer.toHexString(Taint.getTaintInt(detaintedZip));
        Log.d(TAG, "Detainted tag is: "+tstr);
        return detaintedZip;
    }
    
    /**
     * Something something...
     *
     * @returns An integer for now, a String array later.
     */
    public static final native int getArgV();

//    /**
//     * Intentional build error:
//     */
//    private bah buildError()

    /**
     * Updates the target String's taint tag.
     *
     * @param str
     *	    the target string
     * @param tag
     *	    tag to update (bitwise or) onto the object
     */
    //native public static void addTaintString(String str, int tag);
    
    /**
     * Updates the target boolean array's taint tag.
     *
     * @param array
     *	    the target boolean array
     * @param tag
     *	    tag to update (bitwise or) onto the boolean array
     */
    //native public static void addTaintBooleanArray(boolean[] array, int tag);

    /**
     * Add taint to a primiative char value. Only the return value has the
     * updated taint tag.
     *
     * @param val
     *	    the input value
     * @param tag
     *	    tag to add (bitwise or) onto the input value
     * @return val with the added taint tag
     */
    //native public static char addTaintChar(char val, int tag);
    
    /**
     * Get the current taint tag from a String.
     *
     * @param str
     *	    the target String
     * @return the taint tag
     */
    //native public static int getTaintString(String str);

    /**
     * Logging utility accessible from places android.util.Log
     * is not.
     *
     * @param msg
     *	    the message to log
     */
    //native public static void log(String msg);
}

