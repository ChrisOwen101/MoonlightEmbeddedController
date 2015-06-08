package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dd.CircularProgressButton;
import com.marche.moonlightembeddedcontroller.Events.LimelightDownloadedEvent;
import com.marche.moonlightembeddedcontroller.Events.LimelightExistsEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.Events.SSHError;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class LaunchFragment extends Fragment {

    @InjectView(R.id.loginButton)
    CircularProgressButton loginButton;

    @InjectView(R.id.ipaddress)
    EditText ipaddressEditText;

    @InjectView(R.id.login)
    EditText loginEditText;

    @InjectView(R.id.password)
    EditText passwordEditText;

    Device device;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_launch, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SSHManager.getInstance().SSHBus.register(this);
    }

    @OnClick(R.id.loginButton)
    public void submit(View view) {
        loginButton.setIndeterminateProgressMode(true);
        loginButton.setProgress(1);

        device = new Device(ipaddressEditText.getText().toString(), loginEditText.getText().toString(), passwordEditText.getText().toString());
        SSHManager.getInstance().connectToSSH(getActivity(), device);
    }

    @Subscribe
    public void SSHConnectedEvent(SSHConnected event){
        SSHManager.getInstance().doesLimelightExist(getActivity());
    }

    @Subscribe
    public void SSHError(SSHError event){
        loginButton.setProgress(-1);

        new MaterialDialog.Builder(getActivity())
                .title("Device not found")
                .content("Oh dear oh dear. Are you sure you put in the correct details?")
                .positiveText("Let me try that again.")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        loginButton.setProgress(0);
                    }
                })
                .show();
    }

    @Subscribe
    public void LimelightExistsEvent(LimelightExistsEvent event){
        if(event.doesExist){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new PairFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            showAddMoonlightDialog();
        }
    }


    @Subscribe
    public void LimelightDownloadedEvent(final LimelightDownloadedEvent event){
        if(event.done ){
            device.directory = "limelight/limelight.jar";
            loginButton.setProgress(100);
        } else {
            if(event.percentage == -1){
                loginButton.setIndeterminateProgressMode(true);
                loginButton.setProgress(1);
            } else if(event.percentage != 100){
                loginButton.setIndeterminateProgressMode(false);
                System.out.println(event.percentage);
                loginButton.setProgress(event.percentage);
            }
        }
    }

    public void showAddMoonlightDialog(){
        loginButton.setProgress(-1);

        new MaterialDialog.Builder(getActivity())
                .title("Moonlight Was Not Found")
                .content("Hmmm... Seems like Moonlight could not be found. Should I download it? (This may take a couple of minutes)")
                .positiveText("Yep, get downloadin'")
                .negativeText("Nope, it's on there. Let me find it.")
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        loginButton.setProgress(0);
                        loginButton.setProgress(50);
                        SSHManager.getInstance().createFolderAndDownloadFiles(getActivity());
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                        showDirectoryDialog();
                    }
                })
                .show();
    }

    public void showDirectoryDialog(){
        new MaterialDialog.Builder(getActivity())
                .title("Directory")
                .content("Enter the location of the limelight.jar file.")
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        device.directory = input.toString();
                        SSHManager.getInstance().doesLimelightExist(getActivity(), input.toString());
                    }
                }).show();
    }
}
