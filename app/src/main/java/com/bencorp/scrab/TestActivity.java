package com.bencorp.scrab;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TestActivity extends AppCompatActivity{

    private Button takePictureButton;
    ArrayList<ListBlueprint> myVideos;
    ListBlueprint listBlueprint;
    VideoListAdapter videoListAdapter;
    RecyclerView videoRecycler;
    RecyclerView.LayoutManager layoutManager;
    static TestActivity testActivity;
    RelativeLayout swipe_desc;
    TextView empty;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    float dX;
    float dY;
    int lastAction;

    private ImageReader imageReader;
    ImageView about;
    private File file;
    //private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;

    private static final int REQUEST_CODE = 999;
    private static final int REQUEST_PERMISSION = 888;
    private static final int REQUEST_DRAW_OVER_PERMISSION = 777;

    //private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    Animation toggleAnime,toggleShowAnime;
    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaProjectionCallback mediaProjectionCallback;
    public MediaRecorder mediaRecorder;
    TextureView texture;
    SurfaceHolder surfaceHolder;
    private Size imageDimension;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    protected CameraDevice cameraDevice;
    CameraManager cameraManager;
    String cameraId;
    private static final int DISPLAY_WIDTH = 720;
    private static final int DISPLAY_HEIGHT = 1280;
    private int screenDensity;


    public FloatingActionButton scrab;

    private String VideoUri = "";
    private Boolean isRecording = false;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    String msg;
    private android.widget.RelativeLayout.LayoutParams layoutParams;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(!FolderPath.makeDir()){
            Toast.makeText(this,"SD card unavailable",Toast.LENGTH_LONG).show();
            finish();
        }
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_test);
        if(shouldAskPermission()){
            requestAllpermissions();
            //checkDrawOverPermission();
        }

        swipe_desc = (RelativeLayout) findViewById(R.id.swipe_desc);
        videoRecycler = (RecyclerView) findViewById(R.id.video_list);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        videoRecycler.setLayoutManager(layoutManager);
        empty = (TextView) findViewById(R.id.empty_message);
        about = (ImageView) findViewById(R.id.about);
        //videoRecycler.setHasFixedSize(true);
        checkDrawOverPermission();
        testActivity = this;

        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        screenDensity = display.densityDpi;

        mediaRecorder = new MediaRecorder();
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        //toggleAnime.setAnimationListener();



        createList();

        scrab = (FloatingActionButton) findViewById(R.id.scrabButton);
        scrab.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                if(FolderPath.isSpaceEnough()){
                    if(enoughRam()){
                        if(shouldAskPermission()){
                            requestAllpermissions();

                        }
                        checkDrawOverPermission();
                        toggleScreenShare();
                    }else {
                        ramDialog();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Storage is too small, please delete some files before using ScordIt",Toast.LENGTH_LONG).show();
                }


                //startActivity(new Intent(TestActivity.this,SelfieWidgetActivity.class));
            }
        });
        notifyState();
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutDialog();
            }
        });
    }


    public void requestAllpermissions(){
        ArrayList<String> permissions = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(TestActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(TestActivity.this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.RECORD_AUDIO);
        }
        if(ActivityCompat.checkSelfPermission(TestActivity.this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.CAMERA);
        }
        /*if(ActivityCompat.checkSelfPermission(TestActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.SYSTEM_ALERT_WINDOW);
        }*/
        if(permissions.size() > 0){
            //Toast.makeText(this,"requesting",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(TestActivity.this,permissions.toArray(new String[permissions.size()]),REQUEST_PERMISSION);
        }
    }
    public Boolean shouldAskPermission(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }



    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Videos Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBackgroundThread.quitSafely();
        }
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static TestActivity getInstance(){
        return testActivity;
    }


    private void checkDrawOverPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, REQUEST_DRAW_OVER_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case REQUEST_PERMISSION:{
                for (int i = 0; i < grantResults.length;i++){
                    if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                        Toast.makeText(this,"You have to accept all permissions to use this application",Toast.LENGTH_LONG).show();
                        finish();
                        break;
                    }
                }

           }

       }
    }



    @Override
    protected void onResume() {
        if(!FolderPath.makeDir()){
            Toast.makeText(this,"SD card unavailable",Toast.LENGTH_LONG).show();
            finish();
        }
        super.onResume();
        createList();
    }
    private Boolean enoughRam(){
        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        if (memoryInfo.lowMemory){
            return false;
        }
        return true;
    }
    private ActivityManager.MemoryInfo getAvailableMemory(){
        ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }
    @Override
    protected void onPause() {

        //Log.e(TAG, "onPause");
        //closeCamera();

        super.onPause();
    }
    public void notifyState(){
        if(isServicRunning(BackgroundTask.class)){
            scrab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DB4437")));
        }else{
            scrab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196D9")));
        }
    }

    public void toggleScreenShare() {
        scrab.setEnabled(false);
        if(!isServicRunning(BackgroundTask.class)){
            startService( new Intent(TestActivity.this,BackgroundTask.class));
            scrab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DB4437")));
        }else {
            stopService( new Intent(TestActivity.this,BackgroundTask.class));
            scrab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2196D9")));
            //scrab.setBackgroundColor();
            //Service service  = (Service) new BackgroundTask();
            //service.stopSelf();
        }

        //Toast.makeText(this,"begin",Toast.LENGTH_SHORT).show();
        /*if (!isRecording) {
            isRecording = true;
            //toggleButton.setRippleColor(Color.YELLOW);
            initRecorder();
            recordScreen();

        }else{
            isRecording = false;

            stopRecorder();

            //toggleButton.setRippleColor(Color.RED);

        }*/
    }

    private boolean isServicRunning(Class <?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service: manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.bencorp.scrab.BackgroundTask".equals(service.service.getClassName())){
                return  true;
            }
        }
        return false;
    }


    public MediaRecorder initRecorder() {

        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            File rootPath = Environment.getExternalStoragePublicDirectory("ScordIt");
            File sub = new File(rootPath,"videos");
            String rand = UUID.randomUUID().toString().substring(0,8);
            VideoUri = sub+new StringBuilder("/SCREEN").append(new SimpleDateFormat("ddMMyyyy")
                    .format(new Date())).append(rand).append(".mp4").toString();
            //Toast.makeText(this,VideoUri,Toast.LENGTH_LONG).show();
            mediaRecorder.setOutputFile(VideoUri);
            mediaRecorder.setVideoSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setVideoEncodingBitRate(512 * 1000);
            mediaRecorder.setVideoFrameRate(30);

            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            int orientation = ORIENTATIONS.get(rotation + 90);
            mediaRecorder.setOrientationHint(orientation);
            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener(){
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    Toast.makeText(getApplicationContext(),"Something Went Wrong, ScordIt has Stopped",Toast.LENGTH_LONG).show();
                }
            });
            mediaRecorder.prepare();
            return mediaRecorder;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaRecorder;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void recordScreen() {

        if (mediaProjection == null) {
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            return;
        }
        virtualDisplay = createVirtualDisplay();
        mediaRecorder.start();
        //return virtualDisplay;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        return mediaProjection.createVirtualDisplay("LaunchActivity", DISPLAY_WIDTH, DISPLAY_HEIGHT, screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {

            mediaRecorder.stop();
            mediaRecorder.release();
            mediaProjection= null;
            stopRecorder();
            super.onStop();
        }
    }

    public void stopRecorder() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        if(virtualDisplay == null){
            return;
        }
        virtualDisplay.release();
        destroyMediaProjection();
        Toast.makeText(this,"Recorder stopped",Toast.LENGTH_LONG).show();
    }
    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //Log.d("res",Integer.toString(resultCode));
            mediaProjectionCallback = new MediaProjectionCallback();
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode,data);
            mediaProjection.registerCallback(mediaProjectionCallback,null);
            virtualDisplay = createVirtualDisplay();
            //Toast.makeText(this,"Recorder Started",Toast.LENGTH_LONG).show();

            mediaRecorder.start();

            Toast.makeText(this,"ScordIt has started",Toast.LENGTH_LONG).show();
            finish();
            //ftoggtoggleButton.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));

                    //btn.setEnabled(true);
            //animate();

            //updatePreview();
        } else if (requestCode == REQUEST_DRAW_OVER_PERMISSION) {
            //Check if the permission is granted or not.
            //Log.d("res",Integer.toString(resultCode));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                //initializeView();
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_LONG).show();

                finish();
            } //Permission is not available



        }else {
            Toast.makeText(this,
                    "Unknown error",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void destroyMediaProjection() {
        if(mediaProjection != null){
            mediaProjection.unregisterCallback(mediaProjectionCallback);
            mediaProjection.stop();
            mediaProjection = null;
        }
    }

    public void createList(){
        File filepath = Environment.getExternalStoragePublicDirectory(FolderPath.folder_name);
        File subPath = new File(filepath,FolderPath.sub_folder_name);
        myVideos = new ArrayList<>();
        if(subPath.exists()){
            File[] videoFiles = subPath.listFiles();
            if(videoFiles.length > 0){
                empty.setVisibility(View.GONE);
                swipe_desc.setVisibility(View.VISIBLE);
                for(int i = 0; i < videoFiles.length;i++){
                    //String[] name = .split("\\.");
                    File completePath = new File(subPath,videoFiles[i].getName());
                    listBlueprint = new ListBlueprint(completePath);
                    myVideos.add(listBlueprint);
                }
                videoListAdapter =  new VideoListAdapter(myVideos,getApplicationContext(),TestActivity.this);

                videoRecycler.setAdapter(videoListAdapter);
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        startBackgroundThread();
                        confirmDialog((Integer) viewHolder.itemView.getTag());
                    }
                }).attachToRecyclerView(videoRecycler);
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        startBackgroundThread();
                        Integer filePosition = (Integer) viewHolder.itemView.getTag();
                        ListBlueprint videoObject = myVideos.get(filePosition);
                        File filePath = FolderPath.filePath(videoObject.getFileName()+".mp4");
                        Uri videoUri = FileProvider.getUriForFile(getApplicationContext(),getPackageName()+".provider",filePath);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM,videoUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if(shareIntent.resolveActivity(getPackageManager()) != null){
                            //Toast.makeText(getApplicationContext(),"resolved",Toast.LENGTH_LONG).show();
                            startActivity(shareIntent);
                        }else {
                            Toast.makeText(getApplicationContext(),"No applictaion found for that",Toast.LENGTH_LONG).show();
                        }

                       stopBackgroundThread();
                    }
                }).attachToRecyclerView(videoRecycler);
                new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        startBackgroundThread();
                        Integer filePosition = (Integer) viewHolder.itemView.getTag();
                        ListBlueprint videoObject = myVideos.get(filePosition);
                        File filePath = FolderPath.filePath(videoObject.getFileName()+".mp4");
                        Uri videoUri = FileProvider.getUriForFile(getApplicationContext(),getPackageName()+".provider",filePath);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM,videoUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        if(shareIntent.resolveActivity(getPackageManager()) != null){
                            //Toast.makeText(getApplicationContext(),"resolved",Toast.LENGTH_LONG).show();
                            startActivity(shareIntent);
                        }else {
                            Toast.makeText(getApplicationContext(),"No applictaion found for that",Toast.LENGTH_LONG).show();
                        }

                       stopBackgroundThread();
                    }
                }).attachToRecyclerView(videoRecycler);


            }else{
                empty.setVisibility(View.VISIBLE);
            }
        }

    }
    public void confirmDialog(final Integer filePosition){
        new AlertDialog.Builder(TestActivity.this)
                .setCancelable(true)
                .setTitle("Delete Video")
                .setMessage("Are you sure you want to delete this video")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListBlueprint videoObject = myVideos.get(filePosition);

                        FolderPath.filePath(videoObject.getFileName()+".mp4").delete();

                        myVideos.remove(videoObject);
                        videoListAdapter.notifyDataSetChanged();
                        stopBackgroundThread();
                        if(myVideos.size() == 0){
                            empty.setVisibility(View.VISIBLE);
                            swipe_desc.setVisibility(View.GONE);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        videoListAdapter.notifyDataSetChanged();
                    }
                }).show();
    }
    public void aboutDialog(){
        new AlertDialog.Builder(TestActivity.this)
                .setCancelable(true)
                .setTitle("About")
                .setMessage(getString(R.string.app_name_description)).show();
    }
    private void ramDialog(){

        new AlertDialog.Builder(TestActivity.this)
                .setCancelable(true)
                .setTitle("Notice")
                .setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(shouldAskPermission()){
                            requestAllpermissions();

                        }
                        checkDrawOverPermission();
                        toggleScreenShare();
                    }
                }).setNegativeButton("Disagree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
        })
                .setMessage(getString(R.string.app_ram_alert)).show();
    }
}
