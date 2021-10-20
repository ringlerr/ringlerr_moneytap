package com.ringlerr.callplus;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.graphics.Bitmap;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import android.graphics.BitmapFactory;
import java.io.IOException;
import android.graphics.drawable.BitmapDrawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URLEncoder;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

public class DialogActivity extends AppCompatActivity {

    public static DialogActivity.OnRevertListener mListener;
    public static DialogActivity.OnCallListener callListener;
    public static DialogActivity.OnAppOpenListener appOpenListener;
    public static DialogActivity.OnMessageClickListener messageClickListener;
    public static DialogActivity.OnProfileClickListener profileClickListener;

    public static String[] revert_strings = { };
    public static Activity dialogActivity;

    String[] call_back = { "Call back between 9AM-11am.",
            "Call back between 11am-1PM",
            "Call back between 2PM-4PM",
            "Call back between 4PM-6PM",
            "Call back After 6PM",
            "Call back tomorrow" };

    static Dialog dialog;
    public static String title = "";
    public static String key = "1234";
    public static String additional_data = "";

    public static String background = "#FFFFFF";
    public static String title_text_color  = "#FFFFFF";
    public static String name_text_color  = "#FF37474F";
    public static String phone_text_color  = "#FF37474F";
    public static String message_text_color  = "#FF37474F";
    public static int logo = 0, TYPE_CLOSED = 1, TYPE_NOT_INTERESTED = 2, TYPE_SEND_MESSAGE = 3, TYPE_APP_OPEN = 4, TYPE_REVERT = 5,
            TYPE_CANT_TALK_NOW = 6, TYPE_PROFILE_CLICK = 7, TYPE_MESSAGE_CLICK = 8, TYPE_CALL = 8, TYPE_IMPRESSION = 9, layout_call = 0, layout_text = 0;
    public static String package_name = "com.indiamart.m";
    public static String class_name = "MainActivity.class";
    public static String cant_talk_now_text = "Can't talk right now";

    String m_phone;
    public static String type;
    String recever_name = "";
    String business_name = "";
    String name;
    String from;
    String message_id;
    static TextView caller_type_text;
    AlertDialog alertList;
    //static AlertDialog alert;
    static View dialogView;
    LinearLayout profile_section;
    private static final int PERMISSION_REQUEST_CALL_PHONE = 124;

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

        dialogActivity = this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(DialogActivity.this)) {
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
        callBack(from);

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);
        LayoutInflater inflater = LayoutInflater.from(DialogActivity.this);


        dialogView = inflater.inflate(R.layout.dialog_info, null);
        final TextView call_stat = dialogView.findViewById(R.id.call_stat);
        final TextView caller_message = dialogView.findViewById(R.id.caller_message);
        if(type.equals("revert")){
            call_stat.setText("Reply From");
            caller_message.setText("Customer's Message");
        }

        if(type.equals("call")){
            call_stat.setText("Incoming Call From");
        }

        builder.setView(dialogView);
        final AlertDialog alert = builder.create();
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

        final TextView messageView = dialogView.findViewById(R.id.messageView);
        //final TextView messageViewRevert = dialogView.findViewById(R.id.messageViewRevert);
        final TextView my_message = dialogView.findViewById(R.id.my_message);
        final EditText messageTypeView = dialogView.findViewById(R.id.messageTypeView);
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
        fromView.setTextColor(Color.parseColor(phone_text_color));
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

        if(null != messageTypeView) {
            messageTypeView.setSelection(messageTypeView.getText().length());
        }

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

        if(null != background) {
            //layout_body.setBackgroundColor(Color.parseColor(background));;
        }

        ((ImageButton) dialogView.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                alert.dismiss();

                if(mListener!=null) {
                    mListener.onRevert("closed", TYPE_CLOSED, from);
                    closeDialog();
                }
            }
        });

        ((ImageView) dialogView.findViewById(R.id.nt_interested)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertBack("Not Interested", from);
                if(mListener!=null)
                    mListener.onRevert("Not Interested", TYPE_NOT_INTERESTED, from);
                clickRegester(from, TYPE_NOT_INTERESTED);
                my_message.setVisibility(View.VISIBLE);
                //messageViewRevert.setVisibility(View.VISIBLE);
                //messageViewRevert.setText("Not Interested");
                Toast.makeText(DialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                //DialogActivity.this.finish();
            }
        });

        ((ImageView) dialogView.findViewById(R.id.bt_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageTypeView.getText().toString();
                revertBack(message, from);
                if(mListener!=null)
                    mListener.onRevert(message, TYPE_SEND_MESSAGE, from);
                    clickRegester(from, TYPE_SEND_MESSAGE);
                    //messageViewRevert.setVisibility(View.VISIBLE);
                    //messageViewRevert.setText(message);

                //DialogActivity.this.finish();
            }
        });

        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appOpenListener!=null) {
                    boolean isPermissionAvailable = checkCallPermission();
                    appOpenListener.onOpen("App Open", isPermissionAvailable, TYPE_APP_OPEN, DialogActivity.this);
                    clickRegester(from, TYPE_APP_OPEN);
                }

                //DialogActivity.this.finish();
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
                showLists(revert_strings, from, messageView, my_message);
            }
        });

        bt_revert_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                showLists(revert_strings, from, messageView, my_message);
            }
        });

