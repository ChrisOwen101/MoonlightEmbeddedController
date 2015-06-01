package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.marche.moonlightembeddedcontroller.Events.LimelightExistsEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class LaunchFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SSHManager.getInstance().SSHBus.register(this);

        //SSHManager.getInstance().connectToSSH(getActivity(), "10.44.220.224", "Chris Owen", "Redball30");
    }

    @Subscribe
    public void SSHConnectedEvent(SSHConnected event){
        SSHManager.getInstance().doesLimelightExist(getActivity());
    }

    @Subscribe
    public void LimelightExistsEvent(LimelightExistsEvent event){
        if(event.doesExist){
            Log.d("EXIST", "EXIST");
        } else {
            SSHManager.getInstance().createFolderAndDownloadFiles(getActivity());
        }
    }
}
