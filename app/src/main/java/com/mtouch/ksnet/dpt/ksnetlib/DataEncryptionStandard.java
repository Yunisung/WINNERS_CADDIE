package com.mtouch.ksnet.dpt.ksnetlib;

import com.mtouch.ksnet.dpt.common.EncMSRManager;

import java.io.FileInputStream;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

class DataEncryptionStandard {
    final byte[] m_bKey = {-26, -65, 62, -23, -95, 93, -20, 62};

    DataEncryptionStandard() {
    }

    /* access modifiers changed from: package-private */
    public String decryptSign(String str) {
        byte[] bArr;
        String substring = str.substring(4, str.length());
        try {
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.m_bKey));
            Cipher instance = Cipher.getInstance("DES/ECB/PKCS5Padding");
            instance.init(2, generateSecret);
            bArr = instance.doFinal(Base64Coder.decode(substring));
        } catch (Exception e) {
            e.printStackTrace();
            bArr = null;
        }
        byte[] bArr2 = new byte[1086];
        bArr2[0] = 66;
        bArr2[1] = 77;
        bArr2[2] = 62;
        bArr2[3] = 4;
        bArr2[4] = 0;
        bArr2[5] = 0;
        bArr2[6] = 0;
        bArr2[7] = 0;
        bArr2[8] = 0;
        bArr2[9] = 0;
        bArr2[10] = 62;
        bArr2[11] = 0;
        bArr2[12] = 0;
        bArr2[13] = 0;
        bArr2[14] = EncMSRManager.ERROR_TIMEOUT;
        bArr2[15] = 0;
        bArr2[16] = 0;
        bArr2[17] = 0;
        bArr2[18] = Byte.MIN_VALUE;
        bArr2[19] = 0;
        bArr2[20] = 0;
        bArr2[21] = 0;
        bArr2[22] = 64;
        bArr2[23] = 0;
        bArr2[24] = 0;
        bArr2[25] = 0;
        bArr2[26] = 1;
        bArr2[27] = 0;
        bArr2[28] = 1;
        bArr2[29] = 0;
        bArr2[30] = 0;
        bArr2[31] = 0;
        bArr2[32] = 0;
        bArr2[33] = 0;
        bArr2[34] = 0;
        bArr2[35] = 4;
        bArr2[36] = 0;
        bArr2[37] = 0;
        bArr2[38] = EncMSRManager.KSNET_INTEGRITY_REQ;
        bArr2[39] = 14;
        bArr2[40] = 0;
        bArr2[41] = 0;
        bArr2[42] = EncMSRManager.KSNET_INTEGRITY_REQ;
        bArr2[43] = 14;
        bArr2[44] = 0;
        bArr2[45] = 0;
        bArr2[46] = 0;
        bArr2[47] = 0;
        bArr2[48] = 0;
        bArr2[49] = 0;
        bArr2[50] = 0;
        bArr2[51] = 0;
        bArr2[52] = 0;
        bArr2[53] = 0;
        bArr2[54] = 0;
        bArr2[55] = 0;
        bArr2[56] = 0;
        bArr2[57] = 0;
        bArr2[58] = -1;
        bArr2[59] = -1;
        bArr2[60] = -1;
        bArr2[61] = 0;
        for (int i = 0; i < 1024; i++) {
            bArr2[i + 62] = bArr[i];
        }
        return new String(bArr);
    }

    /* access modifiers changed from: package-private */
    public String encryptSign(String str) {
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            byte[] bArr = new byte[1086];
            fileInputStream.read(bArr);
            fileInputStream.close();
            return encryptSign(bArr);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public String encryptSign(byte[] bArr) {
        if (bArr == null || bArr.length != 1086) {
            return null;
        }
        byte[] bArr2 = new byte[1024];
        for (int i = 0; i < 1024; i++) {
            bArr2[i] = bArr[i + 62];
        }
        try {
            SecretKey generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.m_bKey));
            Cipher instance = Cipher.getInstance("DES/ECB/PKCS5Padding");
            instance.init(1, generateSecret);
            char[] encode = Base64Coder.encode(instance.doFinal(bArr2));
            return "LOTE" + new String(encode, 0, encode.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}