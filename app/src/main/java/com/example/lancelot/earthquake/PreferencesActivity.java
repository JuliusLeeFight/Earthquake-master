package com.example.lancelot.earthquake;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaActionSound;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by Lancelot on 2016/2/17.
 */
public class PreferencesActivity extends PreferenceActivity {

    public static final String  PREF_MIN_MAG = "PREF_MIN_MAG";
    public static final String  PREF_UPDATE_FREQ= "PREF_UPDATE_FREQ";
    public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);

    }

    /*
public static final String USER_PREFERENCE = "USER_PREFERENCE";
public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
public static final String PREF_MIN_MAG_INDEX = "PREF_MIN_MAG_INDEX";
public static final String PREF_UPDATE_FREQ_INDEX = "PREF_UPDATE_FREQ_INDEX";

SharedPreferences sharedPreferences;
CheckBox autoUpdate;
Spinner updateFreqSpinner;
Spinner magnitudeSpinner;

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.preferences);
updateFreqSpinner = (Spinner)findViewById(R.id.spinner_update_freq);
magnitudeSpinner = (Spinner)findViewById(R.id.spinner_quake_mag);
autoUpdate =(CheckBox)findViewById(R.id.checkbox_auto_update);

populateSpinner();
Context context = getApplicationContext();
sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
UpdateUIFromPreferences();

Button okbutton = (Button)findViewById(R.id.okButton);
okbutton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
savePreference();
PreferencesActivity.this.setResult(RESULT_OK);
finish();
}
});
Button cancelButton = (Button)findViewById(R.id.cancelButton);

cancelButton.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
PreferencesActivity.this.setResult(RESULT_CANCELED);
finish();
}
});
}

private void savePreference() {
int updateIndex = updateFreqSpinner.getSelectedItemPosition();
int minMagIndex = magnitudeSpinner.getSelectedItemPosition();
boolean autoUpdateChecked = autoUpdate.isChecked();

SharedPreferences.Editor editor = sharedPreferences.edit();
editor.putBoolean(PREF_AUTO_UPDATE, autoUpdateChecked);
editor.putInt(PREF_UPDATE_FREQ_INDEX, updateIndex);
editor.putInt(PREF_MIN_MAG_INDEX, minMagIndex);
editor.commit();
}

private void UpdateUIFromPreferences() {

boolean autoUpdateChecked = sharedPreferences.getBoolean(PREF_AUTO_UPDATE,false);
int updateFreqIndex = sharedPreferences.getInt(PREF_UPDATE_FREQ_INDEX, 2);
int minMagIndex  =sharedPreferences.getInt(PREF_MIN_MAG_INDEX,0);
updateFreqSpinner.setSelection(updateFreqIndex);
magnitudeSpinner.setSelection(minMagIndex);
autoUpdate.setChecked(autoUpdateChecked);

}

private void populateSpinner() {

ArrayAdapter<CharSequence> fAdapter;
fAdapter = ArrayAdapter.createFromResource(this,R.array.update_freq_options,android.R.layout.simple_spinner_item);
int spinner_dd_item = android.R.layout.simple_spinner_dropdown_item;
fAdapter.setDropDownViewResource(spinner_dd_item);
updateFreqSpinner.setAdapter(fAdapter);

ArrayAdapter<CharSequence> mAdapter;
mAdapter = ArrayAdapter.createFromResource(this,R.array.magnitude_options,android.R.layout.simple_spinner_item);
mAdapter.setDropDownViewResource(spinner_dd_item);
magnitudeSpinner.setAdapter(mAdapter);
}
*/


}
