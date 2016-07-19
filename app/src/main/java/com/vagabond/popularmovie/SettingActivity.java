package com.vagabond.popularmovie;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/**
 * Created by HoaNV on 7/19/16.
 */
public class SettingActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        bindPreferenceSumaryToValue(findPreference(getString(R.string.pref_order_key)));
    }

    private void bindPreferenceSumaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);
        onPreferenceChange(preference, PreferenceManager
                .getDefaultSharedPreferences(preference.getContext())
                .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();

        if (preference instanceof ListPreference) {
            ListPreference listPrefs = (ListPreference) preference;
            int preIndex = listPrefs.findIndexOfValue(stringValue);
            if (preIndex >= 0) {
                preference.setSummary(listPrefs.getEntries()[preIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }

        return true;
    }
}
