package com.pswseoul.comunity.http;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.pswseoul.comunity.imp.HttpClientRepository;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by parksuwon on 2017-12-10.
 */

public class HttpClientLibrary implements HttpClientRepository {

    public static final String TAG = "TAG";

    @Override
    public void getHttpClient(final RequestCallback callback) {
        new AsyncTask<Void, Void, String>() {
            // 처리 전에 호출되는 메소드
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            // 처리를 하는 메소드
            @Override
            protected String doInBackground(Void... params) {
                final HttpClient httpClient = new DefaultHttpClient();
                final HttpGet httpGet = new HttpGet(uri.toString());
                final HttpResponse httpResponse;
                try {
                    httpResponse = httpClient.execute(httpGet);
                    final String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                    return new Gson().fromJson(response, String.class);
                } catch (IOException e) {
                    return null;
                }
            }

            // 처리가 모두 끝나면 불리는 메소드
            @Override
            protected void onPostExecute(String response) {
                super.onPostExecute(response);
                // 통신 실패로 처리
                if (response == null) {
                    callback.error(new IOException("HttpClient request error"));
                } else {
                    Log.d(TAG, "result: " + response.toString());
                    // 통신 결과를 표시
                    callback.success(response);
                }
            }
        }.execute();
    }

}
