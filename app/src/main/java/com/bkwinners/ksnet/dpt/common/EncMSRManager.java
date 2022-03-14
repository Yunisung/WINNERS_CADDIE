package com.bkwinners.ksnet.dpt.common;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EncMSRManager {
    public static final byte ACK = 6;
    public static final byte EOT = 4;
    public static final byte ERROR_CARD_DECLINED = 60;
    public static final byte ERROR_NOCARD = 50;
    public static final byte ERROR_PUBLIC_KEY = 15;
    public static final byte ERROR_TIMEOUT = 40;
    public static final byte ERROR_VERIFY_FAIL = 14;
    public static final byte ESC = 27;
    public static final byte ETX = 3;
    public static final String FALLBACK_BAD_CM = "06";
    public static final String FALLBACK_BAD_OPER = "07";
    public static final String FALLBACK_CVM_FAIL = "05";
    public static final String FALLBACK_NO_APPL = "02";
    public static final String FALLBACK_NO_ATR = "01";
    public static final String FALLBACK_NO_DATA = "04";
    public static final String FALLBACK_NO_ERROR = "00";
    public static final String FALLBACK_READ_FAIL = "03";
    public static final byte FS = 28;
    public static final byte KSNET_CARDNO_REQ = -62;
    public static final byte KSNET_CARD_IC_RESP = -46;
    public static final byte KSNET_CARD_MS_RESP = -46;
    public static final byte KSNET_DONGLE_INFO_REQ = -64;
    public static final byte KSNET_DONGLE_INFO_RESP = -48;
    public static final byte KSNET_FALLBACK_REQ = -59;
    public static final byte KSNET_FALLBACK_RESP = -43;
    public static final byte KSNET_IC_2ND_REQ = -61;
    public static final byte KSNET_IC_2ND_RESP = -45;
    public static final byte KSNET_IC_RESP = -42;
    public static final byte KSNET_INTEGRITY_REQ = -60;
    public static final byte KSNET_INTEGRITY_RESP = -44;
    public static final byte KSNET_KEY_SHARED_REQ = -57;
    public static final byte KSNET_KEY_SHARED_RESP = -41;
    public static final byte KSNET_READER_SET_REQ = -63;
    public static final byte KSNET_READER_SET_RESP = -47;
    public static final byte NAK = 21;
    public static final String NOFALLBACK_ApplicationBlock = "31";
    public static final String NOFALLBACK_CardErr = "32";
    public static final String NOFALLBACK_ChipBlock = "30";
    public static final byte STX = 2;
    public static final byte SUCCESS = 0;
    String YYMMDDhhmmss = getTime().substring(0, 12);
    String Year = getTime().substring(0, 2);
    public byte[] packet = new byte[1024];

    private byte cal_lrc(byte[] bArr) {
        return 0;
    }

    private String getTime() {
        return new SimpleDateFormat("yyMMddhhmmss").format(new Date(System.currentTimeMillis()));
    }

    public static int LRC(byte[] bArr, int i) {
        byte b = 0;
        for (int i2 = 1; i2 < i; i2++) {
            b ^= bArr[i2] & 255;
        }
        return b;
    }

    public byte[] makeDongleInfo() {
        byte[] bArr = this.packet;
        bArr[0] = 2;
        bArr[1] = 0;
        bArr[2] = 5;
        bArr[3] = KSNET_DONGLE_INFO_REQ;
        bArr[4] = (byte) this.Year.charAt(0);
        this.packet[5] = (byte) this.Year.charAt(1);
        byte[] bArr2 = this.packet;
        bArr2[6] = 49;
        bArr2[7] = 3;
        byte[] bArr3 = this.packet;
        bArr3[8] = (byte) LRC(bArr2, 8);
        byte[] bArr4 = new byte[9];
        System.arraycopy(bArr3, 0, bArr4, 0, 9);
        return bArr4;
    }

    public byte[] makeCardNumSendReq(byte[] bArr, byte[] bArr2) {
        byte[] bArr3 = this.packet;
        bArr3[0] = 2;
        bArr3[1] = 0;
        bArr3[2] = 25;
        bArr3[3] = KSNET_CARDNO_REQ;
        System.arraycopy(this.YYMMDDhhmmss.getBytes(), 0, this.packet, 4, 12);
        System.arraycopy(bArr, 0, this.packet, 16, 9);
        System.arraycopy(bArr2, 0, this.packet, 25, 2);
        byte[] bArr4 = this.packet;
        bArr4[27] = 3;
        byte[] bArr5 = this.packet;
        bArr5[28] = (byte) LRC(bArr4, 28);
        byte[] bArr6 = new byte[29];
        System.arraycopy(bArr5, 0, bArr6, 0, 29);
        this.packet = new byte[1024];
        return bArr6;
    }

    public byte[] makeIntegrityReq(byte[] bArr, byte b) {
        this.packet[0] = 2;
        Utils.make2ByteLengh(19);
        System.arraycopy(Utils.make2ByteLengh(19), 0, this.packet, 1, 2);
        byte[] bArr2 = this.packet;
        bArr2[3] = KSNET_INTEGRITY_REQ;
        System.arraycopy(bArr, 0, bArr2, 4, 16);
        byte[] bArr3 = this.packet;
        bArr3[20] = b;
        bArr3[21] = 3;
        byte[] bArr4 = this.packet;
        bArr4[22] = (byte) LRC(bArr3, 22);
        byte[] bArr5 = new byte[23];
        System.arraycopy(bArr4, 0, bArr5, 0, 23);
        this.packet = new byte[1024];
        return bArr5;
    }

    public byte[] make2ThGenerateReq(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        this.packet[0] = 2;
        System.arraycopy(Utils.make2ByteLengh(bArr4.length + 27 + 1), 0, this.packet, 1, 2);
        this.packet[3] = KSNET_IC_2ND_REQ;
        System.arraycopy(this.YYMMDDhhmmss.getBytes(), 0, this.packet, 4, 12);
        System.arraycopy(bArr, 0, this.packet, 16, 9);
        System.arraycopy(bArr2, 0, this.packet, 25, 2);
        System.arraycopy(bArr3, 0, this.packet, 27, 3);
        System.arraycopy(bArr4, 0, this.packet, 30, bArr4.length);
        int length = 30 + bArr4.length;
        byte[] bArr5 = this.packet;
        int i = length + 1;
        bArr5[length] = 3;
        byte[] bArr6 = this.packet;
        int i2 = i + 1;
        bArr6[i] = (byte) LRC(bArr5, i);
        byte[] bArr7 = new byte[i2];
        System.arraycopy(bArr6, 0, bArr7, 0, i2);
        this.packet = new byte[1024];
        return bArr7;
    }

    public byte[] makeFallBackCardReq(String str, String str2) {
        this.packet[0] = 2;
        System.arraycopy(Utils.make2ByteLengh(25), 0, this.packet, 1, 2);
        this.packet[3] = KSNET_FALLBACK_REQ;
        System.arraycopy(this.YYMMDDhhmmss.getBytes(), 0, this.packet, 4, 12);
        System.arraycopy(String.format("%09d", new Object[]{Integer.valueOf(Integer.parseInt(str))}).getBytes(), 0, this.packet, 16, 9);
        System.arraycopy(str2.getBytes(), 0, this.packet, 25, 2);
        byte[] bArr = this.packet;
        bArr[27] = 3;
        byte[] bArr2 = this.packet;
        bArr2[28] = (byte) LRC(bArr, 28);
        byte[] bArr3 = new byte[29];
        System.arraycopy(bArr2, 0, bArr3, 0, 29);
        this.packet = new byte[1024];
        return bArr3;
    }

    public byte[] makeRequestTelegram(byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4, byte[] bArr5, byte[] bArr6, byte[] bArr7, byte[] bArr8, byte[] bArr9, byte[] bArr10, byte[] bArr11, byte[] bArr12, byte[] bArr13, byte[] bArr14, byte[] bArr15, byte[] bArr16, byte[] bArr17, byte[] bArr18, byte[] bArr19, byte[] bArr20, byte[] bArr21, byte[] bArr22, byte[] bArr23) {
        int i;
        byte[] bArr24 = bArr;
        byte[] bArr25 = bArr2;
        byte[] bArr26 = bArr3;
        byte[] bArr27 = bArr4;
        byte[] bArr28 = bArr5;
        byte[] bArr29 = bArr17;
        byte[] bArr30 = bArr18;
        byte[] bArr31 = bArr19;
        byte[] bArr32 = bArr20;
        byte[] bArr33 = bArr21;
        byte[] bArr34 = bArr23;
        ByteBuffer allocateDirect = ByteBuffer.allocateDirect(4096);
        allocateDirect.put((byte) 2);
        allocateDirect.put(bArr24);
        if (!new String(bArr26).contains("09")) {
            allocateDirect.put(bArr26);
        } else {
            allocateDirect.put("09".getBytes());
        }
        allocateDirect.put(bArr25);
        allocateDirect.put("N".getBytes());
        allocateDirect.put(bArr8);
        for (int i2 = 0; i2 < 4; i2++) {
            allocateDirect.put(" ".getBytes());
        }
        allocateDirect.put(bArr27);
        int i3 = 0;
        while (i3 < 12 - bArr27.length) {
            allocateDirect.put(" ".getBytes());
            i3++;
            bArr27 = bArr4;
        }
        allocateDirect.put(bArr28);
        int i4 = 0;
        while (true) {
            if (i4 >= 20) {
                break;
            }
            allocateDirect.put(" ".getBytes());
            i4++;
        }
        int i5 = 0;
        for (i = 20; i5 < i; i = 20) {
            allocateDirect.put(" ".getBytes());
            i5++;
        }
        if ((new String(bArr24).equals("HK") || new String(bArr24).equals("PC")) && new String(bArr28).equals("K")) {
            allocateDirect.put("9".getBytes());
        } else {
            allocateDirect.put("1".getBytes());
        }
        allocateDirect.put(bArr9);
        allocateDirect.put(bArr10);
        allocateDirect.put(bArr33);
        for (int i6 = 0; i6 < 40 - bArr33.length; i6++) {
            allocateDirect.put(" ".getBytes());
        }
        if (new String(bArr24).equals("IC")) {
            for (int i7 = 0; i7 < 37; i7++) {
                allocateDirect.put(" ".getBytes());
            }
        } else if (new String(bArr24).equals("MS") || new String(bArr24).equals("HK") || new String(bArr24).equals("PC")) {
            allocateDirect.put(bArr34);
            for (int i8 = 0; i8 < 37 - bArr34.length; i8++) {
                allocateDirect.put(" ".getBytes());
            }
        }
        allocateDirect.put((byte) 28);
        allocateDirect.put(bArr11);
        allocateDirect.put(bArr12);
        allocateDirect.put(bArr14);
        allocateDirect.put(bArr15);
        allocateDirect.put(bArr13);
        allocateDirect.put(bArr16);
        allocateDirect.put("AA".getBytes());
        for (int i9 = 0; i9 < 16; i9++) {
            allocateDirect.put("0".getBytes());
        }
        if (new String(bArr25).equals("0420") || new String(bArr25).equals("0460")) {
            allocateDirect.put(bArr6);
            allocateDirect.put(bArr7);
        } else {
            for (int i10 = 0; i10 < 12; i10++) {
                allocateDirect.put(" ".getBytes());
            }
            for (int i11 = 0; i11 < 6; i11++) {
                allocateDirect.put(" ".getBytes());
            }
        }
        if (!new String(bArr26).contains("09")) {
            for (int i12 = 0; i12 < 15; i12++) {
                allocateDirect.put(" ".getBytes());
            }
            allocateDirect.put(bArr29);
            for (int i13 = 0; i13 < 30 - new String(bArr29).length(); i13++) {
                allocateDirect.put(" ".getBytes());
            }
            String str = new String(bArr26).split("|")[1];
            allocateDirect.put(str.getBytes());
            for (int i14 = 0; i14 < 4 - str.length(); i14++) {
                allocateDirect.put(" ".getBytes());
            }
            for (int i15 = 0; i15 < 20; i15++) {
                allocateDirect.put(" ".getBytes());
            }
            for (int i16 = 0; i16 < 4; i16++) {
                allocateDirect.put(" ".getBytes());
            }
        } else {
            for (int i17 = 0; i17 < 15; i17++) {
                allocateDirect.put(" ".getBytes());
            }
            allocateDirect.put(bArr29);
            for (int i18 = 0; i18 < 30 - new String(bArr29).length(); i18++) {
                allocateDirect.put(" ".getBytes());
            }
            String str2 = new String(bArr26).split("|")[1];
            allocateDirect.put(str2.getBytes());
            for (int i19 = 0; i19 < 4 - str2.length(); i19++) {
                allocateDirect.put(" ".getBytes());
            }
            for (int i20 = 0; i20 < 20; i20++) {
                allocateDirect.put(" ".getBytes());
            }
            for (int i21 = 0; i21 < 4; i21++) {
                allocateDirect.put(" ".getBytes());
            }
        }
        allocateDirect.put(bArr30);
        for (int i22 = 0; i22 < 30 - new String(bArr30).length(); i22++) {
            allocateDirect.put(" ".getBytes());
        }
        for (int i23 = 0; i23 < 60; i23++) {
            allocateDirect.put(" ".getBytes());
        }
        if (new String(bArr24).equals("IC")) {
            allocateDirect.put(bArr22);
        }
        if (new String(bArr31).equals("N")) {
            allocateDirect.put(bArr31);
        } else {
            allocateDirect.put(bArr31);
            allocateDirect.put("83".getBytes());
            for (int i24 = 0; i24 < 16; i24++) {
                allocateDirect.put(" ".getBytes());
            }
            allocateDirect.put(String.format("%04d", new Object[]{Integer.valueOf(new String(bArr32).length())}).getBytes());
            allocateDirect.put(bArr32);
        }
        allocateDirect.put((byte) 3);
        allocateDirect.put((byte) 13);
        byte[] bArr35 = new byte[allocateDirect.position()];
        allocateDirect.rewind();
        allocateDirect.get(bArr35);
        byte[] bArr36 = new byte[(bArr35.length + 4)];
        byte b = 0;
        System.arraycopy(String.format("%04d", new Object[]{Integer.valueOf(bArr35.length)}).getBytes(), 0, bArr36, 0, 4);
        System.arraycopy(bArr35, 0, bArr36, 4, bArr35.length);
        byte[] bArr37 = new byte[bArr35.length];
        byte[] bArr38 = new byte[bArr35.length];
        int i25 = 0;
        while (i25 < bArr35.length) {
            bArr37[i25] = b;
            i25++;
            b = 0;
        }
        for (int i26 = 0; i26 < bArr35.length; i26++) {
            bArr38[i26] = -1;
        }
        allocateDirect.clear();
        allocateDirect.clear();
        allocateDirect.clear();
        System.arraycopy(bArr37, 0, bArr35, 0, bArr35.length);
        System.arraycopy(bArr38, 0, bArr35, 0, bArr35.length);
        System.arraycopy(bArr37, 0, bArr35, 0, bArr35.length);
        return bArr36;
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String chkFallBack(java.lang.String r9) {
        /*
            r8 = this;
            int r0 = r9.hashCode()
            java.lang.String r1 = "07"
            java.lang.String r2 = "06"
            java.lang.String r3 = "05"
            java.lang.String r4 = "04"
            java.lang.String r5 = "03"
            java.lang.String r6 = "02"
            java.lang.String r7 = "01"
            switch(r0) {
                case 1537: goto L_0x0069;
                case 1538: goto L_0x0061;
                case 1539: goto L_0x0059;
                case 1540: goto L_0x0051;
                case 1541: goto L_0x0049;
                case 1542: goto L_0x0041;
                case 1543: goto L_0x0039;
                default: goto L_0x0015;
            }
        L_0x0015:
            switch(r0) {
                case 1629: goto L_0x002e;
                case 1630: goto L_0x0024;
                case 1631: goto L_0x0019;
                default: goto L_0x0018;
            }
        L_0x0018:
            goto L_0x0071
        L_0x0019:
            java.lang.String r0 = "32"
            boolean r9 = r9.equals(r0)
            if (r9 == 0) goto L_0x0071
            r9 = 9
            goto L_0x0072
        L_0x0024:
            java.lang.String r0 = "31"
            boolean r9 = r9.equals(r0)
            if (r9 == 0) goto L_0x0071
            r9 = 7
            goto L_0x0072
        L_0x002e:
            java.lang.String r0 = "30"
            boolean r9 = r9.equals(r0)
            if (r9 == 0) goto L_0x0071
            r9 = 8
            goto L_0x0072
        L_0x0039:
            boolean r9 = r9.equals(r1)
            if (r9 == 0) goto L_0x0071
            r9 = 6
            goto L_0x0072
        L_0x0041:
            boolean r9 = r9.equals(r2)
            if (r9 == 0) goto L_0x0071
            r9 = 5
            goto L_0x0072
        L_0x0049:
            boolean r9 = r9.equals(r3)
            if (r9 == 0) goto L_0x0071
            r9 = 4
            goto L_0x0072
        L_0x0051:
            boolean r9 = r9.equals(r4)
            if (r9 == 0) goto L_0x0071
            r9 = 3
            goto L_0x0072
        L_0x0059:
            boolean r9 = r9.equals(r5)
            if (r9 == 0) goto L_0x0071
            r9 = 2
            goto L_0x0072
        L_0x0061:
            boolean r9 = r9.equals(r6)
            if (r9 == 0) goto L_0x0071
            r9 = 1
            goto L_0x0072
        L_0x0069:
            boolean r9 = r9.equals(r7)
            if (r9 == 0) goto L_0x0071
            r9 = 0
            goto L_0x0072
        L_0x0071:
            r9 = -1
        L_0x0072:
            switch(r9) {
                case 0: goto L_0x008b;
                case 1: goto L_0x0089;
                case 2: goto L_0x0087;
                case 3: goto L_0x0085;
                case 4: goto L_0x0083;
                case 5: goto L_0x0081;
                case 6: goto L_0x008c;
                case 7: goto L_0x007e;
                case 8: goto L_0x007b;
                case 9: goto L_0x0078;
                default: goto L_0x0075;
            }
        L_0x0075:
            java.lang.String r1 = ""
            goto L_0x008c
        L_0x0078:
            java.lang.String r1 = "CardErr"
            goto L_0x008c
        L_0x007b:
            java.lang.String r1 = "ChipBlock"
            goto L_0x008c
        L_0x007e:
            java.lang.String r1 = "ApplicationBlock"
            goto L_0x008c
        L_0x0081:
            r1 = r2
            goto L_0x008c
        L_0x0083:
            r1 = r3
            goto L_0x008c
        L_0x0085:
            r1 = r4
            goto L_0x008c
        L_0x0087:
            r1 = r5
            goto L_0x008c
        L_0x0089:
            r1 = r6
            goto L_0x008c
        L_0x008b:
            r1 = r7
        L_0x008c:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: ksnet.common.EncMSRManager.chkFallBack(java.lang.String):java.lang.String");
    }
}