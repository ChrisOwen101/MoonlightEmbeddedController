package com.marche.moonlightembeddedcontroller;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.marche.moonlightembeddedcontroller.Events.PairEvent;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class PairFragment extends Fragment {

    Device device;

    @InjectView(R.id.loginButton)
    CircularProgressButton loginButton;

    @InjectView(R.id.nextCard)
    CardView next;

    @InjectView(R.id.nextTextCard)
    CardView nextText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair, container, false);
        ButterKnife.inject(this, rootView);

        Bundle bundle = this.getArguments();
        device = (Device) bundle.getSerializable("device");

        return rootView;
    }

    @Subscribe
    public void PairEvent(PairEvent event){
        if(event.didPair){
            loginButton.setIndeterminateProgressMode(false);
            loginButton.setProgress(100);
            loginButton.setCompleteText("Paired!");

            next.setVisibility(View.VISIBLE);
        } else {
            if(event.pairCode.isEmpty()){
                showErrorDialog();
            } else {
                loginButton.setIndeterminateProgressMode(false);
                loginButton.setProgress(0);
                loginButton.setIdleText(event.pairCode);
            }
        }
    }

    public void showErrorDialog(){
        loginButton.setProgress(-1);

        new MaterialDialog.Builder(getActivity())
                .title("Woops")
                .content("Looks like I dropped the ball there sorry. Try going to Geforce Experience on your computer, go to settings, then click Forget Devices. Click the button above to give this another shot.")
                .positiveText("Righty Oh'")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        loginButton.setProgress(0);
                        loginButton.setIdleText("Pair");
                    }
                })
                .show();
    }

    public void saveDevice(){
        Gson gson = new Gson();
        String deviceString = gson.toJson(device);

        SharedPreferences.Editor editor = getActivity().getPreferences(getActivity().MODE_PRIVATE).edit();
        editor.putString("device", deviceString);
        editor.apply();
    }

    @OnClick(R.id.loginButton)
    public void submit(View view) {
        loginButton.setIndeterminateProgressMode(true);
        loginButton.setProgress(1);

        SSHManager.getInstance().pairComputer(getActivity());
    }

    @OnClick(R.id.Next)
    public void next(View view) {
        saveDevice();

        FragmentManager fm = getFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        Bundle b = new Bundle();
        b.putSerializable("device", device);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
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
