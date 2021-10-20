package com.ringlerr.callplus;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import static com.ringlerr.callplus.DialogActivity.additional_data;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String message_id = intent.getStringExtra("message_id");
        String m_phone = intent.getStringExtra("to");
        String from = intent.getStringExtra("from");
        String caller_name = intent.getStringExtra("caller_name");
        String recever_name = intent.getStringExtra("recever_name");
        String business_name = intent.getStringExtra("business_name");

        revertBack(message, from, message_id, m_phone, caller_name, recever_name, business_name);
    }

    @SuppressLint("StaticFieldLeak")
    private void revertBack(final String mgs, final String from, final String message_id, final String m_phone,
                            final String name, final String recever_name, final String  business_name) {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                // Create data variable for sent values to server

                String data = null;
                try {
                    data = URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode(mgs, "UTF-8");
                    data += "&" + URLEncoder.encode("phone_from", "UTF-8") + "="
                            + URLEncoder.encode(m_phone, "UTF-8");

                    data += "&" + URLEncoder.encode("phone_to", "UTF-8")
                            + "=" + URLEncoder.encode(from, "UTF-8");

                    data += "&" + URLEncoder.encode("type", "UTF-8")
                            + "=" + URLEncoder.encode("reply", "UTF-8");

                    data += "&" + URLEncoder.encode("time", "UTF-8") + "="
                            + URLEncoder.encode(tsLong+"", "UTF-8");

                    data += "&" + URLEncoder.encode("caller_name", "UTF-8")
                            + "=" + URLEncoder.encode(recever_name, "UTF-8");

                    data += "&" + URLEncoder.encode("recever_name", "UTF-8")
                            + "=" + URLEncoder.encode(name, "UTF-8");

                    data += "&" + URLEncoder.encode("business_name", "UTF-8")
                            + "=" + URLEncoder.encode(business_name, "UTF-8");

                    data += "&" + URLEncoder.encode("user_image", "UTF-8") + "="
                            + URLEncoder.encode("", "UTF-8");

                    data += "&" + URLEncoder.encode("banner_url", "UTF-8")
                            + "=" + URLEncoder.encode("", "UTF-8");

                    data += "&" + URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode(DialogActivity.key, "UTF-8");

                    data += "&" + URLEncoder.encode("message_id", "UTF-8")
                            + "=" + URLEncoder.encode(message_id, "UTF-8");

                    data += "&" + URLEncoder.encode("additional_data", "UTF-8")
                            + "=" + URLEncoder.encode(additional_data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = null;
                String urlImage = "https://api.ringlerr.com/v4/notifications.php";
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }catch(Exception e){
                    e.printStackTrace();
                }finally {
                    if(connection != null) // Make sure the connection is not null.
                        connection.disconnect();
                }
                return true;
            }

            protected void onPostExecute(Boolean result) {

            }
        }.execute();
    }

}
