package com.example.alexandro.myapplication.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.persistence.BackendlessDataQuery;
import com.example.alexandro.myapplication.R;
import com.example.alexandro.myapplication.model.User;
import com.example.alexandro.myapplication.presenter.ListFragmentPersistence;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Alexandro on 01.09.2016.
 */
public class ListFragment extends Fragment implements AdapterView.OnItemClickListener {
    @Bind(R.id.listView)
    ListView list;
    @Bind(R.id.emtyView)
    TextView emtyView;
    @Bind(R.id.refresh)
    SwipeRefreshLayout refresh;
    ListFragmentPersistence persistence;
    MainActivity mainActivity;
    private static final String TAG = ListFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.list_fragment, container, false);
        ButterKnife.bind(this, rootView);
        mainActivity = (MainActivity) getActivity();
        persistence = new ListFragmentPersistence(mainActivity, this);
        list.setOnItemClickListener(this);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadUsers();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUsers();
    }

    public void refreshList(String[] users) {
        Log.e(TAG, "refreshList " + users != null ? users.length + "" : "00");
        ArrayAdapter adapter = new ArrayAdapter(getActivity(),
                R.layout.list_item_block, R.id.itemName, users);


//        HashMap<Integer, HashMap<Integer, String>> usersMap = new HashMap<>();
//        for (int i = 0; i < users.length; i++) {
//            HashMap<Integer, String> values = new HashMap<>();
//            values.put(0, users[i]);
//            usersMap.put(i, values);
//        }
//        ListAdapter listAdapter = new ListAdapter(mainActivity, usersMap);
        list.setAdapter(adapter);
        emtyView.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        visibleRefresh(false);
    }

    public void visibleRefresh(boolean show) {
        refresh.setRefreshing(show);
    }

    public void showEmptyVIew() {
        emtyView.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    public void loadUsers() {
        persistence.getListUsers(getCurentUser());
    }

    public User getCurentUser() {
        return mainActivity.getCurrentUser();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String companionNickname = list.getItemAtPosition(position).toString();
//        ListAdapter listAdapter = (ListAdapter) list.getAdapter();
//        HashMap<Integer, String> values = listAdapter.getItem(position);
//        if(values.containsKey(0)){
//            String companionNickname =  values.get(0);
//            String whereClause = "nickname = '".concat(companionNickname).concat("'");
//            persistence.getUserClick(whereClause);
//            values.c
//        }else if(values.containsKey(-1)){
//            String companionNickname =  values.get(-1);
        String whereClause = "nickname = '".concat(companionNickname).concat("'");
        persistence.getUserClick(whereClause);
//        }
    }

    public void ShowMessage(String message) {
        Toast.makeText(mainActivity, message, Toast.LENGTH_LONG).show();
    }

}
