package com.avast.android.dialogs.iface;

import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * Interface for ListDialogFragment in modes: CHOICE_MODE_NONE, CHOICE_MODE_SINGLE
 * Implement it in Activity or Fragment to react to item selection.
 */
public interface IListDialogListener {

    public void onListItemSelected(CharSequence value, int number, int requestCode, @Nullable Bundle data);
}
