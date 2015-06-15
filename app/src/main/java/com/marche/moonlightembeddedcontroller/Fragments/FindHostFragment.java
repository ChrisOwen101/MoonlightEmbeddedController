package com.marche.moonlightembeddedcontroller.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.R;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class FindHostFragment extends Fragment {

    Device device;

    @InjectView(R.id.ipaddress)
    EditText ipaddressEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_findhost, container, false);
        ButterKnife.inject(this, rootView);

        Bundle bundle = this.getArguments();
        device = (Device) bundle.getSerializable("device");

        return rootView;
    }

    @OnClick(R.id.ipaddress)
    public void getIPAddress(View view) {
        openSearchIPDialog();
    }

    @OnClick(R.id.skipButton)
    public void skip(View view) {
        goToNextScreen();
    }

    @OnClick(R.id.nextButton)
    public void next(View view) {

        device.hostIP = ipaddressEditText.getText().toString();
        goToNextScreen();
    }

    public void openSearchIPDialog(){
        aa = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, deviceNames);
        new MaterialDialog.Builder(getActivity())
                .title("Search For Device")
                .adapter(aa,
                        new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                                ipaddressEditText.setText(deviceIPs.get(which));
                                dialog.dismiss();
                            }
                        })
                .show();

        scanSubNet("192.168.1.");
    }

    ArrayList<String> deviceIPs = new ArrayList<>();
    ArrayList<String> deviceNames = new ArrayList<>();
    ArrayAdapter<String> aa;

    private void scanSubNet(final String subnet){
        deviceIPs = new ArrayList<>();
        deviceNames = new ArrayList<>();

        for(int i=1; i<255; i++){
            final int j = i;
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        InetAddress inetAddress = InetAddress.getByName(subnet + String.valueOf(j));
                        if(inetAddress.isReachable(1000)){
                            Log.d("scan", inetAddress.getHostName());

                            deviceNames.add(inetAddress.getHostName().replace(".home", ""));
                            deviceIPs.add(subnet + String.valueOf(j));

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    aa.clear();
                                    aa.addAll(deviceNames);
                                    aa.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }
    }

    public void goToNextScreen(){

        FragmentManager fm = getFragmentManager();
        fm.popBackStack("all", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Bundle b = new Bundle();
        b.putSerializable("device", device);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter, R.anim.exit);

        GameFragment pair = new GameFragment();
        pair.setArguments(b);

        transaction.replace(R.id.container, pair);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        SSHManager.getInstance().SSHBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SSHManager.getInstance().SSHBus.unregister(this);
    }


}
