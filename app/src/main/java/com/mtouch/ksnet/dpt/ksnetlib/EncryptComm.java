package com.mtouch.ksnet.dpt.ksnetlib;

import com.mtouch.ksnet.dpt.common.EncMSRManager;
import com.mtouch.ksnet.dpt.common.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Random;
import javax.crypto.Cipher;

class EncryptComm {
    Utility m_Util = new Utility();
    public byte[] m_bErrorTelegram = null;
    public int m_iTimeOut = 10000;
    public String m_sErrorCode = "";
    public String m_sErrorMsg = "";

    private byte[] encryptMessage(byte[] bArr, byte[] bArr2) {
        byte[] ks_seed_encrypt;
        byte[] ks_rsa_encrypt = ks_rsa_encrypt(bArr2);
        if (ks_rsa_encrypt == null || (ks_seed_encrypt = ks_seed_encrypt(bArr2, bArr)) == null) {
            return null;
        }
        byte[] bArr3 = new byte[(ks_seed_encrypt.length + 141)];
        System.arraycopy("0001292".getBytes(), 0, bArr3, 0, 7);
        System.arraycopy(ks_rsa_encrypt, 0, bArr3, 7, 128);
        System.arraycopy(this.m_Util.matchFormat(String.valueOf(ks_seed_encrypt.length), 6, '9').getBytes(), 0, bArr3, 135, 6);
        System.arraycopy(ks_seed_encrypt, 0, bArr3, 141, ks_seed_encrypt.length);
        int length = ks_seed_encrypt.length;
        return bArr3;
    }

