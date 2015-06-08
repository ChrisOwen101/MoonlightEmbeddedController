package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
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

    @OnClick(R.id.loginButton)
    public void submit(View view) {
        loginButton.setIndeterminateProgressMode(true);
        loginButton.setProgress(1);

        device = new Device(ipaddressEditText.getText().toString(), loginEditText.getText().toString(), passwordEditText.getText().toString());
        SSHManager.getInstance().connectToSSH(getActivity(), device);
    }

    @Subscribe
    public void SSHConnectedEvent(SSHConnected event){
        SSHManager.getInstance().doesLimelightExist(getActivity(), device);
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
            Bundle b = new Bundle();
            b.putSerializable("device", device);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            PairFragment pair = new PairFragment();
            pair.setArguments(b);

            transaction.replace(R.id.container, pair);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            showAddMoonlightDialog();
        }
    }


    @Subscribe
    public void LimelightDownloadedEvent(final LimelightDownloadedEvent event){
        if(event.done ){
            device.directory = "limelight";
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
                .content("Enter the location of the limelight.jar file. (Do not include the filename and the file must be named limelight.jar))")
                .input("", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                        String dir = input.toString();
                        dir = dir.replace("/limelight.jar", "");
                        device.directory = dir;
                        SSHManager.getInstance().doesLimelightExist(getActivity(), device);
                    }
                }).show();
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
