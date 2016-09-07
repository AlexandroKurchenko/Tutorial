package com.example.alexandro.myapplication.view;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.alexandro.myapplication.R;
import com.example.alexandro.myapplication.model.User;
import com.example.alexandro.myapplication.presenter.MainPersistence;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    MainPersistence persistence;
    public static Handler handler;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        handler = new Handler(callback);
        if (persistence == null) {
            persistence = new MainPersistence(this);
        }
//        persistence.OnCreate();
//        if (getIntent().getBooleanExtra("restore", false)) {
//            persistence.SetCurrentFragment(1, false, null);
//        } else {
//            persistence.SetCurrentFragment(0, false, null);
//        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {




                return null;
            }
        }.execute();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        persistence.OnDestroy();

    }

    public void ShowProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void HideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public User getCurrentUser() {
        return persistence.getCurrentUser();
    }

    public void UnlockDevice() {
        persistence.SetCurrentFragment(0, false, null);
    }

    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (!(message.obj instanceof Error)) {
                Log.e(TAG, "message input to mainactivity " + message.obj.toString());
                if (message.obj.toString().contains(getString(R.string.block_message))) {
                    persistence.SetCurrentFragment(1, false, null);
                }
            } else {

                Log.e(TAG, "handleMessage error " + ((Error) message.obj).getMessage());
            }
            return true;
        }
    };


    @Override
    public void onBackPressed() {
        if (!persistence.screenLock()) {

            finish();
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {


        List blockedKeys = new ArrayList(Arrays.asList(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP));

        if (persistence.screenLock() && blockedKeys.contains(event.getKeyCode())) {
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (persistence.screenLock()) {
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).moveTaskToFront(getTaskId(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (persistence.screenLock()) {
            if (!hasFocus) {
                // Close every kind of system dialog
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
            }
        }
    }

}

