package com.ringlerr.callplus;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;
import static com.ringlerr.callplus.DialogActivity.additional_data;
import static com.ringlerr.callplus.DialogActivity.revert_strings;

public class CallPlus {

    public static final String TAG = "Ringlerr";
    static View dialogView;
    public static int logo = 0, TYPE_CLOSED = 1, TYPE_NOT_INTERESTED = 2, TYPE_SEND_MESSAGE = 3, TYPE_APP_OPEN = 4, TYPE_REVERT = 5,
            TYPE_CANT_TALK_NOW = 6, TYPE_PROFILE_CLICK = 7, TYPE_MESSAGE_CLICK = 8, TYPE_CALL = 8, TYPE_IMPRESSION = 9, layout_call = 0, layout_text = 0;
    public static String package_name = "com.indiamart.m";
    public static String class_name = "MainActivity.class";
    public static String cant_talk_now_text = "Can't talk right now";

    static String m_phone;
    public static String type;
    static String recever_name = "";
    static String business_name = "";
    static String name;
    static String from;
    static String message_id;
    static String message_id_pull;
    static String message_id_call;
    static TextView caller_type_text;
    static AlertDialog alertList;
    static AlertDialog alert;
    static String[] call_back = { "Call back between 9AM-11am.",
            "Call back between 11am-1PM",
            "Call back between 2PM-4PM",
            "Call back between 4PM-6PM",
            "Call back After 6PM",
            "Call back tomorrow" };

    private static DialogActivity.OnRevertListener mListener;
    private static DialogActivity.OnCallListener callListener;
    public static DialogActivity.OnAppOpenListener appOpenListener;
    public static DialogActivity.OnMessageClickListener messageClickListener;
    public static DialogActivity.OnProfileClickListener profileClickListener;

    private CallPlus() {
        // no direct instantiation
    }

    public static void showDialog(final Context context, String from, String message) {

        Intent dialogIntent = new Intent(context, DialogActivity.class);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        dialogIntent.putExtra("message", message);
        dialogIntent.putExtra("from", from);
        dialogIntent.putExtra("type", "call");
        dialogIntent.putExtra("to", "to");
        dialogIntent.putExtra("caller_name", " ");
        dialogIntent.putExtra("recever_name", "mickey");
        dialogIntent.putExtra("user_image", "");
        dialogIntent.putExtra("banner_url", "");
        context.startActivity(dialogIntent);
    }

    public static void showDialog(final Context context, JSONObject json) {
        handleDataMessage(context, json);
    }

    public static void saveIntents(final Activity context, HashMap<String, String> intentHashMap){

        //convert to string using gson
        Gson gson = new Gson();
        String hashMapString = gson.toJson(intentHashMap);

        SharedPreferences sharedPref = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        sharedPref.edit().putString("saved_intent_key", hashMapString).apply();
    }

    public static HashMap<String, String> getIntents(final Context context){

        String defaultmap  = "{}";
        SharedPreferences sharedPref = context.getSharedPreferences("preference_key", Context.MODE_PRIVATE);
        String storedHashMapString = sharedPref.getString("saved_intent_key", defaultmap);
        java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>(){}.getType();

        if(storedHashMapString.equals("defaultmap")){
            return null;
        }else {
            Gson gson = new Gson();
            HashMap<String, String> intents = gson.fromJson(storedHashMapString, type);

            return intents;
        }
    }

