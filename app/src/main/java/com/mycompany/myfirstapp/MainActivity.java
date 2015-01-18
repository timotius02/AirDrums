package com.mycompany.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;
import com.thalmic.myo.Vector3;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private TextView mLockStateView;
    private TextView mTextView;
    private TextView xString;
    private TextView yString;
    private TextView zString;


    private SoundManager soundmanager;

    private ArrayList<Myo> mKnownMyos = new ArrayList<Myo>();

    private static final String TAG = "MainActivity";

    private boolean raisedLeft = false;
    private boolean raisedRight = false;

    private float yawLeft;
    private float yawRight;

    private int buffer = 0;

    private Context that = this;

    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {

        @Override
        public void onAttach(Myo myo, long timestamp) {
            Log.e(TAG, "connected");
            mKnownMyos.add(myo);
        }

        @Override
        public void onGyroscopeData(Myo myo, long timestamp, Vector3 rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.

            xString.setText(Double.toString(rotation.x()));
            yString.setText(Double.toString(rotation.y()));
            zString.setText(Double.toString(rotation.z()));

        }

        //Called when an attached Myo has provided new accelerometer data.
        @Override
        public void onAccelerometerData(Myo myo, long timestamp, Vector3 accel) {
            if(identifyMyo(myo)==1){
                double x = accel.x();

                if (x > 0.8) {

                    raisedLeft = true;
                }

                if (raisedLeft && x < .1) {

                    soundmanager.bd1();
                    raisedLeft = false;
                }
            }
            else{
                double x = accel.x();

                if (x > 0.8) {
                    raisedRight = true;
                }
                if (raisedRight && x < .1) {

                    soundmanager.bd2();

                    raisedRight = false;
                }
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLockStateView = (TextView) findViewById(R.id.lock_state);
        mTextView = (TextView) findViewById(R.id.text);
        xString = (TextView) findViewById(R.id.xString);
        yString = (TextView) findViewById(R.id.yString);
        zString = (TextView) findViewById(R.id.zString);
        yawLeft = 0;
        yawRight = 0;

        soundmanager = new SoundManager(this);


        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final int attachingCount = 2;
        hub.setMyoAttachAllowance(attachingCount);
        hub.attachToAdjacentMyos(attachingCount);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Activity is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        if (isFinishing()) {
            // The Activity is finishing, so shutdown the Hub. This will disconnect from the Myo.
            Hub.getInstance().shutdown();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }

    private int identifyMyo(Myo myo) {

        return mKnownMyos.indexOf(myo) + 1;
    }
}

