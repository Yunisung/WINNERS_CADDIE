package com.mtouch.ksnet.dpt.action.process.ksnetmodule.util;

/**
 * Created by parksuwon on 2018-02-03.
 */
class CharToByte {
    private static final String f1025a = System.getProperty("line.separator");
    private static char[] f1026b = new char[64];
    private static byte[] f1027c = new byte[128];

    static {
        int i;
        int i2 = 0;
        char c = 'A';
        int i3 = 0;
        while (c <= 'Z') {
            i = i3 + 1;
            f1026b[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        c = 'a';
        while (c <= 'z') {
            i = i3 + 1;
            f1026b[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        c = '0';
        while (c <= '9') {
            i = i3 + 1;
            f1026b[i3] = c;
            c = (char) (c + 1);
            i3 = i;
        }
        i = i3 + 1;
        f1026b[i3] = '+';
        i3 = i + 1;
        f1026b[i] = '/';
        for (int i4 = 0; i4 < f1027c.length; i4++) {
            f1027c[i4] = (byte) -1;
        }
        while (i2 < 64) {
            f1027c[f1026b[i2]] = (byte) i2;
            i2++;
        }
    }



}