//        profile_section = dialogView.findViewById(R.id.profile_section);
//        profile_section.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(profileClickListener!=null) {
//                    boolean isPermissionAvailable = checkCallPermission();
//                    profileClickListener.onProfileClick("profile click", isPermissionAvailable, TYPE_PROFILE_CLICK, DialogActivity.this);
//                    clickRegester(from, TYPE_PROFILE_CLICK);
//                }
//            }
//        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageClickListener!=null) {
                    boolean isPermissionAvailable = checkCallPermission();
                    messageClickListener.onMessageClick("message click", isPermissionAvailable, TYPE_PROFILE_CLICK, DialogActivity.this);
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
                showLists(call_back, from, messageView, my_message);

            }
        });

        final TextView bt_callback_txt = dialogView.findViewById(R.id.bt_callback_txt);
        bt_callback_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call number
                //call_number(from);
                showLists(call_back, from, messageView, my_message);

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
                    boolean isInCall = isCallActive(DialogActivity.this);
                    if (isInCall) {
                        //call_stat.setText("Received Call From");
                        running[0] = false;
                        pickup();
                    }

                    boolean isCallRinging = isCallRinging(DialogActivity.this);
                    if (isCallRinging) {
                        ringing[0] = true;
                    }

                    boolean isCallIdle = isCallIdle(DialogActivity.this);
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

        DialogActivity.this.finish();
    }

    private void showLists(final String[] stringArray, final String from, final TextView messageViewRevert, final TextView my_message){

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogActivity.this);
        LayoutInflater inflater = LayoutInflater.from(DialogActivity.this);
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
                if(mListener!=null)
                    mListener.onRevert(mgs,  TYPE_REVERT, from);
                clickRegester(from, TYPE_REVERT);
                my_message.setVisibility(View.VISIBLE);
                //messageViewRevert.setVisibility(View.VISIBLE);
                //messageViewRevert.setText(mgs);
                Toast.makeText(DialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();

                alertList.cancel();
                alertList.dismiss();
                DialogActivity.this.finish();
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

        dialog.setContentView(R.layout.dialog_info);
        final TextView call_stat = dialog.findViewById(R.id.call_stat);
        final TextView caller_message = dialog.findViewById(R.id.caller_message);
        if(type.equals("revert")){
            call_stat.setText("Reply From");
            caller_message.setText("Customer's Message");
        }

        if(type.equals("call")){
            call_stat.setText("Incoming Call From");
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);

        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        LinearLayout main_layout = dialog.findViewById(R.id.main_layout);

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
                        dialog.getWindow().setAttributes(lp);
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

        final TextView messageView = dialog.findViewById(R.id.messageView);
        //final TextView messageViewRevert = dialog.findViewById(R.id.messageViewRevert);
        final TextView my_message = dialog.findViewById(R.id.my_message);
        final EditText messageTypeView = dialog.findViewById(R.id.messageTypeView);
        TextView fromView = dialog.findViewById(R.id.phone);
        //TextView caller_name = dialog.findViewById(R.id.caller_name);
        //LinearLayout layout_body = dialog.findViewById(R.id.layout_body);
        TextView pop_title = dialog.findViewById(R.id.title);
        ImageView main_logo = dialog.findViewById(R.id.imageView2);
        ImageView userImageView = dialog.findViewById(R.id.image);
        TextView bt_open = dialog.findViewById(R.id.bt_open);
        //caller_type_text = dialog.findViewById(R.id.caller_type);

        //pop_title.setTextColor(Color.parseColor(title_text_color));
        //caller_name.setTextColor(Color.parseColor(name_text_color));
        fromView.setTextColor(Color.parseColor(phone_text_color));
//        messageView.setTextColor(Color.parseColor(message_text_color));

        if(logo != 0) {
            main_logo.setImageResource(logo);
        }

        if(!urlImage.equals("")) {
            getImage(urlImage, userImageView);
        }
//
        if(null != message) {
            messageView.setText(message);
        }

//        if(null != messageTypeView) {
//            messageTypeView.setSelection(messageTypeView.getText().length());
//        }

        if(null != business_name) {
            fromView.setText(business_name);
        }

//        if(null != name) {
//            String cc_name = name+" "+from+" ";
//            caller_name.setText(cc_name);
//        }

//        if(null != product_name) {
//            if(!product_name.equals("")) {
//                TextView message_title_text = dialog.findViewById(R.id.message_title_text);
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

        if(null != background) {
            //layout_body.setBackgroundColor(Color.parseColor(background));;
        }

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                dialog.dismiss();
                if(mListener!=null)
                    mListener.onRevert("closed", TYPE_CLOSED, from);
                DialogActivity.this.finish();
            }
        });

        ((ImageView) dialog.findViewById(R.id.nt_interested)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revertBack("Not Interested", from);
                if(mListener!=null)
                    mListener.onRevert("Not Interested", TYPE_NOT_INTERESTED, from);
                clickRegester(from, TYPE_NOT_INTERESTED);
                my_message.setVisibility(View.VISIBLE);
                //messageViewRevert.setVisibility(View.VISIBLE);
                //messageViewRevert.setText("Not Interested");
                Toast.makeText(DialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                //dialog.dismiss();
                //DialogActivity.this.finish();
            }
        });

        ((ImageView) dialog.findViewById(R.id.bt_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageTypeView.getText().toString();
                revertBack(message, from);
                if(mListener!=null)
                    mListener.onRevert(message, TYPE_SEND_MESSAGE, from);
                    clickRegester(from, TYPE_SEND_MESSAGE);
                //dialog.dismiss();
                //DialogActivity.this.finish();
            }
        });

        bt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(appOpenListener!=null) {
                    boolean isPermissionAvailable = checkCallPermission();
                    appOpenListener.onOpen("App Open", isPermissionAvailable, TYPE_APP_OPEN, DialogActivity.this);
                    clickRegester(from, TYPE_APP_OPEN);
                }
                //dialog.dismiss();
                //DialogActivity.this.finish();
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

        final ImageView bt_revert = dialog.findViewById(R.id.bt_revert);
        final TextView bt_revert_txt = dialog.findViewById(R.id.bt_revert_txt);

        bt_revert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                Context wrapper = new ContextThemeWrapper(DialogActivity.this, R.style.MyPopupMenu);
                final PopupMenu popup = new PopupMenu(wrapper, bt_revert);

                for (String s: revert_strings) {
                    //Do your stuff here
                    popup.getMenu().add(s);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String mgs = item.getTitle().toString();
                        revertBack(mgs, from);
                        if(mListener!=null)
                            mListener.onRevert(mgs,  TYPE_REVERT, from);
                        my_message.setVisibility(View.VISIBLE);
                        //messageViewRevert.setVisibility(View.VISIBLE);
                        //messageViewRevert.setText(mgs);
                        clickRegester(from, TYPE_REVERT);

                        Toast.makeText(DialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                        //dialog.dismiss();
                        //finish();
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        bt_revert_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //creating a popup menu
                Context wrapper = new ContextThemeWrapper(DialogActivity.this, R.style.MyPopupMenu);
                final PopupMenu popup = new PopupMenu(wrapper, bt_revert);

                for (String s: revert_strings) {
                    //Do your stuff here
                    popup.getMenu().add(s);
                }
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String mgs = item.getTitle().toString();
                        revertBack(mgs, from);
                        if(mListener!=null)
                            mListener.onRevert(mgs, TYPE_REVERT, from);
                        clickRegester(from, TYPE_REVERT);
                        my_message.setVisibility(View.VISIBLE);
                        //messageViewRevert.setVisibility(View.VISIBLE);
                        //messageViewRevert.setText(mgs);
                        Toast.makeText(DialogActivity.this, "Message Sent", Toast.LENGTH_LONG).show();
                        //dialog.dismiss();
                        //finish();
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
            }
        });

        profile_section = dialog.findViewById(R.id.profile_section);
        profile_section.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profileClickListener!=null) {
                    boolean isPermissionAvailable = checkCallPermission();
                    profileClickListener.onProfileClick("profile click", isPermissionAvailable, TYPE_PROFILE_CLICK, DialogActivity.this);
                    clickRegester(from, TYPE_PROFILE_CLICK);
                }
            }
        });

        messageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(messageClickListener!=null) {
                    boolean isPermissionAvailable = checkCallPermission();
                    messageClickListener.onMessageClick("message click", isPermissionAvailable, TYPE_MESSAGE_CLICK, DialogActivity.this);
                    clickRegester(from, TYPE_MESSAGE_CLICK);
                }
            }
        });

