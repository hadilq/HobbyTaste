package ir.asparsa.android.core.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import junit.framework.Assert;

/**
 * Created by hadi on 12/14/2016 AD.
 */
public class CorePreferencesManager {

    private static final String VERSIONED = "VERSIONED";
    private static final String NOT_VERSIONED = "NOT_VERSIONED";

    private final SharedPreferences mVersionedSharedPreferences;
    private final SharedPreferences mNotVersionedSharedPreferences;

    public CorePreferencesManager(Context context) {
        mVersionedSharedPreferences = context.getSharedPreferences(VERSIONED, Context.MODE_PRIVATE);
        mNotVersionedSharedPreferences = context.getSharedPreferences(NOT_VERSIONED, Context.MODE_PRIVATE);
    }

    protected static String getVersionedKey(@NonNull String key) {
        return VERSIONED + key;
    }

    protected static String getNotVersionedKey(@NonNull String key) {
        return NOT_VERSIONED + key;
    }

    @NonNull
    private SharedPreferences getProperSharedPreferences(@NonNull String key) {
        if (key.startsWith(VERSIONED)) {
            return mVersionedSharedPreferences;
        } else if (key.startsWith(NOT_VERSIONED)) {

            return mNotVersionedSharedPreferences;
        }
        Assert.fail("Key must be either versioned or not versioned");
        return null;
    }

    public boolean getBoolean(
            @NonNull String key,
            boolean defValue
    ) {
        return getProperSharedPreferences(key).getBoolean(key, defValue);
    }

    public float getFloat(
            @NonNull String key,
            float defValue
    ) {
        return getProperSharedPreferences(key).getFloat(key, defValue);
    }

    public int getInt(
            @NonNull String key,
            int defValue
    ) {
        return getProperSharedPreferences(key).getInt(key, defValue);
    }

    public long getLong(
            @NonNull String key,
            long defValue
    ) {
        return getProperSharedPreferences(key).getLong(key, defValue);
    }

    public String getString(
            @NonNull String key,
            String defValue
    ) {
        return getProperSharedPreferences(key).getString(key, defValue);
    }

    public void put(
            @NonNull String key,
            boolean value
    ) {
        SharedPreferences.Editor sharedPreferencesEditor = getProperSharedPreferences(key).edit();
        sharedPreferencesEditor.putBoolean(key, value);
        sharedPreferencesEditor.apply();
    }

    public void put(
            @NonNull String key,
            float value
    ) {
        SharedPreferences.Editor sharedPreferencesEditor = getProperSharedPreferences(key).edit();
        sharedPreferencesEditor.putFloat(key, value);
        sharedPreferencesEditor.apply();
    }

    public void put(
            @NonNull String key,
            int value
    ) {
        SharedPreferences.Editor sharedPreferencesEditor = getProperSharedPreferences(key).edit();
        sharedPreferencesEditor.putInt(key, value);
        sharedPreferencesEditor.apply();
    }

    public void put(
            @NonNull String key,
            long value
    ) {
        SharedPreferences.Editor sharedPreferencesEditor = getProperSharedPreferences(key).edit();
        sharedPreferencesEditor.putLong(key, value);
        sharedPreferencesEditor.apply();
    }

    public void put(
            @NonNull String key,
            String value
    ) {
        SharedPreferences.Editor sharedPreferencesEditor = getProperSharedPreferences(key).edit();
        sharedPreferencesEditor.putString(key, value);
        sharedPreferencesEditor.apply();
    }
}
