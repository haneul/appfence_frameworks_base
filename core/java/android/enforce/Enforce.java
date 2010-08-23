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

package android.enforce;

// begin WITH_TAINT_TRACKING
import dalvik.system.Taint;
// end WITH_TAINT_TRACKING
//import android.location.Location;
import android.os.Parcel;
import android.util.Log;

/**
 * Contains methods for enforcing IFC for TaintDroid.
 */
public final class Enforce {

    private static final String TAG = "phornyac-Enforce";

    /**
     * Constructor...
     */
    Enforce() {
        //Nothing to do yet.
    }

//    /**
//     * Sample native method.
//     */
//    native public void enforceSomethingNative();

//    /**
//     * "Transforms" a Location to a less-sensitive zip code, clearing the
//     * TAINT_LOCATION tag.
//     * Declared as static so it can be invoked without instantiating the class.
//     *
//     * @param loc
//     *      the location to transform
//     * throws ...
//     */
//    public static int transformLocationToZip(Location loc) {
//        String tstr;
//
//        /**
//         * "Transform" the Location into a zip code. For now, just pick a random
//         * zip code:
//         */
//        int zip = 19101;
//
//        /**
//         * To set the taint for the transformed value, we must first determine
//         * the taint of the original Location, which is currently done by ORing
//         * together the taint values of its latitude and longitude (is this the
//         * same as finding the "least upper bound"?). The location taint is then
//         * removed in a second step.
//         */
//        int locTaint = (Taint.getTaintDouble(loc.getLatitude()) |
//                Taint.getTaintDouble(loc.getLongitude()));
//            /* XXX: turn this into its own function(s)... */
//        tstr = "0x" + Integer.toHexString(Taint.TAINT_LOCATION);
//        Log.d(TAG, "Taint.TAINT_LOCATION is: "+tstr);
//        tstr = "0x" + Integer.toHexString(Taint.TAINT_LOCATION_GPS);
//        Log.d(TAG, "Taint.TAINT_LOCATION_GPS is: "+tstr);
//        tstr = "0x" + Integer.toHexString(locTaint);
//        Log.d(TAG, "Calculated location taint tag: "+tstr);
//
//        /* Remove TAINT_LOCATION_GPS, but not TAINT_LOCATION: */
//        int taintedZip = Taint.addTaintInt(zip, locTaint);
//        int detaintedZip = Taint.removeTaintInt(taintedZip,
//                Taint.TAINT_LOCATION_GPS);
//        tstr = "0x" + Integer.toHexString(Taint.getTaintInt(detaintedZip));
//        Log.d(TAG, "Detainted tag is: "+tstr);
//        return detaintedZip;
//    }
    
    /**
     * New Enforce function by phornyac ...
     * 
     * @param code
     * 		...
     * @param data
     * 		...
     * @param flags
     * 		...
     */
    public static boolean checkPolicyIPC(int code, Parcel data, int flags) {
    	String tstr;
    	int dataTaint;
    	Log.w("phornyac", "Enforce.checkPolicyIPC(): entered");
    	/* XXX: log code, flags too */
    	
    	if (data == null) {
    		Log.w("phornyac", "Enforce.checkPolicyIPC(): Parcel data is null, returning true");
    		return true;
    	}

    	/* Get and log the taint tag of the Parcel: */
    	dataTaint = data.getTaint();
    	tstr = "0x" + Integer.toHexString(dataTaint);
    	Log.w("phornyac", "Enforce.checkPolicyIPC(): Parcel data.getTaint() is "+tstr);

    	/**
    	 * Check that this app is allowed to send Parcels with the current
    	 * taint tag through IPC:
    	 */
    	if ((dataTaint & Taint.TAINT_LOCATION_GPS) != 0) {
    		Log.w("phornyac", "Enforce.checkPolicyIPC(): TAINT_LOCATION_GPS set, returning false");
    		return false;
    	} else if ((dataTaint & Taint.TAINT_LOCATION) != 0) {
    		Log.w("phornyac", "Enforce.checkPolicyIPC(): TAINT_LOCATION set, returning true for now");
    		/* XXX: turn this into an Alert or whatever */
    		return true;
    	}
    	Log.w("phornyac", "Enforce.checkPolicyIPC(): no location taint, returning true");
    	return true;
    }
}

