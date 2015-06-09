package com.marche.moonlightembeddedcontroller;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.marche.moonlightembeddedcontroller.SSH.SSHManager;

/**
 * Created by Chris on 08/06/2015.
 */
public class UserSettingActivity extends PreferenceActivity {

    Context con = this;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.user_settings);

        Preference deletePref = (Preference) findPreference("mappings");
        deletePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(con)
                        .title("Controller Mappings")
                        .items(R.array.mappingsArray)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                if (text.equals("Xbox")) {
                                    SSHManager.getInstance().downloadMappings(con , "https://raw.githubusercontent.com/RoelofBerg/limelightpisteambox/master/xbox.map");
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(con).edit();
                                    editor.putString("mappings", "xbox.map");
                                    editor.commit();
                                }
                                return true;
                            }
                        })
                        .positiveText("Download & Use")
                        .show();
                return true;
            }
        });


    }
}
