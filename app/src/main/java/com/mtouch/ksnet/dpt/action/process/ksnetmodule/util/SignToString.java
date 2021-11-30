package com.mtouch.ksnet.dpt.action.process.ksnetmodule.util;

/**
 * Created by parksuwon on 2018-02-03.
 */

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class SignToString {
    final byte[] enKey = new byte[]{(byte) -26, (byte) -65, (byte) 62, (byte) -23, (byte) -95, (byte) 93, (byte) -20, (byte) 62};
    StringBuffer str = new StringBuffer();

    private char[] _char = new char[64];
    private byte[] _buf = new byte[128];


    public SignToString() {
    }

    public byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    public String getBitString(Bitmap $bitmap) {
        byte[] bArr = bitmapToByteArray($bitmap);
        String str = null;
        if (bArr == null) {
            this.str.append("싸인 데이터를 확인하십시오.");
            /*park pswseoul 임시로 막음 */
  //      } else if (bArr.length != 1086) {
//            this.str.append("암호화할 싸인 데이터의 크기가 맞지 않습니다.");
        } else {
            byte[] bArr2 = new byte[1024];
            for (int i = 0; i < 1024; i++) {
                bArr2[i] = bArr[i + 62];
            }
            try {
                Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.enKey));
                Cipher instance = Cipher.getInstance("DES/ECB/PKCS5Padding");
                instance.init(1, generateSecret);
                char[] a = CharToByte(instance.doFinal(bArr2));
                str = "LOTE" + new String(a, 0, a.length);
            } catch (Exception e) {
                this.str.append(e.getMessage() + "\n");
            }
        }
        return str;
    }


    public  String getBitString(byte[] bArr) {
        String str = null;
        if (bArr == null) {
            this.str.append("싸인 데이터를 확인하십시오.");
        } else if (bArr.length != 1086) {
            this.str.append("암호화할 싸인 데이터의 크기가 맞지 않습니다.");
        } else {
            byte[] bArr2 = new byte[1024];
            for (int i = 0; i < 1024; i++) {
                bArr2[i] = bArr[i + 62];
            }
            try {
                Key generateSecret = SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(this.enKey));
                Cipher instance = Cipher.getInstance("DES/ECB/PKCS5Padding");
                instance.init(1, generateSecret);
                char[] a = CharToByte(instance.doFinal(bArr2));
                str = "LOTE" + new String(a, 0, a.length);
            } catch (Exception e) {
                this.str.append(e.getMessage() + "\n");
            }
        }
        return str;
    }

    public  char[] CharToByte(byte[] bArr) {
        return byteToSubChar(bArr, 0, bArr.length);
    }

    public  char[] byteToSubChar(byte[] bArr, int i, int i2) {
        int i3 = ((i2 * 4) + 2) / 3;
        char[] cArr = new char[(((i2 + 2) / 3) * 4)];
        int i4 = i + i2;
        int i5 = 0;
        while (i < i4) {
            int i6;
            int i7;
            int i8 = i + 1;
            int i9 = bArr[i] & 255;
            if (i8 < i4) {
                i6 = bArr[i8] & 255;
                i8++;
            } else {
                i6 = 0;
            }
            if (i8 < i4) {
                i7 = i8 + 1;
                i8 = bArr[i8] & 255;
            } else {
                i7 = i8;
                i8 = 0;
            }
            int i10 = i9 >>> 2;
            i9 = ((i9 & 3) << 4) | (i6 >>> 4);
            i6 = ((i6 & 15) << 2) | (i8 >>> 6);
            int i11 = i8 & 63;
            i8 = i5 + 1;
            cArr[i5] = _char[i10];
            i5 = i8 + 1;
            cArr[i8] = _char[i9];
            cArr[i5] = i5 < i3 ? _char[i6] : '=';
            i6 = i5 + 1;
            cArr[i6] = i6 < i3 ? _char[i11] : '=';
            i5 = i6 + 1;
            i = i7;
        }
        return cArr;
    }


}
