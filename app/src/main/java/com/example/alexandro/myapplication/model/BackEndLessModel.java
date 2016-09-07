package com.example.alexandro.myapplication.model;

import android.os.Build;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.DeviceRegistration;
import com.backendless.Messaging;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.Message;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.alexandro.myapplication.entitys.Defaults;
import com.example.alexandro.myapplication.interfaces.BackEndLessModelStatus;
import com.example.alexandro.myapplication.presenter.MainPersistence;
import com.example.alexandro.myapplication.view.MainActivity;

import java.util.List;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class BackEndLessModel {

    private static BackEndLessModel ourInstance = new BackEndLessModel();
    private static String TAG = BackEndLessModel.class.getSimpleName();
    private DeviceRegistration deviceRegistration;
    private Subscription subscription;
    private User user;
    BackEndLessModelStatus status;
    MainActivity mainActivity;

    public static BackEndLessModel getInstance() {
        return ourInstance;
    }

    private BackEndLessModel() {

    }

    public void init(MainActivity mainActivity, MainPersistence mainPresenter) {
        this.status = mainPresenter;
        this.mainActivity = mainActivity;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public DeviceRegistration getDeviceReg() {
        return deviceRegistration;
    }

    public BackendlessUser RegisterUser() {
        final String deviceId = Build.SERIAL;
        mainActivity.ShowProgressBar();
        User.currentUser().setNickname(deviceId);
        User.currentUser().setDeviceId(deviceId);

        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        backendlessDataQuery.setWhereClause("nickname='" + User.currentUser().getNickname() + "'");
        Backendless.Persistence.of(User.class).find(backendlessDataQuery,
                new BackendlessCallback<BackendlessCollection<User>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<User> response) {

                        if (response.getCurrentPage().isEmpty()) {
                            Backendless.Persistence.of(User.class).save(User.currentUser(), new BackendlessCallback<User>() {
                                @Override
                                public void handleResponse(User response) {
                                    User.currentUser().setObjectId(response.getObjectId());
                                    status.userEntered(User.currentUser());
                                }
                            });
                        } else {
                            User foundUser = response.getCurrentPage().iterator().next();
                            User.currentUser().setObjectId(foundUser.getObjectId());
                            if (!User.currentUser().getDeviceId().equals(foundUser.getDeviceId())) {
                                Backendless.Persistence.of(User.class).save(User.currentUser(), new BackendlessCallback<User>() {
                                    @Override
                                    public void handleResponse(User response) {
                                        status.userEntered(User.currentUser());
                                    }
                                });
                            } else {
                                status.userEntered(User.currentUser());
                            }
                        }
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        String notExistingTableErrorCode = "1009";
                        if (fault.getCode().equals(notExistingTableErrorCode)) {

                            Backendless.Persistence.of(User.class).save(User.currentUser(), new BackendlessCallback<User>() {
                                @Override
                                public void handleResponse(User user) {
                                    User.currentUser().setObjectId(user.getObjectId());
                                    status.userEntered(User.currentUser());
                                }


                            });
                        } else {
                            super.handleFault(fault);
                        }
                    }
                });
        return null;
    }

    public DeviceRegistration RegisterDevice() {
        mainActivity.ShowProgressBar();
        Backendless.Messaging.registerDevice(Defaults.SENDER_ID, Defaults.CHANNEL_NAME);
        Messaging messaging = Backendless.Messaging;
        if (messaging != null) {
            try {
                messaging.getDeviceRegistration(new AsyncCallback<DeviceRegistration>() {
                    @Override
                    public void handleResponse(DeviceRegistration deviceRegistration) {
                        BackEndLessModel.this.deviceRegistration = deviceRegistration;
                        mainActivity.HideProgressBar();
                        status.deviceRegistered();
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        mainActivity.HideProgressBar();
                    }
                });

            } catch (BackendlessException exception) {
                // either device is not registered or an error occurred during de-registration
                exception.printStackTrace();
                mainActivity.HideProgressBar();
            }

        } else {
            Log.e(TAG, "messaging handleFault is null");
            mainActivity.HideProgressBar();
        }
        return null;
    }


    public void onDestroy() {
        try {
            if (subscription != null) {
                subscription.cancelSubscription();
            }
            Backendless.Messaging.unregisterDevice();
        } catch (BackendlessException exception) {
            // either device is not registered or an error occurred during de-registration
        }
    }
}
