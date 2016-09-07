package com.example.alexandro.myapplication.presenter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.backendless.Backendless;
import com.example.alexandro.myapplication.model.BackEndLessModel;
import com.example.alexandro.myapplication.R;
import com.example.alexandro.myapplication.entitys.Defaults;
import com.example.alexandro.myapplication.interfaces.BackEndLessModelStatus;
import com.example.alexandro.myapplication.interfaces.MainPresenter;
import com.example.alexandro.myapplication.model.User;
import com.example.alexandro.myapplication.view.ListFragment;
import com.example.alexandro.myapplication.view.LockFragment;
import com.example.alexandro.myapplication.view.MainActivity;


/**
 * Created by Alexandro on 31.08.2016.
 */
public class MainPersistence implements MainPresenter, BackEndLessModelStatus {
    private final MainActivity mainActivity;
    private static final String TAG = MainPersistence.class.getSimpleName();
    private static int currentIndex;

    BackEndLessModel backModel;

    public MainPersistence(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        Log.e(TAG, "MainPersistence mainActivity " + mainActivity);
        Backendless.initApp(mainActivity.getApplicationContext(), Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION);

        backModel = BackEndLessModel.getInstance();
        backModel.init(mainActivity, this);
    }


    @Override
    public void OnCreate() {
        backModel.RegisterDevice();


    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void OnDestroy() {
        backModel.onDestroy();
    }

    public User getCurrentUser() {
        return backModel.getUser();
    }

    public void SetCurrentFragment(final int index, boolean addToBackStack, Bundle bundle) {
        Log.e(TAG, "SetCurentFragment " + index + " addToBackStack= " + addToBackStack);

        Fragment fragment = fragmentArray[index];
        if (bundle != null) {
            if (fragment.getArguments() == null) {
                fragment.setArguments(bundle);
            } else {
                fragment.getArguments().clear();
                fragment.getArguments().putAll(bundle);
            }
        }
        FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment, fragment.getClass().getSimpleName());
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getTag());
        } else {
            transaction.addToBackStack(null);
        }
        transaction.commit();
        currentIndex = index;

    }

    public final Fragment[] fragmentArray = new Fragment[]{
            new ListFragment(),//0
            new LockFragment(),//1
    };

    public boolean screenLock() {
        if (currentIndex == 1) {
            return true;
        }
        return false;
    }

    @Override
    public void userEntered(User user) {
        mainActivity.HideProgressBar();
        if (user != null) {
            backModel.setUser(user);
            ListFragment listFragment = (ListFragment) mainActivity.getSupportFragmentManager().findFragmentByTag(new ListFragment().getClass().getSimpleName());
            if (listFragment != null) listFragment.loadUsers();
        }
    }

    @Override
    public void deviceRegistered() {
        backModel.RegisterUser();
    }


}
