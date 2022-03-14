package com.bkwinners.ksnet.dpt.ks03;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    public static final int RESULT_PERMISSION_CANCEL = 21;
    public static final int RESULT_PERMISSION_OK = 20;
    String[] permission;
    String permission1;
    String permission2;
    PermissionListener permissionListener = new PermissionListener() {
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "승인됨", Toast.LENGTH_SHORT).show();
            setResult(RESULT_PERMISSION_OK);
            finish();
        }

        public void onPermissionDenied(List<String> arrayList) {
            requireCheck = false;
            Iterator<String> it = arrayList.iterator();
            while (it.hasNext()) {
                String next = it.next();
                for (String str : require) {
                    Log.d("requirePermission!!!", str);
                }
                String[] strArr = require;
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String str2 = strArr[i];
                    Log.d(str2, next);
                    StringBuilder sb = new StringBuilder();
                    sb.append(next);
                    sb.append(";");
                    sb.append(next.toUpperCase().indexOf(str2) > -1);
                    Log.d(str2, sb.toString());
                    if (next.toUpperCase().indexOf(str2) > -1) {
                        requireCheck = true;
                        break;
                    }
                    i++;
                }
            }
            if (requireCheck) {
                setResult(RESULT_PERMISSION_CANCEL);
                finish();
                return;
            }
            setResult(RESULT_PERMISSION_OK);
            finish();
        }
    };
    boolean[] permissionType;
    String[] require;
    boolean requireCheck = false;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestWindowFeature(1);
        setContentView(R.layout.activity_permission);
        this.permission = getIntent().getStringArrayExtra("permission");
        this.require = getIntent().getStringArrayExtra("require");
        this.permission1 = getIntent().getStringExtra("permission1");
        this.permission2 = getIntent().getStringExtra("permission2");
        ((TextView) findViewById(R.id.permission1)).setText(this.permission1);
        ((TextView) findViewById(R.id.permission2)).setText(this.permission2);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TedPermission.with(PermissionActivity.this).setPermissionListener(permissionListener).setDeniedMessage("권한 거부시 [애플리케이션 정보] > [권한]에서 허용설정 바랍니다.").setPermissions(permission).check();
            }
        });
        this.requireCheck = true;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_PERMISSION_CANCEL);
        super.onBackPressed();
    }
}
