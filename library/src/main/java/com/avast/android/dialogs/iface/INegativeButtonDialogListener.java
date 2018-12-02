package com.avast.android.dialogs.iface;

import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Implement this interface in Activity or Fragment to react to negative dialog buttons.
 *
 * @author Tomáš Kypta
 * @since 2.1.0
 */
public interface INegativeButtonDialogListener {

    public void onNegativeButtonClicked(int requestCode, @Nullable Bundle data);
}
