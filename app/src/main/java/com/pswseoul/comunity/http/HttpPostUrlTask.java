package com.pswseoul.comunity.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import com.pswseoul.comunity.imp.AsyncTaskCompleteListener;
import com.pswseoul.util.tools;

import android.os.AsyncTask;
import android.util.Log;

public class HttpPostUrlTask extends AsyncTask<String, Void, String>
{

    private AsyncTaskCompleteListener callback;

    public HttpPostUrlTask(AsyncTaskCompleteListener callback)
    {
        this.callback = callback;
    }

    @Override
    protected String doInBackground(String... strings)
    {
        //   String url = strings[0];

        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();

        HttpPost postRequest  = new HttpPost(strings[0]);
        Log.d("REQUEST", strings[0]);

        try
        {
            StringEntity input = new StringEntity( strings[0],  HTTP.UTF_8);
            postRequest.setEntity(input);

            HttpResponse response = client.execute(postRequest);
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() == 200)
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                String line;
                while ((line = reader.readLine()) != null)
                {
                    builder.append(line).append("\n");
                }
            }
            else
            {
            }
            entity.consumeContent();

    	   /*
	       HttpResponse responsePOST = client.execute(postRequest);
	       HttpEntity resEntity = responsePOST.getEntity();

	        if (resEntity != null)
	        {
	        	String response =  EntityUtils.toString(resEntity).trim();
	        	//response = new String (response.getBytes() ,"UTF-8");
	        	//response  = tools.ltrim(response)  ;
	            Log.d("RESPONSE", response);
	            builder.append(response);
	        }
	        */
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            // handle "error connecting to the server"
        }
        return builder.toString();
    }

    @Override
    protected void onPostExecute(String result)
    {
        callback.onTaskComplete(result);
    }


}