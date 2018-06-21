package com.example.daniel.imageserviceapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ImageService extends Service {
    IntentFilter intentFilter = new IntentFilter();
    BroadcastReceiver broadcastReceiver;
    List<File> files;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {
        //show start message
        Toast.makeText(this, "Starting Service", Toast.LENGTH_LONG).show();
        this.broadcastReceiver = new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onReceive(Context context, Intent intent) {
                //WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                //check wifi is connected
                if (networkInfo != null &&
                        networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    startTransfer(context);
                }
            }
        };
        this.registerReceiver(this.broadcastReceiver, intentFilter);
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startTransfer(Context context) {
        //set id
        final int id = 1;
        //set builder
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
        //set notification manager
        final NotificationManager NM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //set channel
        NotificationChannel channel = new NotificationChannel("default", "Progress bar", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Image Service Progress Bar");
        NM.createNotificationChannel(channel);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
//        builder.setContentTitle("Transferring Pictures...");
        builder.setContentText("Passing...");
        //start the transfer
        new Thread(new Runnable() {
            @Override
            public void run() {
                //set percent of the bar
                int percent = 0;
                updatePictures();
                for (File file : files) {
                    //set communication
                    Communication communication = new Communication(file);
                    //try to start communication
                    try {
                        communication.startCommunication();
                    } catch (Exception e) {
                        Log.e(this.getClass().getSimpleName(), e.getMessage());
                    }
                    //monitor the percent of the bar
                    percent = percent + 100 / files.size();
                    builder.setProgress(100, percent, false);
                    NM.notify(id, builder.build());
                }
                builder.setProgress(0, 0, false);
                builder.setContentText("Done Transferring Pictures!");
                NM.notify(id, builder.build());
            }
        }).start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.intentFilter.addAction("android.net.wifi.supplicant.CONNECTION_CHANGE");
        this.intentFilter.addAction("android.net.wifi.STATE_CHANGE");
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Stopping Service", Toast.LENGTH_LONG).show();
        this.unregisterReceiver(this.broadcastReceiver);
    }

    public void getPicturesFromDir(File directory, List<File> pictures) {
        File[] filesArray = directory.listFiles();
        for (File file : filesArray) {
            if (file.isDirectory()) {
                getPicturesFromDir(file, pictures);
            } else if (file.toString().contains(".jpg")) {
                pictures.add(file);
            }
        }
    }

    public void updatePictures() {
        List<File> pictures = new LinkedList<>();
        File dataCenter = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //set the files
        File[] filesArray = dataCenter.listFiles();
        //check filesArray isn't null
        if (filesArray != null) {
            //check for each file in files array
            for (File file : filesArray) {
                //for directory
                if (file.isDirectory()) {
                    //search for pictures there
                    getPicturesFromDir(file, pictures);
                }
                //for picture
                else if (file.toString().contains(".jpg")) {
                    //add it
                    pictures.add(file);
                }
            }
        }
        //set files
        this.files = pictures;
    }
}