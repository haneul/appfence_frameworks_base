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

//import android.privacy.Base64;
//import android.privacy.Base64DecoderException;

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

    //private Cipher mEncryptor;
    //private Cipher mDecryptor;

    private static final byte[] SALT = new byte[] {
          93, 52  ,  -42,  -23,   22, -121,   28,   25,  124, -117,
         -47,  103,   25, -101,  -99,  -20,   65,  -94,   24,  111
    };
    //private String mProcessName;
//    private String mDeviceId;

    ///**
    // * @param salt an array of random bytes to use for each (un)obfuscation
    // * @param applicationId application identifier, e.g. the package name
    // * @param deviceId device identifier. Use as many sources as possible to
    // *    create this unique identifier.
    // */
//    public IMEIAnonymizer() {
//        //XXX: update these to appropriate values!
//        Log.w(LOG_TAG, "phornyac: IMEIAnonymizer: entered");
//        //mProcessName = ProcessName.getProcessName();
//
//        /* In original AESObfuscator class, mDeviceId was supposed to be a
//         * unique device identifier (a la IMEI) that was subsequently used
//         * in the hashing / key generation, to make it the obfuscated data
//         * more unique between devices. Not sure that matters much here
//         * though...
//         */
//        mDeviceId = "123456789012";
//        Log.w(LOG_TAG, "phornyac: IMEIAnonymizer: mDeviceId="+mDeviceId);
//        /*
//        try {
//            SecretKeyFactory factory = SecretKeyFactory.getInstance(KEYGEN_ALGORITHM);
//            KeySpec keySpec =
//                new PBEKeySpec((mProcessName + mDeviceId).toCharArray(), SALT, 1024, 256);
//            SecretKey tmp = factory.generateSecret(keySpec);
//            SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
//            mEncryptor = Cipher.getInstance(CIPHER_ALGORITHM);
//            mEncryptor.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(IV));
//            mDecryptor = Cipher.getInstance(CIPHER_ALGORITHM);
//            mDecryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
//        } catch (GeneralSecurityException e) {
//            // This can't happen on a compatible Android device.
//            throw new RuntimeException("Invalid environment", e);
//        }
//        */
//    }

//    public String anonymizeIMEI(String imei) {
//        Log.w(LOG_TAG, "phornyac: anonymizeIMEI: entered");
//        //XXX: update this to just to what we need it to do; skip all the other
//        //  junk in obfuscate()
//        //XXX: make sure this returns a string of just digits (no alphabetic
//        //  characters), and with proper length!
//        return obfuscate(imei);
//    }

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
        Cipher encryptor;
        //Cipher decryptor;
        String processName;
        String deviceId;
        String result;

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
            //decryptor = Cipher.getInstance(CIPHER_ALGORITHM);
            //decryptor.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(IV));
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
            Log.w(LOG_TAG, "phornyac: anonymize: encoded result="+result);
            return result;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment", e);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException("Invalid environment", e);
        }
    }

/*  public String unobfuscate(String obfuscated) throws ValidationException {
        if (obfuscated == null) {
            return null;
        }
        try {
            String result = new String(mDecryptor.doFinal(Base64.decode(obfuscated)), UTF8);
            // Check for presence of header. This serves as a final integrity check, for cases
            // where the block size is correct during decryption.
            int headerIndex = result.indexOf(header);
            if (headerIndex != 0) {
                throw new ValidationException("Header not found (invalid data or key)" + ":" +
                        obfuscated);
            }
            return result.substring(header.length(), result.length());
        } catch (Base64DecoderException e) {
            throw new ValidationException(e.getMessage() + ":" + obfuscated);
        } catch (IllegalBlockSizeException e) {
            throw new ValidationException(e.getMessage() + ":" + obfuscated);
        } catch (BadPaddingException e) {
            throw new ValidationException(e.getMessage() + ":" + obfuscated);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Invalid environment", e);
        }
    }
*/
}
