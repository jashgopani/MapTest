package com.example.maptest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    Button startServiceBtn, stopServiceBtn, compareIconsBtn;
    ImageView imageIcon;
    TextView statustv, logtv;
    ArrayList<int[]> largeIcons;
    BitmapDrawable previous, current;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        startServiceBtn = findViewById(R.id.startServiceBtn);
        stopServiceBtn = findViewById(R.id.stopServiceBtn);
        compareIconsBtn = findViewById(R.id.compareIconsBtn);
        imageIcon = (ImageView) findViewById(R.id.imageIcon);
        statustv = (TextView) findViewById(R.id.statustv);
        logtv = (TextView) findViewById(R.id.logtv);
        largeIcons = new ArrayList<>();

        startServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statustv.setText("Monitoring onn");
                LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(onNotice, new IntentFilter("Msg"));
                Toast.makeText(MainActivity.this, "Monitoring on", Toast.LENGTH_SHORT);
                Log.i(TAG, "clicked start");

            }
        });

        stopServiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statustv.setText("Monitoring off");
                LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(onNotice);
            }
        });

        compareIconsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Compare Icons", Toast.LENGTH_SHORT);
            }
        });
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        // To recieve intents from service, perform the actions of onRecieve method
        @Override
        public void onReceive(Context context, Intent intent) {

            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String icon_type = intent.getStringExtra("icon_type");
            String old = logtv.getText().toString();

            String not = "\n{title : " + title + "\ntext :" + text + "\nicon_type : " + icon_type + " }\n";


            if (intent != null) {
                //getting current BitmapDrawable
                BitmapDrawable icon = (BitmapDrawable) NotificationMonitor.getIconResource();

                //working with Bitmap
                Bitmap cb = icon.getBitmap();
                int width = cb.getWidth();
                int height = cb.getHeight();
                int[] pixels = new int[width * height];

//                PixelWrapper p = new PixelWrapper(pixels);

                System.out.println("--------------------------Pixels Start--------------------------------");
                largeIcons.add(pixels);
                storeImage(cb);
//                if (largeIcons.size() == 0){
//                    largeIcons.add(pixels);
//                    storeImage(cb);
//                }else if (isUnique(pixels) && pixels != null) {
//                    largeIcons.add(pixels);
//                    storeImage(cb);
//                    System.out.println("Pixels were Unique");
//                } else {
//                    System.out.println("Pixels were same");
//                }

                System.out.println(largeIcons.size());
                System.out.println("--------------------------Pixels End--------------------------------");


                imageIcon.setImageDrawable(icon);
            }

            logtv.setText(not + old);
        }
    };


    private boolean isUnique(int[] a) {
        if (a == null) return false;

        for (int[] arr : largeIcons) {
            for (int i = 0; i < arr.length; i++) {
                if ((arr[i] - a[i]) != 0) {
                    return true;//if a single pixel is different, then also icon is unique
                }
            }
        }
        return false;
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.i(TAG, "Image saved successfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}
