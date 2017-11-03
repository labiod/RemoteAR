package com.kgb.remotear;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {
    public static final String TAG = RecordService.class.getSimpleName();
    public static final String START_RECORDING = "start_recording";
    public static final String STOP_RECORDING = "stop_recording";
    public static final String CHECK_REMOTE = "check_remote";

    private MediaRecorder mRecorder;
    private String mFilePath;
    private boolean mRecording = false;

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case START_RECORDING:
                startRecording();
                break;
            case STOP_RECORDING:
                stopRecording();
                break;
            case CHECK_REMOTE:
                checkRemote();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkRemote() {
        Log.d(TAG, "checking remote server");
        mFilePath = getExternalCacheDir().getAbsolutePath();
        long count = new File(mFilePath).list().length;
        mFilePath += "/remote_ar_" + count + ".3gp";
        Log.d(TAG, "onCreate: file name: " + mFilePath);
        if (mRecording) {
            stopRecording();
        } else {
            startRecording();
        }
    }

    private void startRecording() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(RecordService.this, "Exception during record prepare, checks log for more details", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "startRecording: prepare failed", e);
        }
        mRecording = true;
        mRecorder.start();
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecording = false;
            mRecorder.stop();
        }
    }
}
