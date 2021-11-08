package com.anso.mylinetest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.anso.mylinetest.view.TestSurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {


    private LinearLayout canvasLayout = null;

    TestSurfaceView testSurfaceView = null;
    DisplayMetrics dm;
    Intent service;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SurfaceView");
        dm = new DisplayMetrics();
        // 获取屏幕信息
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int centerX = dm.widthPixels / 2;
        initControls();
        // Hide the app title bar.
        getSupportActionBar().hide();
        // Make app full screen to hide top status bar.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Create custom surfaceview object.
        testSurfaceView = new TestSurfaceView(getApplicationContext());
        testSurfaceView.setCenterX(centerX);
        testSurfaceView.setListener(new TestSurfaceView.SurfaceListener() {
            @Override
            public void finish() {
                mMediaProjectionManager = (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
            }
        });
        // Add the custom surfaceview object to the layout.
        canvasLayout.addView(testSurfaceView);
        findViewById(R.id.btn_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testSurfaceView.start();
            }
        });
        service = new Intent(this, ScreenRecorder.class);
        startForegroundService(service);
    }

    /* Initialise ui controls. */
    private void initControls() {
        // This layout is used to contain custom surfaceview object.
        if (canvasLayout == null) {
            canvasLayout = (LinearLayout) findViewById(R.id.customViewLayout);
        }
    }

    private int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;
    private ImageReader mImageReader = null;
    private String pathImage = Environment.getExternalStorageDirectory().getPath() + "/LJY/";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
                service.putExtra("code", resultCode);
                service.putExtra("data", data);
                startForegroundService(service);
            }
        }
    }


    private void saveJpeg(Image image, String name) {
        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * dm.widthPixels;
        Bitmap bitmap = Bitmap.createBitmap(dm.widthPixels + rowPadding / pixelStride, dm.heightPixels, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, dm.widthPixels, dm.heightPixels);
        if (bitmap != null) {
            try {
                File fileImage = new File(pathImage + name + ".jpg");
                if (!fileImage.exists()) {
                    fileImage.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(fileImage);
                if (out != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();
                    Intent media = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(fileImage);
                    media.setData(contentUri);
                    this.sendBroadcast(media);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}