package com.pswseoul.comunity.fcm;

/**
 * Created by parksuwon on 2017-12-20.
 */
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.mtouch.ksnet.dpt.ks03.pay.ksr03.MtouchPayKsnet;

import com.mtouch.caddie.BuildConfig;
import com.mtouch.caddie.R;
//import com.google.firebase.messaging.RemoteMessage;
//
//public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
//    private static final String TAG = "FirebaseMsgService";
//
//    // [START receive_message]
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        //추가한것
//        sendNotification(remoteMessage.getData().get("message"));
//    }
//
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, MtouchPayKsnet.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle("FCM Push Test") // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//
//}
