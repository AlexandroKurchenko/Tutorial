package com.example.alexandro.myapplication.view.viewHelpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.alexandro.myapplication.R;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Alexandro on 02.09.2016.
 */
public class ListAdapter extends BaseAdapter {
    private final HashMap<Integer, HashMap<Integer, String>> users;
    Activity activity;

    public ListAdapter(Activity activity, HashMap<Integer, HashMap<Integer, String>> users) {
        this.activity = activity;
        this.users = users;

    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public HashMap<Integer, String> getItem(int position) {
        HashMap<Integer, String> obj = users.get(position);
        return obj;
    }

    public HashMap<Integer, HashMap<Integer, String>> getData() {
        return users;
    }

    public void changeValue(int pos, int oldKey, int newKey) {
        HashMap<Integer, String> valOld = users.get(pos);
        String val = valOld.remove(oldKey);
        valOld.put(newKey, val);
        users.remove(pos);
        users.put(pos, valOld);
        this.notifyDataSetChanged();

    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder {
        public ImageView blockImg;
        public TextView itemName;
        public LinearLayout relativeLayout;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_block, parent, false);
            viewHolder.relativeLayout = (LinearLayout) convertView.findViewById(R.id.relativeLayout);
            viewHolder.itemName = (TextView) convertView.findViewById(R.id.itemName);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // show content of input list with data
        HashMap<Integer, String> obj = users.get(position);
        Iterator it = obj.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            int status = (int) pair.getKey();
            if (status == -1) {
                viewHolder.relativeLayout.setBackgroundColor(activity.getResources().getColor(android.R.color.holo_red_light));
            }
            String name = (String) pair.getValue();
            viewHolder.itemName.setText(name);
            it.remove(); // avoids a ConcurrentModificationException
        }
        return convertView;
    }


}
