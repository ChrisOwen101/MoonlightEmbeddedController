package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.marche.moonlightembeddedcontroller.Events.LimelightExistsEvent;
import com.marche.moonlightembeddedcontroller.Events.SSHConnected;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;
import com.squareup.otto.Subscribe;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            SSHManager.getInstance().SSHBus.register(this);

            SSHManager.getInstance().connectToSSH(getActivity(), "10.44.220.224", "Chris Owen", "Redball30");
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
}
