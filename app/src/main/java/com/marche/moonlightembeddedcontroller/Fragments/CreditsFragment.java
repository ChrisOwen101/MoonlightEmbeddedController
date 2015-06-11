package com.marche.moonlightembeddedcontroller.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.marche.moonlightembeddedcontroller.R;

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

        openSource.setClickable(true);
        openSource.setMovementMethod(LinkMovementMethod.getInstance());

        return rootView;
    }

}
