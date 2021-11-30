package com.pswseoul.comunity.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.mtouch.ksnet.dpt.ks03.pay.ksnet.KSNETStatus;
import com.pswseoul.comunity.imp.AsyncTaskCompleteListener;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class HttpPostTask extends AsyncTask<String, Void, String>
{

    private AsyncTaskCompleteListener callback;

    public HttpPostTask(AsyncTaskCompleteListener callback)
    {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost;
        httpPost = new HttpPost(getCheckUrl(strings[0]).toString() );

        final HttpResponse httpResponse;
        try {
            StringEntity input = new StringEntity( strings[1],  HTTP.UTF_8);
            input.setContentType("application/json");
            httpPost.setEntity(input);
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("authorization", KSNETStatus.token);
            httpResponse = httpClient.execute(httpPost);
            final String response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            int status = httpResponse.getStatusLine().getStatusCode();
            Log.d ( "tag" , "status : ==========================================================="+ status);

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return "{result :\"-1\" }";
        }
    }

    @Override
    protected void onPostExecute(String result)
    {
        callback.onTaskComplete(result);
    }

    protected String getCheckUrl(String arg) {
        if(arg.equals("REGISTRY_STORE")) return callback.REGISTRY_STORE_URL;
        else if(arg.equals("REGISTRY_APP")) return callback.REGISTRY_APP_URL;
        else if(arg.equals("AVALABLE_KEY")) return callback.AVALABLE_KEY_URL;
        else if(arg.equals("BASIC_VALUE")) return callback.BASIC_VALUE_URL;
        else if(arg.equals("APPROVE_CEHCK")) return callback.APPROVE_CEHCK_URL;
        else if(arg.equals("SERVER_SEND")) return callback.SERVER_SEND_URL;
        else
            return  "";
    }

    protected Uri getCheckUri(String arg) {
        if(arg.equals("REGISTRY_STORE")) return callback.REGISTRY_STORE_URI;
        else if(arg.equals("REGISTRY_APP")) return callback.REGISTRY_APP_URI;
        else if(arg.equals("AVALABLE_KEY")) return callback.AVALABLE_KEY_URI;
        else if(arg.equals("BASIC_VALUE")) return callback.BASIC_VALUE_URI;
        else if(arg.equals("APPROVE_CEHCK")) return callback.APPROVE_CEHCK_URI;
        else if(arg.equals("SERVER_SEND")) return   callback.SERVER_SEND_URI;
        else
            return  new Uri.Builder().scheme(callback.SCHEME).authority(callback.AUTHORITY).path(arg).build();
    }
}