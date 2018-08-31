package com.bencorp.scrab;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by hp-pc on 7/14/2018.
 */

public  class FolderPath {
    public static final String folder_name = "ScordIt";
    public static final String sub_folder_name = "videos";
    //public static final String sub_folder_name2 = "scanResults";
    private FolderPath(){}

    public static File filePath(String fileName){
        File filepath = Environment.getExternalStoragePublicDirectory(folder_name);
        File files = new File(filepath,sub_folder_name+"/"+fileName);
        return files;
    }

    public static boolean makeDir(){

        String state = Environment.getExternalStorageState();
        File filepath = Environment.getExternalStoragePublicDirectory(folder_name);
        File files = new File(filepath,sub_folder_name);

        if(Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            //Toast.makeText(get,"available",Toast.LENGTH_LONG).show();
            files.mkdirs();
            return true;
            //Toast.makeText(context,"available",Toast.LENGTH_LONG).show();
        }else{
            return false;
        }


    }
    public static String getFileName(){
        String date = new SimpleDateFormat("ddMMyyyy", Locale.getDefault()).format(new Date());
        String rand = UUID.randomUUID().toString().substring(0,8);
        return "SCREEN-"+date+"-"+rand+".mp4";
    }
    public static Boolean isSpaceEnough(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        long bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();

        long megAvailable = (long) bytesAvailable / (1024*1024);

        if(megAvailable < 20){
            return false;
        }
        return true;
    }

}
