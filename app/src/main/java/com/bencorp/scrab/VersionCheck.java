package com.bencorp.scrab;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.json.JSONArray;

/**
 * Created by hp-pc on 9/16/2018.
 */

public class VersionCheck extends AsyncTask{

    private static Boolean hasUpdate = false;
    Context ctx;
    Activity activity;
    VersionCheck(Context context, Activity activity){
        this.ctx = context;
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        hasUpdate = true;
        return null;
    }
    @Override
    protected void onPostExecute(Object o) {
        if(hasUpdate){
            updateDialog();
        }
    }
    private void updateDialog(){

        new AlertDialog.Builder(activity)
                .setCancelable(true)
                .setTitle("Update Notice")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ctx.getApplicationContext(), "going to playstore", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        })
                .setMessage("new version is available").show();
    }
}
