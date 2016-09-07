package com.example.alexandro.myapplication.presenter;

import android.os.Build;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.MessageStatus;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PublishStatusEnum;
import com.backendless.messaging.PushPolicyEnum;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.alexandro.myapplication.R;
import com.example.alexandro.myapplication.entitys.Defaults;
import com.example.alexandro.myapplication.interfaces.MainPresenter;
import com.example.alexandro.myapplication.model.User;
import com.example.alexandro.myapplication.view.ListFragment;
import com.example.alexandro.myapplication.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class ListFragmentPersistence implements MainPresenter {
    private static final String TAG = ListFragmentPersistence.class.getSimpleName();
    private final MainActivity mainActivity;
    private final ListFragment listFragment;

    public ListFragmentPersistence(MainActivity mainActivity, ListFragment listFragment) {
        this.mainActivity = mainActivity;
        this.listFragment = listFragment;
    }

    @Override
    public void OnCreate() {

    }

    @Override
    public void onResume() {
        getListUsers(listFragment.getCurentUser());
    }

    @Override
    public void onPause() {

    }

    @Override
    public void OnDestroy() {

    }

    public void getUserClick(String whereClause) {
        BackendlessDataQuery backendlessDataQuery = new BackendlessDataQuery();
        backendlessDataQuery.setWhereClause(whereClause);
        Backendless.Persistence.of(User.class).find(backendlessDataQuery, new BackendlessCallback<BackendlessCollection<User>>() {
            @Override
            public void handleResponse(BackendlessCollection<User> response) {

                onCompanionFound(response.getCurrentPage().iterator().next());
            }
        });
    }

    private void onCompanionFound(final User user) {
        PublishOptions publishOptions = new PublishOptions();
        DeliveryOptions deliveryOptions = new DeliveryOptions();
        deliveryOptions.setPushPolicy(PushPolicyEnum.ONLY);
        deliveryOptions.addPushSinglecast(user.getDeviceId());

        final String message_subtopic = listFragment.getString(R.string.block_message) + " " + user.getNickname();//User.currentUser().getNickname().concat("_with_").concat( );

        Backendless.Messaging.publish(Defaults.CHANNEL_NAME, message_subtopic, publishOptions, deliveryOptions, new BackendlessCallback<MessageStatus>() {
            @Override
            public void handleResponse(MessageStatus response) {

                PublishStatusEnum messageStatus = response.getStatus();

                if (messageStatus == PublishStatusEnum.SCHEDULED) {
                    /*user blocked
                    anyway push send and status retrieved
                     */
                    listFragment.ShowMessage("User blocked");
                } else {
                    Log.e(TAG, "block push send to " + user.getNickname() + " status " + messageStatus.toString());
                }
            }
        });
    }

    public void getListUsers(final User user) {
        Log.e(TAG, "getListUser");
        Backendless.Persistence.of(User.class).find(new BackendlessCallback<BackendlessCollection<User>>() {
            @Override
            public void handleResponse(BackendlessCollection<User> response) {
                if (response == null) {
                    listFragment.showEmptyVIew();
                    listFragment.visibleRefresh(false);
                    return;
                }
                String[] usersArray = removeNulls(response, user);
                if (usersArray.length == 0) {
                    listFragment.showEmptyVIew();
                    listFragment.visibleRefresh(false);
                    return;
                }
                listFragment.refreshList(usersArray);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                String notExistingTableErrorCode = "1009";
                if (fault.getCode().equals(notExistingTableErrorCode)) {
                            listFragment.showEmptyVIew();
                            listFragment.visibleRefresh(false);
                            Log.e(TAG, "table not found");
                } else {
                    super.handleFault(fault);
                }

            }
        });
    }

    private String[] removeNulls(BackendlessCollection<User> users, User currentUser) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < users.getCurrentPage().size(); i++) {
            User chatUser = users.getCurrentPage().get(i);
            if (chatUser.getNickname() != null && chatUser.getDeviceId() != null && !chatUser.getDeviceId().isEmpty() && !chatUser.getNickname().isEmpty()) {
                if (currentUser != null && !chatUser.getDeviceId().equals(currentUser.getDeviceId())) {
                    result.add(chatUser.getNickname());
                }
            }
        }

        return result.toArray(new String[result.size()]);
    }

}