//        final TextView cant_talk_now = dialog.findViewById(R.id.cant_talk_now);
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

        final ImageView callback = dialog.findViewById(R.id.bt_callback);
        callback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call number
                call_number(from);

            }
        });

        final TextView bt_callback_txt = dialog.findViewById(R.id.bt_callback_txt);
        bt_callback_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call number
                call_number(from);

            }
        });

        if(!type.equals("revert")) {

            final Handler handler = new Handler();
            final int delay = 3000; //milliseconds
            final boolean[] running = {true};
            final boolean[] ringing = {false};
            handler.postDelayed(new Runnable() {
                public void run() {
                    //do something
                    boolean isInCall = isCallActive(DialogActivity.this);
                    if (isInCall) {
                        //call_stat.setText("Received Call From");
                        running[0] = false;
                        pickup();
                    }

                    boolean isCallRinging = isCallRinging(DialogActivity.this);
                    if (isCallRinging) {
                        ringing[0] = true;
                    }

                    boolean isCallIdle = isCallIdle(DialogActivity.this);
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

    private void call_number(String from) {

        if(callListener!=null) {
            boolean isPermissionAvailable = checkCallPermission();
            callListener.onRevert(from, isPermissionAvailable, DialogActivity.this);
            clickRegester(from, TYPE_CALL);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    call_number(from);

                } else {

                    // permission denied, boo!
                    if(callListener!=null)
                        callListener.onRevert(from, false, DialogActivity.this);

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkCallPermission(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                return false;

            }else{

                return true;
            }
        }else{
            return true;
        }
    }

    public void getImage(final String urlImage, final ImageView imageView){
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

    public static void closeDialog(){
        if(null != dialog){
            dialog.cancel();
            dialog.dismiss();
        }
//        if(null != alert){
//            alert.cancel();
//            alert.dismiss();
//        }

        if(null != dialogActivity) {
            dialogActivity.finish();
        }
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

    @SuppressLint("StaticFieldLeak")
    private void pickup() {
        new AsyncTask<String, Integer, Boolean>(){

            @Override
            protected Boolean doInBackground(String... strings) {

                Long tsLong = System.currentTimeMillis()/1000;
                // Create data variable for sent values to server

                String data = null;
                try {
                    data = URLEncoder.encode("pickup", "UTF-8")
                            + "=" + URLEncoder.encode("1", "UTF-8");

                    data += "&" + URLEncoder.encode("message_id", "UTF-8")
                            + "=" + URLEncoder.encode(message_id, "UTF-8");

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

    @Override
    public void onBackPressed() {
        DialogActivity.this.finish();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(null != dialog){
            dialog.cancel();
            dialog.dismiss();
        }
//        if(null != alert){
//            alert.cancel();
//            alert.dismiss();
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(DialogActivity.this)) {
            mainRun(intent);
        }else{
            drawApp(intent);
        }

    }

    public interface OnRevertListener {
        void onRevert(String message, int type, String phone);
    }

    public static void setOnRevertListener(OnRevertListener eventListener) {
        mListener = eventListener;
    }

    public interface OnCallListener {
        void onRevert(String number, boolean permission, Context context);
    }

    public static void setOnCallListener(OnCallListener eventListener) {
        callListener = eventListener;
    }

    public interface OnAppOpenListener {
        void onOpen(String number, boolean permission, int type, Context context);
    }

    public static void setAppOpenListener(OnAppOpenListener eventListener) {
        appOpenListener = eventListener;
    }

    public interface OnProfileClickListener {
        void onProfileClick(String number, boolean permission, int type, Context context);
    }

    public static void setProfileClickListener(OnProfileClickListener eventListener) {
        profileClickListener = eventListener;
    }

    public interface OnMessageClickListener {
        void onMessageClick(String number, boolean permission, int type, Context context);
    }

    public static void setMessageClickListener(OnMessageClickListener eventListener) {
        messageClickListener = eventListener;
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
