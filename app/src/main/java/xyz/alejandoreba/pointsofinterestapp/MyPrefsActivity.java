package xyz.alejandoreba.pointsofinterestapp;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
//task 4
public class MyPrefsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