    private static void handleDataMessage(final Context context, JSONObject json) {

        try {
            Long tsLong = System.currentTimeMillis()/1000;

            final String message = json.getString("message");
            final String key = json.getString("key");
            final String from = json.getString("phone_from");
            final String type = json.getString("type");
            final String time = tsLong+"";
            final String to = json.getString("phone_to");
            final String caller_name = json.getString("caller_name");
            final String recever_name = json.getString("recever_name");
            final String business_name = json.getString("business_name");
            final String user_image = json.getString("user_image");
            final String banner_url = json.getString("banner_url");

            String message_id = sendContext(key, from, message, type, banner_url, to, caller_name, recever_name, business_name, user_image, "", "", additional_data, false);

            //message_id = json.getString("message_id");
            final String product_name = "";
            final String cvc = json.getString("cvc");
            String product = "";
            String platform = "";
            String page = "";

            Intent dialogIntent = null;

            message_id_pull = message_id;

            if(type.equals("callback") || type.equals("reply")){
                dialogIntent = new Intent(context, CallBackDialogActivity.class);
                message_id_pull = null;
            }else{
                dialogIntent = new Intent(context, NewDialogActivity.class);
                message_id_call = message_id;
            }

            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            dialogIntent.putExtra("message", message);
            dialogIntent.putExtra("from", from);
            dialogIntent.putExtra("type", type);
            dialogIntent.putExtra("to", to);
            dialogIntent.putExtra("caller_name", caller_name);
            dialogIntent.putExtra("recever_name", recever_name);
            dialogIntent.putExtra("business_name", business_name);
            dialogIntent.putExtra("user_image", user_image);
            dialogIntent.putExtra("banner_url", banner_url);
            dialogIntent.putExtra("message_id", message_id);
            dialogIntent.putExtra("product_name", product_name);
            dialogIntent.putExtra("cvc", cvc);
            if(type.equals("insta")){
                product = json.getString("product");
                platform = json.getString("platform");
                page = json.getString("page");

                dialogIntent.putExtra("product", product);
                dialogIntent.putExtra("platform", platform);
                dialogIntent.putExtra("page", page);
            }

            Long recTime = Long.valueOf(time);
            //String timestamp = data.getString("timestamp");
            //JSONObject payload = data.getJSONObject("payload");

            if((tsLong - recTime)<600) {

                if (type.equals("call") || type.equals("image") || type.equals("video") || type.equals("slide") || type.equals("payment") || type.equals("insta") || type.equals("instaA") || type.equals("instaB")) {
                    context.startActivity(dialogIntent);
                }
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                //handlefullScreenNotification(context, from, message, type, message_id, to, caller_name, recever_name, business_name, user_image, banner_url, product_name, cvc);
                handleNotification(context, from, message, type, message_id, to, caller_name, recever_name, business_name, user_image, banner_url, product_name, cvc, product, platform, page);
                //drawApp(context, dialogIntent);
            }else{
                handleNotification(context, from, message, type, message_id, to, caller_name, recever_name, business_name, user_image, banner_url, product_name, cvc, product, platform, page);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public static void setBackdroundColor(String color){
        DialogActivity.background = color;
    }

    public static void setTitle(String title){
        DialogActivity.title = title;
    }

    public static void setMessageTextColor(String textColor){
        DialogActivity.message_text_color = textColor;
    }

    public static void setTitleColor(String titleColor){
        DialogActivity.title_text_color = titleColor;
    }

    public static void setNameTextColor(String color){
        DialogActivity.name_text_color = color;
    }

    public static void setPhoneTextColor(String color){
        DialogActivity.phone_text_color = color;
    }

    public static void setLogo(int logo){
        DialogActivity.logo = logo;
        NewDialogActivity.logo = logo;
    }

    public static void setKey(String key){
        DialogActivity.key = key;
        NewDialogActivity.key = key;
    }

    public static void setPackageName(String name){
        DialogActivity.package_name = name;
    }

    public static void setClassName(String name){
        DialogActivity.class_name = name;
    }

    public static void setAdditionalData(String additional_data){
        DialogActivity.additional_data = additional_data;
    }

    public static void setCantTalkNowText(String cant_talk_now_text){
        DialogActivity.cant_talk_now_text = cant_talk_now_text;
    }

    public static void setRevertStrings(String[] reverts){
        revert_strings = reverts;
    }

    public static void hideOnRevert(boolean isHideOnRevert){
        NewDialogActivity.hideOnRevert = isHideOnRevert;
    }

    public static void setCallLayout(int layout){
        DialogActivity.layout_call = layout;
    }

    public static void setTextLayout(int layout){
        DialogActivity.layout_text = layout;
    }

    public static void closeDialog(){
        DialogActivity.closeDialog();
    }

    private static boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        assert manager != null;
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }else return manager.getMode() == AudioManager.MODE_RINGTONE;
    }

    private static void handleNotification(Context context, String from, String message, String type, String message_id, String to,
                                           String caller_name, String recever_name, String business_name, String user_image, String banner_url, String product_name, String cvc, String product, String platform, String page) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_09";

        //when this notification is clicked and the upload is running, open the upload fragment
        final int random = new Random().nextInt((90 - 10) + 1) + 10;
        Intent notificationIntent;

        notificationIntent = new Intent(context, NewDialogActivity.class);

        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("message", message);
        notificationIntent.putExtra("from", from);
        notificationIntent.putExtra("type", type);
        notificationIntent.putExtra("message_id", message_id);

        notificationIntent.putExtra("to", to);
        notificationIntent.putExtra("caller_name", caller_name);
        notificationIntent.putExtra("recever_name", recever_name);
        notificationIntent.putExtra("business_name", business_name);
        notificationIntent.putExtra("user_image", user_image);
        notificationIntent.putExtra("banner_url", banner_url);
        notificationIntent.putExtra("product_name", product_name);
        notificationIntent.putExtra("cvc", cvc);
        if(type.equals("insta")){
            notificationIntent.putExtra("product", product);
            notificationIntent.putExtra("platform", platform);
            notificationIntent.putExtra("page", page);
        }

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(context, random, notificationIntent, 0);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("message", "Not Interested");
        broadcastIntent.putExtra("message_id", message_id);
        broadcastIntent.putExtra("to", to);
        broadcastIntent.putExtra("from", from);
        broadcastIntent.putExtra("caller_name", caller_name);
        broadcastIntent.putExtra("recever_name", recever_name);
        broadcastIntent.putExtra("business_name", business_name);
        broadcastIntent.putExtra("cvc", "");
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
                10091, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent confirmbroadcastIntent = new Intent(context, NotificationReceiver.class);
        confirmbroadcastIntent.putExtra("message", "Confirm");
        confirmbroadcastIntent.putExtra("message_id", message_id);
        confirmbroadcastIntent.putExtra("to", to);
        confirmbroadcastIntent.putExtra("from", from);
        confirmbroadcastIntent.putExtra("caller_name", caller_name);
        confirmbroadcastIntent.putExtra("recever_name", recever_name);
        confirmbroadcastIntent.putExtra("business_name", business_name);
        confirmbroadcastIntent.putExtra("cvc", "");
        PendingIntent confirmactionIntent = PendingIntent.getBroadcast(context,
                10090, confirmbroadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastBackIntent = new Intent(context, NotificationReceiver.class);
        broadcastBackIntent.putExtra("message", "Call Back");
        broadcastBackIntent.putExtra("message_id", message_id);
        broadcastBackIntent.putExtra("to", to);
        broadcastBackIntent.putExtra("from", from);
        broadcastBackIntent.putExtra("caller_name", caller_name);
        broadcastBackIntent.putExtra("recever_name", recever_name);
        broadcastBackIntent.putExtra("business_name", business_name);
        broadcastBackIntent.putExtra("cvc", cvc);
        PendingIntent callBackIntent = PendingIntent.getBroadcast(context,
                10089, broadcastBackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastOkIntent = new Intent(context, NotificationReceiver.class);
        broadcastOkIntent.putExtra("message", "Ok");
        broadcastOkIntent.putExtra("message_id", message_id);
        broadcastOkIntent.putExtra("to", to);
        broadcastOkIntent.putExtra("from", from);
        broadcastOkIntent.putExtra("caller_name", caller_name);
        broadcastOkIntent.putExtra("recever_name", recever_name);
        broadcastOkIntent.putExtra("business_name", business_name);
        broadcastOkIntent.putExtra("cvc", cvc);
        PendingIntent okIntent = PendingIntent.getBroadcast(context,
                10088, broadcastOkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Notification", NotificationManager.IMPORTANCE_HIGH);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        String appName = context.getResources().getString(com.ringlerr.callplus.R.string.app_name);

        if(cvc.equals("")) {
            notificationBuilder.setAutoCancel(true)
                    .setSmallIcon(com.ringlerr.callplus.R.drawable.ic_message_black_24dp)
                    .setContentText(message)
                    .setContentIntent(intent)
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                    .setPriority(Notification.PRIORITY_MAX) //Important for heads-up notification
                    .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Reply", intent);
        }

        if(type.equals("call") || type.equals("video") || type.equals("image") || type.equals("payment")){
            notificationBuilder.setContentTitle(from + "("+caller_name+") is calling you")
                    .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Call Back", callBackIntent)
                    .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Not Interested", actionIntent);

        }else if(type.equals("slide")){
            notificationBuilder.setContentTitle("Call from "+from + "("+caller_name+")")
                    .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Confirm", confirmactionIntent);
        }else if(type.equals("instaA") || type.equals("instaB") || type.equals("insta")){
            notificationBuilder.setContentTitle("Call from "+from + "("+caller_name+")");
                    //.addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Confirm", confirmactionIntent);
        }else {
            notificationBuilder.setContentTitle("Message from "+from + "("+caller_name+")");
            //.addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Ok", okIntent);
        }

        notificationManager.notify(/*notification id*/random, notificationBuilder.build());
    }


    private static void handlefullScreenNotification(Context context, String from, String message, String type, String message_id, String to,
                                           String caller_name, String recever_name, String business_name, String user_image, String banner_url, String product_name, String cvc) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_05";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Ringlerr Notification", NotificationManager.IMPORTANCE_LOW);

            // Configure the notification channel.
            notificationChannel.setDescription("Ringlerr Notification");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        //when this notification is clicked and the upload is running, open the upload fragment
        final int random = new Random().nextInt((90 - 10) + 1) + 10;
        Intent notificationIntent = new Intent(context, DialogActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.putExtra("message", message);
        notificationIntent.putExtra("from", from);
        notificationIntent.putExtra("type", type);
        notificationIntent.putExtra("message_id", message_id);

        notificationIntent.putExtra("to", to);
        notificationIntent.putExtra("caller_name", caller_name);
        notificationIntent.putExtra("recever_name", recever_name);
        notificationIntent.putExtra("business_name", business_name);
        notificationIntent.putExtra("user_image", user_image);
        notificationIntent.putExtra("banner_url", banner_url);
        notificationIntent.putExtra("product_name", product_name);
        notificationIntent.putExtra("cvc", cvc);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );

        //Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.logo_main);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        // set intent so it does not start a new activity
        PendingIntent intent = PendingIntent.getActivity(context, random, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        notificationBuilder.setContentIntent(intent);

        Intent broadcastIntent = new Intent(context, NotificationReceiver.class);
        broadcastIntent.putExtra("message", "Not Interested");
        broadcastIntent.putExtra("message_id", message_id);
        broadcastIntent.putExtra("to", to);
        broadcastIntent.putExtra("from", from);
        broadcastIntent.putExtra("caller_name", caller_name);
        broadcastIntent.putExtra("recever_name", recever_name);
        broadcastIntent.putExtra("business_name", business_name);
        broadcastIntent.putExtra("cvc", cvc);
        PendingIntent actionIntent = PendingIntent.getBroadcast(context,
                10090, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Intent broadcastBackIntent = new Intent(context, NotificationReceiver.class);
        broadcastBackIntent.putExtra("message", "Call Back");
        broadcastBackIntent.putExtra("message_id", message_id);
        broadcastBackIntent.putExtra("to", to);
        broadcastBackIntent.putExtra("from", from);
        broadcastBackIntent.putExtra("caller_name", caller_name);
        broadcastBackIntent.putExtra("recever_name", recever_name);
        broadcastBackIntent.putExtra("business_name", business_name);
        broadcastBackIntent.putExtra("cvc", cvc);
        PendingIntent callBackIntent = PendingIntent.getBroadcast(context,
                10089, broadcastBackIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent broadcastOkIntent = new Intent(context, NotificationReceiver.class);
        broadcastOkIntent.putExtra("message", "Ok");
        broadcastOkIntent.putExtra("message_id", message_id);
        broadcastOkIntent.putExtra("to", to);
        broadcastOkIntent.putExtra("from", from);
        broadcastOkIntent.putExtra("caller_name", caller_name);
        broadcastOkIntent.putExtra("recever_name", recever_name);
        broadcastOkIntent.putExtra("business_name", business_name);
        broadcastOkIntent.putExtra("cvc", cvc);
        PendingIntent okIntent = PendingIntent.getBroadcast(context,
                10088, broadcastOkIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String appName = context.getResources().getString(R.string.app_name);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setTicker(appName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(intent,true)
                .setContentTitle(from + "("+caller_name+") is calling you")
                .setContentText(message)
               // .setOngoing(true)
                .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Reply", intent);


        if(type.equals("revert")){
            notificationBuilder.setContentTitle("Message from "+from + "("+caller_name+")")
                    .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Ok", okIntent);
        }else {
            if(cvc.equals("")) {
                notificationBuilder.setContentTitle(from + "(" + caller_name + ") is calling you")
                        .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Call Back", callBackIntent)
                        .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Not Interested", actionIntent);
            }else{
                notificationBuilder.setContentTitle(from + "(" + caller_name + ") is calling you with CVC "+cvc)
                        .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Call Back", callBackIntent)
                        .addAction(com.ringlerr.callplus.R.drawable.ic_message_black_24dp, "Not Interested", actionIntent);
            }
        }


        notificationManager.notify(/*notification id*/random, notificationBuilder.build());
    }

    public static boolean updateToken(final String key, final String phone_no, final String token){

        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                String data = null;
                try {
                    data = URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode(key, "UTF-8");
                    data += "&" + URLEncoder.encode("phone_no", "UTF-8") + "="
                            + URLEncoder.encode(phone_no, "UTF-8");

                    data += "&" + URLEncoder.encode("token", "UTF-8")
                            + "=" + URLEncoder.encode(token, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    //e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

                String urlImage = "https://api.ringlerr.com/v4/token.php";
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        Log.d(TAG, "responseCode : "+responseCode);
                    }
                }catch (IOException e) {
                    //e.printStackTrace();
                    //Log.e(TAG, e.getMessage());
                }catch(Exception e){
                    //e.printStackTrace();
                    //Log.e(TAG, e.getMessage());
                }finally {
                    if(connection != null) // Make sure the connection is not null.
                        connection.disconnect();
                }
                return true;
            }

            protected void onPostExecute(Boolean result) {

            }


        }.execute();

        return true;
    }

    public static String sendContext(final String key, final String phone_number, final String message, final String type, final String image_url, final String phone_from, final String caller_name, final String recever_name, final String business_name, final String user_image, final String serverToken, final String product_name, final String additional_data, boolean send_cvc){

        final String[] message_id_pull = {""};

        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                // Create data variable for sent values to server

                String data = null;
                try {
                    data = URLEncoder.encode("message", "UTF-8")
                            + "=" + URLEncoder.encode(message, "UTF-8");
                    data += "&" + URLEncoder.encode("phone_from", "UTF-8") + "="
                            + URLEncoder.encode(phone_from, "UTF-8");

                    data += "&" + URLEncoder.encode("phone_to", "UTF-8")
                            + "=" + URLEncoder.encode(phone_number, "UTF-8");

                    data += "&" + URLEncoder.encode("type", "UTF-8")
                            + "=" + URLEncoder.encode(type, "UTF-8");

                    data += "&" + URLEncoder.encode("time", "UTF-8") + "="
                            + URLEncoder.encode(tsLong+"", "UTF-8");

                    data += "&" + URLEncoder.encode("caller_name", "UTF-8")
                            + "=" + URLEncoder.encode(caller_name, "UTF-8");

                    data += "&" + URLEncoder.encode("recever_name", "UTF-8")
                            + "=" + URLEncoder.encode(recever_name, "UTF-8");

                    data += "&" + URLEncoder.encode("business_name", "UTF-8")
                            + "=" + URLEncoder.encode(business_name, "UTF-8");

                    data += "&" + URLEncoder.encode("user_image", "UTF-8") + "="
                            + URLEncoder.encode(user_image, "UTF-8");

                    data += "&" + URLEncoder.encode("banner_url", "UTF-8")
                            + "=" + URLEncoder.encode(image_url, "UTF-8");

                    data += "&" + URLEncoder.encode("cvc", "UTF-8")
                                                + "=" + URLEncoder.encode("", "UTF-8");

                    data += "&" + URLEncoder.encode("serverToken", "UTF-8")
                            + "=" + URLEncoder.encode(serverToken, "UTF-8");

                    data += "&" + URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode(key, "UTF-8");

                    data += "&" + URLEncoder.encode("product_name", "UTF-8")
                            + "=" + URLEncoder.encode(product_name, "UTF-8");

                    data += "&" + URLEncoder.encode("additional_data", "UTF-8")
                            + "=" + URLEncoder.encode(additional_data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    //e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }

                Bitmap bmp = null;
                String urlImage = "https://api.ringlerr.com/v4/housing.php";
                HttpURLConnection connection = null;
                try {
                    connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                    wr.write( data );
                    wr.flush();

                    int responseCode=connection.getResponseCode();
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        Log.d(TAG, "responseCode : "+responseCode);
                        String resMessgae = connection.getResponseMessage();
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String output = br.readLine();
                        message_id_pull[0] = output;
                    }
                }catch (IOException e) {
                    //e.printStackTrace();
                    //Log.e(TAG, e.getMessage());
                }catch(Exception e){
                    //e.printStackTrace();
                   // Log.e(TAG, e.getMessage());
                }finally {
                    if(connection != null) // Make sure the connection is not null.
                        connection.disconnect();
                }
                return message_id_pull[0];
            }

            protected void onPostExecute(String result) {

                //Add image to ImageView


            }


        }.execute();

        return message_id_pull[0];
    }

    // Function to generate random alpha-numeric password of specific length
    public static String generateRandomPassword(int len)
    {
        // ASCII range - alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789#@*()%$";

        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        // each iteration of loop choose a character randomly from the given ASCII range
        // and append it to StringBuilder instance

        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }

        return sb.toString();
    }

    public static void drawApp(final Context context, Intent intent){

        final String message = intent.getExtras().getString("message");
        from = intent.getExtras().getString("from");
        type = intent.getExtras().getString("type");
        name = intent.getExtras().getString("caller_name");
        m_phone = intent.getExtras().getString("to");
        recever_name = intent.getExtras().getString("recever_name");
        business_name = intent.getExtras().getString("business_name");
        message_id = intent.getExtras().getString("message_id");
        final String urlImage = intent.getExtras().getString("user_image");
        callBack(from);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);


        dialogView = inflater.inflate(R.layout.dialog_info, null);
        final TextView call_stat = dialogView.findViewById(R.id.call_stat);
        if(type.equals("revert")){
            call_stat.setText("Message From");
        }

        if(type.equals("call")){
            call_stat.setText("Incoming Call From");
        }

        builder.setView(dialogView);
        alert = builder.create();
        alert.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);

        alert.setCanceledOnTouchOutside(false);
        alert.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alert.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.y = 10;
        lp.x = 10;

        window.setAttributes(lp);

        final TextView messageView = dialogView.findViewById(R.id.messageView);
        final TextView messageViewRevert = dialogView.findViewById(R.id.messageViewRevert);
        //final EditText messageTypeView = dialogView.findViewById(R.id.messageTypeView);
        TextView fromView = dialogView.findViewById(R.id.phone);
        //TextView caller_name = dialogView.findViewById(R.id.caller_name);
        //LinearLayout layout_body = dialogView.findViewById(R.id.layout_body);
        TextView pop_title = dialogView.findViewById(R.id.title);
        ImageView main_logo = dialogView.findViewById(R.id.imageView2);
        ImageView userImageView = dialogView.findViewById(R.id.image);
        TextView bt_open = dialogView.findViewById(R.id.bt_open);
        //caller_type_text = dialogView.findViewById(R.id.caller_type);

        //pop_title.setTextColor(Color.parseColor(title_text_color));
        //caller_name.setTextColor(Color.parseColor(name_text_color));
        //fromView.setTextColor(Color.parseColor(phone_text_color));
        //messageView.setTextColor(Color.parseColor(message_text_color));

        if(logo != 0) {
            main_logo.setImageResource(logo);
        }

        if(!urlImage.equals("")) {
            getImage(urlImage, userImageView);
        }

        if(null != message) {
            messageView.setText(message);
        }

//        if(null != messageTypeView) {
//            messageTypeView.setSelection(messageTypeView.getText().length());
//        }

        if(null != business_name) {
            fromView.setText(business_name);
        }

        if(null != name) {
            String cc_name = name+" "+from+" ";
            //caller_name.setText(cc_name);
        }

//        if(null != product_name) {
//            if(!product_name.equals("")) {
//                //TextView message_title_text = dialogView.findViewById(R.id.message_title_text);
//                String first = "MESSAGE FOR ";
//                String next = product_name;
//                message_title_text.setText(first + next + " :", TextView.BufferType.SPANNABLE);
//                Spannable s = (Spannable) message_title_text.getText();
//                int start = first.length();
//                int end = start + next.length();
//                s.setSpan(new ForegroundColorSpan(0xFF3F51B5), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }
//        }

        if(null != name) {
            pop_title.setText(name);
        }

        ((ImageButton) dialogView.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                alert.dismiss();
                if(mListener!=null)
                    mListener.onRevert("closed", TYPE_CLOSED, from);
                closeDialog();
            }
        });

        ((ImageView) dialogView.findViewById(R.id.nt_interested)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertBack("Not Interested", from);
                if(mListener!=null)
                    mListener.onRevert("Not Interested", TYPE_NOT_INTERESTED, from);
                clickRegester(from, TYPE_NOT_INTERESTED);
                messageViewRevert.setVisibility(View.VISIBLE);
                messageViewRevert.setText("Not Interested");
                Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();
                //context.finish();
            }
        });

