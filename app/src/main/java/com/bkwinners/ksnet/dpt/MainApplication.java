package com.bkwinners.ksnet.dpt;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.bkwinners.ksnet.dpt.design.util.LOG;

import com.bkwinners.caddie.BuildConfig;
import com.bkwinners.caddie.R;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

public class MainApplication extends Application {

        public static final int HOME = 1;
        public static final int PREV = 0;
        private Boolean IsBlueToothConnect = false;
        private Boolean IsBootFirst = true;

    public String fcmToken;


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Boolean getIsBlueToothConnect() {
        return this.IsBlueToothConnect;
    }

    public void setIsBlueToothConnect(Boolean bool) {
        this.IsBlueToothConnect = bool;
    }

    public Boolean getIsBootFirst() {
        return this.IsBootFirst;
    }

    public void setIsBootFirst(Boolean bool) {
        this.IsBootFirst = bool;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(BuildConfig.VERSION_CODE)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

                        RealmSchema schema = realm.getSchema();

                        LOG.w("Realm version: old-"+oldVersion);
                        LOG.w("Realm version: newVersion-"+newVersion);

                        //최초스키마버전일 경우 업데이트
                        if(oldVersion==0 ) {
//                            RealmObjectSchema paymentInfo = schema.get("PaymentInfo");
//                            paymentInfo.addField("mchtAddr", String.class, null);
//                            paymentInfo.addField("mchtBizNum", String.class, null);
                        }

                    }
                })
                .build();
        Realm.setDefaultConfiguration(config);

        /**
         * 노티피케이션 채널 생성하기 안드로이드 버전 오레오 이상부터 필요
         */
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "0";//getString(R.string.notification_channel_id); // 채널 아이디
//            CharSequence channelName = "default";//getString(R.string.notification_channel_name); //채널 이름
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
    }
}
