package ir.asparsa.hobbytaste.core.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import junit.framework.Assert;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * @author hadi
 * @since 12/2/2016 AD
 */
@Singleton
public class PreferenceManager {

    public static final String PREFERENCES_KEY_AUTHENTICATION = "PREFERENCES_KEY_AUTHENTICATION";

    private static final String SHARED_PREFERENCES_NAME = PreferenceManager.class.getPackage().getName();
    private SharedPreferences mSharedPreferences;

    private final Object committerSync = new Object();

    @Inject
    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public void put(@NonNull Map<String, Object> entries) {
        synchronized (committerSync) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            for (String key : entries.keySet()) {
                Object value = entries.get(key);
                if (value instanceof String) {
                    editor.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    editor.putInt(key, (Integer) value);
                } else if (value instanceof Long) {
                    editor.putLong(key, (Long) value);
                } else if (value instanceof Boolean) {
                    editor.putBoolean(key, (Boolean) value);
                } else if (value instanceof Float) {
                    editor.putFloat(key, (Float) value);
                } else {
                    Assert.fail("Try to save invalid value to shared preferences: " + value + ", " + value.getClass());
                }
            }
            editor.apply();
        }
    }

    public void put(@NonNull String key, String value) {
        synchronized (committerSync) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(key, value);
            editor.apply();
        }
    }

    public String get(@NonNull String key, @Nullable String defaultValue) {
        synchronized (committerSync) {
            return mSharedPreferences.getString(key, defaultValue);
        }
    }

    public Integer get(@NonNull String key, @NonNull Integer defaultValue) {
        synchronized (committerSync) {
            return mSharedPreferences.getInt(key, defaultValue);
        }
    }

    public Long get(@NonNull String key, @NonNull Long defaultValue) {
        synchronized (committerSync) {
            return mSharedPreferences.getLong(key, defaultValue);
        }
    }

    public Boolean get(@NonNull String key, @NonNull Boolean defaultValue) {
        synchronized (committerSync) {
            return mSharedPreferences.getBoolean(key, defaultValue);
        }
    }

    public Float get(@NonNull String key, @NonNull Float defaultValue) {
        synchronized (committerSync) {
            return mSharedPreferences.getFloat(key, defaultValue);
        }
    }
}
