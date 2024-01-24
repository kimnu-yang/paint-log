package com.diary.paintlog.view.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.diary.paintlog.R


class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}