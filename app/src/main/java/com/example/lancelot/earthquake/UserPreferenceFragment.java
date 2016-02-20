package com.example.lancelot.earthquake;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by Julius on 2016/2/20.
 */
public class UserPreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);

    }
}
