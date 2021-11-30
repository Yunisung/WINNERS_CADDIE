package com.mtouch.ksnet.dpt.common;

import android.app.Activity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class TextPref {
    private StringBuilder mBuf;
    private String mFileName;
    private final String mHeader = "__Text Preference File__\n";
    private Activity mParent;

    public TextPref(Activity activity, String str) throws Exception {
        this.mParent = activity;
        this.mFileName = str;
        if (!new File(this.mParent.getFilesDir(), this.mFileName).exists()) {
            FileOutputStream openFileOutput = this.mParent.openFileOutput(this.mFileName, 0);
            openFileOutput.write("__Text Preference File__\n".getBytes());
            openFileOutput.close();
        }
    }

    public void reset() {
        new File(this.mParent.getFilesDir(), this.mFileName).delete();
    }

    public void ready() throws Exception {
        FileInputStream openFileInput = this.mParent.openFileInput(this.mFileName);
        int available = openFileInput.available();
        byte[] bArr = new byte[available];
        do {
        } while (openFileInput.read(bArr) != -1);
        openFileInput.close();
        this.mBuf = new StringBuilder(available);
        this.mBuf.append(new String(bArr));
    }

    public void commitWrite() throws Exception {
        FileOutputStream openFileOutput = this.mParent.openFileOutput(this.mFileName, 0);
        openFileOutput.write(this.mBuf.toString().getBytes());
        openFileOutput.close();
        this.mBuf = null;
    }

    public void endReady() {
        this.mBuf = null;
    }

    private int findIdx(String str) {
        String str2 = "__" + str + "=";
        int indexOf = this.mBuf.indexOf(str2);
        if (indexOf == -1) {
            return -1;
        }
        return indexOf + str2.length();
    }

    public void writeString(String str, String str2) {
        int findIdx = findIdx(str);
        if (findIdx == -1) {
            this.mBuf.append("__");
            this.mBuf.append(str);
            this.mBuf.append("=");
            this.mBuf.append(str2);
            this.mBuf.append("\n");
            return;
        }
        this.mBuf.delete(findIdx, this.mBuf.indexOf("\n", findIdx));
        this.mBuf.insert(findIdx, str2);
    }

    public String readString(String str, String str2) {
        int findIdx = findIdx(str);
        if (findIdx == -1) {
            return str2;
        }
        return this.mBuf.substring(findIdx, this.mBuf.indexOf("\n", findIdx));
    }

    public void writeInt(String str, int i) {
        writeString(str, Integer.toString(i));
    }

    public int readInt(String str, int i) {
        String readString = readString(str, "__none");
        if (readString.equals("__none")) {
            return i;
        }
        try {
            return Integer.parseInt(readString);
        } catch (Exception unused) {
            return i;
        }
    }

    public void writeBoolean(String str, boolean z) {
        writeString(str, z ? "1" : "0");
    }

    public boolean readBoolean(String str, boolean z) {
        String readString = readString(str, "__none");
        if (readString.equals("__none")) {
            return z;
        }
        try {
            return readString.equals("1");
        } catch (Exception unused) {
            return z;
        }
    }

    public void writeFloat(String str, float f) {
        writeString(str, Float.toString(f));
    }

    public float readFloat(String str, float f) {
        String readString = readString(str, "__none");
        if (readString.equals("__none")) {
            return f;
        }
        try {
            return Float.parseFloat(readString);
        } catch (Exception unused) {
            return f;
        }
    }

    /* access modifiers changed from: package-private */
    public void bulkWriteReady(int i) {
        this.mBuf = new StringBuilder(i);
        this.mBuf.append("__Text Preference File__\n");
        this.mBuf.append("\n");
    }

    /* access modifiers changed from: package-private */
    public void bulkWrite(String str, String str2) {
        this.mBuf.append("__");
        this.mBuf.append(str);
        this.mBuf.append("=");
        this.mBuf.append(str2);
        this.mBuf.append("\n");
    }

    /* access modifiers changed from: package-private */
    public void deleteKey(String str) {
        int findIdx = findIdx(str);
        if (findIdx != -1) {
            this.mBuf.delete(findIdx - (str.length() + 3), this.mBuf.indexOf("\n", findIdx) + 1);
        }
    }
}