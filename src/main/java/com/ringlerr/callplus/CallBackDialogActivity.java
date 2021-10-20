package com.ringlerr.callplus;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class CallBackDialogActivity extends AppCompatActivity {

    public static int logo = 0, TYPE_CLOSED = 1, TYPE_NOT_INTERESTED = 2, TYPE_SEND_MESSAGE = 3, TYPE_APP_OPEN = 4, TYPE_REVERT = 5,
            TYPE_CANT_TALK_NOW = 6, TYPE_PROFILE_CLICK = 7, TYPE_MESSAGE_CLICK = 8, TYPE_CALL = 8, TYPE_IMPRESSION = 9, layout_call = 0, layout_text = 0;
    String from;
    public static String type;
    static Dialog dialog;
    String recever_name = "";
    String business_name = "";
    String name;
    String message_id;
    String m_phone;
    AlertDialog alert;
    public static String key = "12a4f62af7991d51ba9bef728e260cd5e5ddacb2b3a60fa33b7cc526f26edb";
    public static boolean hideOnRevert = false;
    static View dialogView;
    AlertDialog alertList;
    String[] call_back = { "Call back between 11AM-12Noon.",
            "Call back between 12Noon-1PM",
            "Call back between 1PM-2PM",
            "Call back between 2PM-3PM",
            "Call back between 3PM-4PM",
            "Call back between 4PM-5PM",
            "Call back After 6PM", };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        this.getWindow().setLayout( WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

        //setContentView(R.layout.activity_new_dialog);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(CallBackDialogActivity.this)) {
            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            //setContentView(R.layout.activity_dialog);
            Intent intent = getIntent();
            mainRun(intent);
        }else{
            Intent intent = getIntent();
            drawApp(intent);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void drawApp(Intent intent){

        final String message = intent.getExtras().getString("message");
        from = intent.getExtras().getString("from");
        type = intent.getExtras().getString("type");
        name = intent.getExtras().getString("caller_name");
        m_phone = intent.getExtras().getString("to");
        recever_name = intent.getExtras().getString("recever_name");
        business_name = intent.getExtras().getString("business_name");
        message_id = intent.getExtras().getString("message_id");
        final String urlImage = intent.getExtras().getString("user_image");
        final String cvc = intent.getExtras().getString("cvc");
        callBack(from);

        AlertDialog.Builder builder = new AlertDialog.Builder(CallBackDialogActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CallBackDialogActivity.this);


        dialogView = inflater.inflate(R.layout.dialog_info_callback, null);

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
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        final Window window = alert.getWindow();
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

        LinearLayout main_layout = dialogView.findViewById(R.id.main_layout);

        final TextView messageView = dialogView.findViewById(R.id.messageView);
        final TextView messageViewRevert = dialogView.findViewById(R.id.messageViewRevert);
        final EditText messageTypeView = dialogView.findViewById(R.id.messageTypeView);
        final TextView my_message = dialogView.findViewById(R.id.my_message);
        final TextView bt_open = dialogView.findViewById(R.id.bt_open);
        final TextView title = dialogView.findViewById(R.id.title);
        final TextView caller_message = dialogView.findViewById(R.id.caller_message);
        final TextView cvc_view = dialogView.findViewById(R.id.cvc);
        final LinearLayout cta_layout = dialogView.findViewById(R.id.cta_layout);
        final TextView call_stat = dialogView.findViewById(R.id.call_stat);
        if(type.equals("reply")){
            caller_message.setText(name+"'s Message");
            cta_layout.setVisibility(View.GONE);
        }else{
            call_stat.setText("Incoming Call From "+name);
            cta_layout.setVisibility(View.VISIBLE);
        }

        title.setText(name);

        if(null != message) {
            messageView.setText(message);
        }

        if(null != cvc && !cvc.equals("")){
            cvc_view.setText("CV Code : "+cvc);
        }

        bt_open.setText(from);

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            int orgX, orgY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.v("Motion", String.valueOf(motionEvent.getActionMasked()));
                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_MOVE:
                        Log.v("Motion", "rawY : "+(int) motionEvent.getRawY());
                        int startX = (int) motionEvent.getRawY() - (orgY+320);
                        lp.y = startX;
                        lp.x = (int) motionEvent.getRawX() - (orgX+15);
                        window.setAttributes(lp);
                        break;

                    case MotionEvent.ACTION_DOWN:
                        orgX = (int) motionEvent.getX();
                        orgY = (int) motionEvent.getY();
                        Log.v("Motion", "orgX : "+orgX+" orgY : "+orgY);
                        break;

                }
                return true;
            }
        });

        //calender
        ImageView calender = dialogView.findViewById(R.id.bt_send);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageTypeView.getText().toString();
                revertBack(message, from);

                alert.cancel();
                alert.dismiss();
                CallBackDialogActivity.this.finish();
            }
        });

        //sms
        ImageView sms_image = dialogView.findViewById(R.id.bt_callback);
        sms_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:"+from)); // This ensures only SMS apps respond
                intent.putExtra("sms_body", " ");
                startActivity(intent);

                alert.cancel();
                alert.dismiss();

                CallBackDialogActivity.this.finish();
            }
        });

        ((ImageButton) dialogView.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                alert.dismiss();
//                if(mListener!=null)
//                    mListener.onRevert("closed", TYPE_CLOSED, from);
                CallBackDialogActivity.this.finish();
            }
        });

        if(!type.equals("revert")) {
            final Handler handler = new Handler();
            final int delay = 3000; //milliseconds
            //final TextView call_stat = dialogView.findViewById(R.id.call_stat);
            final boolean[] running = {true};
            final boolean[] ringing = {false};

            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    boolean isInCall = isCallActive(CallBackDialogActivity.this);
                    if (isInCall) {
                        //call_stat.setText("Received Call From");
                        running[0] = false;
                    }

                    boolean isCallRinging = isCallRinging(CallBackDialogActivity.this);
                    if (isCallRinging) {
                        ringing[0] = true;
                    }

                    boolean isCallIdle = isCallIdle(CallBackDialogActivity.this);
                    if (isCallIdle && running[0] && ringing[0]) {
                        //call_stat.setText("Missed Call From");
                        running[0] = false;
                    }
                    if (running[0]) {
                        handler.postDelayed(this, delay);
                    }
                }
            }, delay);
        }else{
            //final TextView call_stat = dialogView.findViewById(R.id.call_stat);
            final TextView phone = dialogView.findViewById(R.id.phone);
            //call_stat.setText("Message From "+name);
            phone.setVisibility(View.GONE);
        }

    }

    private void mainRun(Intent intent){

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

        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before

        dialog.setContentView(R.layout.dialog_info_callback);

        //not interested
        final TextView messageView = dialog.findViewById(R.id.messageView);
        final TextView messageViewRevert = dialog.findViewById(R.id.messageViewRevert);
        final EditText messageTypeView = dialog.findViewById(R.id.messageTypeView);
        final TextView my_message = dialog.findViewById(R.id.my_message);
        final TextView bt_open = dialog.findViewById(R.id.bt_open);
        final TextView caller_message = dialog.findViewById(R.id.caller_message);
        final TextView call_stat = dialog.findViewById(R.id.call_stat);
        final LinearLayout cta_layout = dialog.findViewById(R.id.cta_layout);
        if(type.equals("reply")){
            caller_message.setText(name+"'s Message");
            cta_layout.setVisibility(View.GONE);
        }else{
            call_stat.setText("Incoming Call From "+name);
            cta_layout.setVisibility(View.VISIBLE);
        }

        if(null != message) {
            messageView.setText(message);
        }

        bt_open.setText(from);

        //calender
        ImageView calender = dialog.findViewById(R.id.bt_send);
        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageTypeView.getText().toString();
                revertBack(message, from);

                alert.cancel();
                alert.dismiss();
                CallBackDialogActivity.this.finish();
            }
        });

        //sms
        ImageView sms_image = dialog.findViewById(R.id.bt_callback);
        sms_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:"+from)); // This ensures only SMS apps respond
                intent.putExtra("sms_body", " ");
                startActivity(intent);

                dialog.cancel();
                dialog.dismiss();
                CallBackDialogActivity.this.finish();
            }
        });

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
//                if(mListener!=null)
//                    mListener.onRevert("closed", TYPE_CLOSED, from);
                CallBackDialogActivity.this.finish();
            }
        });

        if(!type.equals("revert_main")) {
            final Handler handler = new Handler();
            final int delay = 3000; //milliseconds
            final boolean[] running = {true};
            final boolean[] ringing = {false};

            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    boolean isInCall = isCallActive(CallBackDialogActivity.this);
                    if (isInCall) {
                        //call_stat.setText("Received Call From");
                        running[0] = false;
                    }

                    boolean isCallRinging = isCallRinging(CallBackDialogActivity.this);
                    if (isCallRinging) {
                        ringing[0] = true;
                    }

                    boolean isCallIdle = isCallIdle(CallBackDialogActivity.this);
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

    private void showLists(final String[] stringArray, final String from, final TextView messageViewRevert, final TextView my_message){

        AlertDialog.Builder builder = new AlertDialog.Builder(CallBackDialogActivity.this);
        LayoutInflater inflater = LayoutInflater.from(CallBackDialogActivity.this);
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

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_listview, stringArray);

        ListView listView = dialogView.findViewById(R.id.mobile_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                String mgs = stringArray[arg2].toString();
                revertBack(mgs, from);
                clickRegester(from, TYPE_REVERT);
                my_message.setVisibility(View.VISIBLE);
                messageViewRevert.setVisibility(View.VISIBLE);
                messageViewRevert.setText(mgs);

                Toast.makeText(CallBackDialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();

                alertList.cancel();
                alertList.dismiss();
                CallBackDialogActivity.this.finish();
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

    @SuppressLint("StaticFieldLeak")
    private void revertBack(final String mgs, final String from) {
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
                            + "=" + URLEncoder.encode(key, "UTF-8");

                    data += "&" + URLEncoder.encode("message_id", "UTF-8")
                            + "=" + URLEncoder.encode(message_id, "UTF-8");

                    data += "&" + URLEncoder.encode("additional_data", "UTF-8")
                            + "=" + URLEncoder.encode("", "UTF-8");
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

                //Add image to ImageView

            }


        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void clickRegester(final String from, final int type) {
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
                            + "=" + URLEncoder.encode(key, "UTF-8");

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

        if(hideOnRevert) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != alert) {
                        alert.cancel();
                        alert.dismiss();
                    }

                    if (null != dialog) {
                        dialog.cancel();
                        dialog.dismiss();
                    }


                    CallBackDialogActivity.this.finish();
                }
            }, 3000);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void callBack(final String from) {
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
                            + "=" + URLEncoder.encode(key, "UTF-8");

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

    public boolean isCallActive(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_IN_CALL){
            return true;
        }else{
            return false;
        }
    }

    public boolean isCallRinging(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_RINGTONE){
            return true;
        }else{
            return false;
        }
    }

    public boolean isCallIdle(Context context){
        AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        if(manager.getMode()==AudioManager.MODE_NORMAL){
            return true;
        }else{
            return false;
        }
    }
}
