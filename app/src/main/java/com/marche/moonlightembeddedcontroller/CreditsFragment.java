package com.marche.moonlightembeddedcontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Chris.Owen on 01/06/2015.
 */
public class CreditsFragment extends Fragment {

    @InjectView(R.id.opensource)
    TextView openSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_credits, container, false);
        ButterKnife.inject(this, rootView);
        return rootView;
    }

}
