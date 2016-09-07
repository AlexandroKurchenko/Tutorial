package com.example.alexandro.myapplication.view;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.example.alexandro.myapplication.R;
import com.example.alexandro.myapplication.model.KioskService;
import com.example.alexandro.myapplication.model.OnScreenOffReceiver;
import com.example.alexandro.myapplication.view.viewHelpers.CustomViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class LockFragment extends Fragment {
    @Bind(R.id.unlock)
    Button unlock;
    MainActivity mainActivity;
    OnScreenOffReceiver onScreenOffReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.lock_fragment, container, false);
        ButterKnife.bind(this, rootView);
        mainActivity = (MainActivity) getActivity();
        // for  fater boot
        mainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

        // disable status bar-----------------------------------------
        WindowManager manager = ((WindowManager) mainActivity
                .getSystemService(Context.WINDOW_SERVICE));

        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |

                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |

                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = (int) (50 * getResources()
                .getDisplayMetrics().scaledDensity);
        localLayoutParams.format = PixelFormat.TRANSPARENT;

        view = new CustomViewGroup(mainActivity);

        manager.addView(view, localLayoutParams);
        //-----------------------------------------

        registerKioskModeScreenOffReceiver();
        startKioskService();
        return rootView;
    }

    CustomViewGroup view;

    private void startKioskService() {
        mainActivity.startService(new Intent(mainActivity, KioskService.class));
    }

    private void stopKioskService() {
        mainActivity.stopService(new Intent(mainActivity, KioskService.class));
    }

    private void registerKioskModeScreenOffReceiver() {
        // register screen off receiver
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        onScreenOffReceiver = new OnScreenOffReceiver();
        mainActivity.registerReceiver(onScreenOffReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("TAG", "ondestryy");
        WindowManager manager = ((WindowManager) mainActivity
                .getSystemService(Context.WINDOW_SERVICE));
        manager.removeView(view);
        mainActivity.unregisterReceiver(onScreenOffReceiver);
        stopKioskService();

    }

    @OnClick(R.id.unlock)
    public void submit(View view) {
        onDestroy();
        mainActivity.UnlockDevice();
    }
}
