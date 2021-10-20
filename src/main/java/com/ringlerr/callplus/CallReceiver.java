package com.ringlerr.callplus;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CallReceiver extends PhonecallReceiver {

    @Override
    protected void onIncomingCallReceived(final Context ctx, final String number, Date start)
    {

        Toast.makeText(ctx,"Incoming call", Toast.LENGTH_SHORT).show();

        JSONObject json = new JSONObject();
        try {
            json.put("phone_from", "7002019851");
            json.put("message_id", "0");
            json.put("recever_name", "Customer");
            json.put("message", "HI, Customer is interested in your property");
            json.put("phone_to", "+917002019851");
            json.put("banner_url", "");
            json.put("cvc", "");
            json.put("type", "call");
            json.put("business_name", "Housing");
            json.put("caller_name", "Housing");
            json.put("user_image", "");
            json.put("key", "12a4f62af7991d51ba9bef728e260cd5e5ddacb2b3a60fa33b7cc526f26edb");

            CallPlus.showDialog(ctx, json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onIncomingCallAnswered(final Context ctx, String number, Date start)
    {
        //
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //CallPlus.pickup();
                Toast.makeText(ctx,"pickup run", Toast.LENGTH_SHORT).show();
            }
        }, 7000);

    }

    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start)
    {
        //
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end)
    {
        //
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start)
    {
        //
    }
}
