package com.daahae.damoyeo2.view.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.daahae.damoyeo2.R;
import com.daahae.damoyeo2.databinding.ItemFriendBinding;
import com.daahae.damoyeo2.model.Friend;
import com.daahae.damoyeo2.view_model.FriendsModel;

import java.util.ArrayList;

public class FriendsAdapter extends ArrayAdapter<FriendsModel>{

    private ArrayList<FriendsModel> friendsModelArrayList;
    private Context context;

    public FriendsAdapter(@NonNull Context context, ArrayList<FriendsModel> friendsModelArrayList) {
        super(context, R.layout.item_friend, friendsModelArrayList);
        this.context = context;
        this.friendsModelArrayList = friendsModelArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFriendBinding itemFriendBinding = DataBindingUtil.inflate(layoutInflater,R.layout.item_friend,parent,false);
        itemFriendBinding.setModel(friendsModelArrayList.get(position));

        return itemFriendBinding.getRoot();
    }
}
