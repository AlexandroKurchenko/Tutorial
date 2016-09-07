package com.example.alexandro.myapplication.presenter;

import com.example.alexandro.myapplication.interfaces.MainPresenter;
import com.example.alexandro.myapplication.view.ListFragment;
import com.example.alexandro.myapplication.view.LockFragment;
import com.example.alexandro.myapplication.view.MainActivity;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class LockFragmentPersistence implements MainPresenter {


    private final MainActivity mainActivity;
    private final LockFragment lockFragment;

    public LockFragmentPersistence(MainActivity mainActivity, LockFragment lockFragment) {
        this.mainActivity = mainActivity;
        this.lockFragment = lockFragment;
    }

    @Override
    public void OnCreate() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void OnDestroy() {

    }
}