//        ((ImageView) dialogView.findViewById(R.id.bt_send)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String message = messageTypeView.getText().toString();
//                revertBack(message, from);
//                if(mListener!=null)
//                    mListener.onRevert(message, TYPE_SEND_MESSAGE);
//                    clickRegester(from, TYPE_SEND_MESSAGE);
//                    messageViewRevert.setVisibility(View.VISIBLE);
//                    messageViewRevert.setText(message);
//
//                //context.finish();
//            }
//        });

        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appOpenListener!=null) {
                    boolean isPermissionAvailable = false; //checkCallPermission();
                    appOpenListener.onOpen("App Open", isPermissionAvailable, TYPE_APP_OPEN, context);
                    clickRegester(from, TYPE_APP_OPEN);
                }

                //context.finish();
//                try {
//                    Intent intent = getPackageManager().getLaunchIntentForPackage(package_name);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//
//                }catch (Exception e) {
//                    System.out.println("Error " + e.getMessage());
//                }
            }
        });

        final ImageView bt_revert = dialogView.findViewById(R.id.bt_revert);
        final TextView bt_revert_txt = dialogView.findViewById(R.id.bt_revert_txt);

        bt_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                showLists(context, revert_strings, from, messageViewRevert);
            }
        });

        bt_revert_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                showLists(context, revert_strings, from, messageViewRevert);
            }
        });

