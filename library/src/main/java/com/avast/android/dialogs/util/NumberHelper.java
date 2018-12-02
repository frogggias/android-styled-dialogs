package com.avast.android.dialogs.util;

import androidx.annotation.Nullable;

/**
 * Created by frogggias on 19.03.18.
 */

public class NumberHelper {

    public static int parseInt(@Nullable String value, int defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.valueOf(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
