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

package android.privacy;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import android.util.ProcessName;
import android.util.Log;

/**
 * Anonymizes an IMEI number by hashing it with the current application's
 * process name. Based on AESObfuscator class from Android SDK licensing
 * package.
 */
public final class IMEIAnonymizer {
    private static final String LOG_TAG = "IMEIAnonymizer";
    private static final String UTF8 = "UTF-8";
    private static final String KEYGEN_ALGORITHM = "PBEWITHSHAAND256BITAES-CBC-BC";
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] IV =
        { 16, 74, 71, -80, 32, 101, -47, 72, 117, -14, 0, -29, 70, 65, -12, 74 };
    private static final String header = "android.privacy.IMEIAnonymizer-1|";
    private static final byte[] SALT = new byte[] {
          93, 52  ,  -42,  -23,   22, -121,   28,   25,  124, -117,
         -47,  103,   25, -101,  -99,  -20,   65,  -94,   24,  111
    };

    /**
     * Anonymize the input string, which presumably represents an IMEI
     * number already gotten from the phone internals (TelephonyManager ->
     * PhoneSubInfo -> GSMPhone).
     * Static method, so that class doesn't have to be instantiated in
     * order to anonymize.
     * Returns: anonymized IMEI (a string of exactly 15 digits) on success,
     *   or null on error.
     */
    public static String anonymize(String orig) {
        int IMEI_LEN = 15;
        Cipher encryptor;
        String processName;
        String deviceId;
        String result;
        char chars[];
        char c;
        int val;
        StringBuilder sb;
        String imei;

        Log.w(LOG_TAG, "phornyac: anonymize: entered");
        if (orig == null) {
            Log.w(LOG_TAG, "phornyac: anonymize: orig is null, so "+
                    "returning null");
            return null;
        }

        processName = ProcessName.getProcessName();
        if (processName == null) {
            Log.w(LOG_TAG, "phornyac: anonymize: getProcessName() returned "+
                    "null, so returning null");
            return null;
        }
        Log.w(LOG_TAG, "phornyac: anonymize: got processName="+processName);

        /* In original AESObfuscator class, mDeviceId was supposed to be a
         * unique device identifier (a la IMEI) that was subsequently used
         * in the hashing / key generation, to make it the obfuscated data
         * more unique between devices. Not sure that matters much here
         * though...
         */
        deviceId = "1234567890";
        Log.w(LOG_TAG, "phornyac: anonymize: deviceId="+deviceId);

        /* Create an encryptor (based on AESObfuscator code): */
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
            KeySpec keySpec =
                new PBEKeySpec((processName + deviceId).toCharArray(), SALT, 1024, 256);
            SecretKey tmp = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
            encryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            encryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
        } catch (GeneralSecurityException e) {
            // This can't happen on a compatible Android device.
            throw new RuntimeException("Invalid environment", e);
        }
        Log.w(LOG_TAG, "phornyac: anonymize: successfully generated "+
                "key, created encryptor");

        /* "Hash" the IMEI string with the encryptor: */
        try {
            // Header is appended as an integrity check
            result = Base64.encode(encryptor.doFinal((header + orig).getBytes(UTF8)));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment", e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Invalid environment", e);
        }
        Log.w(LOG_TAG, "phornyac: anonymize: encoded result="+result);

        /* Finally, adjust the encrypted string so that it matches the
         * IMEI number format: 15 numbers, no letters or other characters.
         * Do this by stepping through the first 15 characters in the
         * encrypted string and, if the character is not a digit, transforming
         * it into a digit by casting it to an int, then taking modulo 10
         * (this is deterministic, so we will still get an IMEI number that
         * is the same every time an app calls this method).
         * For some more info, see:
         *   http://download.oracle.com/javase/6/docs/api/java/lang/Character.html
         *   http://download.oracle.com/javase/6/docs/api/java/lang/String.html
         */

        /* I expect that this case will never happen, but I never explicitly
         * looked at the encryption/encoding code to make sure...
         */
        if (result.length() < IMEI_LEN) {
            Log.w(LOG_TAG, "phornyac: anonymize: encoded result string "+
                    "is too short, returning null");
            return null;
        }
        chars = result.toCharArray();
        sb = new StringBuilder();
        
        /* When converting from chars to ints, Character.digit() is
         * another option besides just direct casting, but it fails
         * on non-alphanumeric characters, which can result from the
         * encryption above.
         */
        for (int i=0; i < IMEI_LEN; i++) {
            c = chars[i];
            //val = Character.digit(c, 10);
            val = (int)c;
            val = val % 10;
            sb.append(val);
        }
        imei = sb.toString();

        /* Sanity check: */
        if (imei.length() != IMEI_LEN) {
            Log.w(LOG_TAG, "phornyac: anonymize: anonymized IMEI "+
                    imei+" has invalid length, returning null");
            return null;
        }
        Log.w(LOG_TAG, "phornyac: anonymize: returning final anonymized "+
                "IMEI="+imei);
        return imei;
    }
}
