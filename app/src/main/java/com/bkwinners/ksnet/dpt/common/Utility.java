package com.bkwinners.ksnet.dpt.common;

import android.graphics.Bitmap;
import androidx.core.view.ViewCompat;

import androidx.core.view.ViewCompat;

import com.ksnet.util.Util;
import java.io.UnsupportedEncodingException;

public class Utility {
    public StringBuffer m_sbErrorMsg = new StringBuffer();
    public String m_strUnicode = Util.ENCODING;

    /* access modifiers changed from: package-private */
    public byte[] bitmapTo1BitBitmap(Bitmap bitmap, int i) {
        char c;
        int i2 = 128;
        Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, 128, 64, true);
        char c2 = 256;
        int[] iArr = new int[256];
        int height = createScaledBitmap.getHeight() - 1;
        int i3 = 0;
        int i4 = 0;
        while (height >= 0) {
            int i5 = i3;
            int i6 = 0;
            while (i6 < createScaledBitmap.getWidth()) {
                if (i4 == 32) {
                    i5++;
                    i4 = 0;
                }
                if (createScaledBitmap.getPixel(i6, height) == i) {
                    switch (i4) {
                        case 0:
                            c = 256;
                            iArr[i5] = iArr[i5] | Integer.MIN_VALUE;
                            break;
                        case 1:
                            c = 256;
                            iArr[i5] = iArr[i5] | 1073741824;
                            break;
                        case 2:
                            c = 256;
                            iArr[i5] = iArr[i5] | 536870912;
                            break;
                        case 3:
                            c = 256;
                            iArr[i5] = iArr[i5] | 268435456;
                            break;
                        case 4:
                            c = 256;
                            iArr[i5] = iArr[i5] | 134217728;
                            break;
                        case 5:
                            c = 256;
                            iArr[i5] = iArr[i5] | 67108864;
                            break;
                        case 6:
                            c = 256;
                            iArr[i5] = iArr[i5] | 33554432;
                            break;
                        case 7:
                            c = 256;
                            iArr[i5] = iArr[i5] | 16777216;
                            break;
                        case 8:
                            c = 256;
                            iArr[i5] = iArr[i5] | 8388608;
                            break;
                        case 9:
                            c = 256;
                            iArr[i5] = iArr[i5] | 4194304;
                            break;
                        case 10:
                            c = 256;
                            iArr[i5] = iArr[i5] | 2097152;
                            break;
                        case 11:
                            c = 256;
                            iArr[i5] = iArr[i5] | 1048576;
                            break;
                        case 12:
                            c = 256;
                            iArr[i5] = iArr[i5] | 524288;
                            break;
                        case 13:
                            c = 256;
                            iArr[i5] = iArr[i5] | 262144;
                            break;
                        case 14:
                            c = 256;
                            iArr[i5] = iArr[i5] | 131072;
                            break;
                        case 15:
                            c = 256;
                            iArr[i5] = iArr[i5] | 65536;
                            break;
                        case 16:
                            c = 256;
                            iArr[i5] = iArr[i5] | 32768;
                            break;
                        case 17:
                            c = 256;
                            iArr[i5] = iArr[i5] | 16384;
                            break;
                        case 18:
                            c = 256;
                            iArr[i5] = iArr[i5] | 8192;
                            break;
                        case 19:
                            c = 256;
                            iArr[i5] = iArr[i5] | 4096;
                            break;
                        case 20:
                            c = 256;
                            iArr[i5] = iArr[i5] | 2048;
                            break;
                        case 21:
                            c = 256;
                            iArr[i5] = iArr[i5] | 1024;
                            break;
                        case 22:
                            c = 256;
                            iArr[i5] = iArr[i5] | 512;
                            break;
                        case 23:
                            c = 256;
                            iArr[i5] = iArr[i5] | 256;
                            break;
                        case 24:
                            iArr[i5] = iArr[i5] | i2;
                            break;
                        case 25:
                            iArr[i5] = iArr[i5] | 64;
                            break;
                        case 26:
                            iArr[i5] = iArr[i5] | 32;
                            break;
                        case 27:
                            iArr[i5] = iArr[i5] | 16;
                            break;
                        case 28:
                            iArr[i5] = iArr[i5] | 8;
                            break;
                        case 29:
                            iArr[i5] = iArr[i5] | 4;
                            break;
                        case 30:
                            iArr[i5] = iArr[i5] | 2;
                            break;
                        case 31:
                            iArr[i5] = iArr[i5] | 1;
                            break;
                    }
                }
                c = 256;
                i4++;
                i6++;
                c2 = c;
                i2 = 128;
            }
            char c3 = c2;
            int i7 = i;
            height--;
            c2 = c3;
            i3 = i5;
            i2 = 128;
        }
        byte[] bArr = new byte[1086];
        int i8 = 62;
        for (int intToByte : iArr) {
            System.arraycopy(intToByte(intToByte), 0, bArr, i8, 4);
            i8 += 4;
        }
        bArr[0] = 66;
        bArr[1] = 77;
        bArr[2] = 62;
        bArr[3] = 4;
        bArr[4] = 0;
        bArr[5] = 0;
        bArr[6] = 0;
        bArr[7] = 0;
        bArr[8] = 0;
        bArr[9] = 0;
        bArr[10] = 62;
        bArr[11] = 0;
        bArr[12] = 0;
        bArr[13] = 0;
        bArr[14] = EncMSRManager.ERROR_TIMEOUT;
        bArr[15] = 0;
        bArr[16] = 0;
        bArr[17] = 0;
        bArr[18] = Byte.MIN_VALUE;
        bArr[19] = 0;
        bArr[20] = 0;
        bArr[21] = 0;
        bArr[22] = 64;
        bArr[23] = 0;
        bArr[24] = 0;
        bArr[25] = 0;
        bArr[26] = 1;
        bArr[27] = 0;
        bArr[28] = 1;
        bArr[29] = 0;
        bArr[30] = 0;
        bArr[31] = 0;
        bArr[32] = 0;
        bArr[33] = 0;
        bArr[34] = 0;
        bArr[35] = 4;
        bArr[36] = 0;
        bArr[37] = 0;
        bArr[38] = 0;
        bArr[39] = 0;
        bArr[40] = 0;
        bArr[41] = 0;
        bArr[42] = 0;
        bArr[43] = 0;
        bArr[44] = 0;
        bArr[45] = 0;
        bArr[46] = 0;
        bArr[47] = 0;
        bArr[48] = 0;
        bArr[49] = 0;
        bArr[50] = 0;
        bArr[51] = 0;
        bArr[52] = 0;
        bArr[53] = 0;
        bArr[54] = 0;
        bArr[55] = 0;
        bArr[56] = 0;
        bArr[57] = 0;
        bArr[58] = -1;
        bArr[59] = -1;
        bArr[60] = -1;
        bArr[61] = 0;
        return bArr;
    }

    /* access modifiers changed from: package-private */
    public byte[] intToByte(int i) {
        return new byte[]{(byte) ((i & ViewCompat.MEASURED_STATE_MASK) >>> 24), (byte) ((16711680 & i) >>> 16), (byte) ((65280 & i) >>> 8), (byte) (i & 255)};
    }

    /* access modifiers changed from: package-private */
    public String byteToString(byte[] bArr) {
        return byteToString(bArr, 0, bArr.length);
    }

    public String byteToString(byte[] bArr, int i, int i2) {
        if (bArr == null) {
            return null;
        }
        try {
            return new String(bArr, i, i2, this.m_strUnicode);
        } catch (UnsupportedEncodingException e) {
            StringBuffer stringBuffer = this.m_sbErrorMsg;
            stringBuffer.append("이 장치에서 " + e.getMessage() + " 인코딩은 지원하지 않습니다.\n");
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public String matchFormat(int i, int i2, char c) {
        return matchFormat(String.valueOf(i), i2, c);
    }

    /* access modifiers changed from: package-private */
    public String matchFormat(String str, int i, char c) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = i - (str == null ? new byte[0] : stringToByte(str)).length;
        if (length < 0) {
            int i2 = 0;
            int i3 = 0;
            while (i2 < i - 4) {
                i2 += str.charAt(i3) > 127 ? 2 : 1;
                stringBuffer.append(str.charAt(i3));
                i3++;
            }
            str = stringBuffer.toString();
            byte[] stringToByte = stringToByte(str);
            int length2 = i - stringToByte.length;
            if (length2 <= 0) {
                return new String(stringToByte, 0, i);
            }
            stringBuffer.delete(0, stringBuffer.length());
            length = length2;
        }
        if (c == '9') {
            for (int i4 = 0; i4 < length; i4++) {
                stringBuffer.append('0');
            }
            stringBuffer.append(str);
        } else {
            for (int i5 = 0; i5 < length; i5++) {
                stringBuffer.append(' ');
            }
            stringBuffer.insert(0, str);
        }
        return stringBuffer.toString();
    }

    public byte[] stringToByte(String str) {
        try {
            return str.getBytes(this.m_strUnicode);
        } catch (UnsupportedEncodingException e) {
            StringBuffer stringBuffer = this.m_sbErrorMsg;
            stringBuffer.append("이 장치에서 " + e.getMessage() + " 인코딩은 지원하지 않습니다.\n");
            return null;
        }
    }
}