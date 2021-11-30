package com.pswseoul.comunity.imp;

import android.net.Uri;

/**
 * Created by parksuwon on 2017-12-10.
 */

public interface HttpClientRepository {

    // URL Const Vals
    String SCHEME = "https";
    String AUTHORITY = "cyrexpay";
    String PATH = "/forecast/webservice/json/v1";
    // URI
    Uri uri = new Uri.Builder()
            .scheme(SCHEME)
            .authority(AUTHORITY)
            .path(PATH)
            .build();

    void getHttpClient(RequestCallback callback);

    interface RequestCallback {
        // 생성시의 Callback
        void success(String response);

        // 실패시의 Callback
        void error(Throwable throwable);
    }

}
