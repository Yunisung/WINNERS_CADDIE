package com.bkwinners.ksnet.dpt.common;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESUtils {
    private static final int KEY_SIZE = 32;
    private static final String SHA1_PRNG = "SHA1PRNG";

    @interface AESType {
    }

    @SuppressLint({"DeletedProvider", "GetInstance"})
    public static String des(String str, String str2, @AESType int i) {
        SecretKeySpec secretKeySpec;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                if (Build.VERSION.SDK_INT > 26) {
                    secretKeySpec = deriveKeyInsecurely(str2);
                } else {
                    secretKeySpec = fixSmallVersion(str2);
                }
                Cipher instance = Cipher.getInstance("AES");
                instance.init(i, secretKeySpec);
                if (i == 1) {
                    return parseByte2HexStr(instance.doFinal(str.getBytes("utf-8")));
                }
                return new String(instance.doFinal(parseHexStr2Byte(str)));
            } catch (UnsupportedEncodingException | InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @SuppressLint({"DeletedProvider"})
    private static SecretKeySpec fixSmallVersion(String str) throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom secureRandom;
        KeyGenerator instance = KeyGenerator.getInstance("AES");
        if (Build.VERSION.SDK_INT >= 24) {
            secureRandom = SecureRandom.getInstance(SHA1_PRNG, new CryptoProvider());
        } else {
            secureRandom = SecureRandom.getInstance(SHA1_PRNG, "Crypto");
        }
        secureRandom.setSeed(str.getBytes());
        instance.init(128, secureRandom);
        return new SecretKeySpec(instance.generateKey().getEncoded(), "AES");
    }

    private static SecretKeySpec deriveKeyInsecurely(String str) {
        byte[] bArr = new byte[0];
        if (Build.VERSION.SDK_INT >= 19) {
            bArr = str.getBytes(StandardCharsets.US_ASCII);
        }
        return new SecretKeySpec(InsecureSHA1PRNGKeyDerivator.deriveInsecureKey(bArr, 32), "AES");
    }

    private static String parseByte2HexStr(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            sb.append(hexString.toUpperCase());
        }
        return sb.toString();
    }

    private static byte[] parseHexStr2Byte(String str) {
        if (str.length() < 1) {
            return null;
        }
        byte[] bArr = new byte[(str.length() / 2)];
        for (int i = 0; i < str.length() / 2; i++) {
            int i2 = i * 2;
            int i3 = i2 + 1;
            bArr[i] = (byte) ((Integer.parseInt(str.substring(i2, i3), 16) * 16) + Integer.parseInt(str.substring(i3, i2 + 2), 16));
        }
        return bArr;
    }

    private static final class CryptoProvider extends Provider {
        CryptoProvider() {
            super("Crypto", 1.0d, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
            put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
            put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
        }
    }
}