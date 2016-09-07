package com.example.alexandro.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.backendless.messaging.PublishOptions;
import com.backendless.push.BackendlessBroadcastReceiver;
import com.example.alexandro.myapplication.view.MainActivity;

/**
 * Created by Alexandro on 31.08.2016.
 */
public class PushReceiver extends BackendlessBroadcastReceiver {
    private static String TAG = PushReceiver.class.getSimpleName();

    @Override
    public void onRegistered(Context context, String registrationId) {
        super.onRegistered(context, registrationId);
        Log.e(TAG, "onRegistered " + registrationId);

    }

    @Override
    public void onUnregistered(Context context, Boolean unregistered) {
        super.onUnregistered(context, unregistered);
        Log.e(TAG, "onUnregistered " + unregistered);
    }

    @Override
    public boolean onMessage(Context context, Intent intent) {
        Log.e(TAG, "onMessage " + intent);
        if (MainActivity.handler != null) {
            Message message = new Message();
            message.obj = intent.getStringExtra(PublishOptions.MESSAGE_TAG);
            MainActivity.handler.sendMessage(message);
        }

        return super.onMessage(context, intent);
    }

    @Override
    public void onError(Context context, String messageError) {
        Log.e(TAG, "onError " + messageError);
        if (MainActivity.handler != null) {
            Message message = new Message();
            message.obj = new Error(messageError);
            MainActivity.handler.sendMessage(message);
        }
    }
}
