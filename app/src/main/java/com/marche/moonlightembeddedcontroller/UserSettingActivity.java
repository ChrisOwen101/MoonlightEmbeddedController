package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Chris on 08/06/2015.
 */
public class UserSettingActivity extends PreferenceActivity{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.user_settings);


    }

}
