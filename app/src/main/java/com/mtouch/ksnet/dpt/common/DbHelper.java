package com.mtouch.ksnet.dpt.common;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.reflect.Array;

public class DbHelper extends SQLiteOpenHelper {
    private static final String[] mCreateQrys = {"CREATE TABLE KSN_APP_TRANS(TR_ID TEXT PRIMARY KEY,TR_TYPE TEXT NOT NULL,TR_TERMID TEXT NOT NULL,TR_PEM TEXT NOT NULL,TR_CRDNO TEXT NOT NULL,TR_QUOTA INTEGER,TR_SERVICE INTEGER,TR_TAX INTEGER,TR_AMOUNT INTEGER,TR_TOTAL INTEGER,TR_FREE INTEGER,TR_USER TEXT,TR_RESVD TEXT,TR_RESVDK TEXT,TR_TIME TEXT NOT NULL,TR_ADMNO TEXT NOT NULL,TR_MEMNO TEXT,TR_CRDTYPE TEXT,TR_CRDNAME TEXT,TR_CISCODE TEXT,TR_CPRCODE TEXT,TR_CPRNAME TEXT,TR_BALANCE INTEGER,TR_STAFF_ID TEXT,TR_EV_ID TEXT,TR_POINT1 INTEGER,TR_POINT2 INTEGER,TR_POINT3 INTEGER,TR_NOTICE1 TEXT,TR_NOTICE2 TEXT,TR_CNCLYN TEXT,TR_CNCLTIME TEXT,TR_BRANCHNM TEXT,TR_BIZNO TEXT, TR_RESERVED1 TEXT, TR_RESERVED2 TEXT);", "CREATE TABLE KSN_APP_ENV(EV_ID TEXT ,EV_SUBID TEXT PRIMARY KEY,EV_VALUE1 TEXT,EV_VALUE2 TEXT,EV_VALUE3 TEXT,EV_ENTDATE TEXT,EV_UPDDATE TEXT);", "CREATE TABLE KSN_APP_CUSTOMER(CS_CUSTID TEXT PRIMARY KEY,CS_CUSTNAME TEXT NOT NULL, CS_RESERVED1 TEXT, CS_RESERVED2 TEXT);", "CREATE TABLE KSN_APP_INTEGRITY(IG_RESULT TEXT ,IG_TYPE TEXT NOT NULL,IG_TIME TEXT NOT NULL , IG_RESERVED1 TEXT, IG_RESERVED2 TEXT);", "CREATE INDEX IDX_KSN_APP_TRANS ON KSN_APP_TRANS(TR_TIME, TR_TYPE, TR_CPRNAME);"};
    private static final String mFile = "ksn_app.db";
    private static final int mVersion = 1;
    private String mMessage = "";

    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
    }

    public DbHelper(Activity activity) {
        super(activity, mFile, (SQLiteDatabase.CursorFactory) null, 1);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        for (String execSQL : mCreateQrys) {
            sQLiteDatabase.execSQL(execSQL);
        }
    }

    public String getErrMsg() {
        return this.mMessage;
    }

    public boolean excuteNonQuery(String str) {
        this.mMessage = "";
        try {
            getWritableDatabase().execSQL(str);
            close();
            return true;
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return false;
        }
    }

    public String[][] excuteQuery(String str) {
        this.mMessage = "";
        try {
            String[][] strArr = null;
            Cursor rawQuery = getReadableDatabase().rawQuery(str, (String[]) null);
            int count = rawQuery.getCount();
            int columnCount = rawQuery.getColumnCount();
            String[][] strArr2 = (String[][]) Array.newInstance(String.class, new int[]{count, columnCount});
            int i = 0;
            while (rawQuery.moveToNext()) {
                for (int i2 = 0; i2 < columnCount; i2++) {
                    strArr2[i][i2] = rawQuery.getString(i2);
                }
                i++;
            }
            rawQuery.close();
            close();
            if (count != i) {
                return null;
            }
            return strArr2;
        } catch (Exception e) {
            this.mMessage = e.getMessage();
            return null;
        }
    }
}