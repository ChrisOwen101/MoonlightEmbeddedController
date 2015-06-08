package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.marche.moonlightembeddedcontroller.Events.GotGamesEvent;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class GameFragment extends Fragment {

    Device device;

    @InjectView(R.id.progress_circular)
    CircleProgressBar loadingSpinner;

    @InjectView(R.id.listView)
    ListView listView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SSHManager.getInstance().SSHBus.register(this);
    }

    @Subscribe
    public void GotGamesEvent(GotGamesEvent event){
        loadingSpinner.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
    }
}
