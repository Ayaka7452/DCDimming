package xyz.cirno.pseudodcdimming;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * No-UI entry point used by the launcher long-press shortcuts.
 * Enables or disables the DC dimming override without showing any UI,
 * then finishes immediately.
 */
public class ToggleActivity extends Activity {
    private static final String TAG = "ToggleActivity";
    public static final String EXTRA_ENABLED = "enabled";

    @Override
    @SuppressLint("WorldReadableFiles")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean enable = false;
        try {
            final Intent intent = getIntent();
            if (intent != null) {
                final Object raw = intent.hasExtra(EXTRA_ENABLED)
                        ? intent.getExtras().get(EXTRA_ENABLED) : null;
                if (raw instanceof Boolean) {
                    enable = (Boolean) raw;
                } else if (raw != null) {
                    enable = Boolean.parseBoolean(String.valueOf(raw));
                }
            }
            final var service = ServiceDiscovery.getService();
            if (service == null) {
                Log.w(TAG, "service not available");
            } else {
                final var pref = service.getPreference();
                pref.enabled = enable;
                service.putPreference(pref);
                try {
                    final SharedPreferences xsp = getSharedPreferences("config", Context.MODE_WORLD_READABLE);
                    xsp.edit().putBoolean("enabled", enable).apply();
                } catch (SecurityException ignored) {
                    // world-readable prefs are blocked on newer Android, same as SettingsActivity
                }
            }
        } catch (Exception ignored) {
            // never crash on a shortcut tap
        } finally {
            finish();
        }
    }
}
