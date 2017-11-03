package com.kgb.remotear;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.test.mock.MockApplication;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CHECK_ACTION_FROM_SERVICE = 667;
    public static final int REQUEST_CODE = 667;
    private boolean mRecording;
    private Button mStart;
    private final String[] mPermissions = new String[] {
            "android.permission.RECORD_AUDIO", "android.permission.RECEIVE_BOOT_COMPLETED"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermissions(mPermissions)) {
            registerAlarm(this);
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                requestPermissions(mPermissions, REQUEST_CODE);
            }
        }

        setContentView(R.layout.activity_main);
        mStart = findViewById(R.id.start);
        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mRecording) {
                    Toast.makeText(MainActivity.this, "Start recording!", Toast.LENGTH_SHORT).show();
                    startRecording();
                    mRecording = true;
                    mStart.setText("Stop");
                } else {
                    Toast.makeText(MainActivity.this, "Stop recording!", Toast.LENGTH_SHORT).show();
                    stopRecording();
                    mRecording = false;
                    mStart.setText("Start");
                }
            }
        });
    }

    private void stopRecording() {
        Intent startIntent = new Intent(this, RecordService.class);
        startIntent.setAction(RecordService.START_RECORDING);
        startService(startIntent);
    }

    private void startRecording() {
        Intent startIntent = new Intent(this, RecordService.class);
        startIntent.setAction(RecordService.STOP_RECORDING);
        startService(startIntent);
    }

    private void registerAlarm(Context context) {
        Intent i = new Intent(context, AlarmBootReceiver.class);

        PendingIntent sender = PendingIntent.getBroadcast(context, CHECK_ACTION_FROM_SERVICE, i, 0);

        // We want the alarm to go off 3 seconds from now.
        long firstTime = SystemClock.elapsedRealtime();
        firstTime += 3 * 1000;//start 3 seconds after first register.

        // Schedule the alarm!
        AlarmManager am = (AlarmManager) context
                .getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, firstTime,
                60000, sender);//10min interval
    }

    private boolean checkPermissions(String[] permissions) {
        boolean resutl = true;
        if (Build.VERSION.SDK_INT >= 23) {
            for (String per : permissions) {
                if (checkSelfPermission(per) != PackageManager.PERMISSION_GRANTED) {
                    resutl &= false;
                }
            }
        }
        return resutl;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        for (int result : grantResults) {
            granted &= result == PackageManager.PERMISSION_GRANTED;
        }
        if (granted) {
            registerAlarm(this);
        }
    }
}