    /* access modifiers changed from: package-private */
    public byte[] kspay_send_socket(String str, int i, byte[] bArr, boolean z) {
        int i2;
        String str2;
        int i3;
        String str3 = str;
        int i4 = i;
        byte[] bArr2 = bArr;
        boolean z2 = z;
        if (!z2) {
            this.m_sErrorCode = "0";
            this.m_sErrorMsg = "";
            this.m_bErrorTelegram = null;
        }
        char[] cArr = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        int i5 = 0;
        while (i5 < 16) {
            sb.append(cArr[random.nextInt(cArr.length)]);
            i5++;
            z2 = z;
        }
        byte[] stringToByte = this.m_Util.stringToByte(sb.toString());
        byte[] encryptMessage = encryptMessage(bArr2, stringToByte);
        if (encryptMessage == null) {
            if (!z2) {
                this.m_sErrorCode = "4";
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "전문 암호화 실패";
            } else {
                this.m_sErrorCode = "104";
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)전문 암호화 실패";
            }
            return null;
        }
        Socket socket = new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(str3, i4);
        try {
            socket.setSoTimeout(this.m_iTimeOut);
            socket.connect(inetSocketAddress, this.m_iTimeOut);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            byte[] sendMessage = sendMessage(encryptMessage, dataOutputStream, dataInputStream, z2);
            if (sendMessage == null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    IOException iOException = e;
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + iOException.getMessage();
                    if (!z2) {
                        this.m_sErrorCode = "10";
                        if (this.m_sErrorMsg.length() > 0) {
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                        }
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "송수신 실패 후 소켓 닫기 실패";
                    } else {
                        this.m_sErrorCode = "110";
                        if (this.m_sErrorMsg.length() > 0) {
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                        }
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)송수신 실패 후 소켓 닫기 실패";
                    }
                }
                return null;
            }
            byte[] ks_seed_decrypt = ks_seed_decrypt(stringToByte, sendMessage);
            if (!z2) {
                int sendEOT = sendEOT(socket, dataOutputStream, dataInputStream);
                String byteToString = this.m_Util.byteToString(ks_seed_decrypt, 27, 1);
                char c = 200;
                if (!(byteToString.compareTo("O") == 0 || byteToString.compareTo("X") == 0)) {
                    byteToString = this.m_Util.byteToString(ks_seed_decrypt, 31, 1);
                    c = 204;
                }
                if (!(byteToString.compareTo("O") == 0 || byteToString.compareTo("X") == 0)) {
                    byteToString = this.m_Util.byteToString(ks_seed_decrypt, 33, 1);
                    c = 240;
                }
                if (byteToString.compareTo("O") == 0) {
                    if (sendEOT == -1) {
                        this.m_sErrorCode = "11";
                        if (this.m_sErrorMsg.length() > 0) {
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                        }
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "ACK 송신 실패";
                        return null;
                    } else if (sendEOT == -2) {
                        this.m_bErrorTelegram = new byte[ks_seed_decrypt.length];
                        System.arraycopy(ks_seed_decrypt, 0, this.m_bErrorTelegram, 0, ks_seed_decrypt.length);
                        if (c == 240) {
                            str2 = cncl_gubuncheck(this.m_Util.byteToString(bArr2, 6, 2));
                            if (str2.compareTo("  ") != 0) {
                                String byteToString2 = this.m_Util.byteToString(ks_seed_decrypt, 79, 12);
                                String byteToString3 = this.m_Util.byteToString(ks_seed_decrypt, 34, 6);
                                System.arraycopy(str2.getBytes(), 0, bArr2, 6, 2);
                                System.arraycopy(byteToString2.getBytes(), 0, bArr2, 129, 12);
                                System.arraycopy(byteToString3.getBytes(), 0, bArr2, 141, 6);
                            }
                            i2 = 0;
                        } else if (c == 200) {
                            str2 = cncl_gubuncheck(this.m_Util.byteToString(bArr2, 2, 2));
                            if (str2.compareTo("  ") != 0) {
                                if (str2.compareTo("CC") == 0) {
                                    String byteToString4 = this.m_Util.byteToString(ks_seed_decrypt, 70, 8);
                                    String byteToString5 = this.m_Util.byteToString(ks_seed_decrypt, 28, 6);
                                    System.arraycopy(str2.getBytes(), 0, bArr2, 2, 2);
                                    int findFS = findFS(bArr2, 0);
                                    byte[] bArr3 = new byte[(findFS + 81)];
                                    int i6 = findFS + 39;
                                    System.arraycopy(bArr2, 0, bArr3, 0, i6);
                                    System.arraycopy(byteToString4.getBytes(), 0, bArr3, i6, 8);
                                    System.arraycopy(byteToString5.getBytes(), 0, bArr3, findFS + 47, 6);
                                    System.arraycopy(bArr2, i6, bArr3, findFS + 53, 20);
                                    System.arraycopy(bArr2, findFS + 129, bArr3, findFS + 73, 4);
                                    i3 = 0;
                                    System.arraycopy("  ".getBytes(), 0, bArr3, findFS + 77, 2);
                                    System.arraycopy(bArr2, findFS + 133, bArr3, findFS + 79, 2);
                                    bArr2 = new byte[bArr3.length];
                                    System.arraycopy(bArr3, 0, bArr2, 0, bArr2.length);
                                } else {
                                    i3 = 0;
                                    if (str2.compareTo("DC") == 0) {
                                        String byteToString6 = this.m_Util.byteToString(ks_seed_decrypt, 70, 12);
                                        String byteToString7 = this.m_Util.byteToString(ks_seed_decrypt, 28, 6);
                                        System.arraycopy(str2.getBytes(), 0, bArr2, 2, 2);
                                        int findFS2 = findFS(bArr2, 0);
                                        byte[] bArr4 = new byte[(findFS2 + 83)];
                                        int i7 = findFS2 + 37;
                                        System.arraycopy(bArr2, 0, bArr4, 0, i7);
                                        System.arraycopy(byteToString6.getBytes(), 0, bArr4, i7, 12);
                                        System.arraycopy(byteToString7.getBytes(), 0, bArr4, findFS2 + 49, 6);
                                        System.arraycopy(bArr2, i7, bArr4, findFS2 + 55, 28);
                                        bArr2 = new byte[bArr4.length];
                                        System.arraycopy(bArr4, 0, bArr2, 0, bArr2.length);
                                    } else if (str2.compareTo("bs") == 0) {
                                        String byteToString8 = this.m_Util.byteToString(ks_seed_decrypt, 71, 9);
                                        String byteToString9 = this.m_Util.byteToString(ks_seed_decrypt, 28, 6);
                                        String byteToString10 = this.m_Util.byteToString(ks_seed_decrypt, 147, 12);
                                        i2 = 0;
                                        System.arraycopy(str2.getBytes(), 0, bArr2, 2, 2);
                                        byte[] bArr5 = new byte[274];
                                        System.arraycopy(bArr2, 0, bArr5, 0, 105);
                                        System.arraycopy(byteToString8.getBytes(), 0, bArr5, 105, 9);
                                        System.arraycopy(byteToString10.getBytes(), 0, bArr5, 114, 12);
                                        System.arraycopy(byteToString9.getBytes(), 0, bArr5, 126, 6);
                                        System.arraycopy(bArr2, 105, bArr5, 132, 138);
                                        System.arraycopy(EncMSRManager.FALLBACK_NO_ATR.getBytes(), 0, bArr5, 270, 2);
                                        System.arraycopy(bArr2, 249, bArr5, 272, 2);
                                        bArr2 = new byte[bArr5.length];
                                        System.arraycopy(bArr5, 0, bArr2, 0, bArr2.length);
                                    } else {
                                        String byteToString11 = this.m_Util.byteToString(ks_seed_decrypt, 70, 12);
                                        String byteToString12 = this.m_Util.byteToString(ks_seed_decrypt, 28, 6);
                                        int findFS3 = findFS(bArr2, 0);
                                        System.arraycopy(str2.getBytes(), 0, bArr2, 2, 2);
                                        System.arraycopy(byteToString11.getBytes(), 0, bArr2, findFS3 + 12, 12);
                                        System.arraycopy(byteToString12.getBytes(), 0, bArr2, findFS3 + 24, 6);
                                    }
                                }
                                i2 = i3;
                            }
                            i2 = 0;
                        } else if (c == 204) {
                            String cncl_gubuncheck = cncl_gubuncheck(this.m_Util.byteToString(bArr2, 6, 2));
                            if (cncl_gubuncheck.compareTo("  ") == 0) {
                                i2 = 0;
                            } else if (cncl_gubuncheck.compareTo("NC") == 0) {
                                String byteToString13 = this.m_Util.byteToString(ks_seed_decrypt, 74, 8);
                                String byteToString14 = this.m_Util.byteToString(ks_seed_decrypt, 32, 6);
                                System.arraycopy(cncl_gubuncheck.getBytes(), 0, bArr2, 6, 2);
                                int length = (((bArr2.length - 40) - 20) - 10) + 8 + 6 + 2;
                                String format = String.format("%04d", new Object[]{Integer.valueOf((length - 1) - 4)});
                                byte[] bArr6 = new byte[length];
                                System.arraycopy(bArr2, 0, bArr6, 0, 109);
                                System.arraycopy(format.getBytes(), 0, bArr6, 1, 4);
                                System.arraycopy(byteToString13.getBytes(), 0, bArr6, 109, 8);
                                System.arraycopy(byteToString14.getBytes(), 0, bArr6, 117, 6);
                                System.arraycopy(bArr2, 109, bArr6, 123, 20);
                                System.arraycopy(bArr2, 199, bArr6, 143, 4);
                                System.arraycopy("  ".getBytes(), 0, bArr6, 147, 2);
                                System.arraycopy(bArr2, 203, bArr6, 149, bArr2.length - 203);
                                bArr2 = new byte[bArr6.length];
                                System.arraycopy(bArr6, 0, bArr2, 0, bArr2.length);
                                str2 = cncl_gubuncheck;
                                i2 = 0;
                            } else if (cncl_gubuncheck.compareTo("bs") == 0) {
                                String byteToString15 = this.m_Util.byteToString(ks_seed_decrypt, 75, 9);
                                String byteToString16 = this.m_Util.byteToString(ks_seed_decrypt, 32, 6);
                                String byteToString17 = this.m_Util.byteToString(ks_seed_decrypt, 151, 12);
                                i2 = 0;
                                System.arraycopy(cncl_gubuncheck.getBytes(), 0, bArr2, 6, 2);
                                byte[] bArr7 = new byte[278];
                                System.arraycopy(bArr2, 0, bArr7, 0, 109);
                                System.arraycopy(byteToString15.getBytes(), 0, bArr7, 109, 9);
                                System.arraycopy(byteToString17.getBytes(), 0, bArr7, 118, 12);
                                System.arraycopy(byteToString16.getBytes(), 0, bArr7, 130, 6);
                                System.arraycopy(bArr2, 109, bArr7, 136, 138);
                                System.arraycopy(EncMSRManager.FALLBACK_NO_ATR.getBytes(), 0, bArr7, 274, 2);
                                System.arraycopy(bArr2, 249, bArr7, 276, 2);
                                bArr2 = new byte[bArr7.length];
                                System.arraycopy(bArr7, 0, bArr2, 0, bArr2.length);
                            } else {
                                i2 = 0;
                                String byteToString18 = this.m_Util.byteToString(ks_seed_decrypt, 74, 12);
                                String byteToString19 = this.m_Util.byteToString(ks_seed_decrypt, 32, 6);
                                int findFS4 = findFS(bArr2, 0);
                                System.arraycopy(cncl_gubuncheck.getBytes(), 0, bArr2, 6, 2);
                                System.arraycopy(byteToString18.getBytes(), 0, bArr2, findFS4 + 12, 12);
                                System.arraycopy(byteToString19.getBytes(), 0, bArr2, findFS4 + 24, 6);
                            }
                            str2 = cncl_gubuncheck;
                        } else {
                            i2 = 0;
                            str2 = "  ";
                        }
                        System.out.println(new String(bArr2, i2, bArr2.length));
                        if (str2.compareTo("  ") != 0) {
                            ks_seed_decrypt = kspay_send_socket(str3, i4, bArr2, true);
                        } else {
                            this.m_sErrorCode = "101";
                            if (this.m_sErrorMsg.length() > 0) {
                                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                            }
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "망취소 거래 구분자 아님";
                        }
                    }
                }
            }
            try {
                socket.close();
            } catch (Exception e2) {
                Exception exc = e2;
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + exc.getMessage();
            }
            if (z) {
                this.m_sErrorCode = "100";
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "망취소(승인 후 취소)";
            }
            return ks_seed_decrypt;
        } catch (IOException e3) {
            try {
                socket.close();
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e3.getMessage();
                if (!z) {
                    this.m_sErrorCode = "5";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "연결 실패";
                } else {
                    this.m_sErrorCode = "105";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)연결 실패";
                }
                return null;
            } catch (IOException e4) {
                IOException iOException2 = e4;
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + iOException2.getMessage();
                if (!z) {
                    this.m_sErrorCode = "6";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "연결 실패 후 소켓 닫기 실패";
                } else {
                    this.m_sErrorCode = "106";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)연결 실패 후 소켓 닫기 실패";
                }
                return null;
            }
        }
    }

    private String cncl_gubuncheck(String str) {
        if (str.equals("JA") || str.equals("JY")) {
            return "JC";
        }
        if (str.equals("JG")) {
            return "JI";
        }
        if (str.equals("JM")) {
            return "JO";
        }
        if (str.equals("BA")) {
            return "BC";
        }
        if (str.equals("QA")) {
            return "QC";
        }
        if (str.equals("BE")) {
            return "BG";
        }
        if (str.equals("QE")) {
            return "QG";
        }
        if (str.equals("BK")) {
            return "BM";
        }
        if (str.equals("QK")) {
            return "QM";
        }
        if (str.equals("CA") || str.equals("FA")) {
            return "CC";
        }
        if (str.equals("DA")) {
            return "DC";
        }
        if (str.equals("NA") || str.equals("NE")) {
            return "NC";
        }
        if (str.equals("bq")) {
            return "bs";
        }
        if (str.equals("ba")) {
            return "bc";
        }
        if (str.equals("bi")) {
            return "bk";
        }
        if (str.equals("bg")) {
            return "bm";
        }
        if (str.equals("la")) {
            return "lc";
        }
        if (str.equals("li")) {
            return "lk";
        }
        if (str.equals("MA")) {
            return "MC";
        }
        if (str.equals("pa")) {
            return "pc";
        }
        if (str.equals("ya")) {
            return "yc";
        }
        if (str.equals("YA")) {
            return "YC";
        }
        if (str.equals("ha")) {
            return "hc";
        }
        if (str.equals("gg")) {
            return "go";
        }
        if (str.equals("gw")) {
            return "gy";
        }
        if (str.equals("po")) {
            return "pq";
        }
        if (str.equals("pu")) {
            return "pw";
        }
        return str.equals("pg") ? "pi" : "  ";
    }

    private byte[] sendMessage(byte[] bArr, DataOutputStream dataOutputStream, DataInputStream dataInputStream, boolean z) {
        try {
            dataOutputStream.write(bArr, 0, bArr.length);
            dataOutputStream.flush();
            byte[] bArr2 = new byte[8192];
            try {
                int read = dataInputStream.read(bArr2, 0, 6);
                if (read != 6 || read == -1) {
                    throw new Exception();
                }
                int parseInt = Integer.parseInt(this.m_Util.byteToString(bArr2, 0, 6));
                try {
                    dataInputStream.read(bArr2, 0, parseInt);
                    byte[] bArr3 = new byte[parseInt];
                    System.arraycopy(bArr2, 0, bArr3, 0, parseInt);
                    return bArr3;
                } catch (Exception e) {
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e.getMessage();
                    if (!z) {
                        this.m_sErrorCode = "9";
                        if (this.m_sErrorMsg.length() > 0) {
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                        }
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "수신 실패";
                    } else {
                        this.m_sErrorCode = "109";
                        if (this.m_sErrorMsg.length() > 0) {
                            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                        }
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)수신 실패";
                    }
                    return null;
                }
            } catch (Exception e2) {
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e2.getMessage();
                if (!z) {
                    this.m_sErrorCode = "8";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "길이 수신 실패";
                } else {
                    this.m_sErrorCode = "108";
                    if (this.m_sErrorMsg.length() > 0) {
                        this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                    }
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)길이 수신 실패";
                }
                return null;
            }
        } catch (Exception e3) {
            if (this.m_sErrorMsg.length() > 0) {
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
            }
            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e3.getMessage();
            if (!z) {
                this.m_sErrorCode = "7";
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "송신 실패";
            } else {
                this.m_sErrorCode = "107";
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "(망취소)송신 실패";
            }
            return null;
        }
    }

    private int sendEOT(Socket socket, DataOutputStream dataOutputStream, DataInputStream dataInputStream) {
        byte[] bArr = new byte[2];
        bArr[0] = 6;
        try {
            dataOutputStream.write(bArr, 0, 1);
            dataOutputStream.flush();
            byte[] bArr2 = new byte[5];
            try {
                dataInputStream.read(bArr2, 0, 1);
                if (bArr2[0] != 4) {
                    return -2;
                }
                return 0;
            } catch (Exception e) {
                if (this.m_sErrorMsg.length() > 0) {
                    this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
                }
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e.getMessage();
                return -2;
            }
        } catch (IOException e2) {
            if (this.m_sErrorMsg.length() > 0) {
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
            }
            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e2.getMessage();
            return -1;
        }
    }

    private byte[] ks_seed_decrypt(byte[] bArr, byte[] bArr2) {
        return new Seed(bArr).cbc_decrypt(bArr2);
    }

    private byte[] ks_seed_encrypt(byte[] bArr, byte[] bArr2) {
        return new Seed(bArr).cbc_encrypt(bArr2);
    }

    private byte[] ks_rsa_encrypt(byte[] bArr) {
        try {
            PublicKey generatePublic = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(new BigInteger("d4846c2b8228dddfab9e614da2a324c1cc7b29d848cc005624d3a09667a2aab9073290bace6aa536ddceb3c47ddda78d9954da06c83aa65b939c5ec773a3787e71bec5a1c077bb446c06b393d2537967645d386b4b0b4ec21372fdc728c56693028c1c3915c1c4279793eb3dccefd6bf49b86cc7d88a47b0d44aba9e73750fcd", 16), new BigInteger("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010001", 16)));
            Cipher instance = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            instance.init(1, generatePublic);
            return instance.doFinal(bArr);
        } catch (Exception e) {
            if (this.m_sErrorMsg.length() > 0) {
                this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + "\n";
            }
            this.m_sErrorMsg = String.valueOf(this.m_sErrorMsg) + e.getMessage();
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public int findFS(byte[] bArr, int i) {
        if (bArr == null) {
            return -1;
        }
        while (i < bArr.length && bArr[i] != 28) {
            i++;
        }
        return i;
    }
}