package com.kgb.remotear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author Krzysztof Betlej <k.betlej@samsung.com>.
 * @date 11/3/17
 * @copyright Copyright (c) 2016 by Samsung Electronics Polska Sp. z o. o.
 */

public class AlarmBootReceiver extends BroadcastReceiver {

    public static final String TAG = AlarmBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "new alarm Intent");
        Intent startIntent = new Intent(context, RecordService.class);
        startIntent.setAction(RecordService.CHECK_REMOTE);
        context.startService(startIntent);
    }
}
