package com.bencorp.scrab;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by hp-pc on 7/23/2018.
 */

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context,BackgroundTask.class);
        context.stopService(serviceIntent);
        Toast.makeText(context,"ScordIt has stopped",Toast.LENGTH_LONG).show();
        //Toast.makeText(context,"Recorder stopped",Toast.LENGTH_LONG).show();
    }
}
