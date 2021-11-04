package com.anso.mylinetest;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.anso.mylinetest.view.TestSurfaceView;

public class MainActivity extends AppCompatActivity {


    private LinearLayout canvasLayout = null;

    TestSurfaceView testSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("SurfaceView");
        initControls();
        // Hide the app title bar.
        getSupportActionBar().hide();
        // Make app full screen to hide top status bar.
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Create custom surfaceview object.
        testSurfaceView = new TestSurfaceView(getApplicationContext());
        // Add the custom surfaceview object to the layout.
        canvasLayout.addView(testSurfaceView);
    }
    /* Initialise ui controls. */
    private void initControls() {
        // This layout is used to contain custom surfaceview object.
        if (canvasLayout == null) {
            canvasLayout = (LinearLayout) findViewById(R.id.customViewLayout);
        }
    }



}