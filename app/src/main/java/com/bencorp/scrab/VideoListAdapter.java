package com.bencorp.scrab;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hp-pc on 8/21/2018.
 */

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.RecyclerViewHolder> {
    ArrayList<ListBlueprint> arrayList = new ArrayList<>();
    Context context;
    Activity activity;
    public  static  class RecyclerViewHolder extends  RecyclerView.ViewHolder{
        TextView name;
        ImageView thumbnail;
        TextView videoDuration;
        public RecyclerViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.video_name);
            //videoDuration = (TextView) itemView.findViewById(R.id.video_duration);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);


        }

    }
    VideoListAdapter(ArrayList<ListBlueprint> arrayList, Context context,Activity activity){
        this.arrayList = arrayList;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public VideoListAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_video_list,parent,false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoListAdapter.RecyclerViewHolder holder, int position) {
        final ListBlueprint video = arrayList.get(position);
        Glide
                .with(context)
                .load(Uri.fromFile(FolderPath.filePath(video.getFileName()+".mp4")))
                .centerCrop()
                .into(holder.thumbnail);
        holder.name.setText(video.getFileName());
        //Toast.makeText(context,"clicked "+video.getFileName(),Toast.LENGTH_LONG).show();
        /*MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        //try {
            //FileInputStream inputStream = new FileInputStream(FolderPath.filePath(video.getFileName()+".mp4").getAbsolutePath());
            retriever.setDataSource(FolderPath.filePath(video.getFileName()+".mp4").getAbsolutePath());
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Long timeInMillisec = Long.parseLong(time);
            Long duration = timeInMillisec/1000;
            Long hours = duration/3600;
            Long minutes = (duration - hours * 3600)/60;
            Long seconds = duration - (hours * 3600 + minutes * 60);

            holder.videoDuration.setText(String.format(Locale.getDefault(),"%02d:%02d:%02d",hours,minutes,seconds));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        holder.itemView.setTag(position);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(context.getApplicationContext(),"beep3",Toast.LENGTH_LONG).show();
               File filePath = FolderPath.filePath(video.getFileName()+".mp4");
                Uri videoUri = FileProvider.getUriForFile(context.getApplicationContext(),context.getPackageName()+".provider",filePath);
                final Intent shareIntent = new Intent(Intent.ACTION_VIEW);
                shareIntent.setDataAndType(videoUri, "video/*");
                //shareIntent.putExtra(Intent.EXTRA_STREAM,videoUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                if(shareIntent.resolveActivity(context.getPackageManager()) != null){
                    //Toast.makeText(context.getApplicationContext(),"resolved",Toast.LENGTH_LONG).show();
                    v.getContext().startActivity(shareIntent);
                    /* ;*/
                }else{
                    Toast.makeText(context.getApplicationContext(),"No applictaion found for that",Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }
}
