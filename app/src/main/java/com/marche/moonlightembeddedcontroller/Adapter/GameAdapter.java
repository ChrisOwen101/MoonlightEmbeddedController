package com.marche.moonlightembeddedcontroller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.marche.moonlightembeddedcontroller.POJO.Result;
import com.marche.moonlightembeddedcontroller.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Chris on 09/06/2015.
 */
public class GameAdapter extends ArrayAdapter<Result> {

    public GameAdapter(Context context, int resource, ArrayList<Result> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            LayoutInflater vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.game_list, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }

        Result r = getItem(position);
        holder.name.setText(r.getName());

        if(r.getName().equalsIgnoreCase("Steam")){
            Glide.with(getContext())
                    .load(R.drawable.steam)
                    .centerCrop()
                    .crossFade()
                    .thumbnail(0.1f)
                    .into(holder.picture);
        } else {
            if(r.getImage() != null && r.getImage().getScreenUrl() != null){
                Glide.with(getContext())
                        .load(r.getImage().getScreenUrl())
                        .centerCrop()
                        .thumbnail(0.1f)
                        .crossFade()
                        .into(holder.picture);
            }
        }



        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView name;
        @InjectView(R.id.picture)
        ImageView picture;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