//        profile_section = dialogView.findViewById(R.id.profile_section);
//        profile_section.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(profileClickListener!=null) {
//                    boolean isPermissionAvailable = checkCallPermission();
//                    profileClickListener.onProfileClick("profile click", isPermissionAvailable, TYPE_PROFILE_CLICK, context);
//                    clickRegester(from, TYPE_PROFILE_CLICK);
//                }
//            }
//        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageClickListener!=null) {
                    boolean isPermissionAvailable = false; //checkCallPermission();
                    messageClickListener.onMessageClick("message click", isPermissionAvailable, TYPE_PROFILE_CLICK, context);
                    clickRegester(from, TYPE_PROFILE_CLICK);
                }
            }
        });


//        final TextView cant_talk_now = dialogView.findViewById(R.id.cant_talk_now);
//        cant_talk_now.setText(cant_talk_now_text);
//        cant_talk_now.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                revertBack(cant_talk_now_text, from);
//                if(mListener!=null)
//                    mListener.onRevert(cant_talk_now_text, TYPE_CANT_TALK_NOW);
//                    clickRegester(from, TYPE_CANT_TALK_NOW);
//            }
//        });

        final ImageView callback = dialogView.findViewById(R.id.bt_callback);
        callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call number
                //call_number(from);
                showLists(context, call_back, from, messageViewRevert);

            }
        });
        //end here

        if(!type.equals("revert")) {
            final Handler handler = new Handler();
            final int delay = 3000; //milliseconds
            final boolean[] running = {true};
            final boolean[] ringing = {false};

            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    boolean isInCall = isCallActive(context);
                    if (isInCall) {
                        //call_stat.setText("Received Call From");
                        running[0] = false;
                        pickup();
                    }

                    boolean isCallRinging = isCallRinging(context);
                    if (isCallRinging) {
                        ringing[0] = true;
                    }

                    boolean isCallIdle = isCallIdle(context);
                    if (isCallIdle && running[0] && ringing[0]) {
                        //call_stat.setText("Missed Call From");
                        running[0] = false;
                    }
                    if (running[0]) {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);
        }

    }

    @SuppressLint("StaticFieldLeak")
    private static void callBack(final String from) {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                // Create data variable for sent values to server

                String data = null;
                try {
                    data = URLEncoder.encode("phone_from", "UTF-8")
                            + "=" + URLEncoder.encode(from, "UTF-8");

                    data += "&" + URLEncoder.encode("user", "UTF-8")
                            + "=" + URLEncoder.encode(m_phone, "UTF-8");

                    data += "&" + URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode(DialogActivity.key, "UTF-8");

                    data += "&" + URLEncoder.encode("message_id", "UTF-8")
                            + "=" + URLEncoder.encode(message_id, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = null;
                String urlImage = "https://api.ringlerr.com/v4/callback.php";
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

                //Add image to ImageView

            }


        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private static void revertBack(final String mgs, final String from) {
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
                            + "=" + URLEncoder.encode("revert", "UTF-8");

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

    @SuppressLint("StaticFieldLeak")
    private static void clickRegester(final String from, final int type) {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                // Create data variable for sent values to server

                String data = null;
                try {
                    data = URLEncoder.encode("phone_from", "UTF-8")
                            + "=" + URLEncoder.encode(from, "UTF-8");

                    data += "&" + URLEncoder.encode("user", "UTF-8")
                            + "=" + URLEncoder.encode(m_phone, "UTF-8");

                    data += "&" + URLEncoder.encode("key", "UTF-8")
                            + "=" + URLEncoder.encode(DialogActivity.key, "UTF-8");

                    data += "&" + URLEncoder.encode("type", "UTF-8")
                            + "=" + URLEncoder.encode(type+"", "UTF-8");

                    data += "&" + URLEncoder.encode("message_id", "UTF-8")
                            + "=" + URLEncoder.encode(message_id, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                Bitmap bmp = null;
                String urlImage = "https://api.ringlerr.com/v4/clickRegester.php";
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

                //Add image to ImageView

            }


        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void pickup() {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                if(null != message_id_call) {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    // Create data variable for sent values to server

                    String data = null;
                    try {
                        data = URLEncoder.encode("pickup", "UTF-8")
                                + "=" + URLEncoder.encode("1", "UTF-8");

                        data += "&" + URLEncoder.encode("message_id", "UTF-8")
                                + "=" + URLEncoder.encode(message_id_call, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Bitmap bmp = null;
                    String urlImage = "https://api.ringlerr.com/v4/pickup.php";
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) new URL(urlImage).openConnection();
                        connection.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                        wr.write(data);
                        wr.flush();

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) // Make sure the connection is not null.
                            connection.disconnect();
                    }
                }
                return true;
            }

            protected void onPostExecute(Boolean result) {

                //Add image to ImageView

            }


        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public static void pullIntent(final String from, final String to) {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                if(null == message_id_pull) {
                    Log.d("ringlib", "inside condition");

                    String data = null;
                    try {
                        data = URLEncoder.encode("phone_from", "UTF-8")
                                + "=" + URLEncoder.encode(from, "UTF-8");

                        data += "&" + URLEncoder.encode("phone_to", "UTF-8")
                                + "=" + URLEncoder.encode(to, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    Bitmap bmp = null;
                    String urlImage = "https://api.ringlerr.com/v4/pullintent.php";
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) new URL(urlImage).openConnection();
                        connection.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                        wr.write(data);
                        wr.flush();

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpsURLConnection.HTTP_OK) {

                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (connection != null) // Make sure the connection is not null.
                            connection.disconnect();
                    }
                }

                message_id_pull = null;

                return true;
            }

            protected void onPostExecute(Boolean result) {

                //Add image to ImageView

            }


        }.execute();
    }

    private static void showLists(final Context context, final String[] stringArray, final String from, final TextView messageViewRevert){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.lists, null);

        builder.setView(dialogView);
        alertList = builder.create();
        alertList.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alertList.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else {
            alertList.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        }
        //alert.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);

        alertList.setCanceledOnTouchOutside(true);
        alertList.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertList.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.CENTER);
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = 550;  //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.activity_listview, stringArray);

        ListView listView = dialogView.findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String mgs = stringArray[arg2].toString();
                revertBack(mgs, from);
                if(mListener!=null)
                    mListener.onRevert(mgs,  TYPE_REVERT, from);
                clickRegester(from, TYPE_REVERT);
                messageViewRevert.setVisibility(View.VISIBLE);
                messageViewRevert.setText(mgs);
                Toast.makeText(context, "Message Sent", Toast.LENGTH_LONG).show();

                alertList.cancel();
                alertList.dismiss();
            }

        });

        ((ImageButton) dialogView.findViewById(R.id.bt_close_list)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null != alertList)
                    alertList.cancel();
                alertList.dismiss();

            }
        });

    }

    public interface OnRevertListener {
        void onRevert(String message, int type, String phone);
    }

    public static void setOnRevertListener(OnRevertListener eventListener) {
        mListener = (DialogActivity.OnRevertListener) eventListener;
    }

    public interface OnCallListener {
        void onRevert(String number, boolean permission, Context context);
    }

    public static void setOnCallListener(OnCallListener eventListener) {
        callListener = (DialogActivity.OnCallListener) eventListener;
    }

    public interface OnAppOpenListener {
        void onOpen(String number, boolean permission, int type, Context context);
    }

    public static void setAppOpenListener(OnAppOpenListener eventListener) {
        appOpenListener = (DialogActivity.OnAppOpenListener) eventListener;
    }

    public interface OnProfileClickListener {
        void onProfileClick(String number, boolean permission, int type, Context context);
    }

    public static void setProfileClickListener(OnProfileClickListener eventListener) {
        profileClickListener = (DialogActivity.OnProfileClickListener) eventListener;
    }

    public interface OnMessageClickListener {
        void onMessageClick(String number, boolean permission, int type, Context context);
    }

    public static void setMessageClickListener(OnMessageClickListener eventListener) {
        messageClickListener = (DialogActivity.OnMessageClickListener) eventListener;
    }

    public static boolean isCallRinging(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_RINGTONE){
            return true;
        }else{
            return false;
        }
    }

    public static boolean isCallIdle(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_NORMAL){
            return true;
        }else{
            return false;
        }
    }

    public static void getImage(final String urlImage, final ImageView imageView){
        new AsyncTask<String, Integer, Drawable>(){

            @Override
            protected Drawable doInBackground(String... strings) {
                Bitmap bmp = null;
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(urlImage).openConnection();
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    bmp = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new BitmapDrawable(bmp);
            }

            protected void onPostExecute(Drawable result) {

                //Add image to ImageView
                imageView.setImageDrawable(result);

            }


        }.execute();
    }
}
