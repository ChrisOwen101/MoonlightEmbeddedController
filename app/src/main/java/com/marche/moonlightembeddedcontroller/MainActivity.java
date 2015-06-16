package com.marche.moonlightembeddedcontroller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.marche.moonlightembeddedcontroller.Events.RefreshGames;
import com.marche.moonlightembeddedcontroller.Fragments.CreditsFragment;
import com.marche.moonlightembeddedcontroller.Fragments.GameFragment;
import com.marche.moonlightembeddedcontroller.Fragments.LaunchFragment;
import com.marche.moonlightembeddedcontroller.POJO.Device;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends ActionBarActivity {

    MenuItem actionSettings;
    MenuItem actionReload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            SharedPreferences prefs = getPreferences(MODE_PRIVATE);
            String deviceString = prefs.getString("device", "");

            if(!deviceString.isEmpty()){
                Gson gson = new Gson();
                Device device = gson.fromJson(deviceString, Device.class);

                Bundle b = new Bundle();
                b.putSerializable("device", device);

                GameFragment game = new GameFragment();
                game.setArguments(b);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, game)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new LaunchFragment())
                        .commit();
            }

        }

        setupActionBar();
    }

    public void setupActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Moonlight Controller");
        actionBar.setIcon(R.mipmap.ic_launcher);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), UserSettingActivity.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_reload) {
            SSHManager.getInstance().SSHBus.post(new RefreshGames());
            return true;
        } else if (id == R.id.action_credits) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, new CreditsFragment());
            transaction.addToBackStack(null);
            transaction.commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
